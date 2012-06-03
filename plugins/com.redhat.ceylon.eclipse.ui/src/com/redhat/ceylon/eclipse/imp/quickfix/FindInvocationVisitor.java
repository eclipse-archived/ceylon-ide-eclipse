package com.redhat.ceylon.eclipse.imp.quickfix;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.TypedDeclaration;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.DefaultArgument;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.Expression;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.SpecifierOrInitializerExpression;
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
    public void visit(Tree.PositionalArgument that) {
        Expression e = that.getExpression();
        if (e!=null && node==e.getTerm()) {
            result=current;
            parameter=that.getParameter();
        }
        super.visit(that);
    }
    @Override
    public void visit(Tree.NamedArgument that) {
        if (node==that) {
            result=current;
            parameter=that.getParameter();
        }
        super.visit(that);
    }
    @Override
    public void visit(Tree.SpecifierStatement that) {
        Expression e = that.getSpecifierExpression().getExpression();
        if (e!=null && node==e.getTerm()) {
            result=current;
            Term bme = that.getBaseMemberExpression();
            if (bme instanceof Tree.BaseMemberExpression) {
                Declaration d = ((Tree.BaseMemberExpression) bme).getDeclaration();
                if (d instanceof TypedDeclaration) {
                    parameter=(TypedDeclaration) d;
                }
            }
        }
        super.visit(that);
    }
    @Override
    public void visit(Tree.AttributeDeclaration that) {
        SpecifierOrInitializerExpression sie = that.getSpecifierOrInitializerExpression();
        if (sie!=null) {
            Expression e = sie.getExpression();
            if (e!=null && node==e.getTerm()) {
                result=current;
                parameter = that.getDeclarationModel();
            }
        }
        super.visit(that);
    }
    @Override
    public void visit(Tree.Parameter that) {
        DefaultArgument da = that.getDefaultArgument();
        if (da!=null) {
            Expression e = da.getSpecifierExpression().getExpression();
            if (e!=null && node==e.getTerm()) {
                result=current;
                parameter = that.getDeclarationModel();
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
}