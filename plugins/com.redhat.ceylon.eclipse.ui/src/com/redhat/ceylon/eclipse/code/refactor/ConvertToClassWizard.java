package com.redhat.ceylon.eclipse.code.refactor;

import org.eclipse.ltk.ui.refactoring.RefactoringWizard;

public class ConvertToClassWizard extends RefactoringWizard {
    public ConvertToClassWizard(ConvertToClassRefactoring refactoring) {
        super(refactoring, DIALOG_BASED_USER_INTERFACE
                | PREVIEW_EXPAND_FIRST_NODE);
        setDefaultPageTitle(refactoring.getName());
    }

    protected void addUserInputPages() {
        addPage(new ConvertToClassInputPage(getRefactoring().getName()));
    }

    public ConvertToClassRefactoring getConvertToClassRefactoring() {
        return (ConvertToClassRefactoring) getRefactoring();
    }
}
