package com.redhat.ceylon.eclipse.imp.preferences;

import static com.redhat.ceylon.compiler.typechecker.TypeChecker.LANGUAGE_MODULE_VERSION;

import java.io.File;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.CoreException;
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
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.PropertyPage;
import org.osgi.service.prefs.BackingStoreException;

import com.redhat.ceylon.eclipse.imp.builder.CeylonNature;
import com.redhat.ceylon.eclipse.imp.wizard.ExportModuleWizard;
import com.redhat.ceylon.eclipse.ui.CeylonPlugin;
import com.redhat.ceylon.eclipse.ui.ICeylonResources;

public class CeylonPreferencesPage extends PropertyPage {

    private String repositoryPath;
    private boolean useEmbeddedRepo;
    private boolean enableJdtClassesDir;
    
    private Button useEmbedded;
    private Button enableJdtClasses;

    //TODO: fix copy/paste!
    public boolean isRepoValid() {
        if (useEmbeddedRepo) {
            return true;
        }
        else if (repositoryPath==null) {
            return false;
        }
        else {
            String carPath = repositoryPath + "/ceylon/language/" + LANGUAGE_MODULE_VERSION + 
                    "/ceylon.language-" + LANGUAGE_MODULE_VERSION + ".car";
            return new File(carPath).exists();
        }        
    }
    
    @Override
    public boolean performOk() {
        if (!isRepoValid()) return false;
        store();
        return super.performOk();
    }
    
    @Override
    protected void performApply() {
        if (isRepoValid()) {
            store();
            super.performApply();
        }
    }
    
    @Override
    public boolean okToLeave() {
        if (!isRepoValid()) return false;
        return super.okToLeave();
    }
    
    @Override
    protected void performDefaults() {
        useEmbeddedRepo=true;
        useEmbedded.setSelection(true);
        enableJdtClassesDir=false;
        enableJdtClasses.setSelection(false);
        selectFolder.setEnabled(false);
        folder.setEnabled(false);
        super.performDefaults();
    }
    
    private void store() {
        final IProject project = getSelectedProject();
		IEclipsePreferences node = new ProjectScope(project)
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
        if (enableJdtClassesDir) {
        	node.putBoolean("jdtClasses", true);
        }
        else {
        	node.remove("jdtClasses");
        }
        try {
            node.flush();
        } 
        catch (BackingStoreException e) {
            e.printStackTrace();
        }
    	new CeylonNature() {
    		protected void setUpClasspath(IProject project) {
    			try {
					project.getFolder("JDTClasses").delete(true, null);
				} 
    			catch (CoreException e) {
					e.printStackTrace();
				}
    			super.setUpClasspath(project);
    			try {
    				project.getWorkspace()
    				        .build(IncrementalProjectBuilder.CLEAN_BUILD, null);
    			}
    			catch (CoreException e) {
    				e.printStackTrace();
    			}
    		}
    	}.addToProject(project);
    }

    private IProject getSelectedProject() {
        return (IProject) getElement().getAdapter(IProject.class);
    }
    
    private Button selectFolder;
    private Text folder;
    
    //TODO: fix copy/paste!
    void addSelectRepo(Composite parent) {
        
        Label desc = new Label(parent, SWT.LEFT | SWT.WRAP);
        desc.setText("The Ceylon builder compiles Ceylon source contained in the project:");

        final Button enableBuilder = new Button(parent, SWT.PUSH);
        enableBuilder.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER));
        enableBuilder.setText("Enable Ceylon Builder");
        enableBuilder.setEnabled(!builderEnabled);
        enableBuilder.setImage(CeylonPlugin.getInstance()
                .getImageRegistry().get(ICeylonResources.ELE32));
        //enableBuilder.setSize(40, 40);

        Label sep = new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL);
        GridData sgd= new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        sep.setLayoutData(sgd);

        enableJdtClasses = new Button(parent, SWT.CHECK | SWT.LEFT | SWT.WRAP);
        enableJdtClasses.setText("Enable Java classes calling Ceylon (may affect performance)");
        enableJdtClasses.setSelection(enableJdtClassesDir);
        enableJdtClasses.setEnabled(builderEnabled);

        Label title = new Label(parent, SWT.LEFT | SWT.WRAP);
        title.setText("The Ceylon module repository contains dependencies:");
        //final Composite composite= new Composite(parent, SWT.NONE);
        Group composite = new Group(parent, SWT.SHADOW_ETCHED_IN);
        GridData gd= new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        gd.grabExcessHorizontalSpace=true;
        composite.setLayoutData(gd);
        GridLayout layout = new GridLayout();
        layout.numColumns = 4;
        composite.setLayout(layout);        
        
        useEmbedded = new Button(composite, SWT.CHECK);
        useEmbedded.setText("Use embedded module repository (contains only language module)");
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

        folder = new Text(composite, SWT.SINGLE | SWT.BORDER);
        GridData fgd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        fgd.horizontalSpan = 2;
        fgd.grabExcessHorizontalSpace = true;
        folder.setLayoutData(fgd);
        folder.setEnabled(!useEmbeddedRepo&&builderEnabled);
        folder.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                repositoryPath = folder.getText();
                if (!isRepoValid()) {
                    setErrorMessage("Please select a module repository containing the language module");
                }
                else {
                    setErrorMessage(null);
                }
            }
        });
        
        if (repositoryPath!=null) {
            folder.setText(repositoryPath);
        }
        else {
            repositoryPath = ExportModuleWizard.getDefaultRepositoryPath();
            folder.setText(repositoryPath);
        }
        
        selectFolder = new Button(composite, SWT.PUSH);
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
                if (useEmbeddedRepo) {
                    setErrorMessage(null);
                }
                else if (!isRepoValid()) {
                    setErrorMessage("Please select a module repository containing the language module");
                }
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
                enableJdtClasses.setEnabled(true);
                builderEnabled=true;
            }
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {}
        });
    
        enableJdtClasses.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
            	enableJdtClassesDir = !enableJdtClassesDir;
            }
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {}
        });
        
        if (useEmbeddedRepo) {
            setErrorMessage(null);
        }
        else if (!isRepoValid()) {
            setErrorMessage("Please select a module repository containing the language module");
        }

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
        
        IEclipsePreferences node = new ProjectScope(getSelectedProject())
                .getNode(CeylonPlugin.PLUGIN_ID);
		repositoryPath = node.get("repo", null);
        useEmbeddedRepo = repositoryPath==null;
        enableJdtClassesDir = node.getBoolean("jdtClasses", false);

        addSelectRepo(composite);
        return composite;
    }

}
