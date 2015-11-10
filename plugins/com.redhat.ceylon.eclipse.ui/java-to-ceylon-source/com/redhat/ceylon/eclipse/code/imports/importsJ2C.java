package com.redhat.ceylon.eclipse.code.imports;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ltk.core.refactoring.TextChange;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.TextEdit;

import com.redhat.ceylon.eclipse.code.parse.CeylonParseController;
import com.redhat.ceylon.ide.common.imports.AbstractImportsCleaner;
import com.redhat.ceylon.ide.common.imports.AbstractModuleImportUtil;

public class importsJ2C {

    public static AbstractModuleImportUtil<IFile, IProject, IDocument, InsertEdit, TextEdit, TextChange> importUtil() {
        return eclipseModuleImportUtils_.get_();
    }
    
    public static AbstractImportsCleaner<IDocument, InsertEdit, TextEdit, TextChange> importCleaner() {
        return eclipseImportsCleaner_.get_();
    }

    public static void cleanImports(CeylonParseController parseController,
            IDocument doc) {
        eclipseImportsCleaner_.get_().cleanEditorImports(parseController, doc);
    }
}
