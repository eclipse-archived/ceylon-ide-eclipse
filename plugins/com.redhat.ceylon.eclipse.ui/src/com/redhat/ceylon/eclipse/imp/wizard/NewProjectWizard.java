package com.redhat.ceylon.eclipse.imp.wizard;

import static com.redhat.ceylon.compiler.typechecker.TypeChecker.LANGUAGE_MODULE_VERSION;
import static com.redhat.ceylon.eclipse.ui.ICeylonResources.CEYLON_NEW_FILE;
import static org.eclipse.jdt.launching.JavaRuntime.JRE_CONTAINER;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
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
import org.eclipse.jdt.ui.actions.ShowInPackageViewAction;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard;
import org.osgi.service.prefs.BackingStoreException;

import com.redhat.ceylon.eclipse.imp.builder.CeylonNature;
import com.redhat.ceylon.eclipse.ui.CeylonPlugin;

public class NewProjectWizard extends NewElementWizard implements IExecutableExtension {

    private NewCeylonProjectWizardPageOne fFirstPage;
    private NewCeylonProjectWizardPageTwo fSecondPage;

    private IConfigurationElement fConfigElement;

    private String repositoryPath;
    private boolean useEmbeddedRepo=true;
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

	private void addSelectRepoSection(Composite parent) {
		//final Composite composite= new Composite(parent, SWT.NONE);
        Group composite = new Group(parent, SWT.SHADOW_ETCHED_IN);
        composite.setText("Ceylon module repository");
        GridData gd= new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        composite.setLayoutData(gd);
        GridLayout layout = new GridLayout();
        layout.numColumns = 4;
        composite.setLayout(layout);        
        
        Button useEmbedded = new Button(composite, SWT.CHECK);
        useEmbedded.setText("Use embedded module repository (contains only language module)");
        useEmbedded.setSelection(true);
        GridData igd= new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        igd.horizontalSpan = 4;
        igd.grabExcessHorizontalSpace = true;
        useEmbedded.setLayoutData(igd);

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
        folder.setEnabled(false);
        folder.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                repositoryPath = folder.getText();
                if (!isRepoValid()) {
                    fFirstPage.setErrorMessage("Please select a module repository containing the language module");
                }
                else {
                    fFirstPage.setErrorMessage(null);
                }
            }
        });
        
        repositoryPath = ExportModuleWizard.getDefaultRepositoryPath();
        folder.setText(repositoryPath);
        
        final Button selectFolder = new Button(composite, SWT.PUSH);
        selectFolder.setText("Browse...");
        GridData sfgd= new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        sfgd.horizontalSpan = 1;
        selectFolder.setLayoutData(sfgd);
        selectFolder.setEnabled(false);
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
                    fFirstPage.setErrorMessage(null);
                }
                else if (!isRepoValid()) {
                    fFirstPage.setErrorMessage("Please select a module repository containing the language module");
                }
            }
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {}
        });
	}
    
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
    
    protected void finishPage(IProgressMonitor monitor) throws InterruptedException, CoreException {
        fSecondPage.performFinish(monitor); // use the full progress monitor
    }

    public boolean performFinish() {
        for (IClasspathEntry cpe: fFirstPage.getDefaultClasspathEntries()) {
            if (cpe.getEntryKind()==IClasspathEntry.CPE_CONTAINER) {                
                IPath path = cpe.getPath();
                if (path.segment(0).equals(JRE_CONTAINER)) {
                    IVMInstall vm = JavaRuntime.getVMInstall(cpe.getPath());
                    if (!((IVMInstall2)vm).getJavaVersion().startsWith("1.7")) {
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

            BasicNewProjectResourceWizard.updatePerspective(fConfigElement);
            selectAndReveal(fSecondPage.getJavaProject().getProject());             

            //TODO: use classpathentry attributes to persist this stuff!
            IEclipsePreferences node = new ProjectScope(getCreatedElement().getProject())
                    .getNode(CeylonPlugin.PLUGIN_ID);
            
            if (enableJdtClassesDir) {
            	node.putBoolean("jdtClasses", true);
            }
            
            if (!showCompilerWarnings) {
            	node.putBoolean("hideWarnings", true);
            }
            
            if (!useEmbeddedRepo && repositoryPath!=null && !repositoryPath.isEmpty()) {
                node.put("repo", repositoryPath);
                /*getCreatedElement().getProject()
                        .setPersistentProperty(new QualifiedName(CeylonPlugin.PLUGIN_ID, "repo"), 
                                repositoryPath);*/
                ExportModuleWizard.persistDefaultRepositoryPath(repositoryPath);
            }
            
            try {
                node.flush();
            } 
            catch (BackingStoreException e) {
                e.printStackTrace();
            }

            Display.getDefault().asyncExec(new Runnable() {
                public void run() {
                    IWorkbenchPart activePart= getActivePart();
                    if (activePart instanceof IPackagesViewPart) {
                        (new ShowInPackageViewAction(activePart.getSite())).run(getCreatedElement());
                    }
                }
            });
            
            new CeylonNature(fSecondPage.getCeylonOutputLocation())
                    .addToProject(getCreatedElement().getProject());
        }
        
        /*IEclipsePreferences node = new ProjectScope(getCreatedElement().getProject())
                .getNode(JavaCore.PLUGIN_ID);
        node.put(JavaCore.CORE_JAVA_BUILD_RESOURCE_COPY_FILTER, "*.launch, *.ceylon");
        try {
            node.flush();
        } 
        catch (BackingStoreException e) {
            e.printStackTrace();
        }*/
        
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