package com.redhat.ceylon.eclipse.imp.refactoring;

import org.eclipse.imp.refactoring.RefactoringStarter;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ltk.ui.refactoring.RefactoringWizard;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.TextEditorAction;

abstract class AbstractRefactoringAction extends TextEditorAction {
    final AbstractRefactoring refactoring;
    
    AbstractRefactoringAction(String prefix, IEditorPart editor) {
		super(RefactoringMessages.ResBundle, prefix, 
		        editor instanceof ITextEditor ? (ITextEditor) editor : null);
		refactoring = createRefactoring();
		setEnabled(refactoring.isEnabled());
	}

	public void run() {
	    if (refactoring.isEnabled()) {
    		new RefactoringStarter().activate(refactoring, createWizard(refactoring),
    						getTextEditor().getSite().getShell(),
    						refactoring.getName(), false);
	    }
	    else {
            MessageDialog.openWarning(getTextEditor().getEditorSite().getShell(), 
                    "Ceylon Find Error", "No declaration name selected");
	    }
	}
	
	abstract AbstractRefactoring createRefactoring();
	abstract RefactoringWizard createWizard(AbstractRefactoring refactoring);
}
