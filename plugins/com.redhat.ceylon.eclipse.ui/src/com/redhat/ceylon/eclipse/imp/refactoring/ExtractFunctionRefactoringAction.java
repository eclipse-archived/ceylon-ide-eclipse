package com.redhat.ceylon.eclipse.imp.refactoring;

import org.eclipse.ltk.ui.refactoring.RefactoringWizard;
import org.eclipse.ui.texteditor.ITextEditor;

public class ExtractFunctionRefactoringAction extends AbstractRefactoringAction {
    public ExtractFunctionRefactoringAction(ITextEditor editor) {
        super("ExtractFunction.", editor);
        setActionDefinitionId("com.redhat.ceylon.eclipse.ui.action.extractFunction");
    }
    
    @Override
    public AbstractRefactoring getRefactoring() {
        return new ExtractFunctionRefactoring(getTextEditor());
    }
    
    @Override
    public RefactoringWizard getWizard(AbstractRefactoring refactoring) {
        return new ExtractFunctionWizard((ExtractFunctionRefactoring) refactoring);
    }

}