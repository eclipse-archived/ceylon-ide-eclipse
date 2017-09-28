package org.eclipse.ceylon.ide.eclipse.code.refactor;

import org.eclipse.ltk.ui.refactoring.RefactoringWizard;

public class MakeReceiverWizard extends RefactoringWizard {
    public MakeReceiverWizard(MakeReceiverRefactoring refactoring) {
        super(refactoring, DIALOG_BASED_USER_INTERFACE
                | PREVIEW_EXPAND_FIRST_NODE);
        setDefaultPageTitle(refactoring.getName());
    }
    
    @Override
    protected void addUserInputPages() {
        addPage(new MakeReceiverInputPage(getRefactoring().getName()));
    }
    
//    public ExtractFunctionRefactoring getExtractFunctionRefactoring() {
//        return (ExtractFunctionRefactoring) getRefactoring();
//    }
}
