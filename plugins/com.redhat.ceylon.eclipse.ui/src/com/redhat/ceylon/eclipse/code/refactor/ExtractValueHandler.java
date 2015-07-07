package com.redhat.ceylon.eclipse.code.refactor;

import static com.redhat.ceylon.eclipse.code.refactor.ExtractLinkedMode.useLinkedMode;
import static com.redhat.ceylon.eclipse.util.EditorUtil.getCurrentEditor;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.texteditor.ITextEditor;

import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;

public class ExtractValueHandler extends AbstractHandler {
        
    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        ITextEditor editor = (ITextEditor) getCurrentEditor();
        if (useLinkedMode() && editor instanceof CeylonEditor) {
            final CeylonEditor ce = (CeylonEditor) editor;
            if (ce.isInLinkedMode()) {
                Object owner = ce.getLinkedModeOwner();
                if (owner instanceof ExtractValueLinkedMode) {
                    ExtractValueLinkedMode current = 
                            (ExtractValueLinkedMode) 
                                owner;
                    current.enterDialogMode();
                    current.openDialog();
                }
                else {
                    new ExtractValueRefactoringAction(editor).run();
                }
            }
            else {
                Shell shell = editor.getSite().getShell();
                if (ce.getSelection().getLength()>0) {
                    new ExtractValueLinkedMode(ce).start();
                }
                else {
                    new SelectExpressionPopup(shell, 0, ce,
                            "Extract Value") {
                        ExtractLinkedMode linkedMode() {
                            return new ExtractValueLinkedMode(ce);
                        }
                    }
                    .open();
                }
            }
        }
        else {
            new ExtractValueRefactoringAction(editor).run();
        }
        return null;
    }
            
}
