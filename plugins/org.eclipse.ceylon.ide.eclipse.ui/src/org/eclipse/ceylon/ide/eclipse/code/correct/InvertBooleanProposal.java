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

import java.util.Collection;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension6;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;

import org.eclipse.ceylon.ide.eclipse.code.editor.CeylonEditor;
import org.eclipse.ceylon.ide.eclipse.code.refactor.InvertBooleanRefactoringAction;
import org.eclipse.ceylon.ide.eclipse.ui.CeylonResources;
import org.eclipse.ceylon.ide.eclipse.util.Highlights;

class InvertBooleanProposal implements ICompletionProposal, ICompletionProposalExtension6 {

    private final InvertBooleanRefactoringAction action;

    public InvertBooleanProposal(CeylonEditor editor) {
        action = new InvertBooleanRefactoringAction(editor);
    }

    @Override
    public Image getImage() {
        return CeylonResources.COMPOSITE_CHANGE;
    }

    @Override
    public String getDisplayString() {
        return "Invert boolean value of '" + action.getValueName() + "'";
    }

    @Override
    public Point getSelection(IDocument doc) {
        return null;
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

    public static void add(Collection<ICompletionProposal> proposals, CeylonEditor editor) {
        InvertBooleanProposal proposal = new InvertBooleanProposal(editor);
        if (proposal.action.isEnabled()) {
            proposals.add(proposal);
        }
    }

    @Override
    public StyledString getStyledDisplayString() {
        String hint = 
                CorrectionUtil.shortcut(
                        "org.eclipse.ceylon.ide.eclipse.ui.action.invertBoolean");
        return Highlights.styleProposal(getDisplayString(), false)
                .append(hint, StyledString.QUALIFIER_STYLER);
    }

}