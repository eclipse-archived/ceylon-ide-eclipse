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

import static org.eclipse.ceylon.ide.eclipse.code.refactor.InvertBooleanRefactoring.invertTerm;
import static org.eclipse.ceylon.ide.eclipse.util.EditorUtil.getDocument;

import java.util.Collection;

import org.antlr.runtime.CommonToken;
import org.antlr.runtime.Token;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.ltk.core.refactoring.TextChange;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.DeleteEdit;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;

import org.eclipse.ceylon.compiler.typechecker.parser.CeylonLexer;
import org.eclipse.ceylon.compiler.typechecker.tree.Node;
import org.eclipse.ceylon.compiler.typechecker.tree.Tree;
import org.eclipse.ceylon.compiler.typechecker.tree.Visitor;

@Deprecated
public class OperatorProposals {
    
    static void addSwapBinaryOperandsProposal(
            Collection<ICompletionProposal> proposals, 
            IFile file,
            Tree.BinaryOperatorExpression boe) {
        TextChange change = 
                new TextFileChange("Swap Operands", 
                        file);
        change.setEdit(new MultiTextEdit());
        Tree.Term lt = boe.getLeftTerm();
        Tree.Term rt = boe.getRightTerm();
        if (lt!=null && rt!=null) {
            int lto = lt.getStartIndex();
            int ltl = lt.getDistance();
            int rto = rt.getStartIndex();
            int rtl = rt.getDistance();
            IDocument document = getDocument(change);
            try {
                change.addEdit(new ReplaceEdit(lto, ltl, 
                        document.get(rto, rtl)));
                change.addEdit(new ReplaceEdit(rto, rtl, 
                        document.get(lto, ltl)));
                proposals.add(new CorrectionProposal(
                        "Swap operands of " + 
                        boe.getMainToken().getText() + 
                        " expression", change, null));
            }
            catch(Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    static void addReverseOperatorProposal(
            Collection<ICompletionProposal> proposals, 
            IFile file,
            Tree.BinaryOperatorExpression boe) {
        if (boe instanceof Tree.ComparisonOp) {
            TextChange change = 
                    new TextFileChange("Reverse Operator", 
                            file);
            change.setEdit(new MultiTextEdit());
            Tree.Term lt = boe.getLeftTerm();
            Tree.Term rt = boe.getRightTerm();
            if (lt!=null && rt!=null) {
                int lto = lt.getStartIndex();
                int ltl = lt.getDistance();
                int rto = rt.getStartIndex();
                int rtl = rt.getDistance();
                CommonToken op = 
                        (CommonToken) boe.getMainToken();
                String ot = op.getText();
                String iot = reversed(ot);
                change.addEdit(new ReplaceEdit(
                        op.getStartIndex(), 
                        ot.length(), iot));
                if (boe instanceof Tree.ComparisonOp) {
                    IDocument document = getDocument(change);
                    try {
                        change.addEdit(new ReplaceEdit(
                                lto, ltl, 
                                document.get(rto, rtl)));
                        change.addEdit(new ReplaceEdit(
                                rto, rtl, 
                                document.get(lto, ltl)));
                    }
                    catch(Exception e) {
                        e.printStackTrace();
                        return;
                    }
                }
                proposals.add(new CorrectionProposal(
                        "Convert " + ot + " to " + iot, 
                        change, null));
            }
        }
    }
    
    static void addInvertOperatorProposal(
            Collection<ICompletionProposal> proposals, 
            IFile file,
            Tree.BinaryOperatorExpression boe) {
        if (boe instanceof Tree.ComparisonOp || 
            boe instanceof Tree.LogicalOp) {
            TextChange change = 
                    new TextFileChange("Invert Operator", 
                            file);
            change.setEdit(new MultiTextEdit());
            Tree.Term lt = boe.getLeftTerm();
            Tree.Term rt = boe.getRightTerm();
            if (lt!=null && rt!=null) {
                CommonToken op = 
                        (CommonToken) boe.getMainToken();
                String ot = op.getText();
                String iot = inverted(ot);
                change.addEdit(new ReplaceEdit(
                        op.getStartIndex(), 
                        ot.length(), iot));
                change.addEdit(new InsertEdit(
                        boe.getStartIndex(), "!("));
                change.addEdit(new InsertEdit(
                        boe.getEndIndex(), ")"));
                if (boe instanceof Tree.LogicalOp) {
                    invertTerm(boe.getLeftTerm(), change);
                    invertTerm(boe.getRightTerm(), change);
                }
             proposals.add(new CorrectionProposal(
                        "Convert " + ot + " to " + iot, 
                        change, null));
            }
        }
    }
    
    private static String inverted(String ot) {
        switch (ot) {
        case ">": return "<=";
        case ">=": return "<";
        case "<": return ">=";
        case "<=": return ">";
        case "||": return "&&";
        case "&&": return "||";
        default: return ot;
        }
    }
    
    private static String reversed(String ot) {
        switch (ot) {
        case ">": return "<";
        case ">=": return "<=";
        case "<": return ">";
        case "<=": return ">=";
        default: return ot;
        }
    }

    static void addParenthesesProposals(
            Collection<ICompletionProposal> proposals, 
            IFile file, Node node, 
            Tree.CompilationUnit rootNode,
            Tree.OperatorExpression oe) {
        if (node instanceof Tree.ArgumentList) {
            final Tree.ArgumentList argList = 
                    (Tree.ArgumentList) node;
            class FindInvocationVisitor extends Visitor {
                Tree.InvocationExpression current;
                Tree.InvocationExpression result;
                @Override
                public void visit(Tree.InvocationExpression that) {
                    Tree.InvocationExpression old = current;
                    current = that;
                    super.visit(that);
                    current = old;
                }
                @Override
                public void visit(Tree.ArgumentList that) {
                    if (argList==that) {
                        result = current;
                    }
                    else {
                        super.visit(that);
                    }
                }
            }
            FindInvocationVisitor fiv = new FindInvocationVisitor();
            fiv.visit(rootNode);
            node = fiv.result;
        }
        if (node instanceof Tree.Expression) {
            addRemoveParenthesesProposal(proposals, file, node);
        }
        else if (node instanceof Tree.Term) {
            addAddParenthesesProposal(proposals, file, node);
            if (oe!=null && oe!=node) {
                addAddParenthesesProposal(proposals, file, oe);
            }
        }
    }

    private static void addAddParenthesesProposal(
            Collection<ICompletionProposal> proposals, 
            IFile file, Node node) {
        String desc;
        if (node instanceof Tree.OperatorExpression) {
            desc = node.getMainToken().getText() + 
                    " expression";
        }
        else if (node instanceof Tree.QualifiedMemberOrTypeExpression) {
            desc = "member reference";
        }
        else if (node instanceof Tree.BaseMemberOrTypeExpression) {
            desc = "base reference";
        }
        else if (node instanceof Tree.Literal) {
            desc = "literal";
        }
        else if (node instanceof Tree.InvocationExpression) {
            desc = "invocation";
        }
        else {
            desc = "expression";
        }
        TextChange change = 
                new TextFileChange("Add Parentheses", 
                        file);
        change.setEdit(new MultiTextEdit());
        change.addEdit(new InsertEdit(
                node.getStartIndex(), "("));
        change.addEdit(new InsertEdit(
                node.getEndIndex(), ")"));
        proposals.add(new CorrectionProposal(
                "Parenthesize " + desc, change, null));
    }
    
    private static void addRemoveParenthesesProposal(
            Collection<ICompletionProposal> proposals, 
            IFile file, Node node) {
        Token token = node.getToken();
        Token endToken = node.getEndToken();
        if (token!=null && endToken!=null &&
                token.getType()==CeylonLexer.LPAREN &&
                endToken.getType()==CeylonLexer.RPAREN) {
            TextChange change = 
                    new TextFileChange("Remove Parentheses", 
                            file);
            change.setEdit(new MultiTextEdit());
            change.addEdit(new DeleteEdit(
                    node.getStartIndex(), 1));
            change.addEdit(new DeleteEdit(
                    node.getEndIndex()-1, 1));
            proposals.add(new CorrectionProposal(
                    "Remove parentheses", change, null));
            
        }
    }

}
