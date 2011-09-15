package com.redhat.ceylon.eclipse.util;

import org.eclipse.imp.editor.UniversalEditor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;

public abstract class ActionWrapper implements IEditorActionDelegate {
    private Action delegate;
    private UniversalEditor editor;
    
    @Override
    public void run(IAction action) {
        if (delegate!=null) delegate.run();
    }
    
    @Override
    public void selectionChanged(IAction action, ISelection selection) {
        if (editor!=null) {
            delegate = createAction(editor);
            action.setEnabled(delegate.isEnabled());
        }
    }
    
    @Override
    public void setActiveEditor(IAction action, IEditorPart targetEditor) {
        if (targetEditor instanceof UniversalEditor) {
            editor = (UniversalEditor) targetEditor;
            delegate = createAction(editor);
            action.setEnabled(delegate.isEnabled());
        }
        else {
            delegate = null;
            editor = null;
            action.setEnabled(false);
        }
    }

    public abstract Action createAction(UniversalEditor editor);
}