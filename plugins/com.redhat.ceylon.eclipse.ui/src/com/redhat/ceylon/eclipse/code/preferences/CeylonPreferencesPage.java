package com.redhat.ceylon.eclipse.code.preferences;

import static com.redhat.ceylon.compiler.typechecker.TypeChecker.LANGUAGE_MODULE_VERSION;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getCeylonModulesOutputPath;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getDefaultUserRepositories;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getRepositoryPaths;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.interpolateVariablesInRepositoryPath;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.isExplodeModulesEnabled;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.showWarnings;
import static com.redhat.ceylon.eclipse.core.builder.CeylonNature.NATURE_ID;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.internal.ui.wizards.NewWizardMessages;
import org.eclipse.jdt.internal.ui.wizards.TypedElementSelectionValidator;
import org.eclipse.jdt.internal.ui.wizards.TypedViewerFilter;
import org.eclipse.jdt.internal.ui.wizards.buildpaths.FolderSelectionDialog;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import org.eclipse.ui.dialogs.PropertyPage;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.views.navigator.ResourceComparator;

import com.redhat.ceylon.eclipse.core.builder.CeylonNature;
import com.redhat.ceylon.eclipse.ui.CeylonPlugin;
import com.redhat.ceylon.eclipse.ui.CeylonResources;

public class CeylonPreferencesPage extends PropertyPage {

    private List<String> repositoryPaths;
    private IPath outputPath;
    private boolean explodeModules;
    private boolean showCompilerWarnings=true;
    boolean builderEnabled = false;
        
    private Button showWarnings;
    private Button enableExplodeModules;

    //TODO: fix copy/paste!
    public boolean isRepoValid() {
    	for (String repositoryPath: repositoryPaths) {
    		String carPath = interpolateVariablesInRepositoryPath(repositoryPath) + 
    				"/ceylon/language/" + LANGUAGE_MODULE_VERSION + 
    				"/ceylon.language-" + LANGUAGE_MODULE_VERSION + ".car";
    		if (new File(carPath).exists()) {
    			return true;
    		}
    	}
    	return false;
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
        explodeModules=false;
        enableExplodeModules.setSelection(false);
        showCompilerWarnings=true;
        showWarnings.setSelection(true);
        repositoryPaths = Arrays.asList(getDefaultUserRepositories());
        repoFolders.removeAll();
        for(String repo : repositoryPaths)
        	addRepoToTable(repo);
        outputPath=getDefaultOutputPath(getSelectedProject());
        outputFolder.setText(outputPath.toString());
        super.performDefaults();
    }
    
    private void store() {
        final IProject project = getSelectedProject();
		//if (!embeddedRepo) ExportModuleWizard.persistDefaultRepositoryPath(repositoryPath);
		new CeylonNature(outputPath, repositoryPaths,
				explodeModules, !showCompilerWarnings)
		                .addToProject(project);
    }

    private IProject getSelectedProject() {
        return (IProject) getElement().getAdapter(IProject.class);
    }
    
