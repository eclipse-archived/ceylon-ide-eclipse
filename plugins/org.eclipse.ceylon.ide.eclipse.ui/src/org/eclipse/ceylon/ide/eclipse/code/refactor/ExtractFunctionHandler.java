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

import static org.eclipse.ceylon.ide.eclipse.code.refactor.ExtractLinkedMode.useLinkedMode;
import static org.eclipse.ceylon.ide.eclipse.util.EditorUtil.getCurrentEditor;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.texteditor.ITextEditor;

import org.eclipse.ceylon.ide.eclipse.code.editor.CeylonEditor;

public class ExtractFunctionHandler extends AbstractHandler {
        
    @Override
    public Object execute(ExecutionEvent event) 
            throws ExecutionException {
        ITextEditor editor = (ITextEditor) getCurrentEditor();
        if (useLinkedMode() && editor instanceof CeylonEditor) {
            CeylonEditor ce = (CeylonEditor) editor;
            if (ce.isInLinkedMode()) {
                Object owner = ce.getLinkedModeOwner();
                if (owner instanceof ExtractFunctionLinkedMode) {
                    ExtractFunctionLinkedMode current = 
                            (ExtractFunctionLinkedMode) 
                                owner;
                    current.enterDialogMode();
                    current.openDialog();
                }
                else {
                    new ExtractFunctionRefactoringAction(editor).run();
                }
            }
            else {
                ExtractFunctionLinkedMode.selectExpressionAndStart(ce);
            }
        }
        else {
            new ExtractFunctionRefactoringAction(editor).run();
        }
        return null;
    }
            
}
