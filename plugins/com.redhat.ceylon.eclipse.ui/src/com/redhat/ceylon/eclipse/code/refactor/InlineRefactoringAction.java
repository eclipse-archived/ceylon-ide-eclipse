package com.redhat.ceylon.eclipse.code.refactor;

import static com.redhat.ceylon.eclipse.ui.CeylonPlugin.PLUGIN_ID;

import org.eclipse.ltk.ui.refactoring.RefactoringWizard;
import org.eclipse.ui.IEditorPart;

public class InlineRefactoringAction extends AbstractRefactoringAction {
    public InlineRefactoringAction(IEditorPart editor) {
        super("Inline.", editor);
        setActionDefinitionId(PLUGIN_ID + ".action.inline");
    }
    
    @Override
    public AbstractRefactoring createRefactoring() {
        return new InlineRefactoring(getTextEditor());
    }
    
    @Override
    public RefactoringWizard createWizard(AbstractRefactoring refactoring) {
        return new InlineWizard((InlineRefactoring) refactoring);
    }
    
    @Override
    String message() {
        return "No function or value name selected";
    }

    public String currentName() {
        return ((InlineRefactoring) refactoring).getDeclaration().getName();
    }
}