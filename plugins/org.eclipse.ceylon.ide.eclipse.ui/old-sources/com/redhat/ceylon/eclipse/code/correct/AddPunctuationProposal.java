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

import static org.eclipse.ceylon.ide.eclipse.code.correct.CorrectionUtil.getBeforeParenthesisNode;
import static org.eclipse.ceylon.ide.eclipse.code.correct.CorrectionUtil.getDescription;

import java.util.Collection;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.ReplaceEdit;

import org.eclipse.ceylon.model.typechecker.model.Declaration;
import org.eclipse.ceylon.compiler.typechecker.tree.Node;
import org.eclipse.ceylon.compiler.typechecker.tree.Tree;

@Deprecated
class AddPunctuationProposal extends CorrectionProposal {
    
    AddPunctuationProposal(int offset, int len, String desc, 
            TextFileChange change) {
        super(desc, change, new Region(offset, len));
    }
    
    static void addEmptyParameterListProposal(IFile file,
            Collection<ICompletionProposal> proposals, 
            Node node) {
        Tree.Declaration decNode = (Tree.Declaration) node;
        Node n = getBeforeParenthesisNode(decNode);
        if (n!=null) {
            Declaration dec = decNode.getDeclarationModel();
            TextFileChange change = 
                    new TextFileChange(
                            "Add Empty Parameter List", 
                            file);
            int offset = n.getEndIndex();
            change.setEdit(new InsertEdit(offset, "()"));
            proposals.add(new AddPunctuationProposal(
                    offset+1, 0, 
                    "Add '()' empty parameter list to " + 
                    getDescription(dec), 
                    change));
        }
    }

    static void addImportWildcardProposal(IFile file,
            Collection<ICompletionProposal> proposals, 
            Node node) {
        if (node instanceof Tree.ImportMemberOrTypeList) {
            Tree.ImportMemberOrTypeList imtl = 
                    (Tree.ImportMemberOrTypeList) node;
            TextFileChange change = 
                    new TextFileChange(
                            "Add Import Wildcard", 
                            file);
            int offset = imtl.getStartIndex();
            int length = imtl.getDistance();
            change.setEdit(new ReplaceEdit(
                    offset, length, 
                    "{ ... }"));
            proposals.add(new AddPunctuationProposal(
                    offset+2, 3, 
                    "Add '...' import wildcard", 
                    change));
        }
    }
}