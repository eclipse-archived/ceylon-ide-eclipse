/********************************************************************************
 * Copyright (c) {date} Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.code.refactor;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ltk.ui.refactoring.RefactoringWizard;
import org.eclipse.ui.IEditorPart;

public abstract class AbstractRefactoringAction {
    
    final Refactoring refactoring;
    final IEditorPart editor;
    
    public AbstractRefactoringAction(IEditorPart editor) {
        this.editor = editor;
        refactoring = createRefactoring();
    }

    public boolean run() {
        if (refactoring!=null && refactoring.getEnabled()) {
            return new RefactoringStarter()
                    .activate(createWizard(refactoring),
                            editor.getSite().getShell(),
                            refactoring.getName(), 
                            getSaveMode());
        }
        else {
            MessageDialog.openWarning(
                    editor.getEditorSite().getShell(), 
                    "Ceylon Refactoring Error", 
                    message());
            return false;
        }
    }

    int getSaveMode() {
        if (refactoring instanceof EclipseAbstractRefactoring) {
            return ((EclipseAbstractRefactoring) refactoring).getSaveMode();
        }
        else if (refactoring instanceof AbstractRefactoring) {
            return ((AbstractRefactoring) refactoring).getSaveMode();
        }
        else {
            return RefactoringSaveHelper.SAVE_NOTHING;
        }
    }
    
    public abstract Refactoring createRefactoring();
    public abstract RefactoringWizard createWizard(Refactoring refactoring);
    
    public abstract String message();
    
}