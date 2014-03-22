package com.redhat.ceylon.eclipse.code.refactor;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ltk.ui.refactoring.RefactoringWizard;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.TextEditorAction;

import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.code.editor.EditorUtil;

public abstract class AbstractRefactoringAction extends TextEditorAction {
    
    final Refactoring refactoring;
    private final IEditorPart editor;
    
    public AbstractRefactoringAction(String prefix, IEditorPart editor) {
        super(RefactoringMessages.ResBundle, prefix, 
                editor instanceof ITextEditor ? (ITextEditor) editor : null);
        refactoring = createRefactoring();
        this.editor = editor;
        setEnabled(refactoring.isEnabled());
    }

    public void run() {
        for (IEditorPart ed: EditorUtil.getActivePage().getDirtyEditors()) {
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
            new RefactoringStarter().activate(createWizard(refactoring),
                            getTextEditor().getSite().getShell(),
                            refactoring.getName(), 4);
        }
        else {
            MessageDialog.openWarning(getTextEditor().getEditorSite().getShell(), 
                    "Ceylon Refactoring Error", message());
        }
    }
    
    public abstract Refactoring createRefactoring();
    public abstract RefactoringWizard createWizard(Refactoring refactoring);
    
    public abstract String message();
    
}