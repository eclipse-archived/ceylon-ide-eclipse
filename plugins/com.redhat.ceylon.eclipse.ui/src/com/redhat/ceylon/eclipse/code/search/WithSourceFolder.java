package com.redhat.ceylon.eclipse.code.search;

import org.eclipse.jdt.core.IPackageFragmentRoot;

class WithSourceFolder {
    
    Object element;
    IPackageFragmentRoot sourceFolder;
    WithSourceFolder(Object element, IPackageFragmentRoot sourceFolder) {
        super();
        this.element = element;
        this.sourceFolder = sourceFolder;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof WithSourceFolder) {
            WithSourceFolder that = (WithSourceFolder) obj;
            if (sourceFolder==null) {
                if (that.sourceFolder!=null) {
                    return false;
                }
            }
            else {
                if (that.sourceFolder==null ||
                        !that.sourceFolder.equals(sourceFolder)) {
                    return false;
                }
            }
            return element.equals(that.element);
        }
        else {
            return false;
        }
    }
    
    @Override
    public int hashCode() {
        return element.hashCode();
    }
    
}