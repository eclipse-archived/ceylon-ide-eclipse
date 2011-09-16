package com.redhat.ceylon.eclipse.imp.refactoring;

import org.eclipse.imp.editor.UniversalEditor;
import org.eclipse.jface.action.Action;

import com.redhat.ceylon.eclipse.util.ActionWrapper;


public class RenameActionWrapper extends ActionWrapper {
    @Override
    public Action createAction(UniversalEditor editor) {
        return new RenameRefactoringAction(editor);
    }
}