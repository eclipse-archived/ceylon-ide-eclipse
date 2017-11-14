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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.model.IVariable;
import org.eclipse.jdt.internal.debug.ui.heapwalking.JavaWatchExpressionFilter;

public class CeylonWatchExpressionFilter extends JavaWatchExpressionFilter {

    @Override
    public String createWatchExpression(IVariable variable)
            throws CoreException {
        return super.createWatchExpression(variable);
    }

}
