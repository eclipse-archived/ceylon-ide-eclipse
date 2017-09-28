package org.eclipse.ceylon.ide.eclipse.code.search;

import org.eclipse.ceylon.compiler.typechecker.tree.Node;
import org.eclipse.ceylon.compiler.typechecker.tree.Tree;
import org.eclipse.ceylon.compiler.typechecker.tree.Visitor;

@Deprecated
/**
 * @deprecated use org.eclipse.ceylon.ide.common.search.FindContainerVisitor instead
 */
public class FindContainerVisitor extends Visitor {
    
    private final Node node;
    
    private Tree.StatementOrArgument declaration;
    private Tree.StatementOrArgument currentDeclaration;
    
    public Tree.StatementOrArgument getStatementOrArgument() {
        return declaration;
    }
    
    public FindContainerVisitor(Node node) {
        this.node=node;
    }
    
    protected boolean accept(Tree.StatementOrArgument node) {
        return true;
    }
    
    @Override
    public void visit(Tree.ImportModule that) {
        Tree.StatementOrArgument d = currentDeclaration;
        if (accept(that)) currentDeclaration = that;
        super.visit(that);
        currentDeclaration = d;
    }
    
    @Override
    public void visit(Tree.Import that) {
        Tree.StatementOrArgument d = currentDeclaration;
        if (accept(that)) currentDeclaration = that;
        super.visit(that);
        currentDeclaration = d;
    }
    @Override
    public void visit(Tree.ModuleDescriptor that) {
        Tree.StatementOrArgument d = currentDeclaration;
        if (accept(that)) currentDeclaration = that;
        super.visit(that);
        currentDeclaration = d;
    }
    
    @Override
    public void visit(Tree.PackageDescriptor that) {
        Tree.StatementOrArgument d = currentDeclaration;
        if (accept(that)) currentDeclaration = that;
        super.visit(that);
        currentDeclaration = d;
    }
    
    @Override
    public void visit(Tree.ObjectDefinition that) {
        Tree.StatementOrArgument d = currentDeclaration;
        if (accept(that)) currentDeclaration = that;
        super.visit(that);
        currentDeclaration = d;
    }
    
    @Override
    public void visit(Tree.AnyAttribute that) {
        Tree.StatementOrArgument d = currentDeclaration;
        if (accept(that)) currentDeclaration = that;
        super.visit(that);
        currentDeclaration = d;
    }
    
    @Override
    public void visit(Tree.AttributeSetterDefinition that) {
        Tree.StatementOrArgument d = currentDeclaration;
        if (accept(that)) currentDeclaration = that;
        super.visit(that);
        currentDeclaration = d;
    }
    
    @Override
    public void visit(Tree.AnyMethod that) {
        Tree.StatementOrArgument d = currentDeclaration;
        if (accept(that)) currentDeclaration = that;
        super.visit(that);
        currentDeclaration = d;
    }
    
    @Override
    public void visit(Tree.AnyClass that) {
        Tree.StatementOrArgument d = currentDeclaration;
        if (accept(that)) currentDeclaration = that;
        super.visit(that);
        currentDeclaration = d;
    }
    
    @Override
    public void visit(Tree.AnyInterface that) {
        Tree.StatementOrArgument d = currentDeclaration;
        if (accept(that)) currentDeclaration = that;
        super.visit(that);
        currentDeclaration = d;
    }
    
    @Override
    public void visit(Tree.TypeAliasDeclaration that) {
        Tree.StatementOrArgument d = currentDeclaration;
        if (accept(that)) currentDeclaration = that;
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