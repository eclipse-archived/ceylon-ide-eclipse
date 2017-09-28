package org.eclipse.ceylon.ide.eclipse.code.refactor;

import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ltk.ui.refactoring.RefactoringWizard;

public class DeleteWizard extends RefactoringWizard {
    public DeleteWizard(AbstractRefactoring refactoring) {
        super(refactoring, WIZARD_BASED_USER_INTERFACE
                | PREVIEW_EXPAND_FIRST_NODE);
        setDefaultPageTitle(refactoring.getName());
        setWindowTitle("Safe Delete");
    }
    
    @Override
    protected void addUserInputPages() {
        ((WizardDialog) getContainer()).setPageSize(600, 200);
        addPage(new DeleteInputPage(getRefactoring().getName()));
    }

}
