package com.redhat.ceylon.eclipse.code.refactor;

import static com.redhat.ceylon.eclipse.ui.CeylonPlugin.PLUGIN_ID;

import org.eclipse.ltk.ui.refactoring.RefactoringWizard;
import org.eclipse.ui.IEditorPart;

public class MoveOutRefactoringAction extends AbstractRefactoringAction {
    public MoveOutRefactoringAction(IEditorPart editor) {
        super("MoveOut.", editor);
        setActionDefinitionId(PLUGIN_ID + ".action.moveOut");
    }
    
    @Override
    public Refactoring createRefactoring() {
        return new MoveOutRefactoring(getTextEditor());
    }
    
    @Override
    public RefactoringWizard createWizard(Refactoring refactoring) {
        return new MoveOutWizard((MoveOutRefactoring) refactoring);
    }
    
    @Override
    public String message() {
        return "No function or class name selected";
    }

}