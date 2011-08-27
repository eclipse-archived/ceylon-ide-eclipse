package com.redhat.ceylon.eclipse.imp.refactoring;

import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.TextEditorAction;
import org.eclipse.imp.refactoring.RefactoringStarter;

public class ExtractLocalRefactoringAction extends TextEditorAction {
	public ExtractLocalRefactoringAction(ITextEditor editor) {
		super(RefactoringMessages.ResBundle, "ExtractLocal.", editor);
	}

	public void run() {
		final ExtractLocalRefactoring refactoring = new ExtractLocalRefactoring(getTextEditor());
		new RefactoringStarter()
				.activate(refactoring, new ExtractLocalWizard(refactoring),
						getTextEditor().getSite().getShell(),
						refactoring.getName(), false);
	}
}
