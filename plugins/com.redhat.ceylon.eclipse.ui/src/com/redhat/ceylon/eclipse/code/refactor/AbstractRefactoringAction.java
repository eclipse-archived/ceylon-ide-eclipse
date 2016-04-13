package com.redhat.ceylon.eclipse.code.refactor;

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
        if (refactoring instanceof EclipseAbstractRefactoring) {
            return ((EclipseAbstractRefactoring) refactoring).getSaveMode();
        }
        else if (refactoring instanceof AbstractRefactoring) {
            return ((AbstractRefactoring) refactoring).getSaveMode();
        }
        else {
            return RefactoringSaveHelper.SAVE_NOTHING;
        }
    }
    
    public abstract Refactoring createRefactoring();
    public abstract RefactoringWizard createWizard(Refactoring refactoring);
    
    public abstract String message();
    
}