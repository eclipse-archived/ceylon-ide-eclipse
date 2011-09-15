package com.redhat.ceylon.eclipse.imp.open;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

public class OpenDeclarationActionWrapper implements IWorkbenchWindowActionDelegate {

    private IWorkbenchWindow window;

    public void init(IWorkbenchWindow window) {
        this.window = window;
    }

    public void selectionChanged(IAction action, ISelection selection) {
        // do nothing since the action isn't selection dependent.
    }

    public void dispose() {
        window = null;
    }
    
    public void run(IAction action) {
        new OpenDeclarationAction(getEditor()).run();
    }

    private IEditorPart getEditor() {
        IEditorPart activeEditor;
        if (window == null || window.getActivePage() == null) {
            activeEditor = null;
        }
        else {
            activeEditor = window.getActivePage().getActiveEditor();
        }
        return activeEditor;
    }
    
}