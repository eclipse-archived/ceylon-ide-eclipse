package org.eclipse.ceylon.ide.eclipse.code.refactor;

import org.eclipse.ltk.ui.refactoring.RefactoringWizard;
import org.eclipse.ui.IEditorPart;

public class MakeReceiverRefactoringAction extends AbstractRefactoringAction {
    
    public MakeReceiverRefactoringAction(IEditorPart editor) {
        super(editor);
    }
    
    @Override
    public Refactoring createRefactoring() {
        return new MakeReceiverRefactoring(editor);
    }
    
    @Override
    public RefactoringWizard createWizard(Refactoring refactoring) {
        return new MakeReceiverWizard((MakeReceiverRefactoring) refactoring);
    }
    
    @Override
    public String message() {
        return "No parameter name selected";
    }

    public boolean isEnabled() {
        return refactoring.getEnabled();
    }

}