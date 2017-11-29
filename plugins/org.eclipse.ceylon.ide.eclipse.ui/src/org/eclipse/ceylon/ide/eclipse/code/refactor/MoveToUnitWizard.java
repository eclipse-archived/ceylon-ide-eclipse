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

import static org.eclipse.ceylon.ide.eclipse.code.refactor.MoveUtil.getSelection;

import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.ui.refactoring.RefactoringWizard;

public class MoveToUnitWizard extends RefactoringWizard {
    
    public MoveToUnitWizard(Refactoring refactoring) {
        super(refactoring, DIALOG_BASED_USER_INTERFACE
                | PREVIEW_EXPAND_FIRST_NODE);
        setDefaultPageTitle(refactoring.getName());
    }
    
    @Override
    protected void addUserInputPages() {
        MoveToUnitRefactoring refactoring = 
                (MoveToUnitRefactoring) getRefactoring();
        MoveToUnitWizardPage page = 
                new MoveToUnitWizardPage(refactoring.getName(),
                        refactoring.getOriginalFile());
        page.init(getSelection());
        addPage(page);
    }
    
}
