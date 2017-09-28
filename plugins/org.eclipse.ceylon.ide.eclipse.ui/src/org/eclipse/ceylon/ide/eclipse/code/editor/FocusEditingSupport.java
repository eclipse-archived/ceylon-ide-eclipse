package org.eclipse.ceylon.ide.eclipse.code.editor;

import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IEditingSupport;
import org.eclipse.jface.text.IRegion;
import org.eclipse.swt.widgets.Shell;

public class FocusEditingSupport implements IEditingSupport {
    private final CeylonEditor editor;

    public FocusEditingSupport(CeylonEditor editor) {
        this.editor = editor;
    }

    public boolean ownsFocusShell() {
        Shell editorShell = editor.getSite().getShell();
        Shell activeShell = editorShell.getDisplay().getActiveShell();
        return editorShell == activeShell;
    }

    public boolean isOriginator(DocumentEvent event, IRegion subjectRegion) {
        return false; //leave on external modification outside positions
    }
}