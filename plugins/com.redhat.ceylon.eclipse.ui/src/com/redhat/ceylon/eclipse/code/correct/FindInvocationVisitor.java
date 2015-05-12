package com.redhat.ceylon.eclipse.code.correct;

import com.redhat.ceylon.model.typechecker.model.Declaration;
import com.redhat.ceylon.model.typechecker.model.Parameter;
import com.redhat.ceylon.model.typechecker.model.TypedDeclaration;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.Expression;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.Term;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;

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
        if (e!=null && node==e.getTerm()) {
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
        if (e!=null && node==e.getTerm()) {
            result=current;
            Parameter p = that.getParameter();
            if (p!=null) {
                parameter = p.getModel();
            }
        }
        super.visit(that);
    }
    @Override
    public void visit(Tree.NamedArgument that) {
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
    public void visit(Tree.Return that) {
        Expression e = that.getExpression();
        if (e!=null && node==e.getTerm()) {
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
                Declaration d = ((Tree.BaseMemberExpression) lt).getDeclaration();
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
            Term bme = that.getBaseMemberExpression();
            if (bme instanceof Tree.BaseMemberExpression) {
                Declaration d = 
                        ((Tree.BaseMemberExpression) bme).getDeclaration();
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
}