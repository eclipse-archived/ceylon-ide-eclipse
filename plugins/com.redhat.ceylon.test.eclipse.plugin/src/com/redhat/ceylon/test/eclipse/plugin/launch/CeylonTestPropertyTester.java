package com.redhat.ceylon.test.eclipse.plugin.launch;

import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestUtil.isCeylonFile;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestUtil.isCeylonProject;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.ui.IFileEditorInput;

public class CeylonTestPropertyTester extends PropertyTester {

    private static final String CAN_LAUNCH_AS_CEYLON_TEST_PROPERTY = "canLaunchAsCeylonTest";

    @Override
    public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
        if (CAN_LAUNCH_AS_CEYLON_TEST_PROPERTY.equals(property)) {
            if (receiver instanceof IJavaProject) {
                IProject project = ((IJavaProject) receiver).getProject();
                return isCeylonProject(project);
            } else if (receiver instanceof IPackageFragment) {
                IPackageFragment packageFragment = (IPackageFragment) receiver;
                IProject project = packageFragment.getJavaProject().getProject();
                return isCeylonProject(project);
            } else if (receiver instanceof IFile) {
                IFile file = (IFile) receiver;
                return isCeylonFile(file);
            } else if( receiver instanceof IFileEditorInput) {
                IFileEditorInput fileEditorInput = (IFileEditorInput) receiver;
                return isCeylonFile(fileEditorInput.getFile());
            }
        }
        return false;
    }

}