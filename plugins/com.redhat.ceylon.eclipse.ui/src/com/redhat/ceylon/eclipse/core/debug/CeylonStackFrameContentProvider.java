package com.redhat.ceylon.eclipse.core.debug;

import static com.redhat.ceylon.eclipse.core.debug.CeylonPresentationContext.isCeylonContext;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IPresentationContext;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IViewerUpdate;
import org.eclipse.jdt.debug.core.IJavaThread;
import org.eclipse.jdt.internal.debug.ui.variables.JavaStackFrameContentProvider;

public class CeylonStackFrameContentProvider extends JavaStackFrameContentProvider {

    @Override
    protected Object[] getChildren(Object parent, int index, int length, IPresentationContext context, IViewerUpdate monitor) throws CoreException {
        return super.getChildren(parent, index, length, new CeylonPresentationContext(context, monitor), monitor);
    }
        
    @Override
    protected int getChildCount(Object element, IPresentationContext context, IViewerUpdate monitor) throws CoreException {
        return super.getChildCount(element, new CeylonPresentationContext(context, monitor), monitor);
    }
    
    @Override
    protected boolean hasChildren(Object element, IPresentationContext context, IViewerUpdate monitor) throws CoreException {
        if (isCeylonContext(monitor)) {
            return getChildCount(element, context, monitor) > 0;
        } else {
            return super.hasChildren(element, context, monitor);
        }
    }
    
    @Override
    protected Object[] getAllChildren(Object parent, IPresentationContext context, IViewerUpdate monitor) throws CoreException {
        try {
            Object[] variables = super.getAllChildren(parent, context, monitor);
            if (context instanceof CeylonPresentationContext) {
                if (((CeylonPresentationContext) context).isCeylonContext()) {
                    return CeylonContentProviderFilter.filterVariables(variables, context);
                }
            }
            return variables;
        } catch (CoreException e) {
            if (e.getStatus().getCode() == IJavaThread.ERR_THREAD_NOT_SUSPENDED) {
                monitor.cancel();
                return EMPTY;
            }
            throw e;
        }
    }
}
