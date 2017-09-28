package org.eclipse.ceylon.ide.eclipse.code.refactor;

import org.eclipse.ltk.ui.refactoring.RefactoringWizard;
import org.eclipse.ui.IEditorPart;

public class CollectParametersRefactoringAction extends AbstractRefactoringAction {
    public CollectParametersRefactoringAction(IEditorPart editor) {
        super(editor);
    }
    
    @Override
    public Refactoring createRefactoring() {
        return new CollectParametersRefactoring(editor);
    }
    
    @Override
    public RefactoringWizard createWizard(Refactoring refactoring) {
        return new CollectParametersWizard((AbstractRefactoring) refactoring);
    }
    
    @Override
    public String message() {
        return "No function or class selected";
    }
    
}
