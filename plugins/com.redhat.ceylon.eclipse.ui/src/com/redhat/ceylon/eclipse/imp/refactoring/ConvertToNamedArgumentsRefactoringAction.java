package com.redhat.ceylon.eclipse.imp.refactoring;

import org.eclipse.ltk.ui.refactoring.RefactoringWizard;
import org.eclipse.ui.texteditor.ITextEditor;

public class ConvertToNamedArgumentsRefactoringAction extends AbstractRefactoringAction {
    public ConvertToNamedArgumentsRefactoringAction(ITextEditor editor) {
        super("ConvertToNamedArguments.", editor);
        setActionDefinitionId("com.redhat.ceylon.eclipse.ui.action.convertToNamedArguments");
    }
    
    @Override
    public AbstractRefactoring getRefactoring() {
        return new ConvertToNamedArgumentsRefactoring(getTextEditor());
    }
    
    @Override
    public RefactoringWizard getWizard(AbstractRefactoring refactoring) {
        return new ConvertToNamedArgumentsWizard((ConvertToNamedArgumentsRefactoring) refactoring);
    }
    
}
