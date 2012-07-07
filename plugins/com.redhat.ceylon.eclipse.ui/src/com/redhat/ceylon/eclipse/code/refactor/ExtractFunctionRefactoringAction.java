package com.redhat.ceylon.eclipse.code.refactor;

import static com.redhat.ceylon.eclipse.ui.CeylonPlugin.PLUGIN_ID;

import org.eclipse.ltk.ui.refactoring.RefactoringWizard;
import org.eclipse.ui.IEditorPart;

public class ExtractFunctionRefactoringAction extends AbstractRefactoringAction {
    public ExtractFunctionRefactoringAction(IEditorPart editor) {
        super("ExtractFunction.", editor);
        setActionDefinitionId(PLUGIN_ID + ".action.extractFunction");
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