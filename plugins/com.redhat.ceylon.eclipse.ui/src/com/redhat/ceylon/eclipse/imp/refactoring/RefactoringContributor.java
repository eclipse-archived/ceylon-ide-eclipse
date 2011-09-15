package com.redhat.ceylon.eclipse.imp.refactoring;

import org.eclipse.imp.editor.UniversalEditor;
import org.eclipse.imp.services.IRefactoringContributor;
import org.eclipse.jface.action.IAction;

//Note: this class is obsolete and can be removed
public class RefactoringContributor implements IRefactoringContributor {
	
	public IAction[] getEditorRefactoringActions(UniversalEditor editor) {
		return getActions(editor);
	}

	public static IAction[] getActions(UniversalEditor editor) {
		return new IAction[] { 
				new RenameRefactoringAction(editor),
				new InlineRefactoringAction(editor),
				new ExtractValueRefactoringAction(editor),
				new ExtractFunctionRefactoringAction(editor),
				new ConvertToNamedArgumentsRefactoringAction(editor)
			};
	}
	
}
