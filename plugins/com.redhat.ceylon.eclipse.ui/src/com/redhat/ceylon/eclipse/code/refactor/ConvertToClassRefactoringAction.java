package com.redhat.ceylon.eclipse.code.refactor;

import org.eclipse.ltk.ui.refactoring.RefactoringWizard;
import org.eclipse.ui.IEditorPart;

public class ConvertToClassRefactoringAction extends AbstractRefactoringAction {
    public ConvertToClassRefactoringAction(IEditorPart editor) {
        super(editor);
    }
    
    @Override
    public Refactoring createRefactoring() {
        return new ConvertToClassRefactoring(editor);
    }
    
    @Override
    public RefactoringWizard createWizard(Refactoring refactoring) {
        return new ConvertToClassWizard((ConvertToClassRefactoring) refactoring);
    }
    
    @Override
    public String message() {
        return "No declaration name selected";
    }

    public String currentName() {
        return ((ConvertToClassRefactoring) refactoring).getDeclaration().getName();
    }
    
    public boolean isShared() {
        return ((ConvertToClassRefactoring) refactoring).getDeclaration().isShared();
    }

    public boolean isEnabled() {
        return refactoring.isEnabled();
    }
    
}
