package org.eclipse.ceylon.ide.eclipse.code.imports;

import org.eclipse.jface.text.IDocument;

import org.eclipse.ceylon.ide.eclipse.code.parse.CeylonParseController;
import org.eclipse.ceylon.ide.eclipse.java2ceylon.ImportsJ2C;
import org.eclipse.ceylon.ide.common.imports.AbstractImportsCleaner;
import org.eclipse.ceylon.ide.common.imports.moduleImportUtil_;

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
