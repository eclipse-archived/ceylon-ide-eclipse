package com.redhat.ceylon.eclipse.imp.refactoring;

import org.eclipse.imp.editor.UniversalEditor;
import org.eclipse.imp.services.IRefactoringContributor;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.SWT;

public class RefactoringContributor implements IRefactoringContributor {
	
	public IAction[] getEditorRefactoringActions(UniversalEditor editor) {
		return getActions(editor);
	}

	public static IAction[] getActions(UniversalEditor editor) {
		return new IAction[] { 
				new RenameRefactoringAction(editor) {
					{
						setAccelerator(SWT.CONTROL | SWT.ALT | 'R');
					}
				},
				new InlineRefactoringAction(editor) {
					{
						setAccelerator(SWT.CONTROL | SWT.ALT | 'I');
					}
				},
				new ExtractValueRefactoringAction(editor) {
					{
						setAccelerator(SWT.CONTROL | SWT.ALT | 'V');
					}
				},
				new ExtractFunctionRefactoringAction(editor) {
					{
						setAccelerator(SWT.CONTROL | SWT.ALT | 'F');
					}
				}
			};
	}
	
}
