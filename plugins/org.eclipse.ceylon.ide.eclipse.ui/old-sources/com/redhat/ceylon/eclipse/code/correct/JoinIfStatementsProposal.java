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

import static org.eclipse.ceylon.ide.eclipse.java2ceylon.Java2CeylonProxies.utilJ2C;

import java.util.Collection;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.ltk.core.refactoring.TextChange;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.DeleteEdit;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;

import org.eclipse.ceylon.compiler.typechecker.parser.CeylonLexer;
import org.eclipse.ceylon.compiler.typechecker.tree.Tree;

class JoinIfStatementsProposal {

    static void addJoinIfStatementsProposal(
            Collection<ICompletionProposal> proposals, 
            IDocument doc, IFile file, 
            Tree.Statement statement) {
        //TODO: look for containing if statement?
        if (statement instanceof Tree.IfStatement) {
            Tree.IfStatement outer = 
                    (Tree.IfStatement) statement;
            Tree.ElseClause elseClause = outer.getElseClause();
            if (elseClause==null) {
                Tree.Block block = 
                        outer.getIfClause().getBlock();
                if (block!=null) {
                    List<Tree.Statement> statements = 
                            block.getStatements();
                    if (statements.size()==1) {
                        Tree.Statement st = statements.get(0);
                        if (st instanceof Tree.IfStatement) {
                            Tree.IfStatement inner = 
                                    (Tree.IfStatement) st;
                            Tree.ConditionList ocl = 
                                    outer.getIfClause()
                                        .getConditionList();
                            Tree.ConditionList icl = 
                                    inner.getIfClause()
                                        .getConditionList();
                            if (ocl!=null && icl!=null &&
                                    inner.getElseClause()==null) {
                                TextChange change = 
                                        new TextFileChange(
                                                "Join If Statements",
                                                file);
                                change.setEdit(new MultiTextEdit());
                                change.addEdit(new ReplaceEdit(ocl.getEndIndex()-1,
                                        icl.getStartIndex()-ocl.getEndIndex()+2,
                                        ", "));
                                decrementIndent(doc, inner, icl, change,
                                        utilJ2C().indents().getIndent(inner, doc),
                                        utilJ2C().indents().getIndent(outer, doc));
                                change.addEdit(new DeleteEdit(inner.getEndIndex(),
                                        outer.getEndIndex()-inner.getEndIndex()));
                                proposals.add(new CorrectionProposal(
                                        "Join 'if' statements at condition list",
                                        change, null));
                            }
                        }
                    }
                }
            }
            else {
                Tree.Block block = elseClause.getBlock();
                if (block!=null &&
                        block.getToken().getType()
                            != CeylonLexer.IF_CLAUSE) {
                    List<Tree.Statement> statements =
                            block.getStatements();
                    if (statements.size()==1) {
                        Tree.Statement st = statements.get(0);
                        if (st instanceof Tree.IfStatement) {
                            Tree.IfStatement inner =
                                    (Tree.IfStatement) st;
                            Tree.ConditionList icl =
                                    inner.getIfClause()
                                        .getConditionList();
                            if (icl!=null) {
                                TextChange change =
                                        new TextFileChange(
                                                "Join If Statements",
                                                file);
                                change.setEdit(new MultiTextEdit());
                                int from = block.getStartIndex();
                                int to = inner.getStartIndex();
                                change.addEdit(new DeleteEdit(from, to-from));
                                decrementIndent(doc, inner, icl, change,
                                        utilJ2C().indents().getIndent(inner, doc),
                                        utilJ2C().indents().getIndent(outer, doc));
                                change.addEdit(new DeleteEdit(inner.getEndIndex(),
                                        outer.getEndIndex()-inner.getEndIndex()));
                                proposals.add(new CorrectionProposal(
                                        "Join 'if' statements at 'else'",
                                        change, null));
                            }
                        }
                    }
                }
            }
        }
    }

    private static void decrementIndent(IDocument doc, Tree.IfStatement is,
            Tree.ConditionList cl, TextChange change, String indent,
            String outerIndent) {
        String defaultIndent = utilJ2C().indents().getDefaultIndent();
        try {
            for (int line = doc.getLineOfOffset(cl.getStopIndex())+1;
                    line < doc.getLineOfOffset(is.getStopIndex()); 
                    line++) {
                IRegion lineInformation = doc.getLineInformation(line);
                String lineText =
                        doc.get(lineInformation.getOffset(),
                                lineInformation.getLength());
                if (lineText.startsWith(indent) &&
                        indent.startsWith(outerIndent)) {
                    change.addEdit(new DeleteEdit(
                            lineInformation.getOffset() + outerIndent.length(),
                            indent.length()-outerIndent.length()));
                }
                else if (lineText.startsWith(outerIndent+defaultIndent)) {
                    change.addEdit(new DeleteEdit(
                            lineInformation.getOffset() +
                            outerIndent.length(),
                            defaultIndent.length()));
                }
            }
            int line = doc.getLineOfOffset(is.getStopIndex());
            IRegion lineInformation = doc.getLineInformation(line);
            String lineText =
                    doc.get(lineInformation.getOffset(),
                            lineInformation.getLength());
            if (lineText.startsWith(indent) &&
                    indent.startsWith(outerIndent)) {
                change.addEdit(new ReplaceEdit(
                        lineInformation.getOffset(),
                        indent.length(),
                        outerIndent));
            }
            else if (lineText.startsWith(outerIndent+defaultIndent)) {
                change.addEdit(new ReplaceEdit(
                        lineInformation.getOffset(),
                        outerIndent.length() +
                        defaultIndent.length(),
                        outerIndent));
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

}
