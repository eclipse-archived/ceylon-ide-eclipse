package com.redhat.ceylon.eclipse.code.refactor;

import static com.redhat.ceylon.eclipse.code.refactor.ExtractLinkedMode.useLinkedMode;
import static com.redhat.ceylon.eclipse.util.EditorUtil.getCurrentEditor;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.texteditor.ITextEditor;

import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;

public class ExtractParameterHandler extends AbstractHandler {
        
    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        ITextEditor editor = (ITextEditor) getCurrentEditor();
        if (useLinkedMode() && editor instanceof CeylonEditor) {
            CeylonEditor ce = (CeylonEditor)editor;
            if (ce.isInLinkedMode()) {
                if (ce.getLinkedModeOwner() instanceof ExtractParameterLinkedMode) {
                    ExtractParameterLinkedMode current = 
                            (ExtractParameterLinkedMode) ce.getLinkedModeOwner();
                    current.enterDialogMode();
                    current.openDialog();
                }
                else {
                    new ExtractParameterRefactoringAction(editor).run();
                }
            }
            else {
                new ExtractParameterLinkedMode(ce).start();
            }
        }
        else {
            new ExtractParameterRefactoringAction(editor).run();
        }
        return null;
    }
            
}
