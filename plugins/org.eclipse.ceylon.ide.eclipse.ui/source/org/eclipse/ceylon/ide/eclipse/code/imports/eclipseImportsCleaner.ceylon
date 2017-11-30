/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 1.0 which is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
import ceylon.interop.java {
    JavaList
}

import org.eclipse.ceylon.ide.eclipse.code.correct {
    EclipseDocument
}
import org.eclipse.ceylon.ide.eclipse.code.editor {
    CeylonEditor
}
import org.eclipse.ceylon.ide.eclipse.code.parse {
    CeylonParseController
}
import org.eclipse.ceylon.ide.eclipse.util {
    EditorUtil
}
import org.eclipse.ceylon.ide.common.imports {
    AbstractImportsCleaner
}
import org.eclipse.ceylon.model.typechecker.model {
    Declaration
}

import org.eclipse.jface.text {
    IDocument
}
import org.eclipse.jface.window {
    Window
}

object eclipseImportsCleaner satisfies AbstractImportsCleaner {
    
    shared actual Declaration? select(List<Declaration> proposals) {
        assert(is CeylonEditor editor = EditorUtil.currentEditor);
        value shell = editor.site.shell;
        value fid = ImportSelectionDialog(shell, JavaList(proposals));
        
        if (fid.open() == Window.\iOK) {
            assert(is Declaration res = fid.firstResult);
            return res;
        }
        
        return null;
    }
    
    shared void cleanEditorImports(CeylonParseController cpc, IDocument doc) {
         if (!CleanImportsHandler.isEnabled(cpc)) {
             return;
         }
         
         value commonDoc = EclipseDocument(doc);
         cleanImports(cpc.typecheckedRootNode, commonDoc);
    }
}
