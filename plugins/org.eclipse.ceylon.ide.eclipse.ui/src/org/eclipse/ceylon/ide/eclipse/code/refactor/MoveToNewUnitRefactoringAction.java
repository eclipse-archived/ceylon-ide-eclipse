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

import org.eclipse.ltk.ui.refactoring.RefactoringWizard;
import org.eclipse.ui.IEditorPart;

import org.eclipse.ceylon.ide.eclipse.code.editor.CeylonEditor;
import org.eclipse.ceylon.ide.eclipse.code.editor.Navigation;

public class MoveToNewUnitRefactoringAction extends AbstractRefactoringAction {

    public MoveToNewUnitRefactoringAction(IEditorPart editor) {
        super(editor);
    }
    
    @Override
    public Refactoring createRefactoring() {
        return new MoveToNewUnitRefactoring((CeylonEditor) editor);
    }

    @Override
    public RefactoringWizard createWizard(Refactoring refactoring) {
        return new MoveToNewUnitWizard(refactoring);
    }
    
    @Override
    public boolean run() {
        boolean success = super.run();
        if (success) {
            MoveToNewUnitRefactoring r = (MoveToNewUnitRefactoring) refactoring;
            Navigation.gotoLocation(r.getTargetPath(), r.getOffset());
        }
        return success;
    }

    @Override
    public String message() {
        return "No declaration name selected";
    }

}
