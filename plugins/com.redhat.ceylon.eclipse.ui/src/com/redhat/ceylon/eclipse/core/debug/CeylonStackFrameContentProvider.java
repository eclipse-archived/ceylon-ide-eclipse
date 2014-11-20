package com.redhat.ceylon.eclipse.core.debug;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IPresentationContext;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IViewerUpdate;
import org.eclipse.jdt.debug.core.IJavaThread;
import org.eclipse.jdt.internal.debug.ui.variables.JavaStackFrameContentProvider;

public class CeylonStackFrameContentProvider extends JavaStackFrameContentProvider {
    @Override
    protected Object[] getAllChildren(Object parent, IPresentationContext context, IViewerUpdate monitor) throws CoreException {
        try {
            Object[] variables = super.getAllChildren(parent, context, monitor);
            return CeylonContentProviderFilter.filterVariables(variables, context);
        } catch (CoreException e) {
            if (e.getStatus().getCode() == IJavaThread.ERR_THREAD_NOT_SUSPENDED) {
                monitor.cancel();
                return EMPTY;
            }
            throw e;
        }
    }
}
