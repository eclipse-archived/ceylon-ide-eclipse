package com.redhat.ceylon.eclipse.code.refactor;

import static com.redhat.ceylon.eclipse.code.editor.Util.getCurrentEditor;
import static com.redhat.ceylon.eclipse.code.refactor.RenameDeclarationLinkedMode.useLinkedMode;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.texteditor.ITextEditor;

import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;

public class RenameHandler extends AbstractHandler {
        
    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        ITextEditor editor = (ITextEditor) getCurrentEditor();
    	if (useLinkedMode() && editor instanceof CeylonEditor &&
    			!((CeylonEditor)editor).isInLinkedMode()) {
    		new RenameDeclarationLinkedMode((CeylonEditor) editor).start();
    	}
    	else {
    		new RenameRefactoringAction(editor).run();
    	}
        return null;
    }
            
}
