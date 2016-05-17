package com.redhat.ceylon.eclipse.code.imports;

import org.eclipse.jface.text.IDocument;

import com.redhat.ceylon.eclipse.code.parse.CeylonParseController;
import com.redhat.ceylon.eclipse.java2ceylon.ImportsJ2C;
import com.redhat.ceylon.ide.common.imports.AbstractImportsCleaner;
import com.redhat.ceylon.ide.common.imports.moduleImportUtil_;

public class importsJ2C implements ImportsJ2C {

    @Override
    public moduleImportUtil_ importUtil() {
        return moduleImportUtil_.get_();
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
