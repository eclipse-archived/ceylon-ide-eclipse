package com.redhat.ceylon.eclipse.util;

import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;

public class FindContainerVisitor extends Visitor {
    Node node;
    Tree.Declaration declaration;
    Tree.Declaration currentDeclaration;
    public Tree.Declaration getDeclaration() {
        return declaration;
    }
    public FindContainerVisitor(Node node) {
        this.node=node;
    }
    @Override
    public void visit(Tree.ObjectDefinition that) {
        Tree.Declaration d = currentDeclaration;
        currentDeclaration = that;
        super.visit(that);
        currentDeclaration = d;
    }
    @Override
    public void visit(Tree.AnyAttribute that) {
        Tree.Declaration d = currentDeclaration;
        currentDeclaration = that;
        super.visit(that);
        currentDeclaration = d;
    }
    @Override
    public void visit(Tree.AttributeSetterDefinition that) {
        Tree.Declaration d = currentDeclaration;
        currentDeclaration = that;
        super.visit(that);
        currentDeclaration = d;
    }
    @Override
    public void visit(Tree.AnyMethod that) {
        Tree.Declaration d = currentDeclaration;
        currentDeclaration = that;
        super.visit(that);
        currentDeclaration = d;
    }
    @Override
    public void visit(Tree.Constructor that) {
        Tree.Declaration d = currentDeclaration;
        currentDeclaration = that;
        super.visit(that);
        currentDeclaration = d;
    }
    @Override
    public void visit(Tree.ClassDefinition that) {
        Tree.Declaration d = currentDeclaration;
        currentDeclaration = that;
        super.visit(that);
        currentDeclaration = d;
    }
    @Override
    public void visit(Tree.InterfaceDefinition that) {
        Tree.Declaration d = currentDeclaration;
        currentDeclaration = that;
        super.visit(that);
        currentDeclaration = d;
    }
    @Override
    public void visitAny(Node node) {
        if (this.node==node) {
            declaration=currentDeclaration;
        }
        if (declaration==null) {
            super.visitAny(node);
        }
    }
}