package com.redhat.ceylon.eclipse.code.refactor;

import org.eclipse.ltk.ui.refactoring.RefactoringWizard;
import org.eclipse.ui.IEditorPart;

public class RenameRefactoringAction extends AbstractRefactoringAction {
    
    public RenameRefactoringAction(IEditorPart editor) {
        super(editor);
    }
    
    @Override
    public Refactoring createRefactoring() {
        return new RenameRefactoring(editor);
    }
    
    @Override
    public RefactoringWizard createWizard(Refactoring refactoring) {
        return new RenameWizard((RenameRefactoring) refactoring);
    }
    
    @Override
    public String message() {
        return "No declaration name selected";
    }
    
}
