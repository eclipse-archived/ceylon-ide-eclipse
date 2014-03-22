package com.redhat.ceylon.eclipse.code.refactor;

import static com.redhat.ceylon.eclipse.ui.CeylonPlugin.PLUGIN_ID;

import org.eclipse.ltk.ui.refactoring.RefactoringWizard;
import org.eclipse.ui.IEditorPart;

public class EnterAliasRefactoringAction extends AbstractRefactoringAction {
    public EnterAliasRefactoringAction(IEditorPart editor) {
        super("EnterAlias.", editor);
        setActionDefinitionId(PLUGIN_ID + ".action.enterAlias");
    }
    
    @Override
    public Refactoring createRefactoring() {
        return new EnterAliasRefactoring(getTextEditor());
    }
    
    @Override
    public RefactoringWizard createWizard(Refactoring refactoring) {
        return new EnterAliasWizard((EnterAliasRefactoring) refactoring);
    }
    
    @Override
    public String message() {
        return "No expression selected";
    }

}
