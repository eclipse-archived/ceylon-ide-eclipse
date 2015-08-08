package com.redhat.ceylon.eclipse.util;

import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;

public class FindArgumentVisitor 
        extends Visitor {

    private final Node term;
    private Tree.NamedArgument declaration;
    private Tree.NamedArgument current;

    public FindArgumentVisitor(Node term) {
        this.term = term;
    }

    public Tree.NamedArgument getArgumentNode() {
        return declaration;
    }

    @Override
    public void visit(Tree.NamedArgument that) {
        Tree.NamedArgument outer = current;
        current = that;
        super.visit(that);
        current = outer;
    }
    
    @Override
    public void visitAny(Node node) {
        if (node == term) {
            declaration = current;
        }
        if (declaration == null) {
            super.visitAny(node);
        }
    }

}