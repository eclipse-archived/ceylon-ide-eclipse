package com.redhat.ceylon.eclipse.imp.refactoring;

import org.eclipse.ltk.ui.refactoring.RefactoringWizard;

public class ExtractLocalWizard extends RefactoringWizard {
	public ExtractLocalWizard(ExtractLocalRefactoring refactoring) {
		super(refactoring, DIALOG_BASED_USER_INTERFACE
				| PREVIEW_EXPAND_FIRST_NODE);
		setDefaultPageTitle(refactoring.getName());
	}

	protected void addUserInputPages() {
		addPage(new ExtractLocalInputPage(getRefactoring().getName()));
	}

	public ExtractLocalRefactoring getExtractLocalRefactoring() {
		return (ExtractLocalRefactoring) getRefactoring();
	}
}
