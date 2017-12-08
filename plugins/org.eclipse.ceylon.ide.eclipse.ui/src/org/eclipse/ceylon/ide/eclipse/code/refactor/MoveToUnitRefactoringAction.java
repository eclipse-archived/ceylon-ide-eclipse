/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 1.0 which is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.code.refactor;

import org.eclipse.ltk.ui.refactoring.RefactoringWizard;
import org.eclipse.ui.IEditorPart;

import org.eclipse.ceylon.ide.eclipse.code.editor.CeylonEditor;
import org.eclipse.ceylon.ide.eclipse.code.editor.Navigation;

public class MoveToUnitRefactoringAction extends AbstractRefactoringAction {

    public MoveToUnitRefactoringAction(IEditorPart editor) {
        super(editor);
    }
    
    @Override
    public Refactoring createRefactoring() {
        return new MoveToUnitRefactoring((CeylonEditor) editor);
    }

    @Override
    public RefactoringWizard createWizard(Refactoring refactoring) {
        return new MoveToUnitWizard(refactoring);
    }

    @Override
    public boolean run() {
        boolean success = super.run();
        if (success) {
            MoveToUnitRefactoring r = (MoveToUnitRefactoring) refactoring;
            Navigation.gotoLocation(r.getTargetPath(), r.getOffset());
        }
        return success;
    }

    @Override
    public String message() {
        return "No declaration name selected";
    }

}
