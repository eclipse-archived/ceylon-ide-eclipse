package com.redhat.ceylon.eclipse.code.refactor;

import static com.redhat.ceylon.eclipse.code.refactor.MoveUtil.getSelection;

import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.ui.refactoring.RefactoringWizard;
import org.eclipse.ui.PlatformUI;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;

public class MoveToNewUnitWizard extends RefactoringWizard {
    
    public MoveToNewUnitWizard(Refactoring refactoring) {
        super(refactoring, DIALOG_BASED_USER_INTERFACE
                | PREVIEW_EXPAND_FIRST_NODE);
        setDefaultPageTitle(refactoring.getName());
    }
    
    @Override
    protected void addUserInputPages() {
        MoveToNewUnitRefactoring refactoring = 
                (MoveToNewUnitRefactoring) getRefactoring();
        MoveToNewUnitWizardPage page = 
                new MoveToNewUnitWizardPage(refactoring.getName());
        Declaration dec = refactoring.getNode().getDeclarationModel();
        page.setUnitName(dec.getName() + ".ceylon");
        page.init(PlatformUI.getWorkbench(), getSelection());
        addPage(page);
    }
    
}
