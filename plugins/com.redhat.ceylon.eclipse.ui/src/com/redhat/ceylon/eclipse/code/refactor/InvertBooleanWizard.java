package org.eclipse.ceylon.ide.eclipse.code.refactor;

import org.eclipse.ltk.ui.refactoring.RefactoringWizard;

public class InvertBooleanWizard extends RefactoringWizard {
    
    public InvertBooleanWizard(InvertBooleanRefactoring refactoring) {
        super(refactoring, DIALOG_BASED_USER_INTERFACE | PREVIEW_EXPAND_FIRST_NODE);
        setDefaultPageTitle(refactoring.getName());
    }

    @Override
    protected void addUserInputPages() {
    }

}