package com.redhat.ceylon.eclipse.code.imports;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ltk.core.refactoring.TextChange;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.TextEdit;

import com.redhat.ceylon.eclipse.code.parse.CeylonParseController;
import com.redhat.ceylon.eclipse.java2ceylon.ImportsJ2C;
import com.redhat.ceylon.ide.common.imports.AbstractImportsCleaner;
import com.redhat.ceylon.ide.common.imports.AbstractModuleImportUtil;

public class importsJ2C implements ImportsJ2C {

    @Override
    public AbstractModuleImportUtil<IFile, IProject, IDocument, InsertEdit, TextEdit, TextChange> importUtil() {
        return eclipseModuleImportUtils_.get_();
    }
    
    @Override
    public AbstractImportsCleaner importCleaner() {
        return eclipseImportsCleaner_.get_();
    }

    @Override
    public void cleanImports(CeylonParseController parseController,
            IDocument doc) {
        eclipseImportsCleaner_.get_().cleanEditorImports(parseController, doc);
    }
}
