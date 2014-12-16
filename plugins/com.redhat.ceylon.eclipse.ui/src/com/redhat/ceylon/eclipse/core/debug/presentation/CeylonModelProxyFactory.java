package com.redhat.ceylon.eclipse.core.debug.presentation;

import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IModelProxy;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IPresentationContext;
import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.jdt.debug.core.IJavaDebugTarget;
import org.eclipse.jdt.internal.debug.ui.threadgroups.JavaModelProxyFactory;

public class CeylonModelProxyFactory extends JavaModelProxyFactory {
    @Override
    public IModelProxy createModelProxy(Object element,
            IPresentationContext context) {
        if (IDebugUIConstants.ID_DEBUG_VIEW.equals(context.getId())) {
            if (element instanceof IJavaDebugTarget) {
                ILaunch launch = ((IDebugTarget) element).getLaunch();
                Object[] children = launch.getChildren();
                for (int i = 0; i < children.length; i++) {
                    if (children[i] == element) {
                        // ensure the target is a visible child of the launch
                        return new CeylonDebugTargetProxy((IDebugTarget) element);
                    }
                }
            }
        }
        return null;
    }
}
