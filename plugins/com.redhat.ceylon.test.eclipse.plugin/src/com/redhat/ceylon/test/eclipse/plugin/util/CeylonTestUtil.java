package com.redhat.ceylon.test.eclipse.plugin.util;

import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getProjectModules;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestImageRegistry.TEST;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestImageRegistry.TEST_ERROR;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestImageRegistry.TEST_FAILED;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestImageRegistry.TEST_RUNNING;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestImageRegistry.TEST_SUCCESS;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestImageRegistry.getImage;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestPlugin.CEYLON_TEST_MODULE_NAME;
import static java.util.Collections.emptyList;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;

import com.redhat.ceylon.compiler.typechecker.model.Class;
import com.redhat.ceylon.compiler.typechecker.model.Functional;
import com.redhat.ceylon.compiler.typechecker.model.Method;
import com.redhat.ceylon.compiler.typechecker.model.Module;
import com.redhat.ceylon.compiler.typechecker.model.ModuleImport;
import com.redhat.ceylon.compiler.typechecker.model.Modules;
import com.redhat.ceylon.compiler.typechecker.model.Package;
import com.redhat.ceylon.eclipse.core.builder.CeylonNature;
import com.redhat.ceylon.test.eclipse.plugin.CeylonTestPlugin;
import com.redhat.ceylon.test.eclipse.plugin.model.TestElement;

public class CeylonTestUtil {
    
    private static final String CEYLON_FILE_EXTENSION = "ceylon";
    
    private static final NumberFormat ELAPSED_TIME_FORMAT;
    static {
        ELAPSED_TIME_FORMAT = NumberFormat.getNumberInstance();
        ELAPSED_TIME_FORMAT.setGroupingUsed(true);
        ELAPSED_TIME_FORMAT.setMinimumFractionDigits(3);
        ELAPSED_TIME_FORMAT.setMaximumFractionDigits(3);
        ELAPSED_TIME_FORMAT.setMinimumIntegerDigits(1);
    }
    
    public static Display getDisplay() {
        Display display= Display.getCurrent();
        if (display == null) {
            display= Display.getDefault();
        }
        return display;
    }
    
    public static IWorkspaceRoot getWorkspaceRoot() {
        return ResourcesPlugin.getWorkspace().getRoot();
    }

    public static IWorkbenchWindow getActiveWorkbenchWindow() {
        return CeylonTestPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow();
    }

    public static IWorkbenchPage getActivePage() {
        IWorkbenchWindow activeWorkbenchWindow = getActiveWorkbenchWindow();
        if (activeWorkbenchWindow != null) {
            return activeWorkbenchWindow.getActivePage();
        }
        return null;
    }

    public static Shell getShell() {
        IWorkbenchWindow activeWorkbenchWindow = getActiveWorkbenchWindow();
        if (activeWorkbenchWindow != null) {
            return activeWorkbenchWindow.getShell();
        }
        return null;
    }
    
    public static List<IProject> getProjects() {
        List<IProject> ceylonProjects = new ArrayList<IProject>();
        IProject[] projects = getWorkspaceRoot().getProjects();
        if (projects != null) {
            for (IProject project : projects) {
                if (isCeylonProject(project)) {
                    ceylonProjects.add(project);
                }
            }
        }
        return ceylonProjects;
    }

    public static IProject getProject(String projectName) {
        List<IProject> projects = getProjects();
        for (IProject project : projects) {
            if (project.getName().equals(projectName)) {
                return project;
            }
        }
        return null;
    }

    public static List<Module> getModules(IProject project) {
        List<Module> modules = new ArrayList<Module>();
        IJavaProject javaProject = JavaCore.create(project);
        Modules projectModules = getProjectModules(project);
        if (projectModules==null) {
            return emptyList();
        }
        for (Module module : projectModules.getListOfModules()) {
            if (!module.isDefault() && !module.isJava()) {
                try {
                    for (IPackageFragment pkg : javaProject.getPackageFragments()) {
                        if (!pkg.isReadOnly() && pkg.getElementName().equals(module.getNameAsString())) {
                            modules.add(module);
                        }
                    }
                } catch (JavaModelException e) {
                    CeylonTestPlugin.logError("", e);
                }
            }
        }
        return modules;
    }

    public static Module getModule(IProject project, String moduleName) {
        List<Module> modules = getModules(project);
        for (Module module : modules) {
            if (module.getNameAsString().equals(moduleName)) {
                return module;
            }
        }
        return null;
    }

    public static Package getPackage(IProject project, String pkgName) {
        List<Module> modules = getModules(project);
        for (Module module : modules) {
            Package pkg = module.getDirectPackage(pkgName);
            if (pkg != null) {
                return pkg;
            }
        }
        return null;
    }
    
    public static boolean isCeylonProject(IProject project) {
        return project.isOpen() && CeylonNature.isEnabled(project);
    }
    
    public static boolean isCeylonFile(IFile file) {
        return isCeylonProject(file.getProject()) && CEYLON_FILE_EXTENSION.equals(file.getFileExtension());
    }

    public static boolean isTestable(Object element) {
        if (element instanceof IProject || element instanceof Module || element instanceof Package) {
            return true;
        }
        else if( element instanceof Class ) {
            return isTestableClass((Class) element);
        }
        else if( element instanceof Method ) {
            return isTestableMethod((Method) element);
        }
        return false;
    }

    public static boolean isTestableClass(Class clazz) {
        boolean isTestableClass = false;
        if (clazz.isToplevel() 
                && !clazz.isAbstract() 
                && !clazz.isParameterized()
                && isWithoutParameters(clazz) ) {
            isTestableClass = true;
        }
        return isTestableClass;
    }

    public static boolean isTestableMethod(Method method) {
        boolean isTestableMethod = false;
        if (method.isToplevel()
                || (method.getContainer() instanceof Class && isTestableClass((Class) method.getContainer()))) {
            if (method.isDeclaredVoid() && !method.isFormal() && isWithoutParameters(method)) {
                isTestableMethod = true;
            }
        }
        return isTestableMethod;
    }

    private static boolean isWithoutParameters(Functional functional) {
        boolean isWithoutParameters = false;
        if (functional.getParameterLists().size() == 1
                && functional.getParameterLists().get(0).getParameters().size() == 0) {
            isWithoutParameters = true;
        }
        return isWithoutParameters;
    }
    
    public static Image getTestStateImage(TestElement testElement) {
        Image image = null;
        if(testElement != null) {
            switch(testElement.getState()) {
                case RUNNING: image = getImage(TEST_RUNNING); break;
                case SUCCESS: image = getImage(TEST_SUCCESS); break;
                case FAILURE: image = getImage(TEST_FAILED); break;
                case ERROR: image = getImage(TEST_ERROR); break;
                default: image = getImage(TEST); break;
            }
        }
        return image;
    }
    
    public static String getElapsedTimeInSeconds(long milis) {
        double seconds = milis / 1000.0;
        return ELAPSED_TIME_FORMAT.format(seconds);
    }
    
    public static boolean containsCeylonTestImport(Module module) {
        if( module != null ) {
            for (ModuleImport moduleImport : module.getImports()) {
                if (moduleImport.getModule().getNameAsString().equals(CEYLON_TEST_MODULE_NAME)) {
                    return true;
                }
            }
        }
        return false;
    }

}