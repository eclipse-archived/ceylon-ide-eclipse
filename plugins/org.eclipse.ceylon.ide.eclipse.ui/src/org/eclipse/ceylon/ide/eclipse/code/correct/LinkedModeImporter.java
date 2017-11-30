/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 1.0 which is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.code.correct;

import static org.eclipse.ceylon.ide.eclipse.code.correct.ImportProposals.importProposals;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.link.ILinkedModeListener;
import org.eclipse.jface.text.link.LinkedModeModel;
import org.eclipse.swt.widgets.Display;

import org.eclipse.ceylon.compiler.typechecker.tree.Tree;
import org.eclipse.ceylon.ide.eclipse.code.editor.CeylonEditor;
import org.eclipse.ceylon.ide.eclipse.platform.platformJ2C;
import org.eclipse.ceylon.ide.common.platform.TextChange;
import org.eclipse.ceylon.model.typechecker.model.Declaration;
import org.eclipse.ceylon.model.typechecker.model.Type;

import ceylon.interop.java.CeylonMutableSet;

public class LinkedModeImporter implements ILinkedModeListener {
    
    public static final int CANCEL = 1 << 10;
    
    private Type type;
    private IDocument document;
    private CeylonEditor editor;
    
    public LinkedModeImporter(IDocument document, 
            CeylonEditor editor) {
        this.document = document;
        this.editor = editor;
    }

    @Override
    public void left(LinkedModeModel model, int flags) {
        if (type!=null && (flags&CANCEL)==0) {
            Display.getCurrent()
                    .syncExec(new Runnable() {
                @Override
                public void run() {
                    Set<Declaration> imports = 
                            new HashSet<Declaration>();
                    //note: we want the very latest tree here, so 
                    //get it direct from the editor!
                    Tree.CompilationUnit rootNode = 
                            editor.getParseController()
                                .getLastCompilationUnit();
                    importProposals()
                        .importType(
                                new CeylonMutableSet<>(null, imports), 
                                type, rootNode);
                    if (!imports.isEmpty()) {
                        TextChange change = new platformJ2C().newChange("Import Type", 
                                        document);
                        change.initMultiEdit();
                        importProposals()
                            .applyImports(change, 
                                    new CeylonMutableSet<>(null, imports), 
                                    rootNode, change.getDocument());
                        change.apply();
                    }
                }
            });
        }
    }
    
    @Override
    public void suspend(LinkedModeModel model) {}

    @Override
    public void resume(LinkedModeModel model, int flags) {}

    public void selected(Type type) {
        this.type = type;
    }

}