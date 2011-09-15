package com.redhat.ceylon.eclipse.imp.refactoring;

import org.eclipse.ltk.ui.refactoring.RefactoringWizard;
import org.eclipse.ui.texteditor.ITextEditor;

public class InlineRefactoringAction extends AbstractRefactoringAction {
    public InlineRefactoringAction(ITextEditor editor) {
        super("Inline.", editor);
        setActionDefinitionId("com.redhat.ceylon.eclipse.ui.action.inline");
    }
    
    @Override
    public AbstractRefactoring getRefactoring() {
        return new InlineRefactoring(getTextEditor());
    }
    
    @Override
    public RefactoringWizard getWizard(AbstractRefactoring refactoring) {
        return new InlineWizard((InlineRefactoring) refactoring);
    }
}