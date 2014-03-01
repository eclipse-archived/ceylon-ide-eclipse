package com.redhat.ceylon.eclipse.code.search;

import com.redhat.ceylon.compiler.typechecker.tree.NaturalVisitor;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;

public class FindContainerVisitor extends Visitor 
        implements NaturalVisitor {
    Node node;
    Tree.StatementOrArgument declaration;
    Tree.StatementOrArgument currentDeclaration;
    public Tree.StatementOrArgument getStatementOrArgument() {
        return declaration;
    }
    public FindContainerVisitor(Node node) {
        this.node=node;
    }
    @Override
    public void visit(Tree.ImportModule that) {
        Tree.StatementOrArgument d = currentDeclaration;
        currentDeclaration = that;
        super.visit(that);
        currentDeclaration = d;
    }
    @Override
    public void visit(Tree.Import that) {
        Tree.StatementOrArgument d = currentDeclaration;
        currentDeclaration = that;
        super.visit(that);
        currentDeclaration = d;
    }
    @Override
    public void visit(Tree.ModuleDescriptor that) {
        Tree.StatementOrArgument d = currentDeclaration;
        currentDeclaration = that;
        super.visit(that);
        currentDeclaration = d;
    }
    @Override
    public void visit(Tree.PackageDescriptor that) {
        Tree.StatementOrArgument d = currentDeclaration;
        currentDeclaration = that;
        super.visit(that);
        currentDeclaration = d;
    }
    @Override
    public void visit(Tree.ObjectDefinition that) {
        Tree.StatementOrArgument d = currentDeclaration;
        currentDeclaration = that;
        super.visit(that);
        currentDeclaration = d;
    }
    @Override
    public void visit(Tree.AnyAttribute that) {
        Tree.StatementOrArgument d = currentDeclaration;
        currentDeclaration = that;
        super.visit(that);
        currentDeclaration = d;
    }
    @Override
    public void visit(Tree.AttributeSetterDefinition that) {
        Tree.StatementOrArgument d = currentDeclaration;
        currentDeclaration = that;
        super.visit(that);
        currentDeclaration = d;
    }
    @Override
    public void visit(Tree.AnyMethod that) {
        Tree.StatementOrArgument d = currentDeclaration;
        currentDeclaration = that;
        super.visit(that);
        currentDeclaration = d;
    }
    @Override
    public void visit(Tree.AnyClass that) {
        Tree.StatementOrArgument d = currentDeclaration;
        currentDeclaration = that;
        super.visit(that);
        currentDeclaration = d;
    }
    @Override
    public void visit(Tree.AnyInterface that) {
        Tree.StatementOrArgument d = currentDeclaration;
        currentDeclaration = that;
        super.visit(that);
        currentDeclaration = d;
    }
    @Override
    public void visit(Tree.TypeAliasDeclaration that) {
        Tree.StatementOrArgument d = currentDeclaration;
        currentDeclaration = that;
        super.visit(that);
        currentDeclaration = d;
    }
    public void visitAny(Node node) {
        if (this.node==node) {
            declaration=currentDeclaration;
        }
        if (declaration==null) {
            super.visitAny(node);
        }
    }
}