package com.redhat.ceylon.test.eclipse.plugin.util;

import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getProjectDeclaredSourceModules;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestImageRegistry.TEST;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestImageRegistry.TESTS;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestImageRegistry.TESTS_ERROR;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestImageRegistry.TESTS_FAILED;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestImageRegistry.TESTS_SKIPPED;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestImageRegistry.TESTS_RUNNING;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestImageRegistry.TESTS_SUCCESS;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestImageRegistry.TEST_ERROR;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestImageRegistry.TEST_FAILED;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestImageRegistry.TEST_SKIPPED;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestImageRegistry.TEST_RUNNING;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestImageRegistry.TEST_SUCCESS;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestImageRegistry.getImage;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestPlugin.CEYLON_TEST_MODULE_NAME;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestPlugin.LAUNCH_CONFIG_TYPE;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestPlugin.LAUNCH_CONFIG_TYPE_JS;
import static org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

import com.redhat.ceylon.common.Backend;
import com.redhat.ceylon.eclipse.core.builder.CeylonNature;
import com.redhat.ceylon.model.typechecker.model.Annotation;
import com.redhat.ceylon.model.typechecker.model.Class;
import com.redhat.ceylon.model.typechecker.model.ClassOrInterface;
import com.redhat.ceylon.model.typechecker.model.Declaration;
import com.redhat.ceylon.model.typechecker.model.Function;
import com.redhat.ceylon.model.typechecker.model.Module;
import com.redhat.ceylon.model.typechecker.model.ModuleImport;
import com.redhat.ceylon.model.typechecker.model.Package;
import com.redhat.ceylon.model.typechecker.model.Type;
import com.redhat.ceylon.model.typechecker.model.TypeDeclaration;
import com.redhat.ceylon.model.typechecker.model.Value;
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
    
    public static IProject getProject(ILaunch launch) throws CoreException {
    	ILaunchConfiguration launchConfiguration = launch.getLaunchConfiguration();
    	String projectName = launchConfiguration.getAttribute(ATTR_PROJECT_NAME, (String) null);
    	return getProject(projectName);
    }

    public static Module getModule(IProject project, String moduleName) {
        for (Module module : getProjectDeclaredSourceModules(project)) {
            if (module.getNameAsString().equals(moduleName)) {
                return module;
            }
        }
        return null;
    }

    public static Package getPackage(IProject project, String pkgName) {
        for (Module module : getProjectDeclaredSourceModules(project)) {
            Package pkg = module.getDirectPackage(pkgName);
            if (pkg != null) {
                return pkg;
            }
        }
        return null;
    }
    
    public static Object getPackageOrDeclaration(IProject project, String qualifiedName) {
    	Object result = null;
    	
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
				String baseName = qualifiedName.substring(pkgSepIndex + 2, memberSepIndex);
				String methodName = qualifiedName.substring(memberSepIndex + 1);
				d = pkg.getMember(baseName, null, false);
				d = extractAnonymousClassIfRequired(d);
				if (d != null) {
					Declaration m = d.getMember(methodName, null, false);
					if( m instanceof Function && d instanceof Class ) {
					    result = new MethodWithContainer((Class)d, (Function)m);
					}
				}
			} else {
				String baseName = qualifiedName.substring(pkgSepIndex + 2);
				d = pkg.getMember(baseName, null, false);
				result = extractAnonymousClassIfRequired(d);
			}
		} else {
			result = pkg;
		}
    	
    	return result;
    }
    
    public static List<MethodWithContainer> getAllMethods(ClassOrInterface c) {
        List<MethodWithContainer> members = new ArrayList<MethodWithContainer>();
        getAllMethods(c, c, members);
        return members;
    }

    private static void getAllMethods(ClassOrInterface c, TypeDeclaration t, List<MethodWithContainer> members) {
        for (Declaration d : t.getMembers()) {
            if (d instanceof Function) {
                Function m = (Function) d;
                boolean contains = false;
                for (MethodWithContainer member : members) {
                    String name = member.getMethod().getName();
                    if (name!=null && name.equals(m.getName())) {
                        contains = true;
                        break;
                    }
                }
                if (!contains) {
                    members.add(new MethodWithContainer(c, m));
                }
            }
        }
        Type et = t.getExtendedType();
        if (et != null) {
            getAllMethods(c, et.getDeclaration(), members);
        }
        for (Type st : t.getSatisfiedTypes()) {
            getAllMethods(c, st.getDeclaration(), members);
        }
    }
    
    public static Declaration extractAnonymousClassIfRequired(Declaration d) {
        if (d instanceof Value) {
            Value value = (Value) d;
            TypeDeclaration typeDeclaration = value.getTypeDeclaration();
            if (typeDeclaration instanceof Class && typeDeclaration.isAnonymous()) {
                return typeDeclaration;
            }
        }
        return d;
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
        else if( element instanceof Function ) {
            return isTestableMethod((Function) element, null);
        }
        else if( element instanceof MethodWithContainer ) {
            MethodWithContainer m = (MethodWithContainer) element;
            return isTestableMethod(m.getMethod(), m.getContainer());
        }
        return false;
    }

    private static boolean isTestableClass(Class clazz) {
        if (clazz.isToplevel() && !clazz.isAbstract()) {
            List<MethodWithContainer> methods = getAllMethods(clazz);
            for (MethodWithContainer method : methods) {
                if (containsTestAnnotation(method.getMethod())) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean isTestableMethod(Function method, TypeDeclaration container) {
        boolean isTestableMethod = false;
        if (method.isToplevel() || (container instanceof Class && isTestableClass((Class) container))) {
            if (method.isDeclaredVoid() && !method.isFormal() && containsTestAnnotation(method) ) {
                isTestableMethod = true;
            }
        }
        return isTestableMethod;
    }
    
    private static boolean containsTestAnnotation(Function method) {
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
                case SKIPPED_OR_ABORTED: image = getImage(TEST_SKIPPED); break;
                default: image = getImage(TEST); break;
                }
            } else {
                switch(testElement.getState()) {
                case RUNNING: image = getImage(TESTS_RUNNING); break;
                case SUCCESS: image = getImage(TESTS_SUCCESS); break;
                case FAILURE: image = getImage(TESTS_FAILED); break;
                case ERROR: image = getImage(TESTS_ERROR); break;
                case SKIPPED_OR_ABORTED: image = getImage(TESTS_SKIPPED); break;
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

    public static boolean checkNativeBackend(Module module, String launchConfigType) {
        if (module.isNative()) {
            if (Objects.equals(launchConfigType, LAUNCH_CONFIG_TYPE) && !module.getNativeBackends().supports(Backend.Java.asSet())) {
                return false;
            }
            if (Objects.equals(launchConfigType, LAUNCH_CONFIG_TYPE_JS) && !module.getNativeBackends().supports(Backend.JavaScript.asSet())) {
                return false;
            }
        }
        return true;
    }

}