package com.redhat.ceylon.eclipse.code.refactor;

import static com.redhat.ceylon.eclipse.ui.CeylonPlugin.PLUGIN_ID;

import org.eclipse.ltk.ui.refactoring.RefactoringWizard;
import org.eclipse.ui.IEditorPart;

public class ChangeParametersRefactoringAction extends AbstractRefactoringAction {
    public ChangeParametersRefactoringAction(IEditorPart editor) {
        super("ChangeParameters.", editor);
        setActionDefinitionId(PLUGIN_ID + ".action.changeParameters");
    }
    
    @Override
    public Refactoring createRefactoring() {
        return new ChangeParametersRefactoring(getTextEditor());
    }
    
    @Override
    public RefactoringWizard createWizard(Refactoring refactoring) {
        return new ChangeParametersWizard((AbstractRefactoring) refactoring);
    }
    
    @Override
    public String message() {
        return "No function or class selected";
    }
    
}
