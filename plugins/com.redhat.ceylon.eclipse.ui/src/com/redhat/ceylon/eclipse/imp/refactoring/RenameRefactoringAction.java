package com.redhat.ceylon.eclipse.imp.refactoring;

import org.eclipse.ltk.ui.refactoring.RefactoringWizard;
import org.eclipse.ui.texteditor.ITextEditor;

public class RenameRefactoringAction extends AbstractRefactoringAction {
	public RenameRefactoringAction(ITextEditor editor) {
		super("Rename.", editor);
		setActionDefinitionId("com.redhat.ceylon.eclipse.ui.action.rename");
	}
	
	@Override
	public AbstractRefactoring createRefactoring() {
	    return new RenameRefactoring(getTextEditor());
	}
	
	@Override
	public RefactoringWizard createWizard(AbstractRefactoring refactoring) {
	    return new RenameWizard((RenameRefactoring) refactoring);
	}
	
}
