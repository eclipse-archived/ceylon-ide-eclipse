package com.redhat.ceylon.eclipse.code.refactor;

import org.eclipse.ltk.ui.refactoring.RefactoringWizard;

public class ExtractValueWizard extends RefactoringWizard {
    public ExtractValueWizard(Refactoring refactoring) {
        super(refactoring, DIALOG_BASED_USER_INTERFACE
                | PREVIEW_EXPAND_FIRST_NODE);
        setDefaultPageTitle(refactoring.getName());
    }
    
    @Override
    protected void addUserInputPages() {
        addPage(new ExtractValueInputPage(getRefactoring().getName()));
    }

//    public ExtractValueRefactoring getExtractLocalRefactoring() {
//        return (ExtractValueRefactoring) getRefactoring();
//    }
}
