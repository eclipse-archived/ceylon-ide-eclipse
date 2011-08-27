package imp.ceylon.refactoring;

import org.eclipse.ltk.ui.refactoring.RefactoringWizard;

public class RenameWizard extends RefactoringWizard {
	public RenameWizard(RenameRefactoring refactoring) {
		super(refactoring, DIALOG_BASED_USER_INTERFACE
				| PREVIEW_EXPAND_FIRST_NODE);
		setDefaultPageTitle(refactoring.getName());
	}

	protected void addUserInputPages() {
		RenameInputPage page = new RenameInputPage(getRefactoring()
				.getName());

		addPage(page);
	}

	public RenameRefactoring getInsertCrudRefactoring() {
		return (RenameRefactoring) getRefactoring();
	}
}
