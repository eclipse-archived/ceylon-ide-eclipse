package com.redhat.ceylon.eclipse.imp.refactoring;

import org.eclipse.imp.editor.UniversalEditor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.texteditor.ITextEditor;

abstract class RefactoringAction implements IEditorActionDelegate {
    private ITextEditor editor;
    
    ITextEditor getEditor() {
        return editor;
    }
    
    @Override
    public void run(IAction action) {
        if (editor!=null) run();
    }
    
    @Override
    public void selectionChanged(IAction action, ISelection selection) {}
    
    @Override
    public void setActiveEditor(IAction action, IEditorPart targetEditor) {
        if (targetEditor instanceof UniversalEditor) {
            editor = (ITextEditor) targetEditor;
        }
    }
    
    abstract void run();
}