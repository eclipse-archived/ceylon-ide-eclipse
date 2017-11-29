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

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.ltk.core.refactoring.TextChange;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;

import org.eclipse.ceylon.compiler.typechecker.tree.Node;
import org.eclipse.ceylon.compiler.typechecker.tree.Tree;

@Deprecated
class ConvertToSpecifierProposal extends CorrectionProposal {
    
    ConvertToSpecifierProposal(String desc, int offset, 
            TextChange change) {
        super(desc, change, new Region(offset, 0));
    }
    
    static void addConvertToSpecifierProposal(IDocument doc,
            Collection<ICompletionProposal> proposals, 
            IFile file, Tree.Block block) {
        addConvertToSpecifierProposal(doc, proposals, file, 
                block, false);
    }
    static void addConvertToSpecifierProposal(IDocument doc,
            Collection<ICompletionProposal> proposals, 
            IFile file, Tree.Block block, 
            boolean anonymousFunction) {
        if (block.getStatements().size()==1) {
            Tree.Statement s = block.getStatements().get(0);
            Node end = null;
            Node start = null;
            if (s instanceof Tree.Return) {
                Tree.Return ret = (Tree.Return) s;
                start = ret.getExpression();
                end = start;
            }
            else if (s instanceof Tree.ExpressionStatement) {
                Tree.ExpressionStatement es = 
                        (Tree.ExpressionStatement) s;
                start = es.getExpression();
                end = start;
            }
            else if (s instanceof Tree.SpecifierStatement) {
                Tree.SpecifierStatement ss = 
                        (Tree.SpecifierStatement) s;
                start = ss.getBaseMemberExpression();
                end = ss.getSpecifierExpression();
            }
            if (end!=null) {
                TextChange change = 
                        new TextFileChange(
                                "Convert to Specifier", 
                                file);
                change.setEdit(new MultiTextEdit());
                Integer offset = block.getStartIndex();
                String es;
                try {
                    es = doc.get(start.getStartIndex(), 
                            end.getEndIndex()-start.getStartIndex());
                } 
                catch (BadLocationException ex) {
                    ex.printStackTrace();
                    return;
                }
                change.addEdit(new ReplaceEdit(offset, 
                        block.getEndIndex()-offset, 
                        "=> " + es + (anonymousFunction?"":";")));
                String desc = 
                        anonymousFunction ? 
                            "Convert anonymous function body to =>" : 
                            "Convert block to =>";
                proposals.add(new ConvertToSpecifierProposal(
                        desc, offset+2 , change));
            }
        }
    }
    
}