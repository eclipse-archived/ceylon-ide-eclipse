package com.redhat.ceylon.eclipse.code.wizard;

import static com.redhat.ceylon.compiler.typechecker.TypeChecker.LANGUAGE_MODULE_VERSION;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getDefaultUserRepositories;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.interpolateVariablesInRepositoryPath;
import static com.redhat.ceylon.eclipse.ui.CeylonResources.CEYLON_NEW_FILE;
import static org.eclipse.jdt.launching.JavaRuntime.JRE_CONTAINER;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.util.ExceptionHandler;
import org.eclipse.jdt.internal.ui.wizards.NewElementWizard;
import org.eclipse.jdt.internal.ui.wizards.NewWizardMessages;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.IVMInstall2;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jdt.ui.IPackagesViewPart;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard;

import com.redhat.ceylon.eclipse.code.explorer.PackageExplorerPart;
import com.redhat.ceylon.eclipse.core.builder.CeylonNature;
import com.redhat.ceylon.eclipse.ui.CeylonPlugin;

public class NewProjectWizard extends NewElementWizard implements IExecutableExtension {

    private NewCeylonProjectWizardPageOne fFirstPage;
    private NewCeylonProjectWizardPageTwo fSecondPage;

    private IConfigurationElement fConfigElement;

    private List<String> repositoryPaths = new ArrayList<String>();
    private boolean showCompilerWarnings=true;
    private boolean enableJdtClassesDir=false;
    
    public NewProjectWizard() {
        this(null, null);
    }

    public NewProjectWizard(NewCeylonProjectWizardPageOne pageOne, NewCeylonProjectWizardPageTwo pageTwo) {
        setDefaultPageImageDescriptor(CeylonPlugin.getInstance()
                .getImageRegistry().getDescriptor(CEYLON_NEW_FILE));
        setDialogSettings(JavaPlugin.getDefault().getDialogSettings());
        setWindowTitle("New Ceylon Project");
        fFirstPage= pageOne;
        fSecondPage= pageTwo;
    }

    public void addPages() {
        if (fFirstPage == null) {
            fFirstPage= new NewCeylonProjectWizardPageOne() { 
	        	@Override
	            void addExtraControls(Composite parent) {
	            	NewProjectWizard.this.addControls(parent);
	            }
        	};
        }
        fFirstPage.setTitle("New Ceylon Project");
        fFirstPage.setDescription("Create a Ceylon project in the workspace or in an external location.");
        addPage(fFirstPage);

        if (fSecondPage == null) {
            fSecondPage= new NewCeylonProjectWizardPageTwo(fFirstPage);
        }
        fSecondPage.setTitle("Ceylon Project Settings");
        fSecondPage.setDescription("Define the Ceylon build settings.");
        addPage(fSecondPage);
        
        fFirstPage.init(getSelection(), getActivePart());
    }
    
    //TODO: fix copy/paste!
    void addControls(Composite parent) {
        Group composite = new Group(parent, SWT.SHADOW_ETCHED_IN);
        composite.setText("Ceylon compiler settings");
        GridData gd= new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        composite.setLayoutData(gd);
        GridLayout layout = new GridLayout();
        layout.numColumns = 1;
        composite.setLayout(layout);        
        
        final Button enableJdtClasses = new Button(composite, SWT.CHECK | SWT.LEFT | SWT.WRAP);
        enableJdtClasses.setText("Enable Java classes calling Ceylon (may affect performance)");
        enableJdtClasses.setSelection(false);
        enableJdtClasses.setEnabled(true);

        final Button showWarnings = new Button(composite, SWT.CHECK | SWT.LEFT | SWT.WRAP);
        showWarnings.setText("Show compiler warnings (for unused declarations)");
        showWarnings.setSelection(true);
        showWarnings.setEnabled(true);

        addSelectRepoSection(parent);
        
        enableJdtClasses.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
            	enableJdtClassesDir = !enableJdtClassesDir;
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
        
    }

    private Table repoFolders;
    
    private static Image repo = CeylonPlugin.getInstance().image("runtime_obj.gif").createImage();
    
