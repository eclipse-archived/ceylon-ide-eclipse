package com.redhat.ceylon.eclipse.imp.refactoring;

import org.eclipse.imp.refactoring.RefactoringStarter;
import org.eclipse.ltk.ui.refactoring.RefactoringWizard;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.TextEditorAction;

abstract class AbstractRefactoringAction extends TextEditorAction {
	AbstractRefactoringAction(String prefix, ITextEditor editor) {
		super(RefactoringMessages.ResBundle, prefix, editor);
	}

	public void run() {
		final AbstractRefactoring refactoring = getRefactoring();
		new RefactoringStarter().activate(refactoring, getWizard(refactoring),
						getTextEditor().getSite().getShell(),
						refactoring.getName(), false);
	}
	
	abstract AbstractRefactoring getRefactoring();
	abstract RefactoringWizard getWizard(AbstractRefactoring refactoring);
}
