package com.redhat.ceylon.eclipse.code.refactor;

import org.eclipse.ltk.ui.refactoring.RefactoringWizard;

public class InlineWizard extends RefactoringWizard {
    public InlineWizard(EclipseInlineRefactoring refactoring) {
        super(refactoring, DIALOG_BASED_USER_INTERFACE
                | PREVIEW_EXPAND_FIRST_NODE);
        setDefaultPageTitle(refactoring.getName());
    }
    
    @Override
    protected void addUserInputPages() {
        addPage(new InlineInputPage(getRefactoring().getName()));
    }

//    public InlineRefactoring getRenameRefactoring() {
//        return (InlineRefactoring) getRefactoring();
//    }
}
