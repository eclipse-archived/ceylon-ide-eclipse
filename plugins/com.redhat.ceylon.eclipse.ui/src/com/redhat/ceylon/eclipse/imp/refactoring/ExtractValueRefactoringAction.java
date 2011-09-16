package com.redhat.ceylon.eclipse.imp.refactoring;

import org.eclipse.ltk.ui.refactoring.RefactoringWizard;
import org.eclipse.ui.texteditor.ITextEditor;

public class ExtractValueRefactoringAction extends AbstractRefactoringAction {
    public ExtractValueRefactoringAction(ITextEditor editor) {
        super("ExtractValue.", editor);
        setActionDefinitionId("com.redhat.ceylon.eclipse.ui.action.extractValue");
    }
    
    @Override
    public AbstractRefactoring createRefactoring() {
        return new ExtractValueRefactoring(getTextEditor());
    }
    
    @Override
    public RefactoringWizard createWizard(AbstractRefactoring refactoring) {
        return new ExtractValueWizard((ExtractValueRefactoring) refactoring);
    }

}
