package com.redhat.ceylon.eclipse.imp.refactoring;

import org.eclipse.ltk.ui.refactoring.RefactoringWizard;
import org.eclipse.ui.IEditorPart;

public class ConvertToClassRefactoringAction extends AbstractRefactoringAction {
	public ConvertToClassRefactoringAction(IEditorPart editor) {
		super("ConvertToClass.", editor);
		setActionDefinitionId("com.redhat.ceylon.eclipse.ui.action.convertToClass");
	}
	
	@Override
	public AbstractRefactoring createRefactoring() {
	    return new ConvertToClassRefactoring(getTextEditor());
	}
	
	@Override
	public RefactoringWizard createWizard(AbstractRefactoring refactoring) {
	    return new ConvertToClassWizard((ConvertToClassRefactoring) refactoring);
	}
	
	@Override
	String message() {
	    return "No declaration name selected";
	}

    public String currentName() {
        return ((ConvertToClassRefactoring) refactoring).getDeclaration().getName();
    }
	
    public boolean isShared() {
        return ((ConvertToClassRefactoring) refactoring).getDeclaration().isShared();
    }
    
}
