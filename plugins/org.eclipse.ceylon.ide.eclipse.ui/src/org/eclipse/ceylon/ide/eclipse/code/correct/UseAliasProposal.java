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

import static org.eclipse.ceylon.ide.eclipse.code.refactor.RenameLinkedMode.useLinkedMode;
import static org.eclipse.ceylon.ide.eclipse.ui.CeylonResources.ADD_CORR;

import java.util.Collection;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension6;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;

import org.eclipse.ceylon.compiler.typechecker.tree.Tree;
import org.eclipse.ceylon.ide.eclipse.code.editor.CeylonEditor;
import org.eclipse.ceylon.ide.eclipse.code.refactor.EnterAliasLinkedMode;
import org.eclipse.ceylon.ide.eclipse.code.refactor.EnterAliasRefactoringAction;
import org.eclipse.ceylon.ide.eclipse.util.Highlights;
import org.eclipse.ceylon.model.typechecker.model.Declaration;

class UseAliasProposal implements ICompletionProposal, ICompletionProposalExtension6 {
    
    private final Declaration dec;
    private final CeylonEditor editor;
    
    private UseAliasProposal(Declaration dec, CeylonEditor editor) {
        this.dec = dec;
        this.editor = editor;
    }
    
    @Override
    public void apply(IDocument document) {
        if (useLinkedMode()) {
            new EnterAliasLinkedMode(editor).start();
        }
        else {
            new EnterAliasRefactoringAction(editor).run();
        }
    }
    
    static void addUseAliasProposal(Tree.ImportMemberOrType imt,  
            Collection<ICompletionProposal> proposals,
            CeylonEditor editor) {
        if (imt!=null) {
            Declaration dec = imt.getDeclarationModel();
            if (dec!=null && imt.getAlias()==null) {
                proposals.add(new UseAliasProposal(dec, editor));
            }
        }
    }

    @Override
    public StyledString getStyledDisplayString() {
        String hint = 
                CorrectionUtil.shortcut(
                        "org.eclipse.ceylon.ide.eclipse.ui.action.enterAlias");
        return Highlights.styleProposal(getDisplayString(), false)
                .append(hint, StyledString.QUALIFIER_STYLER);
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
        return "Enter alias for '" + dec.getName() + "'";
    }

    @Override
    public Image getImage() {
        return ADD_CORR;
    }

    @Override
    public IContextInformation getContextInformation() {
        return null;
    }
    
}
