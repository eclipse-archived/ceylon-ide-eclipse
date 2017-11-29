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
import org.eclipse.ui.PlatformUI;

import org.eclipse.ceylon.model.typechecker.model.Declaration;

public class MoveToNewUnitWizard extends RefactoringWizard {
    
    public MoveToNewUnitWizard(Refactoring refactoring) {
        super(refactoring, DIALOG_BASED_USER_INTERFACE
                | PREVIEW_EXPAND_FIRST_NODE);
        setDefaultPageTitle(refactoring.getName());
    }
    
    @Override
    protected void addUserInputPages() {
        MoveToNewUnitRefactoring refactoring = 
                (MoveToNewUnitRefactoring) getRefactoring();
        MoveToNewUnitWizardPage page = 
                new MoveToNewUnitWizardPage(refactoring.getName());
        Declaration dec = refactoring.getNode().getDeclarationModel();
        page.setUnitName(dec.getName() + ".ceylon");
        page.init(PlatformUI.getWorkbench(), getSelection());
        addPage(page);
    }
    
}
