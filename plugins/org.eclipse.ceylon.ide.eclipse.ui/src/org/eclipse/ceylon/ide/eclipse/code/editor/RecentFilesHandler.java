/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.code.editor;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.widgets.Shell;

import org.eclipse.ceylon.ide.eclipse.ui.CeylonPlugin;

public class RecentFilesHandler extends AbstractHandler {

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        Shell shell = CeylonPlugin.getInstance().getWorkbench()
                .getActiveWorkbenchWindow().getShell();
        RecentFilesPopup popup = new RecentFilesPopup(shell);
        popup.open();
        popup.setFocus();
        return null;
    }

}
