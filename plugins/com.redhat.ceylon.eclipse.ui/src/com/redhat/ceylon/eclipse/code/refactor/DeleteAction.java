package org.eclipse.ceylon.ide.eclipse.code.refactor;

import static org.eclipse.ceylon.ide.eclipse.util.EditorUtil.getCurrentEditor;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.texteditor.ITextEditor;

import org.eclipse.ceylon.ide.eclipse.code.editor.CeylonEditor;
//import org.eclipse.core.commands.AbstractHandler;

public class DeleteAction extends AbstractHandler {
    
    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        ITextEditor editor = (ITextEditor) getCurrentEditor();
        new DeleteRefactoringAction(editor).run();
        return null;
    }
    
    @Override
    protected boolean isEnabled(CeylonEditor editor) {
        return new DeleteRefactoring(editor).getEnabled();
    }
    
}
