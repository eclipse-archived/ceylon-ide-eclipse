package com.redhat.ceylon.eclipse.imp.search;

import org.eclipse.imp.editor.UniversalEditor;

import com.redhat.ceylon.eclipse.util.ActionWrapper;

public class FindSubtypesActionWrapper extends ActionWrapper {
    
    @Override
    public FindSubtypesAction createAction(UniversalEditor editor) {
        return new FindSubtypesAction(editor);
    }
    
}