package org.eclipse.ceylon.ide.eclipse.core.adapters;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.jdt.core.IJavaElement;

import org.eclipse.ceylon.ide.common.model.IJavaModelAware;
import org.eclipse.ceylon.ide.common.model.IResourceAware;
import org.eclipse.ceylon.ide.common.model.IUnit;


public class CeylonModelAdapterFactory implements IAdapterFactory {

    private static Class<?>[] ADAPTER_LIST= new Class[] {
        IFile.class,
        IJavaElement.class,
    };

    public Class<?>[] getAdapterList() {
        return ADAPTER_LIST;
    }

    public Object getAdapter(Object element, 
            @SuppressWarnings("rawtypes") Class key) {
        IUnit unit = (IUnit) element;

        if (IFile.class.equals(key) 
                && unit instanceof IResourceAware) {
            IResourceAware ra = (IResourceAware) unit;
            return ra.getResourceFile();
        }
        if (IJavaElement.class.equals(key)  
                && unit instanceof IJavaModelAware) {
            IJavaModelAware ja = (IJavaModelAware) unit;
            return ja.getTypeRoot();
        }
        return null;
    }
}
