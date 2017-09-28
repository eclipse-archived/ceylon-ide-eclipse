package org.eclipse.ceylon.ide.eclipse.code.refactor;

import org.eclipse.ltk.ui.refactoring.RefactoringWizard;

public class ChangeParametersWizard extends RefactoringWizard {
    
    public ChangeParametersWizard(AbstractRefactoring refactoring) {
        super(refactoring, DIALOG_BASED_USER_INTERFACE
                | PREVIEW_EXPAND_FIRST_NODE);
        setDefaultPageTitle(refactoring.getName());
    }
    
    @Override
    protected void addUserInputPages() {
        addPage(new ChangeParametersInputPage(getRefactoring().getName()));
    }

}
