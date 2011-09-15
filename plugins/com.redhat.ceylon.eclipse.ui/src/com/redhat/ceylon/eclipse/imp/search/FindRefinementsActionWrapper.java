package com.redhat.ceylon.eclipse.imp.search;

import org.eclipse.imp.editor.UniversalEditor;

import com.redhat.ceylon.eclipse.util.ActionWrapper;

public class FindRefinementsActionWrapper extends ActionWrapper {
    
    @Override
    public FindRefinementsAction createAction(UniversalEditor editor) {
        return new FindRefinementsAction(editor);
    }
    
}