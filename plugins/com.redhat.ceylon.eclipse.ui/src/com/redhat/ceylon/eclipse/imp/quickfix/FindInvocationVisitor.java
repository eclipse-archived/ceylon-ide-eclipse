package com.redhat.ceylon.eclipse.imp.quickfix;

import com.redhat.ceylon.compiler.typechecker.model.Parameter;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.Expression;

class FindInvocationVisitor extends Visitor {
    Node node;
    Tree.InvocationExpression result;
    Tree.InvocationExpression current;
    Parameter parameter;
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
    public void visit(Tree.InvocationExpression that) { 
        Tree.InvocationExpression oc=current;
        current = that;
        super.visit(that);
        current=oc;
    }
}