/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 1.0 which is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.code.open;

import static org.eclipse.ceylon.ide.eclipse.code.editor.Navigation.gotoDeclaration;
import static org.eclipse.ceylon.ide.eclipse.code.editor.Navigation.resolveNative;
import static org.eclipse.ceylon.ide.eclipse.util.EditorUtil.getCurrentEditor;
import static org.eclipse.ceylon.ide.eclipse.util.EditorUtil.getSelection;
import static org.eclipse.ceylon.ide.eclipse.util.Nodes.findNode;
import static org.eclipse.ceylon.ide.eclipse.util.Nodes.getReferencedModel;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.ui.IEditorPart;

import org.eclipse.ceylon.common.Backends;
import org.eclipse.ceylon.compiler.typechecker.tree.Node;
import org.eclipse.ceylon.compiler.typechecker.tree.Tree;
import org.eclipse.ceylon.ide.eclipse.code.editor.CeylonEditor;
import org.eclipse.ceylon.ide.eclipse.code.parse.CeylonParseController;
import org.eclipse.ceylon.model.typechecker.model.Declaration;
import org.eclipse.ceylon.model.typechecker.model.Referenceable;
import org.eclipse.ceylon.model.typechecker.model.TypedDeclaration;

public class OpenSelectedDeclarationHandler extends AbstractHandler {
    
    private Node getSelectedNode(
            ITextSelection textSel, CeylonEditor editor) {
        CeylonParseController controller = 
                editor.getParseController();
        if (controller==null) {
            return null;
        }
        else {
            Tree.CompilationUnit rootNode = 
                    controller.getLastCompilationUnit();
            if (rootNode == null) {
                return null;
            }
            else {
                return findNode(rootNode,
                        controller.getTokens(),
                        textSel.getOffset(),
                        textSel.getOffset() + 
                        textSel.getLength());
            }
        }
    }
    
    public boolean isEnabled() {
        IEditorPart editor = getCurrentEditor();
        if (super.isEnabled() 
                && editor instanceof CeylonEditor) {
            CeylonEditor ce = (CeylonEditor) editor;
            Node selectedNode = 
                    getSelectedNode(getSelection(ce), ce);
            return getReferencedModel(selectedNode)!=null;
        }
        else {
            return false;
        }
                
    }
    
    @Override
    public Object execute(ExecutionEvent event) 
            throws ExecutionException {
        IEditorPart editor = getCurrentEditor();
        if (editor instanceof CeylonEditor) {
            CeylonEditor ce = (CeylonEditor) editor;
            Node selectedNode = 
                    getSelectedNode(getSelection(ce), ce);
            Referenceable referenceable = 
                    getReferencedModel(selectedNode);
            if (referenceable instanceof Declaration) {
                Declaration dec = 
                        (Declaration) 
                            referenceable;
                //look for the "original" declaration, 
                //ignoring narrowing synthetic declarations
                if (dec instanceof TypedDeclaration) {
                    Declaration od = dec;
                    while (od!=null) {
                        referenceable = dec = od;
                        TypedDeclaration td = 
                                (TypedDeclaration) od;
                        od = td.getOriginalDeclaration();
                    }
                }
                if (dec.isNative()) {
                    //for native declarations, go to the
                    //header
                    referenceable = 
                            resolveNative(dec, 
                                    Backends.HEADER);
                }
            }
            if (referenceable!=null) {
                gotoDeclaration(referenceable);
            }
        }
        return null;
    }
        
}