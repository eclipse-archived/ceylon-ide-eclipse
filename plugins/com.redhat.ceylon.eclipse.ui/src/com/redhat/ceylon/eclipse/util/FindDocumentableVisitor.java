package com.redhat.ceylon.eclipse.util;

import com.redhat.ceylon.compiler.typechecker.tree.NaturalVisitor;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;

public class FindDocumentableVisitor extends Visitor implements NaturalVisitor {

    private final Node term;
    private Tree.StatementOrArgument documentableNode;
    private Tree.StatementOrArgument currentDocumentableNode;

    public FindDocumentableVisitor(Node term) {
        this.term = term;
    }

    public Tree.StatementOrArgument getDocumentableNode() {
        return documentableNode;
    }

    @Override
    public void visit(Tree.StatementOrArgument that) {
        boolean isDocumentable = isDocumentable(that);
        Tree.StatementOrArgument originalDocumentableNode = null;
        if (isDocumentable) {
            originalDocumentableNode = currentDocumentableNode;
            currentDocumentableNode = that;
        }
        super.visit(that);
        if (isDocumentable) {
            currentDocumentableNode = originalDocumentableNode;
        }
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
    
    private boolean isDocumentable(Tree.StatementOrArgument that) {
        if (that instanceof Tree.ClassOrInterface || 
                that instanceof Tree.AnyAttribute || 
                that instanceof Tree.AnyMethod || 
                that instanceof Tree.Constructor ||
                that instanceof Tree.ObjectDefinition || 
                that instanceof Tree.ModuleDescriptor || 
                that instanceof Tree.PackageDescriptor ||
                that instanceof Tree.Assertion) {
            return true;
        }
        return false;
    }

}