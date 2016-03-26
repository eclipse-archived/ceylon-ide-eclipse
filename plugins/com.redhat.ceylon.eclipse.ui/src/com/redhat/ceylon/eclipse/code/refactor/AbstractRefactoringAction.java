package com.redhat.ceylon.eclipse.code.refactor;

import static com.redhat.ceylon.eclipse.util.EditorUtil.getActivePage;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ltk.ui.refactoring.RefactoringWizard;
import org.eclipse.ui.IEditorPart;

import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;

public abstract class AbstractRefactoringAction {
    
    final Refactoring refactoring;
    final IEditorPart editor;
    
    public AbstractRefactoringAction(IEditorPart editor) {
        this.editor = editor;
        refactoring = createRefactoring();
    }

    public boolean run() {
        for (IEditorPart ed: getActivePage().getDirtyEditors()) {
            if (ed instanceof CeylonEditor && ed!=editor) {
                String msg = "Please save other open Ceylon editors before refactoring";
                if (editor!=null && editor.isDirty()) {
                    msg += "\nYou don't need to save the current editor";
                }
                MessageDialog.openWarning(
                        editor.getEditorSite().getShell(), 
                        "Ceylon Refactoring Error", msg);
                return false;
            }
        }
        if (refactoring!=null && refactoring.getEnabled()) {
            return new RefactoringStarter()
                    .activate(createWizard(refactoring),
                            editor.getSite().getShell(),
                            refactoring.getName(), 4);
        }
        else {
            MessageDialog.openWarning(
                    editor.getEditorSite().getShell(), 
                    "Ceylon Refactoring Error", 
                    message());
            return false;
        }
    }
    
    public abstract Refactoring createRefactoring();
    public abstract RefactoringWizard createWizard(Refactoring refactoring);
    
    public abstract String message();
    
}