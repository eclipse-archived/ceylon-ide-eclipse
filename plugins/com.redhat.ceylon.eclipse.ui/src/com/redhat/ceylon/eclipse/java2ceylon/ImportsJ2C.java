package com.redhat.ceylon.eclipse.java2ceylon;

import org.eclipse.jface.text.IDocument;

import com.redhat.ceylon.eclipse.code.parse.CeylonParseController;
import com.redhat.ceylon.ide.common.imports.AbstractImportsCleaner;
import com.redhat.ceylon.ide.common.imports.moduleImportUtil_;

public interface ImportsJ2C {

    moduleImportUtil_ importUtil();

    AbstractImportsCleaner importCleaner();

    void cleanImports(CeylonParseController parseController, IDocument doc);

}