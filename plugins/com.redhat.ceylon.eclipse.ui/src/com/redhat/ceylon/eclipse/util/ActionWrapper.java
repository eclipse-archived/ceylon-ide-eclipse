package com.redhat.ceylon.eclipse.util;

import org.eclipse.imp.editor.UniversalEditor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;

public abstract class ActionWrapper implements IEditorActionDelegate {
    //private Action delegate;
    private UniversalEditor editor;
    
    @Override
    public void run(IAction action) {
        //if (delegate!=null) delegate.run();
        if (editor!=null) createAction(editor).run();
    }
    
    @Override
    public void selectionChanged(IAction action, ISelection selection) {
        if (editor==null || !(selection instanceof ITextSelection)) {
            action.setEnabled(false);
        }
        else {
            //delegate = createAction(editor);
            //action.setEnabled(delegate.isEnabled());
            action.setEnabled(true);
        }
    }
    
    @Override
    public void setActiveEditor(IAction action, IEditorPart targetEditor) {
        if (targetEditor!=editor) {
            if (targetEditor instanceof UniversalEditor) {
                editor = (UniversalEditor) targetEditor;
                //delegate = createAction(editor);
                //action.setEnabled(delegate.isEnabled());
                action.setEnabled(true);
            }
            else {
                //delegate = null;
                editor = null;
                action.setEnabled(false);
            }
        }
    }

    public abstract Action createAction(UniversalEditor editor);
}