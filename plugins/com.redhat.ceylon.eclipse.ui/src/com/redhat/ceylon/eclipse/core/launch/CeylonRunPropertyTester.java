package com.redhat.ceylon.eclipse.core.launch;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.ui.IFileEditorInput;

import com.redhat.ceylon.eclipse.core.builder.CeylonBuilder;
import com.redhat.ceylon.eclipse.core.builder.CeylonNature;

public class CeylonRunPropertyTester extends PropertyTester {

    private static final String CAN_LAUNCH_AS_CEYLON_APP_PROPERTY = "canLaunchAsCeylonApp";
    private static final String CAN_LAUNCH_AS_CEYLON_JS_APP_PROPERTY = "canLaunchAsCeylonJsApp";
    private static final String CAN_LAUNCH_AS_CEYLON_MODULE_PROPERTY = "canLaunchAsCeylonModule";
    private static final String CEYLON_FILE_EXTENSION = "ceylon";

    @Override
    public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
        if (CAN_LAUNCH_AS_CEYLON_APP_PROPERTY.equals(property) || CAN_LAUNCH_AS_CEYLON_JS_APP_PROPERTY.equals(property)) {
            if (receiver instanceof IJavaProject) {
                IProject project = ((IJavaProject) receiver).getProject();
                return isCeylonProject(project) && isBuilderEnabled(project, property);
            } else if (receiver instanceof IPackageFragment) {
                IPackageFragment packageFragment = (IPackageFragment) receiver;
                IProject project = packageFragment.getJavaProject().getProject();
                return isCeylonProject(project) && isBuilderEnabled(project, property);
            } else if (receiver instanceof IFile) {
                IFile file = (IFile) receiver;
                return isCeylonFile(file) && isBuilderEnabled(file.getProject(), property);
            } else if (receiver instanceof IFileEditorInput) {
                IFileEditorInput fileEditorInput = (IFileEditorInput) receiver;
                return isCeylonFile(fileEditorInput.getFile()) && isBuilderEnabled(fileEditorInput.getFile().getProject(), property);
            }
        } else if (CAN_LAUNCH_AS_CEYLON_MODULE_PROPERTY.equals(property)) {
            if (receiver instanceof IJavaProject) {
                IProject project = ((IJavaProject) receiver).getProject();
                return isCeylonProject(project) && isBuilderEnabled(project, property);
            } else if (receiver instanceof IPackageFragment) {
                IPackageFragment packageFragment = (IPackageFragment) receiver;
                return isCeylonModule(packageFragment) && isBuilderEnabled(packageFragment.getJavaProject().getProject(), property);
            } 
        }
        return false;
    }
    
    private boolean isBuilderEnabled(IProject project, String property) {
        if (CAN_LAUNCH_AS_CEYLON_APP_PROPERTY.equals(property) || CAN_LAUNCH_AS_CEYLON_MODULE_PROPERTY.equals(property)) {
            return CeylonBuilder.compileToJava(project);
        } else if (CAN_LAUNCH_AS_CEYLON_JS_APP_PROPERTY.equals(property)) {
            return CeylonBuilder.compileToJs(project);
        }
        return false;
    }

    private boolean isCeylonModule(IPackageFragment packageFragment) {
		return (packageFragment.getClassFile("module_.class")  != null && packageFragment.getClassFile("run_.class") != null);
	}

	private boolean isCeylonProject(IProject project) {
        return project.isOpen() && CeylonNature.isEnabled(project);
    }

    private boolean isCeylonFile(IFile file) {
        return isCeylonProject(file.getProject()) && CEYLON_FILE_EXTENSION.equals(file.getFileExtension());
    }

}