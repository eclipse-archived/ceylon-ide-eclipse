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

import java.util.Collection;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension6;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;

import org.eclipse.ceylon.ide.eclipse.code.editor.CeylonEditor;
import org.eclipse.ceylon.ide.eclipse.code.refactor.InlineRefactoringAction;
import org.eclipse.ceylon.ide.eclipse.ui.CeylonResources;
import org.eclipse.ceylon.ide.eclipse.util.Highlights;

class InlineDeclarationProposal implements ICompletionProposal,
        ICompletionProposalExtension6 {

    private final InlineRefactoringAction action;
    
    public InlineDeclarationProposal(CeylonEditor editor) {
        action = new InlineRefactoringAction(editor);
    }
    
    @Override
    public Point getSelection(IDocument doc) {
        return null;
    }

    @Override
    public Image getImage() {
        return CeylonResources.COMPOSITE_CHANGE;
    }

    @Override
    public String getDisplayString() {
        return "Inline '" + action.currentName() + "'";
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
    
    public static void add(Collection<ICompletionProposal> proposals, CeylonEditor editor) {
        InlineDeclarationProposal prop = new InlineDeclarationProposal(editor);
        if (prop.isEnabled()) {
            proposals.add(prop);
        }
    }

    @Override
    public StyledString getStyledDisplayString() {
        String hint = 
                CorrectionUtil.shortcut(
                        "org.eclipse.ceylon.ide.eclipse.ui.action.inline");
        return Highlights.styleProposal(getDisplayString(), false)
                .append(hint, StyledString.QUALIFIER_STYLER);
    }

}