    //TODO: fix copy/paste!
    void addControls(Composite parent) {
        
        Label desc = new Label(parent, SWT.LEFT | SWT.WRAP);
        desc.setText("The Ceylon builder compiles Ceylon source contained in the project");

        final Button enableBuilder = new Button(parent, SWT.PUSH);
        enableBuilder.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER));
        enableBuilder.setText("Enable Ceylon Builder");
        enableBuilder.setEnabled(!builderEnabled && getSelectedProject().isOpen());
        enableBuilder.setImage(CeylonPlugin.getInstance()
                .getImageRegistry().get(CeylonResources.ELE32));
        //enableBuilder.setSize(40, 40);

        Label sep = new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL);
        GridData sgd= new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        sep.setLayoutData(sgd);

        //Label misc = new Label(parent, SWT.LEFT | SWT.WRAP);
        //misc.setText("Ceylon compiler settings:");

        Group composite = new Group(parent, SWT.SHADOW_ETCHED_IN);
        composite.setText("Ceylon compiler settings");
        GridData gd= new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        gd.grabExcessHorizontalSpace=true;
        composite.setLayoutData(gd);
        GridLayout layout = new GridLayout();
        layout.numColumns = 1;
        composite.setLayout(layout); 
        
        enableExplodeModules = new Button(composite, SWT.CHECK | SWT.LEFT | SWT.WRAP);
        enableExplodeModules.setText("Enable Java classes calling Ceylon (may affect performance)");
        enableExplodeModules.setSelection(explodeModules);
        enableExplodeModules.setEnabled(builderEnabled);

        showWarnings = new Button(composite, SWT.CHECK | SWT.LEFT | SWT.WRAP);
        showWarnings.setText("Show compiler warnings (for unused declarations)");
        showWarnings.setSelection(showCompilerWarnings);
        showWarnings.setEnabled(true);
        
        addSelectOutputSection(parent);
        addSelectRepoSection(parent);
        
        enableBuilder.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                new CeylonNature().addToProject(getSelectedProject());
                enableBuilder.setEnabled(false);
                enableExplodeModules.setEnabled(true);
                builderEnabled=true;
            }
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {}
        });
    
        enableExplodeModules.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
            	explodeModules = !explodeModules;
            }
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {}
        });
        
        showWarnings.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
            	showCompilerWarnings = !showCompilerWarnings;
            }
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {}
        });
        
        if (!isRepoValid()) {
            setErrorMessage("Please select a module repository containing the language module");
        }

    }

    private Table repoFolders;
    
    private static Image repo = CeylonPlugin.getInstance().image("runtime_obj.gif").createImage();
    
	private void addSelectRepoSection(Composite parent) {
		//Label title = new Label(parent, SWT.LEFT | SWT.WRAP);
        //title.setText("The Ceylon module repository contains dependencies:");
        //final Composite composite= new Composite(parent, SWT.NONE);
        Group composite = new Group(parent, SWT.SHADOW_ETCHED_IN);
        composite.setText("Ceylon module repository");
        GridData gd= new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        gd.grabExcessHorizontalSpace=true;
        composite.setLayoutData(gd);
        GridLayout layout = new GridLayout();
        layout.numColumns = 4;
        composite.setLayout(layout);        
        
        Label folderLabel = new Label(composite, SWT.LEFT | SWT.WRAP);
        folderLabel.setText("Module repositories on build path: ");
        GridData flgd= new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        flgd.horizontalSpan = 4;
        folderLabel.setLayoutData(flgd);

        repoFolders = new Table(composite, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
        GridData fgd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        fgd.horizontalSpan = 3;
        fgd.verticalSpan = 4;
        fgd.heightHint = 70;
        fgd.grabExcessHorizontalSpace = true;
        fgd.widthHint = 200;
        repoFolders.setLayoutData(fgd);
        repoFolders.setEnabled(builderEnabled);
        
        for (String repositoryPath: repositoryPaths) {
        	addRepoToTable(repositoryPath);
        }
        
        Button selectRepoFolder = new Button(composite, SWT.PUSH);
        selectRepoFolder.setText("Add Repository...");
        GridData sfgd= new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        sfgd.horizontalSpan = 1;
        selectRepoFolder.setLayoutData(sfgd);
        selectRepoFolder.setEnabled(builderEnabled);
        selectRepoFolder.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                String dir = new DirectoryDialog(getShell(), SWT.SHEET).open();
                if(dir != null)
                    addRepo(dir);
            }
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {}
        });

        Button selectRemoteRepo = new Button(composite, SWT.PUSH);
        selectRemoteRepo.setText("Add Remote Repository");
        GridData srrgd= new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        srrgd.horizontalSpan = 1;
        selectRemoteRepo.setLayoutData(srrgd);
        selectRemoteRepo.setEnabled(builderEnabled);
        selectRemoteRepo.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                InputDialog input = new InputDialog(getShell(), "Add Remote Repository", "Enter a remote repository URI", "http://", 
                        new IInputValidator(){
                    @Override
                    public String isValid(String val) {
                        try {
                            new URI(val);
                            // FIXME: we might want to validate it more than that: for example to check that it's http/https
                            return null;
                        } catch (URISyntaxException e) {
                            return "Invalid URI: " + e.getReason();
                        }
                    }
                    
                });
                int ret = input.open();
                if(ret == InputDialog.OK){
                    addRepo(input.getValue());
                }
            }
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {}
        });

        Button selectHerdRepo = new Button(composite, SWT.PUSH);
        selectHerdRepo.setText("Add Ceylon Herd");
        GridData srfgd= new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        srfgd.horizontalSpan = 1;
        selectHerdRepo.setLayoutData(srfgd);
        selectHerdRepo.setEnabled(builderEnabled);
        selectHerdRepo.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                String url = "http://modules.ceylon-lang.org/test";
                addRepo(url);
            }
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {}
        });

        Button deleteRepoFolder = new Button(composite, SWT.PUSH);
        deleteRepoFolder.setText("Remove Repository");
        GridData dfgd= new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        dfgd.horizontalSpan = 1;
        deleteRepoFolder.setLayoutData(dfgd);
        deleteRepoFolder.setEnabled(builderEnabled);
        deleteRepoFolder.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
            	int[] selection = repoFolders.getSelectionIndices();
				repoFolders.remove(selection);
            	Iterator<String> iter = repositoryPaths.iterator();
            	int i=0;
            	while (iter.hasNext()) {
            		iter.next();
            		if (Arrays.binarySearch(selection, i++)>=0) {
            			iter.remove();
            		}
            	}
                if (!isRepoValid()) {
                    setErrorMessage("Please select a module repository containing the language module");
                }
                else {
                    setErrorMessage(null);
                }
            }
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {}
        });
	}

    protected void addRepo(String dir) {
        repositoryPaths.add(dir);
        addRepoToTable(dir);
        if (!isRepoValid()) {
            setErrorMessage("Please select a module repository containing the language module");
        } else {
            setErrorMessage(null);
        }
    }

    private void addRepoToTable(String repositoryPath) {
		TableItem item = new TableItem(repoFolders,SWT.NONE);
		item.setText(repositoryPath);
		item.setImage(repo);
	}

    private Button selectOutputFolder;
    private Text outputFolder;
    
	private void addSelectOutputSection(Composite parent) {
		//Label title = new Label(parent, SWT.LEFT | SWT.WRAP);
        //title.setText("The Ceylon output folder contains compiled module archives:");
        //final Composite composite= new Composite(parent, SWT.NONE);
        Group composite = new Group(parent, SWT.SHADOW_ETCHED_IN);
        composite.setText("The Ceylon output folder contains compiled module archives");
        GridData gd= new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        gd.grabExcessHorizontalSpace=true;
        composite.setLayoutData(gd);
        GridLayout layout = new GridLayout();
        layout.numColumns = 4;
        composite.setLayout(layout);        
        
        Label folderLabel = new Label(composite, SWT.LEFT | SWT.WRAP);
        folderLabel.setText("Output folder: ");
        GridData flgd= new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        flgd.horizontalSpan = 1;
        folderLabel.setLayoutData(flgd);

        outputFolder = new Text(composite, SWT.SINGLE | SWT.BORDER);
        GridData fgd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        fgd.horizontalSpan = 2;
        fgd.grabExcessHorizontalSpace = true;
        outputFolder.setLayoutData(fgd);
        outputFolder.setEnabled(builderEnabled);
        outputFolder.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                outputPath = new Path(outputFolder.getText());
            }
        });
        
        if (outputPath!=null) {
            outputFolder.setText(outputPath.toString());
        }
        
        selectOutputFolder = new Button(composite, SWT.PUSH);
        selectOutputFolder.setText("Browse...");
        GridData sfgd= new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        sfgd.horizontalSpan = 1;
        selectOutputFolder.setLayoutData(sfgd);
        selectOutputFolder.setEnabled(builderEnabled);
        selectOutputFolder.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
    			IProject project = getSelectedProject();
				IWorkspaceRoot root = project.getWorkspace().getRoot();

    			Class<?>[] acceptedClasses= new Class[] { IProject.class, IFolder.class };
        		ISelectionStatusValidator validator= new TypedElementSelectionValidator(acceptedClasses, false);
        		IProject[] allProjects= root.getProjects();
        		ArrayList<IProject> rejectedElements= new ArrayList<IProject>(allProjects.length);
        		for (int i= 0; i < allProjects.length; i++) {
        			if (!allProjects[i].equals(project)) {
        				rejectedElements.add(allProjects[i]);
        			}
        		}
        		ViewerFilter filter= new TypedViewerFilter(acceptedClasses, rejectedElements.toArray());

        		ILabelProvider lp= new WorkbenchLabelProvider();
        		ITreeContentProvider cp= new WorkbenchContentProvider();

        		IResource container= null;
        		if (outputPath!=null) {
        			container= getOutputFolder(project);
					if (container.exists() && container.isHidden()) {
						try {
							container.setHidden(false); //TODO: whooaah awful hack!
						} 
						catch (CoreException ce) {
							ce.printStackTrace();
						}
					}
        		}
        		try {
        			
        			FolderSelectionDialog dialog= new FolderSelectionDialog(getShell(), lp, cp);
        			dialog.setTitle(NewWizardMessages.BuildPathsBlock_ChooseOutputFolderDialog_title);
        			dialog.setValidator(validator);
        			dialog.setMessage(NewWizardMessages.BuildPathsBlock_ChooseOutputFolderDialog_description);
        			dialog.addFilter(filter);
        			dialog.setInput(root);
        			dialog.setInitialSelection(container);
        			dialog.setComparator(new ResourceComparator(ResourceComparator.NAME));

        			if (dialog.open() == Window.OK) {
        				IResource result = (IResource)dialog.getFirstResult();
        				if (result!=null) {
        					outputPath = result.getFullPath();
        					outputFolder.setText(outputPath.toString());
        				}
        			}
        			
        		}
        		finally {
        			if (container.exists()) {
        				try {
        					container.setHidden(true);
        				} 
        				catch (CoreException ce) {
        					ce.printStackTrace();
        				}
        			}
        		}
            }
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {}
        });
 	}

	private IFolder getOutputFolder(IProject project) {
		return project.getFolder(outputPath.removeFirstSegments(1));
	}
	
    @Override
    protected Control createContents(Composite composite) {
        
        IProject project = getSelectedProject();
        
        if (project.isOpen()) {
        
			try {
	            builderEnabled = project.hasNature(NATURE_ID);
	        } 
	        catch (CoreException e) {
	            e.printStackTrace();
	        }
	        
			repositoryPaths = new ArrayList<String>();
			repositoryPaths.addAll(Arrays.asList(getRepositoryPaths(project)));
	        explodeModules = isExplodeModulesEnabled(project);
	        showCompilerWarnings = showWarnings(project);
	        outputPath = getCeylonModulesOutputPath(project);
	        if (outputPath==null) {
	        	outputPath = getDefaultOutputPath(project);
	        }
	        
        }

        addControls(composite);
        return composite;
    }

	private IPath getDefaultOutputPath(IProject project) {
		return project.getFullPath().append("modules");
	}

}
