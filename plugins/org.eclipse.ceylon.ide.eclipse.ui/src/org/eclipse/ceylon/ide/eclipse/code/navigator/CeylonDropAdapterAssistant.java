/********************************************************************************
 * Copyright (c) {date} Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.code.navigator;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.internal.ui.navigator.JavaDropAdapterAssistant;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.ui.navigator.CommonDropAdapter;

public class CeylonDropAdapterAssistant extends JavaDropAdapterAssistant {

    @Override
    public IStatus handleDrop(CommonDropAdapter dropAdapter,
            DropTargetEvent dropTargetEvent, Object target) {
        if (target instanceof SourceModuleNode) {
            SourceModuleNode moduleNode = (SourceModuleNode) target;
            target = moduleNode.getMainPackageFragment();
        }
        final IStatus handleDrop = super.handleDrop(dropAdapter, dropTargetEvent, target);
        return handleDrop;
    }

    @Override
    public IStatus validateDrop(Object target, int operation,
            TransferData transferType) {
        if (target instanceof SourceModuleNode) {
            SourceModuleNode moduleNode = (SourceModuleNode) target;
            target = moduleNode.getMainPackageFragment();
        }
        final IStatus validateDrop = super.validateDrop(target, operation, transferType);
        return validateDrop;
    }
}
