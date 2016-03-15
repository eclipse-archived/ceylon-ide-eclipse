package com.redhat.ceylon.eclipse.code.refactor;

import org.eclipse.ltk.ui.refactoring.RefactoringWizard;

public class ExtractFunctionWizard extends RefactoringWizard {
    public ExtractFunctionWizard(Refactoring refactoring) {
        super(refactoring, DIALOG_BASED_USER_INTERFACE
                | PREVIEW_EXPAND_FIRST_NODE);
        setDefaultPageTitle(refactoring.getName());
    }
    
    @Override
    protected void addUserInputPages() {
        addPage(new ExtractFunctionInputPage(getRefactoring().getName()));
    }

//    public ExtractFunctionRefactoring getExtractFunctionRefactoring() {
//        return (ExtractFunctionRefactoring) getRefactoring();
//    }
}
