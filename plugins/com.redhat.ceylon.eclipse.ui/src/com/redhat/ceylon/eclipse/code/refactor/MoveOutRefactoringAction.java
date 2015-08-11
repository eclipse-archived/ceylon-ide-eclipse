package com.redhat.ceylon.eclipse.code.refactor;

import org.eclipse.ltk.ui.refactoring.RefactoringWizard;
import org.eclipse.ui.IEditorPart;

public class MoveOutRefactoringAction extends AbstractRefactoringAction {
    
    public MoveOutRefactoringAction(IEditorPart editor) {
        super(editor);
    }
    
    @Override
    public Refactoring createRefactoring() {
        return new MoveOutRefactoring(editor);
    }
    
    @Override
    public RefactoringWizard createWizard(Refactoring refactoring) {
        return new MoveOutWizard((MoveOutRefactoring) refactoring);
    }
    
    @Override
    public String message() {
        return "No function or class name selected";
    }

    public boolean isEnabled() {
        return refactoring.getEnabled();
    }

}