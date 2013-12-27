package com.redhat.ceylon.test.eclipse.plugin.util;

import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getModulesInProject;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestImageRegistry.TEST;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestImageRegistry.TESTS;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestImageRegistry.TESTS_ERROR;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestImageRegistry.TESTS_FAILED;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestImageRegistry.TESTS_IGNORED;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestImageRegistry.TESTS_RUNNING;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestImageRegistry.TESTS_SUCCESS;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestImageRegistry.TEST_ERROR;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestImageRegistry.TEST_FAILED;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestImageRegistry.TEST_IGNORED;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestImageRegistry.TEST_RUNNING;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestImageRegistry.TEST_SUCCESS;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestImageRegistry.getImage;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestPlugin.CEYLON_TEST_MODULE_NAME;
import static org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;

import com.redhat.ceylon.compiler.typechecker.model.Annotation;
import com.redhat.ceylon.compiler.typechecker.model.Class;
import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.Method;
import com.redhat.ceylon.compiler.typechecker.model.Module;
import com.redhat.ceylon.compiler.typechecker.model.ModuleImport;
import com.redhat.ceylon.compiler.typechecker.model.Package;
import com.redhat.ceylon.compiler.typechecker.model.Referenceable;
import com.redhat.ceylon.eclipse.core.builder.CeylonNature;
import com.redhat.ceylon.test.eclipse.TestElement;
import com.redhat.ceylon.test.eclipse.plugin.CeylonTestPlugin;

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
    
    public static IProject getProject(ILaunch launch) throws CoreException {
    	ILaunchConfiguration launchConfiguration = launch.getLaunchConfiguration();
    	String projectName = launchConfiguration.getAttribute(ATTR_PROJECT_NAME, (String) null);
    	return getProject(projectName);
    }

    public static Module getModule(IProject project, String moduleName) {
        List<Module> modules = getModulesInProject(project);
        for (Module module : modules) {
            if (module.getNameAsString().equals(moduleName)) {
                return module;
            }
        }
        return null;
    }

    public static Package getPackage(IProject project, String pkgName) {
        List<Module> modules = getModulesInProject(project);
        for (Module module : modules) {
            Package pkg = module.getDirectPackage(pkgName);
            if (pkg != null) {
                return pkg;
            }
        }
        return null;
    }
    
    public static Referenceable getPackageOrDeclaration(IProject project, String qualifiedName) {
    	Referenceable result = null;
    	
		String pkgName = null;
		int pkgSepIndex = qualifiedName.indexOf("::");
		if (pkgSepIndex == -1) {
			pkgName = qualifiedName;
		} else {
			pkgName = qualifiedName.substring(0, pkgSepIndex);
		}
		
		Package pkg = getPackage(project, pkgName);
		if (pkg != null && pkgSepIndex != -1) {
			Declaration d;
			int memberSepIndex = qualifiedName.indexOf(".", pkgSepIndex);
			if (memberSepIndex != -1) {
				String className = qualifiedName.substring(pkgSepIndex + 2, memberSepIndex);
				String methodName = qualifiedName.substring(memberSepIndex + 1);
				d = pkg.getMember(className, null, false);
				if (d != null) {
					d = d.getMember(methodName, null, false);
				}
			} else {
				String fceName = qualifiedName.substring(pkgSepIndex + 2);
				d = pkg.getMember(fceName, null, false);
			}
			result = d;
		} else {
			result = pkg;
		}
    	
    	return result;
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

    private static boolean isTestableClass(Class clazz) {
        if (clazz.isToplevel() && !clazz.isAbstract()) {
            for (Declaration decl : clazz.getMembers()) {
                if (decl instanceof Method && containsTestAnnotation((Method) decl)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean isTestableMethod(Method method) {
        boolean isTestableMethod = false;
        if (method.isToplevel() || (method.getContainer() instanceof Class && isTestableClass((Class) method.getContainer()))) {
            if (method.isDeclaredVoid() && !method.isFormal() && containsTestAnnotation(method) ) {
                isTestableMethod = true;
            }
        }
        return isTestableMethod;
    }
    
    private static boolean containsTestAnnotation(Method method) {
        List<Annotation> annotations = method.getAnnotations();
        for (Annotation annotation : annotations) {
            if (annotation.getName().equals("test")) {
                return true;
            }
        }
        return false;
    }
    
    public static Image getTestStateImage(TestElement testElement) {
        Image image = null;
        if(testElement != null) {
            if( testElement.getChildren() == null || testElement.getChildren().length == 0 ) {
                switch(testElement.getState()) {
                case RUNNING: image = getImage(TEST_RUNNING); break;
                case SUCCESS: image = getImage(TEST_SUCCESS); break;
                case FAILURE: image = getImage(TEST_FAILED); break;
                case ERROR: image = getImage(TEST_ERROR); break;
                case IGNORED: image = getImage(TEST_IGNORED); break;
                default: image = getImage(TEST); break;
                }
            } else {
                switch(testElement.getState()) {
                case RUNNING: image = getImage(TESTS_RUNNING); break;
                case SUCCESS: image = getImage(TESTS_SUCCESS); break;
                case FAILURE: image = getImage(TESTS_FAILED); break;
                case ERROR: image = getImage(TESTS_ERROR); break;
                case IGNORED: image = getImage(TESTS_IGNORED); break;
                default: image = getImage(TESTS); break;
                }
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