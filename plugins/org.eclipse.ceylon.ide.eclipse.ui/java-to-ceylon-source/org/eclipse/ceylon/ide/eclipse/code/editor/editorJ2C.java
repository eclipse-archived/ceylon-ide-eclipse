package org.eclipse.ceylon.ide.eclipse.code.editor;

import org.eclipse.jface.action.Action;

import org.eclipse.ceylon.ide.eclipse.java2ceylon.EditorJ2C;
import org.eclipse.ceylon.ide.common.editor.formatAction_;

public class editorJ2C implements EditorJ2C {
    @Override
    public formatAction_ eclipseFormatAction() {
        return formatAction_.get_();
    }
    
    @Override
    public Action newEclipseTerminateStatementAction(CeylonEditor editor) {
        return new EclipseTerminateStatementAction(editor);
    }

}
