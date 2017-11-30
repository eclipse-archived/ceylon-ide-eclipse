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

import static org.eclipse.ceylon.ide.eclipse.code.refactor.MoveUtil.canMoveDeclaration;
import static org.eclipse.ceylon.ide.eclipse.code.refactor.MoveUtil.getDeclarationName;

import java.util.Collection;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension6;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;

import org.eclipse.ceylon.ide.eclipse.code.editor.CeylonEditor;
import org.eclipse.ceylon.ide.eclipse.code.refactor.MoveToUnitRefactoringAction;
import org.eclipse.ceylon.ide.eclipse.ui.CeylonResources;
import org.eclipse.ceylon.ide.eclipse.util.Highlights;

class MoveToUnitProposal implements ICompletionProposal, ICompletionProposalExtension6 {

    private final CeylonEditor editor;
    private final String name;
    
    public MoveToUnitProposal(String name, CeylonEditor editor) {
        this.editor = editor;
        this.name = name;
    }
    
    @Override
    public Point getSelection(IDocument doc) {
        return null;
    }

    @Override
    public Image getImage() {
        return CeylonResources.MOVE;
    }

    @Override
    public String getDisplayString() {
        return "Move '" + name + "' to another source file";
    }
    
    @Override
    public StyledString getStyledDisplayString() {
        String hint = 
                CorrectionUtil.shortcut(
                        "org.eclipse.ceylon.ide.eclipse.ui.action.moveDeclarationToUnit");
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
        new MoveToUnitRefactoringAction(editor).run();
    }
    
    static void add(Collection<ICompletionProposal> proposals, 
            CeylonEditor editor) {
        if (canMoveDeclaration(editor)) {
            proposals.add(new MoveToUnitProposal(getDeclarationName(editor), 
                    editor));
        }
    }

}