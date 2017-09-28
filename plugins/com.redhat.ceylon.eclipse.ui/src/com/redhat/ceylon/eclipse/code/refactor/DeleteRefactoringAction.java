package org.eclipse.ceylon.ide.eclipse.code.refactor;

import org.eclipse.ltk.ui.refactoring.RefactoringWizard;
import org.eclipse.ui.IEditorPart;

public class DeleteRefactoringAction extends AbstractRefactoringAction {
    public DeleteRefactoringAction(IEditorPart editor) {
        super(editor);
    }
    
    @Override
    public Refactoring createRefactoring() {
        return new DeleteRefactoring(editor);
    }
    
    @Override
    public RefactoringWizard createWizard(Refactoring refactoring) {
        return new DeleteWizard((AbstractRefactoring) refactoring);
    }
    
    @Override
    public String message() {
        return "No declaration name selected";
    }
    
}
