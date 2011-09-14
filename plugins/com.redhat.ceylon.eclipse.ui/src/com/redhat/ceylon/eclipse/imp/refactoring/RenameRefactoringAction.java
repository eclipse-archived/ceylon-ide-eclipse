package com.redhat.ceylon.eclipse.imp.refactoring;

import org.eclipse.ltk.ui.refactoring.RefactoringWizard;
import org.eclipse.ui.texteditor.ITextEditor;

public class RenameRefactoringAction extends AbstractRefactoringAction {
	public RenameRefactoringAction(ITextEditor editor) {
		super("Rename.", editor);
	}
	
	@Override
	public AbstractRefactoring getRefactoring() {
	    return new RenameRefactoring(getTextEditor());
	}
	
	@Override
	public RefactoringWizard getWizard(AbstractRefactoring refactoring) {
	    return new RenameWizard((RenameRefactoring) refactoring);
	}
	
}
