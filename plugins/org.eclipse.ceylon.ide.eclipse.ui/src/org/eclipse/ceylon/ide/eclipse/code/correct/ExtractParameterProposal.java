/********************************************************************************
 * Copyright (c) {date} Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.code.correct;

import static org.eclipse.ceylon.ide.eclipse.code.refactor.ExtractLinkedMode.useLinkedMode;
import static org.eclipse.ceylon.ide.eclipse.java2ceylon.Java2CeylonProxies.refactorJ2C;
import static org.eclipse.ceylon.ide.eclipse.ui.CeylonResources.CHANGE;

import java.util.Collection;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension6;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;

import org.eclipse.ceylon.compiler.typechecker.parser.CeylonLexer;
import org.eclipse.ceylon.compiler.typechecker.tree.Node;
import org.eclipse.ceylon.compiler.typechecker.tree.Tree;
import org.eclipse.ceylon.ide.eclipse.code.editor.CeylonEditor;
import org.eclipse.ceylon.ide.eclipse.code.refactor.ExtractParameterLinkedMode;
import org.eclipse.ceylon.ide.eclipse.code.refactor.ExtractParameterRefactoringAction;

public class ExtractParameterProposal 
        implements ICompletionProposal, 
                   ICompletionProposalExtension6 {

    private CeylonEditor editor;

    public ExtractParameterProposal(CeylonEditor editor) {
        this.editor = editor;
    }
    
    @Override
    public Point getSelection(IDocument doc) {
        return null;
    }

    @Override
    public Image getImage() {
        return CHANGE;
    }

    @Override
    public String getDisplayString() {
        return "Extract parameter";
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
            ExtractParameterLinkedMode.selectExpressionAndStart(editor);
        }
        else {
            new ExtractParameterRefactoringAction(editor).run();
        }
    }
    
    public static void add(
            Collection<ICompletionProposal> proposals, 
            CeylonEditor editor, Node node) {
        if (node instanceof Tree.BaseMemberExpression) {
            Tree.BaseMemberExpression bme = 
                    (Tree.BaseMemberExpression) node;
            Tree.Identifier id = bme.getIdentifier();
            if (id==null || id.getToken().getType()==CeylonLexer.AIDENTIFIER) {
                return;
            }
        }
        if (refactorJ2C().newExtractParameterRefactoring(editor).getEnabled()) {
            proposals.add(new ExtractParameterProposal(editor));
        }
    }

    @Override
    public StyledString getStyledDisplayString() {
        String hint = 
                CorrectionUtil.shortcut(
                        "org.eclipse.ceylon.ide.eclipse.ui.action.extractParameter");
        return new StyledString(getDisplayString())
                .append(hint, StyledString.QUALIFIER_STYLER);
    }

}