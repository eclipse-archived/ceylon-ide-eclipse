/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.core.debug.actions;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.debug.core.model.IDebugElement;
import org.eclipse.jdt.debug.core.IJavaVariable;

public abstract class CeylonOpenVariableTypeAction extends CeylonOpenTypeAction {

    /* (non-Javadoc)
     * @see org.eclipse.jdt.internal.debug.ui.actions.OpenTypeAction#getDebugElement(org.eclipse.core.runtime.IAdaptable)
     */
    @Override
    protected IDebugElement getDebugElement(IAdaptable element) {
        return (IDebugElement)element.getAdapter(IJavaVariable.class);
    }
}
