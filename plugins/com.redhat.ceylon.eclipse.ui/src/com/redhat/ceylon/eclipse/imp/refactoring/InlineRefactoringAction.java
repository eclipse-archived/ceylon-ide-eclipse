package com.redhat.ceylon.eclipse.imp.refactoring;

import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.TextEditorAction;
import org.eclipse.imp.refactoring.RefactoringStarter;

public class InlineRefactoringAction extends TextEditorAction {
	public InlineRefactoringAction(ITextEditor editor) {
		super(RefactoringMessages.ResBundle, "Inline.", editor);
	}

	public void run() {
		final InlineRefactoring refactoring = new InlineRefactoring(getTextEditor());
		new RefactoringStarter()
				.activate(refactoring, new InlineWizard(refactoring),
						getTextEditor().getSite().getShell(),
						refactoring.getName(), false);
	}
}
