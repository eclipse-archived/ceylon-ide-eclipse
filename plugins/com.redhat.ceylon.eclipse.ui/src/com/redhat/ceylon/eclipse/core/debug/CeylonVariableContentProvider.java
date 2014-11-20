package com.redhat.ceylon.eclipse.core.debug;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IPresentationContext;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IViewerUpdate;
import org.eclipse.jdt.internal.debug.ui.variables.JavaVariableContentProvider;

public class CeylonVariableContentProvider extends JavaVariableContentProvider {
    @Override
    protected Object[] getAllChildren(Object parent, IPresentationContext context) throws CoreException {
        Object[] variables = super.getAllChildren(parent, context);
        return CeylonContentProviderFilter.filterVariables(variables, context);
    }
    
    @Override
    protected boolean hasChildren(Object element, IPresentationContext context,
            IViewerUpdate monitor) throws CoreException {
        return getAllChildren(element, context).length > 0;
    }
}
