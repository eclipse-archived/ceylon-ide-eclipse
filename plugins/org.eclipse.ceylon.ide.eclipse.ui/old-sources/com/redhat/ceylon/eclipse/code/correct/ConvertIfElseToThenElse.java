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
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.ltk.core.refactoring.TextChange;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.ReplaceEdit;

import org.eclipse.ceylon.compiler.typechecker.tree.Node;
import org.eclipse.ceylon.compiler.typechecker.tree.Tree;
import org.eclipse.ceylon.compiler.typechecker.tree.Tree.AssignOp;
import org.eclipse.ceylon.compiler.typechecker.tree.Tree.Block;
import org.eclipse.ceylon.compiler.typechecker.tree.Tree.CompilationUnit;
import org.eclipse.ceylon.compiler.typechecker.tree.Tree.Condition;
import org.eclipse.ceylon.compiler.typechecker.tree.Tree.Expression;
import org.eclipse.ceylon.compiler.typechecker.tree.Tree.IfStatement;
import org.eclipse.ceylon.compiler.typechecker.tree.Tree.SpecifierStatement;
import org.eclipse.ceylon.compiler.typechecker.tree.Tree.Statement;
import org.eclipse.ceylon.compiler.typechecker.tree.Tree.Term;
import org.eclipse.ceylon.compiler.typechecker.tree.Tree.ThenOp;
import org.eclipse.ceylon.ide.eclipse.util.Nodes;

class ConvertIfElseToThenElse extends CorrectionProposal {
    
    ConvertIfElseToThenElse(int offset, TextChange change, String desc) {
        super(desc, change, new Region(offset, 0));
    }
    
    static void addConvertToThenElseProposal(CompilationUnit cu, IDocument doc,
            Collection<ICompletionProposal> proposals, IFile file,
            Statement statement) {
        TextChange change = createTextChange(cu, doc, statement, file);
        if (change != null) {
            String desc = change.getName()
                    .replace("If", "'if'")
                    .replace("Then", "'then'")
                    .replace("Else", "'else'") + 
                    " expression";
            proposals.add(new ConvertIfElseToThenElse(change.getEdit().getOffset(), change, desc));
        }
    }

