package com.redhat.ceylon.eclipse.code.wizard;

import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getProjectModules;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getRepositoryPaths;
import static com.redhat.ceylon.eclipse.ui.ICeylonResources.CEYLON_EXPORT_CAR;

import java.io.File;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import com.redhat.ceylon.compiler.typechecker.model.Module;
import com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider;
import com.redhat.ceylon.eclipse.ui.CeylonPlugin;

public class ExportModuleWizardPage extends WizardPage implements IWizardPage {

    //private IStructuredSelection selection;
    private String repositoryPath;
    private IJavaProject project;
    
    ExportModuleWizardPage(String defaultRepositoryPath, IJavaProject project) {
        super("Export Ceylon Module", "Export Ceylon Module", CeylonPlugin.getInstance()
                .getImageRegistry().getDescriptor(CEYLON_EXPORT_CAR));
        setDescription("Export a Ceylon module to a module repository.");
        repositoryPath = defaultRepositoryPath;
        this.project = project;
    }

    /*public void init(IStructuredSelection selection) {
        this.selection = selection;
    }*/
    
    @Override
    public void createControl(Composite parent) {
        initializeDialogUnits(parent);
        
        Composite composite= new Composite(parent, SWT.NONE);
        composite.setFont(parent.getFont());

        GridLayout layout = new GridLayout();
        layout.numColumns = 4;
        composite.setLayout(layout);
        
        //TODO: let you select a module descriptor to
        //      export just that module!
        addSelectProject(composite);
		addSelectRepo(composite);
        
        setControl(composite);

        Dialog.applyDialogFont(composite);

        setPageComplete(isComplete());
    }

    private void updateMessage() {
        if (project==null) {
            setErrorMessage("Please select a project");
        }
        else if (!isValidRepo()) {
            setErrorMessage("Please select an existing repository");
        }
        else {
            setErrorMessage(null);
        }
    }

    void addSelectRepo(Composite composite) {
		Label folderLabel = new Label(composite, SWT.LEFT | SWT.WRAP);
        folderLabel.setText("Target module repository: ");
        GridData flgd= new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        flgd.horizontalSpan = 1;
        folderLabel.setLayoutData(flgd);

        final Combo folder = new Combo(composite, SWT.SINGLE | SWT.BORDER);
        GridData fgd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        fgd.horizontalSpan = 2;
        fgd.grabExcessHorizontalSpace = true;
        fgd.widthHint = 300;
        folder.setLayoutData(fgd);
        folder.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                repositoryPath = folder.getText();
                updateMessage();
        		setPageComplete(isComplete());
            }
        });
        
        folder.setText(repositoryPath);
        for (String path: getRepositoryPaths(project.getProject())) {
        	folder.add(path);
        }
        
        Button selectFolder = new Button(composite, SWT.PUSH);
        selectFolder.setText("Browse...");
        GridData sfgd= new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        sfgd.horizontalSpan = 1;
        selectFolder.setLayoutData(sfgd);
        selectFolder.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
            	String dir = new DirectoryDialog(getShell()).open();
            	if (dir!=null) {
            		repositoryPath = dir;
            		folder.setText(repositoryPath);
            	}
            	updateMessage();
            	setPageComplete(isComplete());
            }
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {}
        });
	
	}
	
    Table modules;
    
    void addSelectProject(Composite composite) {
        
        Label projectLabel = new Label(composite, SWT.LEFT | SWT.WRAP);
        projectLabel.setText("Project containing modules: ");
        GridData plgd= new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        plgd.horizontalSpan = 1;
        projectLabel.setLayoutData(plgd);

        final Text projectField = new Text(composite, SWT.SINGLE | SWT.BORDER);
        GridData pgd= new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        pgd.horizontalSpan = 2;
        pgd.grabExcessHorizontalSpace = true;
        projectField.setLayoutData(pgd);
        
        Button selectProject = new Button(composite, SWT.PUSH);
        selectProject.setText("Browse...");
        GridData spgd= new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        spgd.horizontalSpan = 1;
        selectProject.setLayoutData(spgd);

        Label modulesLabel = new Label(composite, SWT.LEFT | SWT.WRAP);
        modulesLabel.setText("Modules defined in project: ");
        GridData mlgd= new GridData(GridData.HORIZONTAL_ALIGN_FILL|GridData.VERTICAL_ALIGN_BEGINNING);
        mlgd.horizontalSpan = 1;
        modulesLabel.setLayoutData(mlgd);

        modules = new Table(composite, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL);
        modules.setEnabled(false);
        GridData mgd= new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        mgd.horizontalSpan = 2;
        mgd.grabExcessHorizontalSpace = true;
        mgd.heightHint = 50;
        modules.setLayoutData(mgd);
        if (project!=null) {
			projectField.setText(project.getElementName());
        	updateModuleList();
        }
        
        new Label(composite, SWT.NONE);
        
        selectProject.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                ProjectSelectionDialog dialog = new ProjectSelectionDialog(getShell());
                dialog.setMultipleSelection(false);
                dialog.setTitle("Project Selection");
                dialog.setMessage("Select a project:");
                dialog.open();
                Object result = dialog.getFirstResult();
                if (result!=null) {
                    project = (IJavaProject) result;
        			projectField.setText(project.getElementName());
                    updateModuleList();
                }
                updateMessage();
                setPageComplete(isComplete());
            }
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {}
        });
        
        projectField.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                String projectName = projectField.getText();
                if (project==null ||
                        !project.getElementName().equals(projectName)) {
                    setProject(projectName);
                    updateModuleList();
                }
                updateMessage();
                setPageComplete(isComplete());
            }
            private void setProject(String projectName) {
                try {
                    project = null;
                    for (IJavaProject jp: JavaCore.create(ResourcesPlugin.getWorkspace().getRoot())
                            .getJavaProjects()) {
                        if (jp.getElementName().equals(projectName)) {
                        	project = jp;
                        	return;
                        }
                    }
                }
                catch (JavaModelException jme) {
                    jme.printStackTrace();
                }
            }
        });
    }

	private void updateModuleList() {
		if (project!=null) {
			modules.removeAll();
			for (Module m: getProjectModules(project.getProject()).getListOfModules()) {
				if (!m.isDefault() && !m.isJava()) {
					try {
						for (IPackageFragment pkg: project.getPackageFragments()) {
							if (!pkg.isReadOnly() &&
									pkg.getElementName().equals(m.getNameAsString())) {
								TableItem item = new TableItem(modules, SWT.NONE);
								item.setText(m.getNameAsString() + "/" + m.getVersion());
								item.setImage(CeylonLabelProvider.ARCHIVE);
							}
						}
					} 
					catch (JavaModelException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	private boolean isComplete() {
		return project!=null &&
		        isValidRepo();
	}
	
	private boolean isValidRepo() {
	    return repositoryPath!=null &&
                !repositoryPath.isEmpty() &&
                new File(repositoryPath).exists();
	}
	
	public String getRepositoryPath() {
		return repositoryPath;
	}
    
	public IJavaProject getProject() {
		return project;
	}
	
}
