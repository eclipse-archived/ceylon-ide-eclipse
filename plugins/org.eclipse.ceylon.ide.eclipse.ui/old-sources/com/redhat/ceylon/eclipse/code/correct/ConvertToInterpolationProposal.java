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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.DeleteEdit;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;

import org.eclipse.ceylon.compiler.typechecker.tree.Node;
import org.eclipse.ceylon.compiler.typechecker.tree.Tree;
import org.eclipse.ceylon.compiler.typechecker.tree.Visitor;

class ConvertToInterpolationProposal extends CorrectionProposal {

    private ConvertToInterpolationProposal(String name, Change change) {
        super(name, change, null);
    }
    
    private static List<Tree.Term> flatten(Tree.SumOp sum) {
        Tree.Term lt = sum.getLeftTerm();
        Tree.Term rt = sum.getRightTerm();
        List<Tree.Term> result;
        if (lt instanceof Tree.SumOp) {
            result = flatten((Tree.SumOp) lt);
            result.add(rt);
        }
        else {
            result = new ArrayList<Tree.Term>();
            result.add(lt);
            result.add(rt);
        }
        return result;
    }
    
    static class ConcatenationVisitor extends Visitor {
        Node node;
        Tree.SumOp result;
        ConcatenationVisitor(Node node) {
            this.node = node;
        }
        @Override
        public void visit(Tree.SumOp that) {
            if (that.getStartIndex()<=node.getStartIndex() &&
                that.getEndIndex()>=node.getEndIndex() &&
                that.getTypeModel()!=null &&
                that.getTypeModel().isString()) {
                result = that;
                return;
            }
            super.visit(that);
        }
    }
    
    static void addConvertToInterpolationProposal(
            Collection<ICompletionProposal> proposals,
            IFile file, Tree.CompilationUnit cu, Node node, 
            IDocument doc) {
        ConcatenationVisitor tv = 
                new ConcatenationVisitor(node);
        tv.visit(cu);
        Tree.SumOp sum = tv.result;
        if (sum!=null) {
            TextFileChange change = 
                    new TextFileChange(
                            "Convert to Interpolation", 
                            file);
            change.setEdit(new MultiTextEdit());
            List<Tree.Term> terms = flatten(sum);
            Tree.Term lt = terms.get(0);
            Tree.Term rt = terms.get(terms.size()-1);
            boolean expectingLiteral =
                    lt instanceof Tree.StringLiteral ||
                    lt instanceof Tree.StringTemplate;
            if (!expectingLiteral) {
                change.addEdit(new InsertEdit(lt.getStartIndex(), "\"``"));
            }
            for (int i=0; i<terms.size(); i++) {
                Tree.Term term = terms.get(i);
                if (i>0) {
                    Tree.Term previous = terms.get(i-1);
                    int from = previous.getEndIndex();
                    int to = term.getStartIndex();
                    change.addEdit(new DeleteEdit(from, to-from));
                }
                if (expectingLiteral && 
                        !(term instanceof Tree.StringLiteral ||
                          term instanceof Tree.StringTemplate)) {
                    change.addEdit(new InsertEdit(term.getStartIndex(), "````"));
                    expectingLiteral = false;
                }
                if (expectingLiteral) {
                    if (i>0) {
                        change.addEdit(new ReplaceEdit(term.getStartIndex(), 1, "``"));
                    }
                    if (i<terms.size()-1) {
                        change.addEdit(new ReplaceEdit(term.getEndIndex()-1, 1, "``"));
                    }
                    expectingLiteral = false;
                }
                else {
                    if (term instanceof Tree.QualifiedMemberExpression) {
                        Tree.QualifiedMemberExpression lrt = 
                                (Tree.QualifiedMemberExpression) term;
                        if (lrt.getDeclaration().getName().equals("string")) {
                            int from = lrt.getMemberOperator().getStartIndex();
                            int to = lrt.getIdentifier().getEndIndex();
                            change.addEdit(new DeleteEdit(from, to-from));
                        }
                    }
                    expectingLiteral = true;
                }
            }
            if (expectingLiteral) {
                change.addEdit(new InsertEdit(rt.getEndIndex(), "``\""));
            }
            proposals.add(new ConvertToInterpolationProposal(
                    "Convert to string interpolation", change));
        }
    }
    
    
}