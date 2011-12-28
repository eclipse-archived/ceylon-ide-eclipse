package com.redhat.ceylon.eclipse.imp.wizard;

import static com.redhat.ceylon.eclipse.ui.ICeylonResources.CEYLON_NEW_FILE;
import static org.eclipse.jface.dialogs.IDialogConstants.HORIZONTAL_MARGIN;
import static org.eclipse.jface.dialogs.IDialogConstants.HORIZONTAL_SPACING;
import static org.eclipse.jface.dialogs.IDialogConstants.VERTICAL_MARGIN;
import static org.eclipse.jface.dialogs.IDialogConstants.VERTICAL_SPACING;
import static org.eclipse.swt.layout.GridData.FILL_HORIZONTAL;
import static org.eclipse.swt.layout.GridData.HORIZONTAL_ALIGN_FILL;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.util.ExceptionHandler;
import org.eclipse.jdt.internal.ui.wizards.NewElementWizard;
import org.eclipse.jdt.internal.ui.wizards.NewWizardMessages;
import org.eclipse.jdt.ui.IPackagesViewPart;
import org.eclipse.jdt.ui.actions.ShowInPackageViewAction;
import org.eclipse.jdt.ui.wizards.NewJavaProjectWizardPageOne;
import org.eclipse.jdt.ui.wizards.NewJavaProjectWizardPageTwo;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard;

import com.redhat.ceylon.eclipse.imp.builder.CeylonNature;
import com.redhat.ceylon.eclipse.ui.CeylonPlugin;

public class NewProjectWizard extends NewElementWizard implements IExecutableExtension {

    private NewJavaProjectWizardPageOne fFirstPage;
    private NewJavaProjectWizardPageTwo fSecondPage;

    private IConfigurationElement fConfigElement;

    public NewProjectWizard() {
        this(null, null);
    }

    public NewProjectWizard(NewJavaProjectWizardPageOne pageOne, NewJavaProjectWizardPageTwo pageTwo) {
        setDefaultPageImageDescriptor(CeylonPlugin.getInstance()
                .getImageRegistry().getDescriptor(CEYLON_NEW_FILE));
        setDialogSettings(JavaPlugin.getDefault().getDialogSettings());
        setWindowTitle("New Ceylon Project");
        fFirstPage= pageOne;
        fSecondPage= pageTwo;
    }

    public void addPages() {
        if (fFirstPage == null)
            fFirstPage= new NewJavaProjectWizardPageOne() {
            private GridLayout initGridLayout(GridLayout layout, boolean margins) {
                layout.horizontalSpacing= convertHorizontalDLUsToPixels(HORIZONTAL_SPACING);
                layout.verticalSpacing= convertVerticalDLUsToPixels(VERTICAL_SPACING);
                if (margins) {
                    layout.marginWidth= convertHorizontalDLUsToPixels(HORIZONTAL_MARGIN);
                    layout.marginHeight= convertVerticalDLUsToPixels(VERTICAL_MARGIN);
                } else {
                    layout.marginWidth= 0;
                    layout.marginHeight= 0;
                }
                return layout;
            }
            public void createControl(Composite parent) {
                initializeDialogUnits(parent);

                final Composite composite= new Composite(parent, SWT.NULL);
                composite.setFont(parent.getFont());
                composite.setLayout(initGridLayout(new GridLayout(1, false), true));
                composite.setLayoutData(new GridData(HORIZONTAL_ALIGN_FILL));

                // create UI elements
                Control nameControl= createNameControl(composite);
                nameControl.setLayoutData(new GridData(FILL_HORIZONTAL));

                Control locationControl= createLocationControl(composite);
                locationControl.setLayoutData(new GridData(FILL_HORIZONTAL));

                Control jreControl= createJRESelectionControl(composite);
                jreControl.setLayoutData(new GridData(FILL_HORIZONTAL));

                /*Control layoutControl= createProjectLayoutControl(composite);
                layoutControl.setLayoutData(new GridData(FILL_HORIZONTAL));*/

                Control workingSetControl= createWorkingSetControl(composite);
                workingSetControl.setLayoutData(new GridData(FILL_HORIZONTAL));

                Control infoControl= createInfoControl(composite);
                infoControl.setLayoutData(new GridData(FILL_HORIZONTAL));

                setControl(composite);
            }
        };
        fFirstPage.setTitle("New Ceylon Project");
        fFirstPage.setDescription("Create a Ceylon project in the workspace or in an external location.");
        addPage(fFirstPage);

        if (fSecondPage == null)
            fSecondPage= new NewJavaProjectWizardPageTwo(fFirstPage);
        fSecondPage.setTitle("Ceylon Project Settings");
        fSecondPage.setDescription("Define the Ceylon build settings.");
        addPage(fSecondPage);
        
        fFirstPage.init(getSelection(), getActivePart());
    }       

    protected void finishPage(IProgressMonitor monitor) throws InterruptedException, CoreException {
        fSecondPage.performFinish(monitor); // use the full progress monitor
    }

    public boolean performFinish() {
        boolean res= super.performFinish();
        if (res) {
            final IJavaElement newElement= getCreatedElement();

            IWorkingSet[] workingSets= fFirstPage.getWorkingSets();
            if (workingSets.length > 0) {
                PlatformUI.getWorkbench().getWorkingSetManager().addToWorkingSets(newElement, workingSets);
            }

            BasicNewProjectResourceWizard.updatePerspective(fConfigElement);
            selectAndReveal(fSecondPage.getJavaProject().getProject());             

            Display.getDefault().asyncExec(new Runnable() {
                public void run() {
                    IWorkbenchPart activePart= getActivePart();
                    if (activePart instanceof IPackagesViewPart) {
                        (new ShowInPackageViewAction(activePart.getSite())).run(newElement);
                    }
                }
            });
            new CeylonNature().addToProject(getCreatedElement().getProject());
        }
        return res;
    }
    
    private IWorkbenchPart getActivePart() {
        IWorkbenchWindow activeWindow= getWorkbench().getActiveWorkbenchWindow();
        if (activeWindow != null) {
            IWorkbenchPage activePage= activeWindow.getActivePage();
            if (activePage != null) {
                return activePage.getActivePart();
            }
        }
        return null;
    }

    protected void handleFinishException(Shell shell, InvocationTargetException e) {
        String title= NewWizardMessages.JavaProjectWizard_op_error_title; 
        String message= NewWizardMessages.JavaProjectWizard_op_error_create_message;             
        ExceptionHandler.handle(e, getShell(), title, message);
    }   

    public void setInitializationData(IConfigurationElement cfig, String propertyName, Object data) {
        fConfigElement= cfig;
    }

    public boolean performCancel() {
        fSecondPage.performCancel();
        return super.performCancel();
    }

    public IJavaProject getCreatedElement() {
        return fSecondPage.getJavaProject();
    }
}