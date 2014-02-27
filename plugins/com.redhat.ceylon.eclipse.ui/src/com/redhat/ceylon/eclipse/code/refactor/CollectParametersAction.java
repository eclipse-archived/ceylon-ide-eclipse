package com.redhat.ceylon.eclipse.code.refactor;

import static com.redhat.ceylon.eclipse.code.editor.EditorUtil.getCurrentEditor;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.texteditor.ITextEditor;

import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;

public class CollectParametersAction extends AbstractHandler {
        
    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        ITextEditor editor = (ITextEditor) getCurrentEditor();
        new CollectParametersRefactoringAction(editor).run();
        return null;
    }

    @Override
    protected boolean isEnabled(CeylonEditor editor) {
        return new CollectParametersRefactoringAction(editor).isEnabled();
    }
            
}
