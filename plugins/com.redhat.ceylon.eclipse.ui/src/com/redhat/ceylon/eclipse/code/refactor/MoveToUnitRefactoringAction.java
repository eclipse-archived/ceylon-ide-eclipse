package com.redhat.ceylon.eclipse.code.refactor;

import static com.redhat.ceylon.eclipse.ui.CeylonPlugin.PLUGIN_ID;

import org.eclipse.ltk.ui.refactoring.RefactoringWizard;
import org.eclipse.ui.IEditorPart;

import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;

public class MoveToUnitRefactoringAction extends AbstractRefactoringAction {

    public MoveToUnitRefactoringAction(IEditorPart editor) {
        super("MoveToUnit.", editor);
        setActionDefinitionId(PLUGIN_ID + ".action.moveDeclarationToUnit");
    }
    
    @Override
    public Refactoring createRefactoring() {
        return new MoveToUnitRefactoring((CeylonEditor) getTextEditor());
    }

    @Override
    public RefactoringWizard createWizard(Refactoring refactoring) {
        return new MoveToUnitWizard(refactoring);
    }

    @Override
    public String message() {
        return "No declaration name selected";
    }

}
