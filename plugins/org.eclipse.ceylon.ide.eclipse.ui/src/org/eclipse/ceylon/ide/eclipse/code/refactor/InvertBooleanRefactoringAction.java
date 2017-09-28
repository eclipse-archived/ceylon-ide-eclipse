package org.eclipse.ceylon.ide.eclipse.code.refactor;

import org.eclipse.ltk.ui.refactoring.RefactoringWizard;
import org.eclipse.ui.IEditorPart;

public class InvertBooleanRefactoringAction extends AbstractRefactoringAction {

    public InvertBooleanRefactoringAction(IEditorPart editor) {
        super(editor);
    }

    @Override
    public Refactoring createRefactoring() {
        return new InvertBooleanRefactoring(editor);
    }

    @Override
    public RefactoringWizard createWizard(Refactoring refactoring) {
        return new InvertBooleanWizard((InvertBooleanRefactoring) refactoring);
    }

    @Override
    public String message() {
        return "No value selected";
    }

    public boolean isEnabled() {
        return refactoring.getEnabled();
    }

    public String getValueName() {
        return ((InvertBooleanRefactoring) refactoring).getValue().getName();
    }

}