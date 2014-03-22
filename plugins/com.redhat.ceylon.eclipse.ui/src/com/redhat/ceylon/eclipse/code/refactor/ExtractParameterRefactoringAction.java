package com.redhat.ceylon.eclipse.code.refactor;

import static com.redhat.ceylon.eclipse.ui.CeylonPlugin.PLUGIN_ID;

import org.eclipse.ltk.ui.refactoring.RefactoringWizard;
import org.eclipse.ui.IEditorPart;

public class ExtractParameterRefactoringAction extends AbstractRefactoringAction {
    public ExtractParameterRefactoringAction(IEditorPart editor) {
        super("ExtractParameter.", editor);
        setActionDefinitionId(PLUGIN_ID + ".action.extractParameter");
    }
    
    @Override
    public Refactoring createRefactoring() {
        return new ExtractParameterRefactoring(getTextEditor());
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
