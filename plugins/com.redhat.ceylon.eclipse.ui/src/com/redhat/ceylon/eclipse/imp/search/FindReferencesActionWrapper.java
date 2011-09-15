package com.redhat.ceylon.eclipse.imp.search;

import org.eclipse.imp.editor.UniversalEditor;

import com.redhat.ceylon.eclipse.util.ActionWrapper;

public class FindReferencesActionWrapper extends ActionWrapper {
    
    @Override
    public FindReferencesAction createAction(UniversalEditor editor) {
        return new FindReferencesAction(editor);
    }
    
}