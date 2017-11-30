/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 1.0 which is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.code.complete;

import static org.eclipse.ceylon.ide.eclipse.code.complete.CodeCompletions.getDescriptionFor;
import static org.eclipse.ceylon.ide.eclipse.code.correct.ImportProposals.importProposals;
import static org.eclipse.ceylon.ide.eclipse.code.outline.CeylonLabelProvider.getDecoratedImage;
import static org.eclipse.ceylon.ide.eclipse.code.outline.CeylonLabelProvider.getDecorationAttributes;
import static org.eclipse.ceylon.ide.eclipse.ui.CeylonResources.CEYLON_FUN;
import static org.eclipse.ceylon.ide.eclipse.ui.CeylonResources.CEYLON_LOCAL_FUN;

import java.util.HashSet;
import java.util.List;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.ltk.core.refactoring.DocumentChange;
import org.eclipse.text.edits.MultiTextEdit;

import org.eclipse.ceylon.compiler.typechecker.tree.Tree;
import org.eclipse.ceylon.ide.eclipse.code.parse.CeylonParseController;
import org.eclipse.ceylon.ide.eclipse.ui.CeylonPlugin;
import org.eclipse.ceylon.ide.eclipse.util.EditorUtil;
import org.eclipse.ceylon.ide.eclipse.util.Highlights;
import org.eclipse.ceylon.model.typechecker.model.Declaration;
import org.eclipse.ceylon.model.typechecker.model.Functional;
import org.eclipse.ceylon.model.typechecker.model.Unit;

@Deprecated
final class FunctionCompletionProposal extends
        CompletionProposal {
    
    private final CeylonParseController cpc;
    private final Declaration dec;

    FunctionCompletionProposal(int offset, String prefix,
            String desc, String text, Declaration dec,
            CeylonParseController cpc) {
        super(offset, prefix, 
                getDecoratedImage(dec.isShared() ? 
                        CEYLON_FUN : CEYLON_LOCAL_FUN,
                    getDecorationAttributes(dec), false), 
                desc, text);
        this.cpc = cpc;
        this.dec = dec;
    }

    private DocumentChange createChange(IDocument document)
            throws BadLocationException {
        DocumentChange change = 
                new DocumentChange("Complete Invocation", document);
        change.setEdit(new MultiTextEdit());
        HashSet<Declaration> decs = new HashSet<Declaration>();
        Tree.CompilationUnit cu = cpc.getLastCompilationUnit();
        importProposals().importDeclaration(decs, dec, cu);
        int il=(int) importProposals().applyImports(change, decs, cu, document);
        change.addEdit(createEdit(document));
        offset+=il;
        return change;
    }

    @Override
    public boolean isAutoInsertable() {
        return false;
    }

    @Override
    public void apply(IDocument document) {
        try {
            EditorUtil.performChange(createChange(document));
        }
        catch (BadLocationException e) {
            e.printStackTrace();
        }
    }
    
    protected static void addFunctionProposal(int offset,
            final CeylonParseController cpc, 
            Tree.Primary primary,
            List<ICompletionProposal> result, 
            final Declaration dec,
            IDocument doc) {
        Tree.Term arg = primary;
        while (arg instanceof Tree.Expression) {
            arg = ((Tree.Expression) arg).getTerm(); 
        }
        final int start = arg.getStartIndex();
        final int len = arg.getDistance();
        int origin = primary.getStartIndex();
        String argText;
        String prefix;
        try {
            //the argument
            argText = doc.get(start, len);
            //the text to replace
            prefix = doc.get(origin, offset-origin);
        }
        catch (BadLocationException e) {
            return;
        }
        String text = dec.getName(arg.getUnit())
                + "(" + argText + ")";
        if (((Functional)dec).isDeclaredVoid()) {
            text += ";";
        }
        Unit unit = cpc.getLastCompilationUnit().getUnit();
        result.add(new FunctionCompletionProposal(offset, prefix, 
                getDescriptionFor(dec, unit) + "(...)", text, dec, cpc));
    }

    @Override
    public StyledString getStyledDisplayString() {
        StyledString result = new StyledString();
        Highlights.styleFragment(result, 
                getDisplayString(), qualifiedNameIsPath(), 
                null, CeylonPlugin.getCompletionFont());
        return result;
    }

}