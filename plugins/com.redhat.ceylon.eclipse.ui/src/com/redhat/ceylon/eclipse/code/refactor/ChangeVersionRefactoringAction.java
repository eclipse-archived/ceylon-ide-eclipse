package org.eclipse.ceylon.ide.eclipse.code.refactor;

import org.eclipse.ltk.ui.refactoring.RefactoringWizard;
import org.eclipse.ui.IEditorPart;

public class ChangeVersionRefactoringAction extends AbstractRefactoringAction {
    public ChangeVersionRefactoringAction(IEditorPart editor) {
        super(editor);
    }
    
    @Override
    public Refactoring createRefactoring() {
        return new ChangeVersionRefactoring(editor);
    }
    
    @Override
    public RefactoringWizard createWizard(Refactoring refactoring) {
        return new ChangeVersionWizard((AbstractRefactoring) refactoring);
    }
    
    @Override
    public String message() {
        return "No module version selected";
    }
    
}
