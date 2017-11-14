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

import org.eclipse.ceylon.compiler.typechecker.tree.Node;
import org.eclipse.ceylon.compiler.typechecker.tree.Tree;
import org.eclipse.ceylon.compiler.typechecker.tree.Tree.Expression;
import org.eclipse.ceylon.compiler.typechecker.tree.Tree.Term;
import org.eclipse.ceylon.compiler.typechecker.tree.Visitor;
import org.eclipse.ceylon.model.typechecker.model.Declaration;
import org.eclipse.ceylon.model.typechecker.model.Parameter;
import org.eclipse.ceylon.model.typechecker.model.TypedDeclaration;

class FindInvocationVisitor extends Visitor {
    Node node;
    Tree.InvocationExpression result;
    Tree.InvocationExpression current;
    TypedDeclaration parameter;
    FindInvocationVisitor(Node node) {
        this.node=node;
    }
    @Override
    public void visit(Tree.ListedArgument that) {
        Expression e = that.getExpression();
        if (node==that || node==e || 
                e!=null && node==e.getTerm()) {
            result=current;
            Parameter p = that.getParameter();
            if (p!=null) {
                parameter=p.getModel();
            }
        }
        super.visit(that);
    }
    @Override
    public void visit(Tree.SpreadArgument that) {
        Expression e = that.getExpression();
        if (node==that || node==e || 
                e!=null && node==e.getTerm()) {
            result=current;
            Parameter p = that.getParameter();
            if (p!=null) {
                parameter = p.getModel();
            }
        }
        super.visit(that);
    }
    @Override
    public void visit(Tree.TypedArgument that) {
        if (node==that) {
            result=current;
            Parameter p = that.getParameter();
            if (p!=null) {
                parameter = p.getModel();
            }
        }
        super.visit(that);
    }
    @Override
    public void visit(Tree.SpecifiedArgument that) {
        Tree.SpecifierExpression se = 
                that.getSpecifierExpression();
        Tree.Expression e = se==null ? 
                null : se.getExpression();
        if (node==that || node==e || 
                e!=null && node==e.getTerm()) {
            result=current;
            Parameter p = that.getParameter();
            if (p!=null) {
                parameter = p.getModel();
            }
        }
        super.visit(that);
    }
    @Override
    public void visit(Tree.Return that) {
        Expression e = that.getExpression();
        if (node==that || node==e || 
                e!=null && node==e.getTerm()) {
            //result=current;
            parameter = (TypedDeclaration) that.getDeclaration();
        }
        super.visit(that);
    }
    @Override
    public void visit(Tree.AssignOp that) {
        if (node==that.getRightTerm()) {
            //result=current;
            Term lt = that.getLeftTerm();
            if (lt instanceof Tree.BaseMemberExpression) {
                Tree.BaseMemberExpression bme = 
                        (Tree.BaseMemberExpression) lt;
                Declaration d = bme.getDeclaration();
                if (d instanceof TypedDeclaration) {
                    parameter = (TypedDeclaration) d;
                }
            }
        }
        super.visit(that);
    }
    @Override
    public void visit(Tree.SpecifierStatement that) {
        Expression e = that.getSpecifierExpression().getExpression();
        if (e!=null && node==e.getTerm()) {
            //result=current;
            Term term = that.getBaseMemberExpression();
            if (term instanceof Tree.BaseMemberExpression) {
                Tree.BaseMemberExpression bme = 
                        (Tree.BaseMemberExpression) term;
                Declaration d = 
                        bme.getDeclaration();
                if (d instanceof TypedDeclaration) {
                    parameter = (TypedDeclaration) d;
                }
            }
        }
        super.visit(that);
    }
    @Override
    public void visit(Tree.AttributeDeclaration that) {
        Tree.SpecifierOrInitializerExpression sie = 
                that.getSpecifierOrInitializerExpression();
        if (sie!=null) {
            Expression e = sie.getExpression();
            if (e!=null && node==e.getTerm()) {
                //result=current;
                parameter = that.getDeclarationModel();
            }
        }
        super.visit(that);
    }
    @Override
    public void visit(Tree.MethodDeclaration that) {
        Tree.SpecifierOrInitializerExpression sie = 
                that.getSpecifierExpression();
        if (sie!=null) {
            Expression e = sie.getExpression();
            if (e!=null && node==e.getTerm()) {
                //result=current;
                parameter = that.getDeclarationModel();
            }
        }
        super.visit(that);
    }
    @Override
    public void visit(Tree.InitializerParameter that) {
        Tree.SpecifierExpression se = that.getSpecifierExpression();
        if (se!=null) {
            Tree.Expression e = se.getExpression();
            if (e!=null && node==e.getTerm()) {
                //result=current;
                parameter = that.getParameterModel().getModel();
            }
        }
        super.visit(that);
    }
    @Override
    public void visit(Tree.InvocationExpression that) { 
        Tree.InvocationExpression oc=current;
        current = that;
        super.visit(that);
        current=oc;
    }
    @Override
    public void visit(Tree.BaseMemberExpression that) {
        if (that == node) {
            result = current;
        }
        super.visit(that);
    }
    @Override
    public void visit(Tree.QualifiedMemberExpression that) {
        if (that == node) {
            result = current;
        }
        super.visit(that);
    }
}