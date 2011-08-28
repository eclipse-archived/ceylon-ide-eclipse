package com.redhat.ceylon.eclipse.imp.refactoring;

import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.TextEditorAction;
import org.eclipse.imp.refactoring.RefactoringStarter;

public class ExtractFunctionRefactoringAction extends TextEditorAction {
	public ExtractFunctionRefactoringAction(ITextEditor editor) {
		super(RefactoringMessages.ResBundle, "ExtractFunction.", editor);
	}

	public void run() {
		final ExtractFunctionRefactoring refactoring = new ExtractFunctionRefactoring(getTextEditor());
		new RefactoringStarter()
				.activate(refactoring, new ExtractFunctionWizard(refactoring),
						getTextEditor().getSite().getShell(),
						refactoring.getName(), false);
	}
}
