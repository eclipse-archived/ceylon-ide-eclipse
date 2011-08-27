package com.redhat.ceylon.eclipse.imp.refactoring;

import org.eclipse.imp.editor.UniversalEditor;
import org.eclipse.imp.services.IRefactoringContributor;
import org.eclipse.jface.action.IAction;

public class RefactoringContributor implements IRefactoringContributor {
	public IAction[] getEditorRefactoringActions(UniversalEditor editor) {
		return new IAction[] { 
				new RenameRefactoringAction(editor),
				new ExtractLocalRefactoringAction(editor)
			};
	}
}
