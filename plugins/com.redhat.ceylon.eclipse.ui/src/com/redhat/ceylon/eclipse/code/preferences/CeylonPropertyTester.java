package com.redhat.ceylon.eclipse.code.preferences;

import static com.redhat.ceylon.eclipse.core.builder.CeylonNature.NATURE_ID;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IPackageFragment;

public class CeylonPropertyTester extends PropertyTester {

    @Override
    public boolean test(Object receiver, String property, Object[] args,
            Object expectedValue) {
        if (property.equals("module") && 
            receiver instanceof IPackageFragment) {
            IPackageFragment pkg = (IPackageFragment) receiver;
            IResource resource = pkg.getResource();
            if (resource instanceof IFolder) {
                IFolder folder = (IFolder) resource;
                try {
                    return folder.getProject().hasNature(NATURE_ID) &&
                            folder.getFile("module.ceylon").exists();
                } 
                catch (CoreException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

}
