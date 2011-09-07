package com.redhat.ceylon.eclipse.imp.refactoring;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.util.FindReferenceVisitor;

public class FindRefactoringReferenceVisitor extends FindReferenceVisitor {
    
    public FindRefactoringReferenceVisitor(Declaration declaration) {
        super(declaration);
    }
    
    @Override
    public void visit(Tree.ExtendedTypeExpression that) {}
    
    @Override
    protected boolean equals(Declaration x, Declaration y) {
        //TODO: surely there's got to be a more robust
        //      way to do this:
        try {
            return x.getQualifiedName().equals(y.getQualifiedName());
        }
        catch (UnsupportedOperationException uoe) {
            //a union or intersection type
            return false;
        }
    }
    
}