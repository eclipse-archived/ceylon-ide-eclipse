package org.eclipse.ceylon.ide.eclipse.code.refactor;

import org.eclipse.ltk.ui.refactoring.RefactoringWizard;

public class MoveOutWizard extends RefactoringWizard {
    public MoveOutWizard(MoveOutRefactoring refactoring) {
        super(refactoring, DIALOG_BASED_USER_INTERFACE
                | PREVIEW_EXPAND_FIRST_NODE);
        setDefaultPageTitle(refactoring.getName());
    }
    
    @Override
    protected void addUserInputPages() {
        addPage(new MoveOutInputPage(getRefactoring().getName()));
    }

//    public ExtractFunctionRefactoring getExtractFunctionRefactoring() {
//        return (ExtractFunctionRefactoring) getRefactoring();
//    }
}
