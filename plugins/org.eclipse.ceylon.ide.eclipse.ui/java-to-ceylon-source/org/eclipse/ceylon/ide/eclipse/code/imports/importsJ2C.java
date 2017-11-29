/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
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
