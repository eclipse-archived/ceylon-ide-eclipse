package com.redhat.ceylon.eclipse.imp.refactoring;

import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.TextEditorAction;
import org.eclipse.imp.refactoring.RefactoringStarter;

public class ConvertToNamedArgumentsRefactoringAction extends TextEditorAction {
	public ConvertToNamedArgumentsRefactoringAction(ITextEditor editor) {
		super(RefactoringMessages.ResBundle, "ConvertToNamedArguments.", editor);
	}

	public void run() {
		final ConvertToNamedArgumentsRefactoring refactoring = new ConvertToNamedArgumentsRefactoring(getTextEditor());
		new RefactoringStarter()
				.activate(refactoring, new ConvertToNamedArgumentsWizard(refactoring),
						getTextEditor().getSite().getShell(),
						refactoring.getName(), false);
	}
}
