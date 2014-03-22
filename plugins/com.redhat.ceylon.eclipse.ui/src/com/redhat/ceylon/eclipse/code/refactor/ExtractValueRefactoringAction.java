package com.redhat.ceylon.eclipse.code.refactor;

import static com.redhat.ceylon.eclipse.ui.CeylonPlugin.PLUGIN_ID;

import org.eclipse.ltk.ui.refactoring.RefactoringWizard;
import org.eclipse.ui.IEditorPart;

public class ExtractValueRefactoringAction extends AbstractRefactoringAction {
    public ExtractValueRefactoringAction(IEditorPart editor) {
        super("ExtractValue.", editor);
        setActionDefinitionId(PLUGIN_ID + ".action.extractValue");
    }
    
    @Override
    public Refactoring createRefactoring() {
        return new ExtractValueRefactoring(getTextEditor());
    }
    
    @Override
    public RefactoringWizard createWizard(Refactoring refactoring) {
        return new ExtractValueWizard((ExtractValueRefactoring) refactoring);
    }
    
    @Override
    public String message() {
        return "No expression selected";
    }

}
