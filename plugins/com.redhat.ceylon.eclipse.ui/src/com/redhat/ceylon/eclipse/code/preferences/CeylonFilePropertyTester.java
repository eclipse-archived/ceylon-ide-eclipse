package com.redhat.ceylon.eclipse.code.preferences;

import static com.redhat.ceylon.eclipse.core.builder.CeylonNature.NATURE_ID;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;

public class CeylonFilePropertyTester extends PropertyTester {

    @Override
    public boolean test(Object receiver, String property, Object[] args,
            Object expectedValue) {
        if (property.equals("module") && 
                receiver instanceof IFile) {
            IFile file = (IFile) receiver;
            try {
                if (!file.getProject().hasNature(NATURE_ID) ||
                        !file.getName().equals("module.ceylon")) {
                    return false;
                }
                IJavaElement parent = JavaCore.create(file.getParent());
                return parent instanceof IPackageFragment || 
                        parent instanceof IPackageFragmentRoot;
            } 
            catch (CoreException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

}
