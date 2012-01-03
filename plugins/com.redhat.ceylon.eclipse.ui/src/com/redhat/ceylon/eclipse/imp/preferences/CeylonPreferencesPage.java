package com.redhat.ceylon.eclipse.imp.preferences;

import static org.eclipse.core.resources.IncrementalProjectBuilder.FULL_BUILD;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.PropertyPage;
import org.osgi.service.prefs.BackingStoreException;

import com.redhat.ceylon.eclipse.imp.builder.CeylonNature;
import com.redhat.ceylon.eclipse.imp.wizard.ExportModuleWizard;
import com.redhat.ceylon.eclipse.ui.CeylonPlugin;

public class CeylonPreferencesPage extends PropertyPage {

    private String repositoryPath;
    private boolean useEmbeddedRepo;
    
    private Button useEmbedded;
    
    @Override
    public boolean performOk() {
        store();
        return super.performOk();
    }
    
    @Override
    protected void performApply() {
        store();
        super.performApply();
    }
    
    @Override
    protected void performDefaults() {
        useEmbeddedRepo=true;
        useEmbedded.setSelection(true);
        super.performDefaults();
    }
    
    private void store() {
        IEclipsePreferences node = new ProjectScope(getSelectedProject())
                .getNode(CeylonPlugin.PLUGIN_ID);
        if (!useEmbeddedRepo && repositoryPath!=null && !repositoryPath.isEmpty()) {
            node.put("repo", repositoryPath);
            /*getCreatedElement().getProject()
                    .setPersistentProperty(new QualifiedName(CeylonPlugin.PLUGIN_ID, "repo"), 
                            repositoryPath);*/
            ExportModuleWizard.persistDefaultRepositoryPath(repositoryPath);
        }
        else if (useEmbeddedRepo) {
            node.remove("repo");
        }
        try {
            node.flush();
        } 
        catch (BackingStoreException e) {
            e.printStackTrace();
        }
        Job buildJob = new Job("Rebuilding Ceylon project " + getSelectedProject().getName()) {
            @Override
            public IStatus run(IProgressMonitor monitor) {
                try {
                    getSelectedProject().build(FULL_BUILD, monitor);
                } 
                catch (CoreException e) {
                    return new Status(IStatus.ERROR, CeylonPlugin.getInstance().getID(), 
                            "Job '" + this.getName() + "' failed", e);
                }
                return Status.OK_STATUS;
            }

        };
        buildJob.schedule();
    }

    private IProject getSelectedProject() {
        return (IProject) getElement();
    }
    
    //TODO: fix copy/paste!
    void addSelectRepo(Composite parent) {
        
        final Button enableBuilder = new Button(parent, SWT.PUSH);
        enableBuilder.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER));
        enableBuilder.setText("Enable Ceylon Builder");
        enableBuilder.setEnabled(!builderEnabled);

        final Composite composite= new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.numColumns = 4;
        composite.setLayout(layout);
        
        GridData gd= new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        gd.grabExcessHorizontalSpace=true;
        composite.setLayoutData(gd);
        
        useEmbedded = new Button(composite, SWT.CHECK);
        useEmbedded.setText("Use embedded module repository");
        GridData igd= new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        igd.horizontalSpan = 4;
        igd.grabExcessHorizontalSpace = true;
        useEmbedded.setLayoutData(igd);

        useEmbedded.setSelection(useEmbeddedRepo);
        
        useEmbedded.setEnabled(builderEnabled);

        Label folderLabel = new Label(composite, SWT.LEFT | SWT.WRAP);
        folderLabel.setText("External module repository: ");
        GridData flgd= new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        flgd.horizontalSpan = 1;
        folderLabel.setLayoutData(flgd);

        final Text folder = new Text(composite, SWT.SINGLE | SWT.BORDER);
        GridData fgd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        fgd.horizontalSpan = 2;
        fgd.grabExcessHorizontalSpace = true;
        folder.setLayoutData(fgd);
        folder.setEnabled(!useEmbeddedRepo&&builderEnabled);
        folder.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                repositoryPath = folder.getText();
            }
        });
        
        if (repositoryPath!=null) {
            folder.setText(repositoryPath);
        }
        else {
            repositoryPath = ExportModuleWizard.getDefaultRepositoryPath();
            folder.setText(repositoryPath);
        }
        
        final Button selectFolder = new Button(composite, SWT.PUSH);
        selectFolder.setText("Browse...");
        GridData sfgd= new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        sfgd.horizontalSpan = 1;
        selectFolder.setLayoutData(sfgd);
        selectFolder.setEnabled(!useEmbeddedRepo&&builderEnabled);
        selectFolder.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                String dir = new DirectoryDialog(getShell()).open();
                if (dir!=null) {
                    repositoryPath = dir;
                    folder.setText(repositoryPath);
                }
            }
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {}
        });
    
        useEmbedded.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                useEmbeddedRepo = !useEmbeddedRepo;
                folder.setEnabled(!useEmbeddedRepo);
                selectFolder.setEnabled(!useEmbeddedRepo);
            }
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {}
        });
        
        enableBuilder.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                new CeylonNature().addToProject(getSelectedProject());
                enableBuilder.setEnabled(false);
                useEmbedded.setEnabled(true);
                builderEnabled=true;
            }
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {}
        });
    
    }

    boolean builderEnabled = false;
    
    @Override
    protected Control createContents(Composite composite) {
        
        try {
            builderEnabled = getSelectedProject().hasNature(CeylonNature.NATURE_ID);
        } 
        catch (CoreException e) {
            e.printStackTrace();
        }
        
        repositoryPath = new ProjectScope(getSelectedProject())
                .getNode(CeylonPlugin.PLUGIN_ID)
                .get("repo", null);
        useEmbeddedRepo = repositoryPath==null;

        addSelectRepo(composite);
        return composite;
    }

}
