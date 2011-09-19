package com.redhat.ceylon.eclipse.imp.refactoring;

import org.eclipse.ltk.ui.refactoring.RefactoringWizard;
import org.eclipse.ui.IEditorPart;

public class ExtractFunctionRefactoringAction extends AbstractRefactoringAction {
    public ExtractFunctionRefactoringAction(IEditorPart editor) {
        super("ExtractFunction.", editor);
        setActionDefinitionId("com.redhat.ceylon.eclipse.ui.action.extractFunction");
    }
    
    @Override
    public AbstractRefactoring createRefactoring() {
        return new ExtractFunctionRefactoring(getTextEditor());
    }
    
    @Override
    public RefactoringWizard createWizard(AbstractRefactoring refactoring) {
        return new ExtractFunctionWizard((ExtractFunctionRefactoring) refactoring);
    }
    
    @Override
    String message() {
        return "No expression selected";
    }

}