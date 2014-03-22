package com.redhat.ceylon.eclipse.code.refactor;

import org.eclipse.ltk.ui.refactoring.RefactoringWizard;
import org.eclipse.ui.IEditorPart;

public class ExtractFunctionRefactoringAction extends AbstractRefactoringAction {
    
    public ExtractFunctionRefactoringAction(IEditorPart editor) {
        super(editor);
    }
    
    @Override
    public Refactoring createRefactoring() {
        return new ExtractFunctionRefactoring(editor);
    }
    
    @Override
    public RefactoringWizard createWizard(Refactoring refactoring) {
        return new ExtractFunctionWizard((ExtractFunctionRefactoring) refactoring);
    }
    
    @Override
    public String message() {
        return "No expression selected";
    }

}