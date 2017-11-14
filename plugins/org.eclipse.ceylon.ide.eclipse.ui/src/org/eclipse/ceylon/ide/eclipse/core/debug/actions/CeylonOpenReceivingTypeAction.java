/********************************************************************************
 * Copyright (c) {date} Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.core.debug.actions;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.model.IDebugElement;
import org.eclipse.jdt.debug.core.IJavaStackFrame;
import org.eclipse.jdt.debug.core.IJavaType;

public class CeylonOpenReceivingTypeAction extends CeylonOpenStackFrameAction {
    /* (non-Javadoc)
     * @see org.eclipse.jdt.internal.debug.ui.actions.OpenTypeAction#getTypeToOpen(org.eclipse.debug.core.model.IDebugElement)
     */
    @Override
    protected IJavaType getTypeToOpen(IDebugElement element) throws CoreException {
        if (element instanceof IJavaStackFrame) {
            IJavaStackFrame frame = (IJavaStackFrame) element;
            if (frame.isStatic()) {
                return frame.getReferenceType();
            }
            return frame.getThis().getJavaType();
        }
        return null;
    }
}
