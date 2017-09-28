package org.eclipse.ceylon.ide.eclipse.code.refactor;

import static org.eclipse.ceylon.ide.eclipse.code.refactor.RenameLinkedMode.useLinkedMode;
import static org.eclipse.ceylon.ide.eclipse.util.EditorUtil.getCurrentEditor;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.texteditor.ITextEditor;

import org.eclipse.ceylon.ide.eclipse.code.editor.CeylonEditor;

public class EnterAliasHandler extends AbstractHandler {
        
    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        ITextEditor editor = (ITextEditor) getCurrentEditor();
        if (useLinkedMode() && editor instanceof CeylonEditor) {
            CeylonEditor ce = (CeylonEditor)editor;
            if (ce.isInLinkedMode()) {
                if (ce.getLinkedModeOwner() instanceof EnterAliasLinkedMode) {
                    EnterAliasLinkedMode current = 
                            (EnterAliasLinkedMode) ce.getLinkedModeOwner();
                    current.enterDialogMode();
                    current.openDialog();
                }
                else {
                    new EnterAliasRefactoringAction(editor).run();
                }
            }
            else {
                new EnterAliasLinkedMode(ce).start();
            }
        }
        else {
            new EnterAliasRefactoringAction(editor).run();
        }
        return null;
    }
            
}
