package com.redhat.ceylon.eclipse.code.refactor;

import org.eclipse.ltk.ui.refactoring.RefactoringWizard;
import org.eclipse.ui.IEditorPart;

public class ExtractParameterRefactoringAction extends AbstractRefactoringAction {
    
    public ExtractParameterRefactoringAction(IEditorPart editor) {
        super(editor);
    }
    
    @Override
    public Refactoring createRefactoring() {
        return new ExtractParameterRefactoring(editor);
    }
    
    @Override
    public RefactoringWizard createWizard(Refactoring refactoring) {
        return new ExtractParameterWizard((ExtractParameterRefactoring) refactoring);
    }
    
    @Override
    public String message() {
        return "No expression selected";
    }

}
