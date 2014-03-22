package com.redhat.ceylon.eclipse.code.refactor;

import static com.redhat.ceylon.eclipse.ui.CeylonPlugin.PLUGIN_ID;

import org.eclipse.ltk.ui.refactoring.RefactoringWizard;
import org.eclipse.ui.IEditorPart;

import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;

public class MoveToNewUnitRefactoringAction extends AbstractRefactoringAction {

    public MoveToNewUnitRefactoringAction(IEditorPart editor) {
        super("MoveToNewUnit.", editor);
        setActionDefinitionId(PLUGIN_ID + ".action.moveDeclarationToNewUnit");
    }
    
    @Override
    public Refactoring createRefactoring() {
        return new MoveToNewUnitRefactoring((CeylonEditor) getTextEditor());
    }

    @Override
    public RefactoringWizard createWizard(Refactoring refactoring) {
        return new MoveToNewUnitWizard(refactoring);
    }

    @Override
    public String message() {
        return "No declaration name selected";
    }

}
