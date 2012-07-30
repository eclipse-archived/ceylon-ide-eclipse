package com.redhat.ceylon.eclipse.code.refactor;

import static com.redhat.ceylon.eclipse.code.editor.Util.getCurrentEditor;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.code.quickfix.MoveDeclarationProposal;

public class MoveDeclarationHandler extends AbstractHandler {
    
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
        MoveDeclarationProposal.moveDeclaration((CeylonEditor) getCurrentEditor());        
		return null;
	}

    @Override
    protected boolean isEnabled(CeylonEditor editor) {
        return MoveDeclarationProposal.canMoveDeclaration(editor);
    }
}
