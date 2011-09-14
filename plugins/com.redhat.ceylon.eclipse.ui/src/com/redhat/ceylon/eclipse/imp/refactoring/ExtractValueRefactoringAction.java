package com.redhat.ceylon.eclipse.imp.refactoring;

import org.eclipse.ltk.ui.refactoring.RefactoringWizard;
import org.eclipse.ui.texteditor.ITextEditor;

public class ExtractValueRefactoringAction extends AbstractRefactoringAction {
    public ExtractValueRefactoringAction(ITextEditor editor) {
        super("ExtractValue.", editor);
    }
    
    @Override
    public AbstractRefactoring getRefactoring() {
        return new ExtractValueRefactoring(getTextEditor());
    }
    
    @Override
    public RefactoringWizard getWizard(AbstractRefactoring refactoring) {
        return new ExtractValueWizard((ExtractValueRefactoring) refactoring);
    }

}
