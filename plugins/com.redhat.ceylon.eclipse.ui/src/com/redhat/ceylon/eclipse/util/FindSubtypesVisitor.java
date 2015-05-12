package com.redhat.ceylon.eclipse.util;

import java.util.HashSet;
import java.util.Set;

import com.redhat.ceylon.model.typechecker.model.TypeDeclaration;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;

public class FindSubtypesVisitor extends Visitor {
    
    private final TypeDeclaration declaration;
    private Set<Tree.Declaration> declarationNodes = new HashSet<Tree.Declaration>();
    
    public FindSubtypesVisitor(TypeDeclaration declaration) {
        this.declaration = declaration;
    }
    
    public Set<Tree.Declaration> getDeclarationNodes() {
        return declarationNodes;
    }
    
    protected boolean isRefinement(TypeDeclaration dec) {
        return dec!=null && dec.inherits(declaration);
    }
    
    @Override
    public void visit(Tree.TypeDeclaration that) {
        if (isRefinement(that.getDeclarationModel())) {
            declarationNodes.add(that);
        }
        super.visit(that);
    }
        
    @Override
    public void visit(Tree.ObjectDefinition that) {
        if (isRefinement(that.getDeclarationModel().getTypeDeclaration())) {
            declarationNodes.add(that);
        }
        super.visit(that);
    }
        
}