    static TextChange createTextChange(CompilationUnit cu,
            IDocument doc, Statement statement, IFile file) {
        if (! (statement instanceof Tree.IfStatement)) {
            return null;
        }
        IfStatement ifStmt = (IfStatement) statement;
        if (ifStmt.getElseClause() == null) {
            return null;
        }
        
        Block ifBlock = ifStmt.getIfClause().getBlock();
        if (ifBlock.getStatements().size() != 1) {
            return null;
        }
        Block elseBlock = ifStmt.getElseClause().getBlock();
        if (elseBlock.getStatements().size() != 1) {
            return null;
        }

        Node ifBlockNode = ifBlock.getStatements().get(0);
        Node elseBlockNode = elseBlock.getStatements().get(0);
        List<Condition> conditions = ifStmt.getIfClause()
                .getConditionList().getConditions();
        if (conditions.size()!=1) {
            return null;
        }
        Condition condition = conditions.get(0);
        Integer replaceFrom = statement.getStartIndex();
        String test = removeEnclosingParenthesis(getTerm(doc, condition));
        String thenStr = null;
        String elseStr = null;
        String attributeIdentifier = null;
        String operator = null;
        String action;
        if (ifBlockNode instanceof Tree.Return) {
            Tree.Return ifReturn = (Tree.Return) ifBlockNode;
            if (! (elseBlockNode instanceof Tree.Return)) {
                return null;
            }
            Tree.Return elseReturn = (Tree.Return) elseBlockNode;
            action = "return ";
            thenStr = getOperands(doc, ifReturn.getExpression());
            elseStr = getOperands(doc, elseReturn.getExpression());
        } else if (ifBlockNode instanceof Tree.SpecifierStatement) {
            SpecifierStatement ifSpecifierStmt = (Tree.SpecifierStatement) ifBlockNode;
            attributeIdentifier = getTerm(doc, ifSpecifierStmt.getBaseMemberExpression());
            operator = " = ";
            action = attributeIdentifier + operator;
            if (!(elseBlockNode instanceof Tree.SpecifierStatement)) {
                return null;
            }
            String elseId = getTerm(doc, ((Tree.SpecifierStatement)elseBlockNode).getBaseMemberExpression());
            if (!attributeIdentifier.equals(elseId)) {
                return null;
            }
            thenStr = getOperands(doc, ifSpecifierStmt.getSpecifierExpression().getExpression().getTerm());
            elseStr = getOperands(doc, ((Tree.SpecifierStatement) elseBlockNode).getSpecifierExpression().getExpression().getTerm());
        } /*else if (ifBlockNode instanceof Tree.ExpressionStatement) {
            if (!(elseBlockNode instanceof Tree.ExpressionStatement)) {
                return null;
            }
            Term ifOperator = ((Tree.ExpressionStatement) ifBlockNode).getExpression().getTerm();
            if (!(ifOperator instanceof AssignOp)) {
                return null;
            } 
            Term elseOperator = ((Tree.ExpressionStatement) elseBlockNode).getExpression().getTerm();
            if (!(elseOperator instanceof AssignOp)) {
                return null;
            } 
            AssignOp ifAssign = (AssignOp) ifOperator;
            AssignOp elseAssign = (AssignOp) elseOperator;
            attributeIdentifier = getTerm(doc, ifAssign.getLeftTerm());
            String elseId = getTerm(doc, elseAssign.getLeftTerm());
            if (!attributeIdentifier.equals(elseId)) {
                return null;
            }
            
            operator = " = ";
            action = attributeIdentifier + operator;
            thenStr = getOperands(doc, ifAssign.getRightTerm());
            elseStr = getOperands(doc, elseAssign.getRightTerm());
        }*/ else {
            return null;
        }
        
        if (attributeIdentifier != null) {
            Statement prevStatement = findPreviousStatement(cu, doc, statement);
            if (prevStatement != null) {
                if (prevStatement instanceof Tree.AttributeDeclaration) {
                    Tree.AttributeDeclaration attrDecl = 
                            (Tree.AttributeDeclaration) prevStatement;
                    if (attributeIdentifier.equals(getTerm(doc, attrDecl.getIdentifier()))) {
                        action = removeSemiColon(getTerm(doc, attrDecl)) + operator;
                        replaceFrom = attrDecl.getStartIndex();
                    }
                }
            }
        }
        
        boolean abbreviateToElse = false;
        if (condition instanceof Tree.ExistsCondition) {
            Tree.ExistsCondition existsCond = 
                    (Tree.ExistsCondition) condition;
            Tree.Statement st = existsCond.getVariable();
            if (st instanceof Tree.Variable) {
                Tree.Variable variable = (Tree.Variable) st;
                if (thenStr.equals(getTerm(doc, variable.getIdentifier()))) {
                    Expression existsExpr = variable.getSpecifierExpression().getExpression();
                    test = getTerm(doc, existsExpr);
                    abbreviateToElse = true;
                }
            }
        }
        
        boolean abbreviateToThen = 
                condition instanceof Tree.BooleanCondition &&
                    elseStr.equals("null");
        
        StringBuilder replace = new StringBuilder();
        replace.append(action);
        if (!abbreviateToThen && !abbreviateToElse) replace.append("if (");
        replace.append(test);
        if (!abbreviateToThen && !abbreviateToElse) replace.append(")");
        if (!abbreviateToElse) {
            replace.append(" then ").append(thenStr);
        }
        if (!abbreviateToThen) {
            replace.append(" else ").append(elseStr);
        }
        replace.append(";");
        
        String desc;
        if (abbreviateToThen) {
            desc = "Convert to Then";
        }
        else if (abbreviateToElse) {
            desc = "Convert to Else";
        }
        else {
            desc = "Convert to If Then Else";
        }
        TextChange change = new TextFileChange(desc, file);
//      TextChange change = new DocumentChange("Convert to then-else", doc);
        change.setEdit(new ReplaceEdit(replaceFrom, 
                statement.getEndIndex() - replaceFrom, 
                replace.toString()));
        return change;
    }
    
    private static String getOperands(IDocument doc, Term operand) {
        String term = getTerm(doc, operand);
        if (hasLowerPrecedenceThenElse(operand)) {
            return "(" + term + ")";
        }
        return term;
    }

    private static boolean hasLowerPrecedenceThenElse(Term operand) {
        Term node;
        if (operand instanceof Tree.Expression) {
            Tree.Expression exp = (Tree.Expression) operand;
            node = exp.getTerm();
        } else {
            node = operand;
        }
        return node instanceof Tree.DefaultOp || 
                node instanceof ThenOp || 
                node instanceof AssignOp;
    }

    private static String removeSemiColon(String term) {
        if (term.endsWith(";")) {
            return term.substring(0, term.length() - 1);
        }
        return term;
    }

    private static Statement findPreviousStatement(CompilationUnit cu, IDocument doc, 
            Statement statement) {
        try {
            int previousLineNo = doc.getLineOfOffset(statement.getStartIndex());
            while (previousLineNo > 1) {
                previousLineNo--;
                IRegion lineInfo = doc.getLineInformation(previousLineNo);
                String prevLine = doc.get(lineInfo.getOffset(), lineInfo.getLength());
                Matcher m = Pattern.compile("(\\s*)\\w+").matcher(prevLine);
                if (m.find()) {
                    int whitespaceLen = m.group(1).length();
                    Node node = Nodes.findNode(cu, null, lineInfo.getOffset() + whitespaceLen, 
                            lineInfo.getOffset() + whitespaceLen + 1);
                    return Nodes.findStatement(cu, node);
                }
            }
        } catch (BadLocationException e) {
            e.printStackTrace();
        } 
        return null;
    }

    private static String removeEnclosingParenthesis(String s) {
        if (s.charAt(0) == '(' && s.charAt(s.length() - 1) == ')') {
            return s.substring(1, s.length() - 1);
        }
        return s;
    }
    
    private static String getTerm(IDocument doc, Node node) {
        try {
            return doc.get(node.getStartIndex(), node.getDistance());
        } catch (BadLocationException e) {
            throw new RuntimeException(e);
        }
    }
}