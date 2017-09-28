package org.eclipse.ceylon.ide.eclipse.java2ceylon;

import org.eclipse.jface.text.IDocument;

import org.eclipse.ceylon.ide.eclipse.code.parse.CeylonParseController;
import org.eclipse.ceylon.ide.common.imports.AbstractImportsCleaner;
import org.eclipse.ceylon.ide.common.imports.moduleImportUtil_;

public interface ImportsJ2C {

    moduleImportUtil_ importUtil();

    AbstractImportsCleaner importCleaner();

    void cleanImports(CeylonParseController parseController, IDocument doc);

}