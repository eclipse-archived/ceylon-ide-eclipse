package org.eclipse.ceylon.ide.eclipse.code.refactor;

import org.eclipse.ltk.ui.refactoring.RefactoringWizard;
import org.eclipse.ui.IEditorPart;

public class AliasRefactoringAction extends AbstractRefactoringAction {
    
    public AliasRefactoringAction(IEditorPart editor) {
        super(editor);
    }
    
    @Override
    public Refactoring createRefactoring() {
        return new AliasRefactoring(editor);
    }
    
    @Override
    public RefactoringWizard createWizard(Refactoring refactoring) {
        return new AliasWizard((AliasRefactoring) refactoring);
    }
    
    @Override
    public String message() {
        return "No declaration name selected";
    }
    
}
