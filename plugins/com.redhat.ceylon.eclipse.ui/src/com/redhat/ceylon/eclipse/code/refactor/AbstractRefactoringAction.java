package com.redhat.ceylon.eclipse.code.refactor;

import static com.redhat.ceylon.eclipse.code.refactor.RefactoringSaveHelper.SAVE_CEYLON_REFACTORING;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ltk.ui.refactoring.RefactoringWizard;
import org.eclipse.ui.IEditorPart;

public abstract class AbstractRefactoringAction {
    
    final Refactoring refactoring;
    final IEditorPart editor;
    
    public AbstractRefactoringAction(IEditorPart editor) {
        this.editor = editor;
        refactoring = createRefactoring();
    }

    public boolean run() {
        if (refactoring!=null && refactoring.getEnabled()) {
            return new RefactoringStarter()
                    .activate(createWizard(refactoring),
                            editor.getSite().getShell(),
                            refactoring.getName(), 
                            getSaveMode());
        }
        else {
            MessageDialog.openWarning(
                    editor.getEditorSite().getShell(), 
                    "Ceylon Refactoring Error", 
                    message());
            return false;
        }
    }

    int getSaveMode() {
        return SAVE_CEYLON_REFACTORING;
    }
    
    public abstract Refactoring createRefactoring();
    public abstract RefactoringWizard createWizard(Refactoring refactoring);
    
    public abstract String message();
    
}