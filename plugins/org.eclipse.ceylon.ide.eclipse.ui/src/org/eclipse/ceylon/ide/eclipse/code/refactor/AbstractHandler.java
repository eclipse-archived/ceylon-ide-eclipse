/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.code.refactor;

import static org.eclipse.ceylon.ide.eclipse.util.EditorUtil.getCurrentEditor;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

import org.eclipse.ceylon.ide.eclipse.code.editor.CeylonEditor;

public abstract class AbstractHandler extends org.eclipse.core.commands.AbstractHandler
        implements IObjectActionDelegate {
    
    @Override
    public boolean isEnabled() {
        IEditorPart editor = getCurrentEditor();
        if (super.isEnabled() && 
                editor instanceof CeylonEditor &&
                editor.getEditorInput() instanceof IFileEditorInput) {
            return isEnabled((CeylonEditor)editor);
        }
        else {
            return false;
        }
    }

    protected abstract boolean isEnabled(CeylonEditor editor);
    
    @Override
    public void run(IAction action) {
        try {
            execute(null);
        } 
        catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void selectionChanged(IAction action, ISelection selection) {
        action.setEnabled(isEnabled());
    }
    
    @Override
    public void setActivePart(IAction action, IWorkbenchPart targetPart) {}

}
