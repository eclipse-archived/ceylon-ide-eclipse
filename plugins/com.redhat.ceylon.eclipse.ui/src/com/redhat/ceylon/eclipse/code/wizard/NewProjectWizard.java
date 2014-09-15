package com.redhat.ceylon.eclipse.code.wizard;

import static com.redhat.ceylon.common.Constants.DEFAULT_RESOURCE_DIR;
import static com.redhat.ceylon.common.Constants.DEFAULT_SOURCE_DIR;
import static com.redhat.ceylon.eclipse.ui.CeylonResources.CEYLON_NEW_PROJECT;
import static org.eclipse.jdt.launching.JavaRuntime.JRE_CONTAINER;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.internal.ui.util.ExceptionHandler;
import org.eclipse.jdt.internal.ui.wizards.NewElementWizard;
import org.eclipse.jdt.internal.ui.wizards.NewWizardMessages;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.IVMInstall2;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jdt.ui.IPackagesViewPart;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard;

import com.redhat.ceylon.eclipse.code.explorer.PackageExplorerPart;
import com.redhat.ceylon.eclipse.core.builder.CeylonNature;
import com.redhat.ceylon.eclipse.core.builder.CeylonProjectConfig;
import com.redhat.ceylon.eclipse.ui.CeylonPlugin;

public class NewProjectWizard extends NewElementWizard implements IExecutableExtension {

    private NewCeylonProjectWizardPageOne firstPage;
    private NewCeylonProjectWizardPageTwo secondPage;
    private NewCeylonProjectWizardPageThree thirdPage;
    
    public static final String DEFAULT_SOURCE_FOLDER = DEFAULT_SOURCE_DIR;
    public static final String DEFAULT_RESOURCE_FOLDER = DEFAULT_RESOURCE_DIR;

    private IConfigurationElement fConfigElement;

    public NewProjectWizard() {
        this(null, null);
    }

    public NewProjectWizard(NewCeylonProjectWizardPageOne pageOne, NewCeylonProjectWizardPageTwo pageTwo) {
        setDefaultPageImageDescriptor(CeylonPlugin.getInstance()
                .getImageRegistry().getDescriptor(CEYLON_NEW_PROJECT));
        setDialogSettings(CeylonPlugin.getInstance().getDialogSettings());
        setWindowTitle("New Ceylon Project");
        firstPage= pageOne;
        secondPage= pageTwo;
    }

    public void addPages() {
        if (firstPage == null) {
            firstPage= new NewCeylonProjectWizardPageOne();
        }
        firstPage.setTitle("New Ceylon Project");
        firstPage.setDescription("Create a Ceylon project in the workspace or in an external location.");
        addPage(firstPage);

        if (secondPage == null) {
            secondPage= new NewCeylonProjectWizardPageTwo(firstPage);
        }
        secondPage.setTitle("Ceylon Project Settings");
        secondPage.setDescription("Define the Ceylon build settings.");
        addPage(secondPage);
        
        if (thirdPage == null) {
            thirdPage = new NewCeylonProjectWizardPageThree(secondPage);
        }
        thirdPage.setTitle("Ceylon Module Repository Settings");
        thirdPage.setDescription("Specify the Ceylon module repositories for the project.");
        addPage(thirdPage);
        
        firstPage.init(getSelection(), getActivePart());
    }
    
    protected void finishPage(IProgressMonitor monitor) throws InterruptedException, CoreException {
        secondPage.performFinish(monitor); // use the full progress monitor
    }
    
    @Override
    public IWizardPage getNextPage(IWizardPage page) {
        if (page==firstPage && !checkJre()) {
            displayError("Please select a Java 1.7 or 1.8 JRE");
            return page;
        }
        else if (page==secondPage && !checkOutputPaths()) {
            displayError("Please select a different Java output path");
            return page;
        }
        else if (page==thirdPage && !checkOutputPaths()) {
            displayError("Please select a different Ceylon output path");
            return page;
        }
        else {
            clearErrors();
            return super.getNextPage(page);
        }
    }

