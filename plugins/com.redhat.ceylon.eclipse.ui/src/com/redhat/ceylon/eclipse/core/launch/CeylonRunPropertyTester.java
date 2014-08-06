package com.redhat.ceylon.eclipse.core.launch;

import static com.redhat.ceylon.eclipse.core.launch.ICeylonLaunchConfigurationConstants.CAN_LAUNCH_AS_CEYLON_JAVASCIPT_MODULE;
import static com.redhat.ceylon.eclipse.core.launch.ICeylonLaunchConfigurationConstants.CAN_LAUNCH_AS_CEYLON_JAVA_MODULE;
import static com.redhat.ceylon.eclipse.core.launch.ICeylonLaunchConfigurationConstants.CEYLON_FILE_EXTENSION;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.ui.IFileEditorInput;

import com.redhat.ceylon.eclipse.core.builder.CeylonNature;

public class CeylonRunPropertyTester extends PropertyTester {

    @Override
    public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
        if (CAN_LAUNCH_AS_CEYLON_JAVA_MODULE.equals(property) || 
            CAN_LAUNCH_AS_CEYLON_JAVASCIPT_MODULE.equals(property)) {
        	if (receiver instanceof IProject) {
        		IProject project = (IProject) receiver;
                return isCeylonProject(project) && LaunchHelper.isBuilderEnabled(project, property);
        	}
        	else if (receiver instanceof IJavaProject) {
                IProject project = ((IJavaProject) receiver).getProject();
                return isCeylonProject(project) && LaunchHelper.isBuilderEnabled(project, property);
            }
            else if (receiver instanceof IPackageFragmentRoot) {
                IPackageFragmentRoot packageFragment = (IPackageFragmentRoot) receiver;
                IProject project = packageFragment.getJavaProject().getProject();
                return isCeylonProject(project) && LaunchHelper.isBuilderEnabled(project, property);
            }
            else if (receiver instanceof IPackageFragment) {
                IPackageFragment packageFragment = (IPackageFragment) receiver;
                IProject project = packageFragment.getJavaProject().getProject();
                return isCeylonProject(project) && LaunchHelper.isBuilderEnabled(project, property);
            }
            else if (receiver instanceof IFile) {
                IFile file = (IFile) receiver;
                return isCeylonFile(file) && LaunchHelper.isBuilderEnabled(file.getProject(), property);
            }
            else if (receiver instanceof IFileEditorInput) {
                IFileEditorInput fileEditorInput = (IFileEditorInput) receiver;
                return isCeylonFile(fileEditorInput.getFile()) && LaunchHelper.isBuilderEnabled(
                        fileEditorInput.getFile().getProject(), property);
            }
        }
        return false;
    }
    
    private boolean isCeylonProject(IProject project) {
        return project.isOpen() && CeylonNature.isEnabled(project);
    }

    private boolean isCeylonFile(IFile file) {
        return isCeylonProject(file.getProject()) && 
                CEYLON_FILE_EXTENSION.equals(file.getFileExtension());
    }

}