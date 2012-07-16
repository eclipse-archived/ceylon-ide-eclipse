package com.redhat.ceylon.eclipse.code.refactor;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.ui.refactoring.RefactoringWizard;
import org.eclipse.ltk.ui.refactoring.RefactoringWizardOpenOperation;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.TextEditorAction;

import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.code.editor.Util;

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

/**
 * Simple class to wrap the processing flow for user-initiated refactorings. Copied from a similar class in JDT/UI.
 * 
 * @author rfuhrer
 */
class RefactoringStarter {
    // TODO re-enable support for the "Save Helper", which had too many claws in the JDT/UI to excise conveniently.
    // private RefactoringSaveHelper fSaveHelper= new RefactoringSaveHelper();
    private RefactoringStatus fStatus;

    public void activate(Refactoring refactoring, RefactoringWizard wizard, Shell parent, String dialogTitle,
            boolean mustSaveEditors) {
        if (!canActivate(mustSaveEditors, parent))
            return;
        try {
            RefactoringWizardOpenOperation op= new RefactoringWizardOpenOperation(wizard);
            int result= op.run(parent, dialogTitle);
            fStatus= op.getInitialConditionCheckingStatus();
            if (result == IDialogConstants.CANCEL_ID
                    || result == RefactoringWizardOpenOperation.INITIAL_CONDITION_CHECKING_FAILED) {
                /* fSaveHelper.triggerBuild() */
            }
        } catch (InterruptedException e) {
            // do nothing. User action got cancelled
        }
    }

    public RefactoringStatus getInitialConditionCheckingStatus() {
        return fStatus;
    }

    private boolean canActivate(boolean mustSaveEditors, Shell shell) {
        // return !mustSaveEditors || fSaveHelper.saveEditors(shell);
        return true;
    }
}