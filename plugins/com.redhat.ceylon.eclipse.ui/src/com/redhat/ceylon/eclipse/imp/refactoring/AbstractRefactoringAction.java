package com.redhat.ceylon.eclipse.imp.refactoring;

import org.eclipse.imp.refactoring.RefactoringStarter;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ltk.ui.refactoring.RefactoringWizard;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.TextEditorAction;

abstract class AbstractRefactoringAction extends TextEditorAction {
    final AbstractRefactoring refactoring;
    final IEditorPart editor;
    
    AbstractRefactoringAction(String prefix, IEditorPart editor) {
		super(RefactoringMessages.ResBundle, prefix, 
		        editor instanceof ITextEditor ? (ITextEditor) editor : null);
		refactoring = createRefactoring();
		this.editor = editor;
		setEnabled(refactoring.isEnabled() && !editor.isDirty());
	}

	public void run() {
	    if (editor.isDirty()) {
            MessageDialog.openWarning(getTextEditor().getEditorSite().getShell(), 
                    "Ceylon Refactoring Error", 
                    "Please save current editor before refactoring");
	    }
	    else if (refactoring.isEnabled()) {
    		new RefactoringStarter().activate(refactoring, createWizard(refactoring),
    						getTextEditor().getSite().getShell(),
    						refactoring.getName(), false);
	    }
	    else {
            MessageDialog.openWarning(getTextEditor().getEditorSite().getShell(), 
                    "Ceylon Refactoring Error", message());
	    }
	}
	
	abstract AbstractRefactoring createRefactoring();
	abstract RefactoringWizard createWizard(AbstractRefactoring refactoring);
	
	abstract String message();
	
}
