package org.eclipse.ceylon.ide.eclipse.code.refactor;

import static org.eclipse.ceylon.ide.eclipse.code.refactor.MoveUtil.getSelection;

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
        MoveToUnitRefactoring refactoring = 
                (MoveToUnitRefactoring) getRefactoring();
        MoveToUnitWizardPage page = 
                new MoveToUnitWizardPage(refactoring.getName(),
                        refactoring.getOriginalFile());
        page.init(getSelection());
        addPage(page);
    }
    
}
