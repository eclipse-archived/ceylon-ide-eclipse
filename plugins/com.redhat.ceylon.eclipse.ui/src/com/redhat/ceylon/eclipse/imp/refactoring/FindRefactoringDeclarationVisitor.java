package com.redhat.ceylon.eclipse.imp.refactoring;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.eclipse.util.FindDeclarationVisitor;

final class FindRefactoringDeclarationVisitor extends FindDeclarationVisitor {
    FindRefactoringDeclarationVisitor(Declaration declaration) {
        super(declaration);
    }
    
    @Override
    protected boolean equals(Declaration x, Declaration y) {
        return x.getQualifiedName().equals(y.getQualifiedName());
    }
}