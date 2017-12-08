/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 1.0 which is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
import org.eclipse.ceylon.compiler.typechecker.tree {
    Tree
}
import org.eclipse.ceylon.ide.eclipse.code.correct {
    EclipseDocument
}
import org.eclipse.ceylon.ide.eclipse.code.parse {
    CeylonParseController
}
import org.eclipse.ceylon.ide.eclipse.util {
    EditorUtil
}
import org.eclipse.ceylon.ide.common.editor {
    AbstractTerminateStatementAction
}

import java.util {
    List
}

import org.antlr.runtime {
    CommonToken
}
import org.eclipse.core.runtime {
    NullProgressMonitor
}
import org.eclipse.jface.action {
    Action
}

class EclipseTerminateStatementAction(CeylonEditor editor) extends Action(null) {
    
    function createHandler() {
        return object extends AbstractTerminateStatementAction<EclipseDocument>() {
            shared actual [Tree.CompilationUnit, List<CommonToken>] parse(EclipseDocument doc) {
                value cpc = CeylonParseController();
                cpc.initialize(editor.parseController.path,
                    editor.parseController.project, null);
                cpc.parseAndTypecheck(doc.document,
                    0, // don't wait for the source model since we don't even need it.
                    NullProgressMonitor(), null);
                return [cpc.parsedRootNode, cpc.tokens];
            }            
        };
    }

    shared actual void run() {
        value ts = EditorUtil.getSelection(editor);
        String before = editor.selectionText;
        value doc = EclipseDocument(editor.ceylonSourceViewer.document);
        
        createHandler().terminateStatement(doc, ts.endLine);

        if (editor.selectionText != before) {
            //if the caret was at the end of the line, 
            //and a semi was added, it winds up selected
            //so move the caret after the semi
            value selection = editor.selection;
            Integer start = selection.offset + 1;
            editor.ceylonSourceViewer.setSelectedRange(start, 0);
        }
        
        editor.scheduleParsing(false);
    }
}
