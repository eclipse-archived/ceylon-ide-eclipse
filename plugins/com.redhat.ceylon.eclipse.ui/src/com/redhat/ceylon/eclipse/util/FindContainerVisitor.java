package com.redhat.ceylon.eclipse.util;

import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;

public class FindContainerVisitor extends Visitor {

    private Node node;
    private Tree.StatementOrArgument container;
    private Tree.StatementOrArgument currentContainer;

    public FindContainerVisitor(Node node) {
        this.node = node;
    }

    public Tree.Declaration getDeclaration() {
        if (container instanceof Tree.Declaration) {
            return (Tree.Declaration) container;
        } else {
            return null;
        }
    }

    public Tree.StatementOrArgument getStatementOrArgument() {
        return container;
    }

    @Override
    public void visit(Tree.ModuleDescriptor that) {
        Tree.StatementOrArgument d = currentContainer;
        currentContainer = that;
        super.visit(that);
        currentContainer = d;
    }
    
    @Override
    public void visit(Tree.PackageDescriptor that) {
        Tree.StatementOrArgument d = currentContainer;
        currentContainer = that;
        super.visit(that);
        currentContainer = d;
    }    

    @Override
    public void visit(Tree.ObjectDefinition that) {
        Tree.StatementOrArgument d = currentContainer;
        currentContainer = that;
        super.visit(that);
        currentContainer = d;
    }

    @Override
    public void visit(Tree.AttributeGetterDefinition that) {
        Tree.StatementOrArgument d = currentContainer;
        currentContainer = that;
        super.visit(that);
        currentContainer = d;
    }

    @Override
    public void visit(Tree.MethodDefinition that) {
        Tree.StatementOrArgument d = currentContainer;
        currentContainer = that;
        super.visit(that);
        currentContainer = d;
    }

    @Override
    public void visit(Tree.ClassDefinition that) {
        Tree.StatementOrArgument d = currentContainer;
        currentContainer = that;
        super.visit(that);
        currentContainer = d;
    }

    @Override
    public void visit(Tree.InterfaceDefinition that) {
        Tree.StatementOrArgument d = currentContainer;
        currentContainer = that;
        super.visit(that);
        currentContainer = d;
    }

    @Override
    public void visitAny(Node node) {
        if (this.node == node) {
            container = currentContainer;
        }
        if (container == null) {
            super.visitAny(node);
        }
    }
    
}