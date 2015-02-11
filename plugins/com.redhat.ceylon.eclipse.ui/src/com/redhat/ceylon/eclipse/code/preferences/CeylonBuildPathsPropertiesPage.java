package com.redhat.ceylon.eclipse.code.preferences;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.ui.IJavaHelpContextIds;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.actions.WorkbenchRunnableAdapter;
import org.eclipse.jdt.internal.ui.dialogs.StatusUtil;
import org.eclipse.jdt.internal.ui.preferences.PreferencesMessages;
import org.eclipse.jdt.internal.ui.util.ExceptionHandler;
import org.eclipse.jdt.internal.ui.wizards.IStatusChangeListener;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.preference.IPreferencePageContainer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.PropertyPage;
import org.eclipse.ui.preferences.IWorkbenchPreferenceContainer;

import com.redhat.ceylon.eclipse.core.builder.CeylonBuilder;
import com.redhat.ceylon.eclipse.core.builder.CeylonNature;
import com.redhat.ceylon.eclipse.ui.CeylonPlugin;

public class CeylonBuildPathsPropertiesPage extends PropertyPage implements IStatusChangeListener {
    
    public static final String ID = "com.redhat.ceylon.eclipse.ui.preferences.paths";

    private static final String PAGE_SETTINGS = "BuildPathsPropertyPage";
    private static final String INDEX = "pageIndex";
    
    private boolean fBlockOnApply = false;
    
    private CeylonBuildPathsBlock fBuildPathsBlock;

    public void statusChanged(IStatus status) {
        setValid(!status.matches(IStatus.ERROR));
        StatusUtil.applyToStatusLine(this, status);
    }
    
    @Override
    public boolean performOk() {
        if (fBuildPathsBlock != null) {
            getSettings().put(INDEX, fBuildPathsBlock.getPageIndex());
            boolean overwriteCeylonConfig = false;
            if (!fBuildPathsBlock.wasInSyncWithCeylonConfigWhenOpening()
                    && !fBuildPathsBlock.isInSyncWithCeylonConfig()) {
                if (!MessageDialog.openQuestion(getShell(),
                        "Setting Ceylon Build Path", 
                        "The Ceylon configuration file (.ceylon/config) is not synchronized with the current build path settings.\n" +
                        "Do you want to overwrite the configuration file with the settings defined here ?")) {
                    return false;
                }
                overwriteCeylonConfig = true;
            }
            if (fBuildPathsBlock.hasChangesInDialog() || 
                    fBuildPathsBlock.isClassfileMissing() ||
                    overwriteCeylonConfig) {
                IWorkspaceRunnable runnable= new IWorkspaceRunnable() {
                    public void run(IProgressMonitor monitor)
                            throws CoreException, OperationCanceledException {
                        fBuildPathsBlock.configureJavaProject(monitor);
                    }
                };
                WorkbenchRunnableAdapter op= new WorkbenchRunnableAdapter(runnable);
                if (fBlockOnApply) {
                    try {
                        new ProgressMonitorDialog(getShell()).run(true, true, op);
                    }
                    catch (InvocationTargetException e) {
                        ExceptionHandler.handle(e, getShell(), 
                                PreferencesMessages.BuildPathsPropertyPage_error_title, 
                                PreferencesMessages.BuildPathsPropertyPage_error_message);
                        return false;
                    }
                    catch (InterruptedException e) {
                        return false;
                    }
                }
                else {
                    op.runAsUserJob(PreferencesMessages.BuildPathsPropertyPage_job_title, null);
                }
            }
        }
        return true;

    }

    @Override
    protected Control createContents(Composite parent) {
        // ensure the page has no special buttons
        noDefaultAndApplyButton();

        IProject project = getProject();
        Control result;
        if (project == null) {
            result = createWithoutCeylon(parent);
        }
        else if (!project.isOpen()) {
            result = createForClosedProject(parent);
        } 
        else if (!isCeylonProject(project)) {
            result = createWithoutCeylon(parent);
        } 
        else  {
            result = createWithCeylon(parent, project);
        }
        Dialog.applyDialogFont(result);
        return result;
    }

