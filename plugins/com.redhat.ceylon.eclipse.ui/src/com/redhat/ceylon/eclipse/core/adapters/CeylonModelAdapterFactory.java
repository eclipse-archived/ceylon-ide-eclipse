package com.redhat.ceylon.eclipse.core.adapters;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IPackageFragment;

import com.redhat.ceylon.compiler.typechecker.model.Package;
import com.redhat.ceylon.eclipse.core.builder.CeylonBuilder;
import com.redhat.ceylon.eclipse.core.model.IJavaModelAware;
import com.redhat.ceylon.eclipse.core.model.IResourceAware;
import com.redhat.ceylon.eclipse.core.model.IUnit;
import com.redhat.ceylon.eclipse.core.model.JDTModule;


public class CeylonModelAdapterFactory implements IAdapterFactory {

    private static Class<?>[] ADAPTER_LIST= new Class[] {
        IFile.class,
        IJavaElement.class,
    };

    public Class<?>[] getAdapterList() {
        return ADAPTER_LIST;
    }

    public Object getAdapter(Object element, @SuppressWarnings("rawtypes") Class key) {
        IUnit unit = (IUnit) element;

        if (IFile.class.equals(key) && unit instanceof IResourceAware) {
            return ((IResourceAware) unit).getFileResource();
        }
        if (IJavaElement.class.equals(key)  && unit instanceof IJavaModelAware) {
            return ((IJavaModelAware) unit).getTypeRoot();
        }
        return null;
    }
}