	private void addSelectRepoSection(Composite parent) {
		//final Composite composite= new Composite(parent, SWT.NONE);
        Group composite = new Group(parent, SWT.SHADOW_ETCHED_IN);
        composite.setText("Ceylon module repository");
        GridData gd= new GridData(GridData.HORIZONTAL_ALIGN_FILL);
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
        repoFolders.setEnabled(true);
        
        for (String repo: getDefaultUserRepositories()) {
        	addRepoToTable(repo);
        	repositoryPaths.add(repo);
        }
        
        Button selectRepoFolder = new Button(composite, SWT.PUSH);
        selectRepoFolder.setText("Add Repository...");
        GridData sfgd= new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        sfgd.horizontalSpan = 1;
        selectRepoFolder.setLayoutData(sfgd);
        selectRepoFolder.setEnabled(true);
        selectRepoFolder.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                String dir = new DirectoryDialog(getShell(), SWT.SHEET).open();
                if (dir!=null) {
                    addRepo(dir);
                }
            }
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {}
        });

        Button selectRemoteRepo = new Button(composite, SWT.PUSH);
        selectRemoteRepo.setText("Add Remote Repository");
        GridData srrgd= new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        srrgd.horizontalSpan = 1;
        selectRemoteRepo.setLayoutData(srrgd);
        selectRemoteRepo.setEnabled(true);
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
        selectHerdRepo.setEnabled(true);
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
        deleteRepoFolder.setEnabled(true);
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
                	fFirstPage.setErrorMessage("Please select a module repository containing the language module");
                }
                else {
                	fFirstPage.setErrorMessage(null);
                }
            }
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {}
        });
	}
    
	protected void addRepo(String url) {
        repositoryPaths.add(url);
        addRepoToTable(url);
        if (!isRepoValid()) {
            fFirstPage.setErrorMessage("Please select a module repository containing the language module");
        }
        else {
            fFirstPage.setErrorMessage(null);
        }
    }

    private void addRepoToTable(String repositoryPath) {
		TableItem item = new TableItem(repoFolders,SWT.NONE);
		item.setText(repositoryPath);
		item.setImage(repo);
	}

    public boolean isRepoValid() {
    	for (String repositoryPath: repositoryPaths) {
    		String carPath = interpolateVariablesInRepositoryPath(repositoryPath) +
    				"/ceylon/language/" + LANGUAGE_MODULE_VERSION + 
    				"/ceylon.language-" + LANGUAGE_MODULE_VERSION + ".car";
    		if (new File(carPath).exists()) return true;
    	}
    	return false;
    }
    
    protected void finishPage(IProgressMonitor monitor) throws InterruptedException, CoreException {
        fSecondPage.performFinish(monitor); // use the full progress monitor
    }

    public boolean performFinish() {
        for (IClasspathEntry cpe: fFirstPage.getDefaultClasspathEntries()) {
            if (cpe.getEntryKind()==IClasspathEntry.CPE_CONTAINER) {                
                IPath path = cpe.getPath();
                if (path.segment(0).equals(JRE_CONTAINER)) {
                    IVMInstall vm = JavaRuntime.getVMInstall(cpe.getPath());
                    if (vm==null || !((IVMInstall2)vm).getJavaVersion().startsWith("1.7")) {
                        fFirstPage.setErrorMessage("Please select a Java 1.7 JRE");
                        return false;
                    }
                    if (path.segmentCount()==3) {
                        String s = path.segment(2);
                        if ((s.startsWith("JavaSE-")||s.startsWith("J2SE-")) &&
                                !s.contains("1.7")) {
                            fFirstPage.setErrorMessage("Please select a Java 1.7 JRE");
                            return false;
                        }
                    }
                }
            }
        }
        if (!isRepoValid()) return false;
        
        boolean res= super.performFinish();
        if (res) {
            IWorkingSet[] workingSets= fFirstPage.getWorkingSets();
            if (workingSets.length > 0) {
                PlatformUI.getWorkbench().getWorkingSetManager()
                        .addToWorkingSets(getCreatedElement(), workingSets);
            }

            IPath outputPath = fSecondPage.getCeylonOutputLocation();
    		//if (!embeddedRepo) ExportModuleWizard.persistDefaultRepositoryPath(repositoryPath);
    		new CeylonNature(outputPath, repositoryPaths,
    				enableJdtClassesDir, !showCompilerWarnings)
                            .addToProject(getCreatedElement().getProject());

            BasicNewProjectResourceWizard.updatePerspective(fConfigElement);
            selectAndReveal(fSecondPage.getJavaProject().getProject());             

            Display.getDefault().asyncExec(new Runnable() {
                public void run() {
                    IWorkbenchPart activePart= getActivePart();
                    if (activePart instanceof IPackagesViewPart) {
                		PackageExplorerPart.openInActivePerspective()
                		    .tryToReveal(getCreatedElement());
                    }
                }
            });
            
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