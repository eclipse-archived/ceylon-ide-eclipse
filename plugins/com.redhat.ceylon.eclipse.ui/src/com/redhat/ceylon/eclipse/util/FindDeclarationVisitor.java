package com.redhat.ceylon.eclipse.util;

import com.redhat.ceylon.compiler.typechecker.tree.NaturalVisitor;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;

public class FindDeclarationVisitor extends Visitor implements NaturalVisitor {

    private final Node term;
    private Tree.Declaration documentableNode;
    private Tree.Declaration currentDocumentableNode;

    public FindDeclarationVisitor(Node term) {
        this.term = term;
    }

    public Tree.Declaration getDeclarationNode() {
        return documentableNode;
    }

    @Override
    public void visit(Tree.Declaration that) {
        Tree.Declaration originalDocumentableNode = currentDocumentableNode;
        currentDocumentableNode = that;
        super.visit(that);
        currentDocumentableNode = originalDocumentableNode;
    }
    
    @Override
    public void visit(Tree.Body that) {
        currentDocumentableNode = null;
        super.visit(that);
    }

    @Override
    public void visitAny(Node node) {
        if (node == term) {
            documentableNode = currentDocumentableNode;
        }
        if (documentableNode == null) {
            super.visitAny(node);
        }
    }

}