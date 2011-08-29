package com.redhat.ceylon.eclipse.imp.refactoring;

import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.TextEditorAction;
import org.eclipse.imp.refactoring.RefactoringStarter;

public class ExtractValueRefactoringAction extends TextEditorAction {
	public ExtractValueRefactoringAction(ITextEditor editor) {
		super(RefactoringMessages.ResBundle, "ExtractValue.", editor);
	}

	public void run() {
		final ExtractValueRefactoring refactoring = new ExtractValueRefactoring(getTextEditor());
		new RefactoringStarter()
				.activate(refactoring, new ExtractValueWizard(refactoring),
						getTextEditor().getSite().getShell(),
						refactoring.getName(), false);
	}
}
