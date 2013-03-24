package com.redhat.ceylon.test.eclipse.plugin.launch;

import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getModulesInProject;

import java.util.List;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.ui.IFileEditorInput;

import com.redhat.ceylon.compiler.typechecker.model.Module;
import com.redhat.ceylon.test.eclipse.plugin.CeylonTestPlugin;
import com.redhat.ceylon.test.eclipse.plugin.util.CeylonTestUtil;

public class CeylonTestPropertyTester extends PropertyTester {

    private static final String CAN_LAUNCH_AS_CEYLON_TEST_PROPERTY = "canLaunchAsCeylonTest";

    @Override
    public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
        if (CAN_LAUNCH_AS_CEYLON_TEST_PROPERTY.equals(property)) {
            if (receiver instanceof IJavaProject) {
                return isProjectWithAtLeastOneTestableModule((IJavaProject) receiver);
            } else if (receiver instanceof IPackageFragment) {
                return isPackageFromTestableModule((IPackageFragment) receiver);
            } else if (receiver instanceof IFile) {
                return isFileFromTestableModule((IFile) receiver);
            } else if (receiver instanceof IFileEditorInput) {
                return isFileFromTestableModule(((IFileEditorInput) receiver).getFile());
            }
        }
        return false;
    }

    private boolean isProjectWithAtLeastOneTestableModule(IJavaProject javaProject) {
        IProject project = javaProject.getProject();
        List<Module> modules = getModulesInProject(project);
        for (Module module : modules) {
            if (CeylonTestUtil.containsCeylonTestImport(module)) {
                return true;
            }
        }
        return false;
    }

    private boolean isPackageFromTestableModule(IPackageFragment packageFragment) {
        IProject project = packageFragment.getJavaProject().getProject();
        String packageFragmentName = packageFragment.getElementName();

        List<Module> modules = getModulesInProject(project);
        for (Module module : modules) {
            String moduleName = module.getNameAsString();
            if (packageFragmentName.equals(moduleName) || packageFragmentName.startsWith(moduleName + ".")) {
                return CeylonTestUtil.containsCeylonTestImport(module);
            }
        }

        return false;
    }

    private boolean isFileFromTestableModule(IFile file) {
        try {
            IJavaProject javaProject = JavaCore.create(file.getProject());
            IPackageFragment packageFragment = javaProject.findPackageFragment(file.getParent().getFullPath());
            return isPackageFromTestableModule(packageFragment);
        } catch (JavaModelException e) {
            CeylonTestPlugin.logError("", e);
        }
        return false;
    }

}