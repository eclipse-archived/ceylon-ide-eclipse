package com.redhat.ceylon.eclipse.util;

import com.redhat.ceylon.compiler.typechecker.tree.NaturalVisitor;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;

public class FindArgumentVisitor 
        extends Visitor 
        implements NaturalVisitor {

    private final Node term;
    private Tree.TypedArgument declaration;
    private Tree.TypedArgument current;

    public FindArgumentVisitor(Node term) {
        this.term = term;
    }

    public Tree.TypedArgument getArgumentNode() {
        return declaration;
    }

    @Override
    public void visit(Tree.TypedArgument that) {
        Tree.TypedArgument outer = current;
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