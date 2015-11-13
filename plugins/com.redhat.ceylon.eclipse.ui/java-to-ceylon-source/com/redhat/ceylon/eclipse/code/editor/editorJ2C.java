package com.redhat.ceylon.eclipse.code.editor;

import org.eclipse.jface.text.IDocument;
import org.eclipse.ltk.core.refactoring.TextChange;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.TextEdit;

import com.redhat.ceylon.eclipse.java2ceylon.EditorJ2C;
import com.redhat.ceylon.ide.common.editor.AbstractFormatAction;

public class editorJ2C implements EditorJ2C {
    public AbstractFormatAction<IDocument,InsertEdit,TextEdit,TextChange> eclipseFormatAction() {
        return eclipseFormatAction_.get_();
    }
}
