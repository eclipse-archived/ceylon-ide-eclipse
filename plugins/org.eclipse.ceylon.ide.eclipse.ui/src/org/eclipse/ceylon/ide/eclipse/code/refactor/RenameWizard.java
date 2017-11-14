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

public class RenameWizard extends RefactoringWizard {
    public RenameWizard(RenameRefactoring refactoring) {
        super(refactoring, DIALOG_BASED_USER_INTERFACE
                | PREVIEW_EXPAND_FIRST_NODE);
        setDefaultPageTitle(refactoring.getName());
    }
    
    @Override
    protected void addUserInputPages() {
        addPage(new RenameInputPage(getRefactoring().getName()));
    }

//    public AbstractRefactoring getRenameRefactoring() {
//        return (AbstractRefactoring) getRefactoring();
//    }
}
