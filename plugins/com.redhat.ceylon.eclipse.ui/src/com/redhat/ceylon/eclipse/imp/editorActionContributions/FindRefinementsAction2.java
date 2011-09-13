package com.redhat.ceylon.eclipse.imp.editorActionContributions;

import org.eclipse.imp.editor.UniversalEditor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;

public class FindRefinementsAction2 implements IEditorActionDelegate {
    private UniversalEditor editor;
    
    @Override
    public void run(IAction action) {
        if (editor!=null) run();
    }
    
    @Override
    public void selectionChanged(IAction action, ISelection selection) {}
    
    @Override
    public void setActiveEditor(IAction action, IEditorPart targetEditor) {
        if (targetEditor instanceof UniversalEditor) {
            editor = (UniversalEditor) targetEditor;
        }
    }
    
    public void run() {
        new FindRefinementsAction(editor).run();
    }
}