package org.eclipse.ceylon.ide.eclipse.util;

import org.eclipse.ceylon.compiler.typechecker.tree.Node;
import org.eclipse.ceylon.compiler.typechecker.tree.Tree;
import org.eclipse.ceylon.compiler.typechecker.tree.Visitor;

@Deprecated
public class FindDeclarationVisitor 
        extends Visitor {

    private final Node term;
    private Tree.Declaration declaration;
    private Tree.Declaration current;

    public FindDeclarationVisitor(Node term) {
        this.term = term;
    }

    public Tree.Declaration getDeclarationNode() {
        return declaration;
    }

    @Override
    public void visit(Tree.Declaration that) {
        Tree.Declaration outer = current;
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