package org.eclipse.ceylon.ide.eclipse.code.refactor;

import org.eclipse.ltk.ui.refactoring.RefactoringWizard;

public class EnterAliasWizard extends RefactoringWizard {
    public EnterAliasWizard(EnterAliasRefactoring refactoring) {
        super(refactoring, DIALOG_BASED_USER_INTERFACE
                | PREVIEW_EXPAND_FIRST_NODE);
        setDefaultPageTitle(refactoring.getName());
    }
    
    @Override
    protected void addUserInputPages() {
        addPage(new EnterAliasInputPage(getRefactoring().getName()));
    }

}
