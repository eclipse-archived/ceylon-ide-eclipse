package org.eclipse.ceylon.ide.eclipse.code.refactor;

import org.eclipse.ltk.ui.refactoring.RefactoringWizard;
import org.eclipse.ui.IEditorPart;

public class ChangeParametersRefactoringAction extends AbstractRefactoringAction {
    public ChangeParametersRefactoringAction(IEditorPart editor) {
        super(editor);
    }
    
    @Override
    public Refactoring createRefactoring() {
        return new ChangeParametersRefactoring(editor);
    }
    
    @Override
    public RefactoringWizard createWizard(Refactoring refactoring) {
        return new ChangeParametersWizard((AbstractRefactoring) refactoring);
    }
    
    @Override
    public String message() {
        return "No function or class selected";
    }
    
}
