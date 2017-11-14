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

public class MakeReceiverRefactoringAction extends AbstractRefactoringAction {
    
    public MakeReceiverRefactoringAction(IEditorPart editor) {
        super(editor);
    }
    
    @Override
    public Refactoring createRefactoring() {
        return new MakeReceiverRefactoring(editor);
    }
    
    @Override
    public RefactoringWizard createWizard(Refactoring refactoring) {
        return new MakeReceiverWizard((MakeReceiverRefactoring) refactoring);
    }
    
    @Override
    public String message() {
        return "No parameter name selected";
    }

    public boolean isEnabled() {
        return refactoring.getEnabled();
    }

}