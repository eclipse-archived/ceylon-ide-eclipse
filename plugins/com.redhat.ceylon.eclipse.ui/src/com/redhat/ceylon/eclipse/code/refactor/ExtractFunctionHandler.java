package com.redhat.ceylon.eclipse.code.refactor;

import static com.redhat.ceylon.eclipse.code.refactor.ExtractLinkedMode.useLinkedMode;
import static com.redhat.ceylon.eclipse.util.EditorUtil.getCurrentEditor;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.texteditor.ITextEditor;

import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;

public class ExtractFunctionHandler extends AbstractHandler {
        
    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        ITextEditor editor = (ITextEditor) getCurrentEditor();
        if (useLinkedMode() && editor instanceof CeylonEditor) {
            final CeylonEditor ce = (CeylonEditor) editor;
            if (ce.isInLinkedMode()) {
                Object owner = ce.getLinkedModeOwner();
                if (owner instanceof ExtractFunctionLinkedMode) {
                    ExtractFunctionLinkedMode current = 
                            (ExtractFunctionLinkedMode) 
                                owner;
                    current.enterDialogMode();
                    current.openDialog();
                }
                else {
                    new ExtractFunctionRefactoringAction(editor).run();
                }
            }
            else {
                Shell shell = editor.getSite().getShell();
                if (ce.getSelection().getLength()>0) {
                    new ExtractFunctionLinkedMode(ce).start();
                }
                else {
                    new SelectExpressionPopup(shell, 0, ce,
                            "Extract Function") {
                        ExtractLinkedMode linkedMode() {
                            return new ExtractFunctionLinkedMode(ce);
                        }
                    }
                    .open();
                }
            }
        }
        else {
            new ExtractFunctionRefactoringAction(editor).run();
        }
        return null;
    }
            
}
