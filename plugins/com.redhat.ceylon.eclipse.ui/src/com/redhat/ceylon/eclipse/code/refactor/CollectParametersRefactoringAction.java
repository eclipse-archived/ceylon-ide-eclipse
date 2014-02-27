package com.redhat.ceylon.eclipse.code.refactor;

import static com.redhat.ceylon.eclipse.ui.CeylonPlugin.PLUGIN_ID;

import org.eclipse.ltk.ui.refactoring.RefactoringWizard;
import org.eclipse.ui.IEditorPart;

public class CollectParametersRefactoringAction extends AbstractRefactoringAction {
    public CollectParametersRefactoringAction(IEditorPart editor) {
        super("CollectParameters.", editor);
        setActionDefinitionId(PLUGIN_ID + ".action.collectParameters");
    }
    
    @Override
    public AbstractRefactoring createRefactoring() {
        return new CollectParametersRefactoring(getTextEditor());
    }
    
    @Override
    public RefactoringWizard createWizard(AbstractRefactoring refactoring) {
        return new CollectParametersWizard((AbstractRefactoring) refactoring);
    }
    
    @Override
    String message() {
        return "No function or class selected";
    }
    
}
