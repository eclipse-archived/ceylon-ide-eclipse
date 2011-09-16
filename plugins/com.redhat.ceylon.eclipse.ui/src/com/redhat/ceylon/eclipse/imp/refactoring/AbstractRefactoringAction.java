package com.redhat.ceylon.eclipse.imp.refactoring;

import org.eclipse.imp.refactoring.RefactoringStarter;
import org.eclipse.ltk.ui.refactoring.RefactoringWizard;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.TextEditorAction;

abstract class AbstractRefactoringAction extends TextEditorAction {
    final AbstractRefactoring refactoring;
    
    AbstractRefactoringAction(String prefix, ITextEditor editor) {
		super(RefactoringMessages.ResBundle, prefix, editor);
		refactoring = createRefactoring();
		setEnabled(refactoring.isEnabled());
	}

	public void run() {
		new RefactoringStarter().activate(refactoring, createWizard(refactoring),
						getTextEditor().getSite().getShell(),
						refactoring.getName(), false);
	}
	
	abstract AbstractRefactoring createRefactoring();
	abstract RefactoringWizard createWizard(AbstractRefactoring refactoring);
}
