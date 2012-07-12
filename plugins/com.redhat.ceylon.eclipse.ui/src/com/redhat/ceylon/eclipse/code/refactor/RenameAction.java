package com.redhat.ceylon.eclipse.code.refactor;

import static com.redhat.ceylon.eclipse.code.editor.Util.getCurrentEditor;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.texteditor.ITextEditor;

import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
//import org.eclipse.core.commands.AbstractHandler;

public class RenameAction extends AbstractHandler {
        
    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        new RenameRefactoringAction((ITextEditor) getCurrentEditor()).run();
        return null;
    }

	@Override
	protected boolean isEnabled(CeylonEditor editor) {
        return new RenameRefactoringAction(editor).isEnabled();
	}
            
}
