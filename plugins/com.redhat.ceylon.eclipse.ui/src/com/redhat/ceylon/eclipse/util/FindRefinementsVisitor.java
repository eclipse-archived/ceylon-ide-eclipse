package com.redhat.ceylon.eclipse.util;

import java.util.HashSet;
import java.util.Set;

import com.redhat.ceylon.model.typechecker.model.Declaration;
import com.redhat.ceylon.model.typechecker.model.Setter;
import com.redhat.ceylon.compiler.typechecker.tree.NaturalVisitor;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;

public class FindRefinementsVisitor extends Visitor implements NaturalVisitor {
    
    private final Declaration declaration;
    private Set<Tree.StatementOrArgument> declarationNodes = 
            new HashSet<Tree.StatementOrArgument>();
    
    public FindRefinementsVisitor(Declaration declaration) {
        this.declaration = declaration;
    }
    
    public Set<Tree.StatementOrArgument> getDeclarationNodes() {
        return declarationNodes;
    }
    
    protected boolean isRefinement(Declaration dec) {
        return dec!=null && dec.refines(declaration) ||
                isSetterRefinement(dec);
    }

    private boolean isSetterRefinement(Declaration dec) {
        if (dec instanceof Setter) {
            return ((Setter) dec).getGetter().refines(declaration);
        }
        else {
            return false;
        }
    }
    
    @Override
    public void visit(Tree.SpecifierStatement that) {
        if (that.getRefinement() &&
                isRefinement(that.getDeclaration())) {
            declarationNodes.add(that);
        }
        super.visit(that);
    }
    
    @Override
    public void visit(Tree.Declaration that) {
        if (!(that instanceof Tree.TypeConstraint) && 
                isRefinement(that.getDeclarationModel())) {
            declarationNodes.add(that);
        }
        super.visit(that);
    }
        
}
