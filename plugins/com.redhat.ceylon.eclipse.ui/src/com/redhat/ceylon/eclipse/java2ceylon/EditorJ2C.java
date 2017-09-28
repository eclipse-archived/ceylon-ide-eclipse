package org.eclipse.ceylon.ide.eclipse.java2ceylon;

import org.eclipse.jface.action.Action;

import org.eclipse.ceylon.ide.eclipse.code.editor.CeylonEditor;
import org.eclipse.ceylon.ide.common.editor.formatAction_;

public interface EditorJ2C {
    formatAction_ eclipseFormatAction();

    Action newEclipseTerminateStatementAction(CeylonEditor editor);
}
