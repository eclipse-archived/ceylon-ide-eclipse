package com.redhat.ceylon.eclipse.code.imports;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ltk.core.refactoring.TextChange;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.TextEdit;

import com.redhat.ceylon.ide.common.imports.AbstractModuleImportUtil;

public class importsJ2C {

    public static AbstractModuleImportUtil<IFile, IProject, IDocument, InsertEdit, TextEdit, TextChange> importUtil() {
        return eclipseModuleImportUtils_.get_();
    }
}
