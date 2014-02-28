package com.redhat.ceylon.eclipse.code.refactor;

import org.eclipse.ltk.ui.refactoring.RefactoringWizard;

public class DeleteWizard extends RefactoringWizard {
    public DeleteWizard(AbstractRefactoring refactoring) {
        super(refactoring, DIALOG_BASED_USER_INTERFACE
                | PREVIEW_EXPAND_FIRST_NODE);
        setDefaultPageTitle(refactoring.getName());
    }
    
    @Override
    protected void addUserInputPages() {
        addPage(new DeleteInputPage(getRefactoring().getName()));
    }

//    public AbstractRefactoring getRenameRefactoring() {
//        return (AbstractRefactoring) getRefactoring();
//    }
}
