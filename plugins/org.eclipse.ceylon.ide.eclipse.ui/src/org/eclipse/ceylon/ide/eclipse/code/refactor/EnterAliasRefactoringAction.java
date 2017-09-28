package org.eclipse.ceylon.ide.eclipse.code.refactor;

import org.eclipse.ltk.ui.refactoring.RefactoringWizard;
import org.eclipse.ui.IEditorPart;

public class EnterAliasRefactoringAction extends AbstractRefactoringAction {
    
    public EnterAliasRefactoringAction(IEditorPart editor) {
        super(editor);
    }
    
    @Override
    public Refactoring createRefactoring() {
        return new EnterAliasRefactoring(editor);
    }
    
    @Override
    public RefactoringWizard createWizard(Refactoring refactoring) {
        return new EnterAliasWizard((EnterAliasRefactoring) refactoring);
    }
    
    @Override
    public String message() {
        return "No expression selected";
    }
    
}
