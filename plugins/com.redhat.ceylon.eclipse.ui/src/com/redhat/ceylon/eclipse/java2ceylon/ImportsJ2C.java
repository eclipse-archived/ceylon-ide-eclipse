package com.redhat.ceylon.eclipse.java2ceylon;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ltk.core.refactoring.TextChange;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.TextEdit;

import com.redhat.ceylon.eclipse.code.parse.CeylonParseController;
import com.redhat.ceylon.ide.common.imports.AbstractImportsCleaner;
import com.redhat.ceylon.ide.common.imports.AbstractModuleImportUtil;

public interface ImportsJ2C {

    AbstractModuleImportUtil<IFile, IProject, IDocument, InsertEdit, TextEdit, TextChange> importUtil();

    AbstractImportsCleaner importCleaner();

    void cleanImports(CeylonParseController parseController, IDocument doc);

}