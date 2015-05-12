package com.redhat.ceylon.eclipse.util;

import static com.redhat.ceylon.compiler.typechecker.tree.Util.formatPath;

import com.redhat.ceylon.model.typechecker.model.Declaration;
import com.redhat.ceylon.model.typechecker.model.Referenceable;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;

public class FindDeclarationNodeVisitor extends Visitor {
    
    private final Referenceable declaration;
    protected Tree.StatementOrArgument declarationNode;
    
    public FindDeclarationNodeVisitor(Referenceable declaration) {
        this.declaration = declaration;
    }
    
    public Tree.StatementOrArgument getDeclarationNode() {
        return declarationNode;
    }
    
    private boolean isDeclaration(Declaration dec) {
        return dec!=null && dec.equals(declaration);
    }
    
    @Override
    public void visit(Tree.Declaration that) {
        if (isDeclaration(that.getDeclarationModel())) {
            declarationNode = that;
        }
        super.visit(that);
    }
    
    @Override
    public void visit(Tree.ObjectDefinition that) {
        if (isDeclaration(that.getDeclarationModel().getTypeDeclaration())) {
            declarationNode = that;
        }
        super.visit(that);
    }
    
    @Override
    public void visit(Tree.ModuleDescriptor that) {
        if (formatPath(that.getImportPath().getIdentifiers())
                .equals(declaration.getNameAsString())) {
            declarationNode = that;
        }
        super.visit(that);
    }
    
    @Override
    public void visit(Tree.PackageDescriptor that) {
        if (formatPath(that.getImportPath().getIdentifiers())
                .equals(declaration.getNameAsString())) {
            declarationNode = that;
        }
        super.visit(that);
    }
    
    public void visitAny(Node node) {
        if (declarationNode==null) {
            super.visitAny(node);
        }
    }
    
}
