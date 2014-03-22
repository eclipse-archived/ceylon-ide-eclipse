package com.redhat.ceylon.eclipse.code.refactor;

import static com.redhat.ceylon.eclipse.code.refactor.MoveUtil.getSelection;

import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.ui.refactoring.RefactoringWizard;

public class MoveToUnitWizard extends RefactoringWizard {
    
    public MoveToUnitWizard(Refactoring refactoring) {
        super(refactoring, DIALOG_BASED_USER_INTERFACE
                | PREVIEW_EXPAND_FIRST_NODE);
        setDefaultPageTitle(refactoring.getName());
    }
    
    @Override
    protected void addUserInputPages() {
        String name = ((MoveToUnitRefactoring) getRefactoring()).getNode()
                .getDeclarationModel().getName();
        MoveToUnitWizardPage page = 
                new MoveToUnitWizardPage(getRefactoring().getName(), name);
        page.init(getSelection());
        addPage(page);
    }
    
}
