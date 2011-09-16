package com.redhat.ceylon.eclipse.imp.refactoring;

import org.eclipse.ltk.ui.refactoring.RefactoringWizard;
import org.eclipse.ui.IEditorPart;

public class ConvertToNamedArgumentsRefactoringAction extends AbstractRefactoringAction {
    public ConvertToNamedArgumentsRefactoringAction(IEditorPart editor) {
        super("ConvertToNamedArguments.", editor);
        setActionDefinitionId("com.redhat.ceylon.eclipse.ui.action.convertToNamedArguments");
    }
    
    @Override
    public AbstractRefactoring createRefactoring() {
        return new ConvertToNamedArgumentsRefactoring(getTextEditor());
    }
    
    @Override
    public RefactoringWizard createWizard(AbstractRefactoring refactoring) {
        return new ConvertToNamedArgumentsWizard((ConvertToNamedArgumentsRefactoring) refactoring);
    }
    
}
