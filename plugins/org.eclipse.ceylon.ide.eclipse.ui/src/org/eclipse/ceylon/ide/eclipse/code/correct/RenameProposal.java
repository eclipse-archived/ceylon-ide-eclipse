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
import static org.eclipse.ceylon.ide.eclipse.ui.CeylonResources.RENAME;

import java.util.Collection;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension6;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;

import org.eclipse.ceylon.ide.eclipse.code.editor.CeylonEditor;
import org.eclipse.ceylon.ide.eclipse.code.refactor.RenameLinkedMode;
import org.eclipse.ceylon.ide.eclipse.code.refactor.RenameRefactoring;
import org.eclipse.ceylon.ide.eclipse.code.refactor.RenameRefactoringAction;
import org.eclipse.ceylon.ide.eclipse.util.Highlights;
import org.eclipse.ceylon.model.typechecker.model.Declaration;

class RenameProposal implements ICompletionProposal,
        ICompletionProposalExtension6 {

    private final Declaration dec;
    private final CeylonEditor editor;
    
    RenameProposal(Declaration dec, 
            CeylonEditor editor) {
        this.dec = dec;
        this.editor = editor;
    }
    
    @Override
    public Point getSelection(IDocument doc) {
        return null;
    }

    @Override
    public Image getImage() {
        return RENAME;
    }

    @Override
    public String getDisplayString() {
        return "Rename '" + dec.getName() + "'";
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
        if (useLinkedMode()) {
            new RenameLinkedMode(editor).start();
        }
        else {
            new RenameRefactoringAction(editor).run();
        }
    }
    
    @Override
    public StyledString getStyledDisplayString() {
        String hint = 
                CorrectionUtil.shortcut(
                        "org.eclipse.ceylon.ide.eclipse.ui.action.rename");
        return Highlights.styleProposal(getDisplayString(), false)
                .append(hint, StyledString.QUALIFIER_STYLER);
    }

    public static void add(Collection<ICompletionProposal> proposals,
            CeylonEditor editor) {
        RenameRefactoring rr = new RenameRefactoring(editor);
        if (rr.getEnabled()) {
            proposals.add(new RenameProposal(rr.getDeclaration(), editor));
        }
    }

}