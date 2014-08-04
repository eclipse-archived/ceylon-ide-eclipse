package com.redhat.ceylon.eclipse.code.refactor;

import static com.redhat.ceylon.eclipse.code.refactor.RenameLinkedMode.useLinkedMode;
import static com.redhat.ceylon.eclipse.util.EditorUtil.getCurrentEditor;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.texteditor.ITextEditor;

import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
//import org.eclipse.core.commands.AbstractHandler;

public class RenameAction extends AbstractHandler {
        
    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        ITextEditor editor = (ITextEditor) getCurrentEditor();
        if (useLinkedMode() && editor instanceof CeylonEditor) {
            new RenameLinkedMode((CeylonEditor) editor).start();
        }
        else {
            new RenameRefactoringAction(editor).run();
        }
        return null;
    }

    @Override
    protected boolean isEnabled(CeylonEditor editor) {
        return new RenameRefactoring(editor).isEnabled();
    }
            
}
