package org.eclipse.ceylon.ide.eclipse.code.refactor;

import org.eclipse.ltk.ui.refactoring.RefactoringWizard;
import org.eclipse.ui.IEditorPart;

public class ExtractInterfaceRefactoringAction extends AbstractRefactoringAction {

    public ExtractInterfaceRefactoringAction(IEditorPart editor) {
        super(editor);
    }

    @Override
    public Refactoring createRefactoring() {
        return new ExtractInterfaceRefactoring(editor);
    }

    @Override
    public RefactoringWizard createWizard(Refactoring refactoring) {
        return new ExtractInterfaceWizard((ExtractInterfaceRefactoring) refactoring);
    }

    @Override
    public String message() {
        return "No class selected";
    }

}