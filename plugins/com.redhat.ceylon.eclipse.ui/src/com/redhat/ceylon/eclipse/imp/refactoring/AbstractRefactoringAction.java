package com.redhat.ceylon.eclipse.imp.refactoring;

import org.eclipse.imp.refactoring.RefactoringStarter;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ltk.ui.refactoring.RefactoringWizard;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.TextEditorAction;

import com.redhat.ceylon.eclipse.imp.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.imp.editor.Util;

abstract class AbstractRefactoringAction extends TextEditorAction {
    final AbstractRefactoring refactoring;
    final IEditorPart editor;
    
    AbstractRefactoringAction(String prefix, IEditorPart editor) {
        super(RefactoringMessages.ResBundle, prefix, 
                editor instanceof ITextEditor ? (ITextEditor) editor : null);
        refactoring = createRefactoring();
        this.editor = editor;
        setEnabled(refactoring.isEnabled());
        if (editor instanceof CeylonEditor) {
            IDocumentProvider dp = ((CeylonEditor)editor).getDocumentProvider();
            refactoring.document = dp.getDocument(editor.getEditorInput());
            refactoring.editor = (CeylonEditor) editor;
        }
}

    public void run() {
        for (IEditorPart ed: Util.getActivePage().getDirtyEditors()) {
            if (ed instanceof CeylonEditor && ed!=editor) {
                String msg = "Please save other open Ceylon editors before refactoring";
                if (editor!=null && editor.isDirty()) {
                    msg+="\nYou don't need to save the current editor";
                }
                MessageDialog.openWarning(getTextEditor().getEditorSite().getShell(), 
                        "Ceylon Refactoring Error", msg);
                return;
            }
        }
        if (refactoring.isEnabled()) {
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
