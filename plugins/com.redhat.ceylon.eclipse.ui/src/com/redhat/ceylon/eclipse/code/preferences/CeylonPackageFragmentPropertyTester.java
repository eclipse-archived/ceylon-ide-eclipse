package org.eclipse.ceylon.ide.eclipse.code.preferences;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.IPackageFragment;

import org.eclipse.ceylon.ide.eclipse.core.builder.CeylonNature;

public class CeylonPackageFragmentPropertyTester extends PropertyTester {

    @Override
    public boolean test(Object receiver, String property, Object[] args,
            Object expectedValue) {
        if (property.equals("module") && 
            receiver instanceof IPackageFragment) {
            IPackageFragment pkg = (IPackageFragment) receiver;
            IResource resource = pkg.getResource();
            if (resource instanceof IFolder) {
                IFolder folder = (IFolder) resource;
                return CeylonNature.isEnabled(folder.getProject()) &&
                        folder.getFile("module.ceylon").exists();
            }
        }
        return false;
    }

}
