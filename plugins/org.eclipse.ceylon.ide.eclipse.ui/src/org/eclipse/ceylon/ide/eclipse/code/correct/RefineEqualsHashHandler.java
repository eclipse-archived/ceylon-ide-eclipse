/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.code.correct;

import static org.eclipse.ceylon.ide.eclipse.java2ceylon.Java2CeylonProxies.correctJ2C;
import static org.eclipse.ceylon.ide.eclipse.util.EditorUtil.getCurrentEditor;
import static org.eclipse.ceylon.ide.eclipse.util.Nodes.findNode;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.ui.IEditorPart;

import org.eclipse.ceylon.compiler.typechecker.tree.Node;
import org.eclipse.ceylon.compiler.typechecker.tree.Tree;
import org.eclipse.ceylon.ide.eclipse.code.editor.CeylonEditor;
import org.eclipse.ceylon.ide.eclipse.util.EditorUtil;

public class RefineEqualsHashHandler extends AbstractHandler {

    @Override
    public Object execute(ExecutionEvent event) 
            throws ExecutionException {
        IEditorPart editor = getCurrentEditor();
        if (editor instanceof CeylonEditor) {
            CeylonEditor ce = (CeylonEditor) editor;
            Tree.CompilationUnit rootNode = 
                    ce.getParseController().getTypecheckedRootNode();
            if (rootNode!=null) {
                IRegion selection = ce.getSelection();
                int start = selection.getOffset();
                int end = start + selection.getLength();
                Node node = findNode(rootNode, ce.getParseController().getTokens(), start, end);
                List<ICompletionProposal> list = 
                        new ArrayList<ICompletionProposal>();
                IProject project = EditorUtil.getProject(editor.getEditorInput());
                correctJ2C().addRefineEqualsHashProposal(rootNode, node, list, ce, project);
                if (!list.isEmpty()) {
                    IDocument doc = ce.getCeylonSourceViewer().getDocument();
                    ICompletionProposal proposal = list.get(0);
                    proposal.apply(doc);
//                    Point point = proposal.getSelection(doc);
//                    ce.getSelectionProvider().setSelection(new TextSelection(point.x, point.y));
                }
            }
        }
        return null;
    }

}
