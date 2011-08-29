package com.redhat.ceylon.eclipse.imp.refactoring;

import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.TextEditorAction;
import org.eclipse.imp.refactoring.RefactoringStarter;

public class RenameRefactoringAction extends TextEditorAction {
	public RenameRefactoringAction(ITextEditor editor) {
		super(RefactoringMessages.ResBundle, "Rename.", editor);
	}

	public void run() {
		final RenameRefactoring refactoring = new RenameRefactoring(getTextEditor());
		new RefactoringStarter()
				.activate(refactoring, new RenameWizard(refactoring),
						getTextEditor().getSite().getShell(),
						refactoring.getName(), false);
	}
}
