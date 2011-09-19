package com.redhat.ceylon.eclipse.imp.refactoring;

import org.eclipse.ltk.ui.refactoring.RefactoringWizard;
import org.eclipse.ui.IEditorPart;

public class InlineRefactoringAction extends AbstractRefactoringAction {
    public InlineRefactoringAction(IEditorPart editor) {
        super("Inline.", editor);
        setActionDefinitionId("com.redhat.ceylon.eclipse.ui.action.inline");
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
}