    public boolean performFinish() {
        if (!checkJre()) {
            displayError("Please select a Java 1.7 or 1.8 JRE");
            return false;
        }
        if (!checkOutputPaths()) {
            displayError("Java and Ceylon output paths collide");
            return false;
        }
        
        boolean res= super.performFinish();
        if (res) {
            IWorkingSet[] workingSets= firstPage.getWorkingSets();
            if (workingSets.length > 0) {
                PlatformUI.getWorkbench().getWorkingSetManager()
                        .addToWorkingSets(getCreatedElement(), workingSets);
            }
            
            IProject project = getCreatedElement().getProject();
            
            CeylonProjectConfig projectConfig = CeylonProjectConfig.get(project);
            
            if (thirdPage.getBlock().getProject() != null) {
                projectConfig.setOutputRepo(thirdPage.getBlock().getOutputRepo());
                projectConfig.setProjectLocalRepos(thirdPage.getBlock().getProjectLocalRepos());
                projectConfig.setProjectRemoteRepos(thirdPage.getBlock().getProjectRemoteRepos());
                projectConfig.save();
            }
            
            try {
                project.setDefaultCharset(projectConfig.getEncoding(), new NullProgressMonitor());
            } catch (CoreException e) {
                throw new RuntimeException(e);
            }

            new CeylonNature(thirdPage.getBlock().getSystemRepo(),
                    firstPage.isEnableJdtClassesDir(), 
                    !firstPage.isShowCompilerWarnings(),
                    firstPage.isCompileJava(),
                    firstPage.isCompileJs(),
                    firstPage.areAstAwareIncrementalBuildsEnabled())
                            .addToProject(getCreatedElement().getProject());

            BasicNewProjectResourceWizard.updatePerspective(fConfigElement);
            selectAndReveal(secondPage.getJavaProject().getProject());             

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

    private boolean checkJre() {
        for (IClasspathEntry cpe: firstPage.getDefaultClasspathEntries()) {
            if (cpe.getEntryKind()==IClasspathEntry.CPE_CONTAINER) {                
                IPath path = cpe.getPath();
                if (path.segment(0).equals(JRE_CONTAINER)) {
                    IVMInstall vm = JavaRuntime.getVMInstall(cpe.getPath());
                    if (!(vm instanceof IVMInstall2)) {
                        return false;
                    }
                    String javaVersion = ((IVMInstall2)vm).getJavaVersion();
                    if (!javaVersion.startsWith("1.7") && 
                        !javaVersion.startsWith("1.8")) {
                        return false;
                    }
                    if (path.segmentCount()==3) {
                        String s = path.segment(2);
                        if ((s.startsWith("JavaSE-") || s.startsWith("J2SE-")) &&
                                !s.contains("1.7") && !s.contains("1.8")) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }
    
    private boolean checkOutputPaths() {
        String ceylonOutString = thirdPage.getBlock().getOutputRepo();
        if (ceylonOutString.startsWith("."+File.separator)) {
            ceylonOutString = firstPage.getProjectName() + 
                    ceylonOutString.substring(1);
        }
        IPath ceylonOut = Path.fromOSString(ceylonOutString);
        IPath javaOut = secondPage.getJavaOutputLocation();
        return javaOut==null || 
        		!ceylonOut.isPrefixOf(javaOut) && 
                !javaOut.isPrefixOf(ceylonOut);
    }

    private void displayError(String message) {
        for (IWizardPage page: getPages()) {
            if (page instanceof WizardPage) {
                ((WizardPage) page).setErrorMessage(message);
            }
        }
    }

    private void clearErrors() {
        for (IWizardPage page: getPages()) {
            if (page instanceof WizardPage) {
                ((WizardPage)page).setErrorMessage(null);
            }
        }
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
        secondPage.performCancel();
        return super.performCancel();
    }

    public IJavaProject getCreatedElement() {
        return secondPage.getJavaProject();
    }
}