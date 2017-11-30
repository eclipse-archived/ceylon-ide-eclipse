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

import static org.eclipse.ceylon.ide.eclipse.ui.CeylonResources.COMPOSITE_CHANGE;
import static org.eclipse.ceylon.ide.eclipse.util.Nodes.getContainer;

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
import org.eclipse.ceylon.ide.eclipse.code.refactor.MakeReceiverRefactoringAction;
import org.eclipse.ceylon.ide.eclipse.util.Highlights;
import org.eclipse.ceylon.model.typechecker.model.Declaration;

public class MakeReceiverProposal implements ICompletionProposal, ICompletionProposalExtension6 {

    private final MakeReceiverRefactoringAction action;
    private String name;
    
    public MakeReceiverProposal(CeylonEditor editor, Node node) {
        action = new MakeReceiverRefactoringAction(editor);
        if (node instanceof Tree.Declaration) {
            Tree.Declaration d = (Tree.Declaration) node;
            Declaration dec = d.getDeclarationModel();
            if (dec!=null) {
                Tree.CompilationUnit rootNode = 
                        editor.getParseController()
                            .getLastCompilationUnit();
                Tree.Declaration container = 
                        getContainer(rootNode, dec);
                if (container!=null) {
                    name = container.getDeclarationModel().getName();
                }
            }
        }
    }
    
    @Override
    public Point getSelection(IDocument doc) {
        return null;
    }

    @Override
    public Image getImage() {
        return COMPOSITE_CHANGE;
    }

    @Override
    public String getDisplayString() {
        return "Make receiver of '" + name + "'";
    }
    
    @Override
    public StyledString getStyledDisplayString() {
        String hint = 
                CorrectionUtil.shortcut(
                        "org.eclipse.ceylon.ide.eclipse.ui.action.makeReceiver");
        return Highlights.styleProposal(getDisplayString(), false)
                .append(hint, StyledString.QUALIFIER_STYLER);
    }
    
    @Override
    public IContextInformation getContextInformation() {
        return null;
    }

    @Override
    public String getAdditionalProposalInfo() {
        return null;
    }

    @Override
    public void apply(IDocument doc) {
        action.run();
    }
    
    boolean isEnabled() {
        return action.isEnabled();
    }
    
    public static void add(Collection<ICompletionProposal> proposals, 
            CeylonEditor editor, Node node) {
        MakeReceiverProposal prop = new MakeReceiverProposal(editor, node);
        if (prop.isEnabled()) {
            proposals.add(prop);
        }
    }

}