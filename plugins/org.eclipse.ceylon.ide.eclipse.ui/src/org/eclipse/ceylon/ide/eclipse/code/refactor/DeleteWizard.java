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

import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ltk.ui.refactoring.RefactoringWizard;

public class DeleteWizard extends RefactoringWizard {
    public DeleteWizard(AbstractRefactoring refactoring) {
        super(refactoring, WIZARD_BASED_USER_INTERFACE
                | PREVIEW_EXPAND_FIRST_NODE);
        setDefaultPageTitle(refactoring.getName());
        setWindowTitle("Safe Delete");
    }
    
    @Override
    protected void addUserInputPages() {
        ((WizardDialog) getContainer()).setPageSize(600, 200);
        addPage(new DeleteInputPage(getRefactoring().getName()));
    }

}
