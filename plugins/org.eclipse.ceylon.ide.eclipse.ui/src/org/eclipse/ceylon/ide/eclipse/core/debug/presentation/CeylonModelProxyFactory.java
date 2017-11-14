/********************************************************************************
 * Copyright (c) {date} Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.core.debug.presentation;

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
