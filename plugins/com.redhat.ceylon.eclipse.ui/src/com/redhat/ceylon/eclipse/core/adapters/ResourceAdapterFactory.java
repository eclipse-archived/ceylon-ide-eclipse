package com.redhat.ceylon.eclipse.core.adapters;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdapterFactory;

import com.redhat.ceylon.model.typechecker.model.Package;
import com.redhat.ceylon.eclipse.core.builder.CeylonBuilder;
import com.redhat.ceylon.ide.common.model.BaseIdeModule;
import com.redhat.ceylon.ide.common.model.IResourceAware;
import com.redhat.ceylon.ide.common.model.IUnit;


public class ResourceAdapterFactory implements IAdapterFactory {

    private static Class<?>[] ADAPTER_LIST= new Class[] {
        IUnit.class,
        IResourceAware.class,
        BaseIdeModule.class,
        Package.class,
    };

    public Class<?>[] getAdapterList() {
        return ADAPTER_LIST;
    }

    public Object getAdapter(Object element, @SuppressWarnings("rawtypes") Class key) {
        IResource resource = (IResource) element;

        if (IUnit.class.equals(key) && resource instanceof IFile) {
            return CeylonBuilder.getUnit((IFile) resource);
        }
        if (IResourceAware.class.equals(key) && resource instanceof IFile) {
            return CeylonBuilder.getUnit((IFile) resource);
        }
        if (Package.class.equals(key) && element instanceof IFolder) {
            return CeylonBuilder.getPackage((IFolder) element);
        }
        if (BaseIdeModule.class.equals(key) && element instanceof IFolder) {
            return CeylonBuilder.asSourceModule((IFolder) element);
        }
        return null;
    }
}