    /*
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
     */
    @Override
    public void createControl(Composite parent) {
        super.createControl(parent);
        PlatformUI.getWorkbench().getHelpSystem().setHelp(getControl(), 
                IJavaHelpContextIds.BUILD_PATH_PROPERTY_PAGE);
    }

    private IDialogSettings getSettings() {
        IDialogSettings javaSettings = 
                CeylonPlugin.getInstance().getDialogSettings();
        IDialogSettings pageSettings = 
                javaSettings.getSection(PAGE_SETTINGS);
        if (pageSettings == null) {
            pageSettings = 
                    javaSettings.addNewSection(PAGE_SETTINGS);
            pageSettings.put(INDEX, 1);
        }
        return pageSettings;
    }

    @Override
    public boolean okToLeave() {
        if (fBuildPathsBlock != null && 
                fBuildPathsBlock.hasChangesInDialog()) {
            String title = PreferencesMessages.BuildPathsPropertyPage_unsavedchanges_title;
            String message = PreferencesMessages.BuildPathsPropertyPage_unsavedchanges_message;
            String[] buttonLabels = new String[] {
                    PreferencesMessages.BuildPathsPropertyPage_unsavedchanges_button_save,
                    PreferencesMessages.BuildPathsPropertyPage_unsavedchanges_button_discard,
                    PreferencesMessages.BuildPathsPropertyPage_unsavedchanges_button_ignore
            };
            MessageDialog dialog= new MessageDialog(getShell(), 
                    title, null, message, 
                    MessageDialog.QUESTION, buttonLabels, 0);
            int res= dialog.open();
            if (res == 0) { //save
                fBlockOnApply= true;
                return performOk() && super.okToLeave();
            } else if (res == 1) { // discard
                fBuildPathsBlock.init(JavaCore.create(getProject()), 
                        null, null, 
                        CeylonBuilder.compileToJava(getProject()));
            } else {
                // keep unsaved
            }
        }
        return super.okToLeave();
    }
    
    @Override
    public void setVisible(boolean visible) {
        if (fBuildPathsBlock != null) {
            if (visible) {
                if (!fBuildPathsBlock.hasChangesInDialog() && 
                        fBuildPathsBlock.hasChangesInClasspathFile()) {
                    fBuildPathsBlock.init(JavaCore.create(getProject()), 
                            null, null, 
                            CeylonBuilder.compileToJava(getProject()));
                }
            }
        }
        super.setVisible(visible);
    }


    /*
     * Content for valid projects.
     */
    private Control createWithCeylon(Composite parent, IProject project) {
        IWorkbenchPreferenceContainer pageContainer= null;
        IPreferencePageContainer container= getContainer();
        if (container instanceof IWorkbenchPreferenceContainer) {
            pageContainer= (IWorkbenchPreferenceContainer) container;
        }

        fBuildPathsBlock= new CeylonBuildPathsBlock(this, 
                getSettings().getInt(INDEX), pageContainer);
        fBuildPathsBlock.init(JavaCore.create(project), 
                null, null, 
                CeylonBuilder.compileToJava(project));
        return fBuildPathsBlock.createControl(parent);
    }

    /*
     * Content for non-Java projects.
     */
    private Control createWithoutCeylon(Composite parent) {
        Label label = new Label(parent, SWT.LEFT);
        label.setText("Not a Ceylon project");
        fBuildPathsBlock= null;
        setValid(true);
        return label;
    }

    /*
     * Content for closed projects.
     */
    private Control createForClosedProject(Composite parent) {
        Label label= new Label(parent, SWT.LEFT);
        label.setText("Information not available for closed project.");
        fBuildPathsBlock= null;
        setValid(true);
        return label;
    }

    private IProject getProject() {
        IAdaptable adaptable = getElement();
        return adaptable == null ? null : 
            (IProject) adaptable.getAdapter(IProject.class);
    }

    private boolean isCeylonProject(IProject proj) {
        try {
            return proj.hasNature(JavaCore.NATURE_ID) &&
                    proj.hasNature(CeylonNature.NATURE_ID);
        } catch (CoreException e) {
            JavaPlugin.log(e);
        }
        return false;
    }


}