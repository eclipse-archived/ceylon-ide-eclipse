package com.redhat.ceylon.eclipse.code.refactor;

import static com.redhat.ceylon.eclipse.code.editor.EditorUtil.getCurrentEditor;
import static com.redhat.ceylon.eclipse.code.move.MoveUtil.canMoveDeclaration;
import static com.redhat.ceylon.eclipse.code.move.MoveUtil.moveDeclaration;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;

public class MoveToNewUnitHandler extends AbstractHandler {
    
    @Override
    public Object execute(ExecutionEvent event) 
            throws ExecutionException {
        moveDeclaration((CeylonEditor) getCurrentEditor());        
        return null;
    }

    @Override
    protected boolean isEnabled(CeylonEditor editor) {
        return canMoveDeclaration(editor);
    }
}
