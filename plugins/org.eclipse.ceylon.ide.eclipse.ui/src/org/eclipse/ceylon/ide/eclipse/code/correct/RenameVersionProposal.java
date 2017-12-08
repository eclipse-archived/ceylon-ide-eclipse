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

import static org.eclipse.ceylon.compiler.typechecker.tree.TreeUtil.formatPath;
import static org.eclipse.ceylon.ide.eclipse.ui.CeylonResources.RENAME;

import java.util.Collection;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension6;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;

import org.eclipse.ceylon.compiler.typechecker.tree.Node;
import org.eclipse.ceylon.compiler.typechecker.tree.Tree;
import org.eclipse.ceylon.ide.eclipse.code.editor.CeylonEditor;
import org.eclipse.ceylon.ide.eclipse.code.refactor.ChangeVersionLinkedMode;
import org.eclipse.ceylon.ide.eclipse.util.Highlights;

class RenameVersionProposal implements ICompletionProposal, 
        ICompletionProposalExtension6 {
    
    private final Tree.ModuleDescriptor node;
    private final CeylonEditor editor;
    
    RenameVersionProposal(Tree.ModuleDescriptor node, 
            CeylonEditor editor) {
        this.node = node;
        this.editor = editor;
    }
    
    @Override
    public void apply(IDocument document) {
        new ChangeVersionLinkedMode(node.getVersion(), node.getImportPath(), editor).start();
    }
    
    static void addRenameVersionProposals(Node node, 
            Collection<ICompletionProposal> proposals, 
            Tree.CompilationUnit cu, CeylonEditor editor) {
        for (Tree.ModuleDescriptor md: cu.getModuleDescriptors()) {
            if (md.getVersion()==node || md==node || md.getImportPath()==node) {
                proposals.add(new RenameVersionProposal(md, editor));
            }
        }
    }

    @Override
    public StyledString getStyledDisplayString() {
        return Highlights.styleProposal(getDisplayString(), true);
    }

    @Override
    public Point getSelection(IDocument document) {
        return null;
    }

    @Override
    public String getAdditionalProposalInfo() {
        return null;
    }

    @Override
    public String getDisplayString() {
        return "Change version '" + node.getVersion().getText() + 
                "' of module '" + formatPath(node.getImportPath().getIdentifiers()) + "'";
    }

    @Override
    public Image getImage() {
        return RENAME;
    }

    @Override
    public IContextInformation getContextInformation() {
        return null;
    }
    
}
