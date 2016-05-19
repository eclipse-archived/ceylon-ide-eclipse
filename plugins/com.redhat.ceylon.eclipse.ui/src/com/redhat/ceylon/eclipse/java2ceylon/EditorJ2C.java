package com.redhat.ceylon.eclipse.java2ceylon;

import org.eclipse.jface.action.Action;

import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.ide.common.editor.formatAction_;

public interface EditorJ2C {
    formatAction_ eclipseFormatAction();

    Action newEclipseTerminateStatementAction(CeylonEditor editor);
}
