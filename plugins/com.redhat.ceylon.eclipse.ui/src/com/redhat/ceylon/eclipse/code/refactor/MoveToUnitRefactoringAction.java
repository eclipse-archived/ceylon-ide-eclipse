package org.eclipse.ceylon.ide.eclipse.code.refactor;

import org.eclipse.ltk.ui.refactoring.RefactoringWizard;
import org.eclipse.ui.IEditorPart;

import org.eclipse.ceylon.ide.eclipse.code.editor.CeylonEditor;
import org.eclipse.ceylon.ide.eclipse.code.editor.Navigation;

public class MoveToUnitRefactoringAction extends AbstractRefactoringAction {

    public MoveToUnitRefactoringAction(IEditorPart editor) {
        super(editor);
    }
    
    @Override
    public Refactoring createRefactoring() {
        return new MoveToUnitRefactoring((CeylonEditor) editor);
    }

    @Override
    public RefactoringWizard createWizard(Refactoring refactoring) {
        return new MoveToUnitWizard(refactoring);
    }

    @Override
    public boolean run() {
        boolean success = super.run();
        if (success) {
            MoveToUnitRefactoring r = (MoveToUnitRefactoring) refactoring;
            Navigation.gotoLocation(r.getTargetPath(), r.getOffset());
        }
        return success;
    }

    @Override
    public String message() {
        return "No declaration name selected";
    }

}
