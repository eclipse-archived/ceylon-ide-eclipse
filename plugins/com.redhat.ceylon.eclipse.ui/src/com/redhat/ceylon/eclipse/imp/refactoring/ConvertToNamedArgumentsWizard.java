package com.redhat.ceylon.eclipse.imp.refactoring;

import org.eclipse.ltk.ui.refactoring.RefactoringWizard;

public class ConvertToNamedArgumentsWizard extends RefactoringWizard {
	public ConvertToNamedArgumentsWizard(ConvertToNamedArgumentsRefactoring refactoring) {
		super(refactoring, DIALOG_BASED_USER_INTERFACE
				| PREVIEW_EXPAND_FIRST_NODE);
		setDefaultPageTitle(refactoring.getName());
	}

	protected void addUserInputPages() {
		addPage(new ConvertToNamedArgumentsInputPage(getRefactoring().getName()));
	}

	public ConvertToNamedArgumentsRefactoring getConvertToNamedArgumentsRefactoring() {
		return (ConvertToNamedArgumentsRefactoring) getRefactoring();
	}
}
