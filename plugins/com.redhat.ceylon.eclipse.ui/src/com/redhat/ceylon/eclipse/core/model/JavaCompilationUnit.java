package com.redhat.ceylon.eclipse.core.model;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.Package;

public class JavaCompilationUnit extends JavaUnit {
    ICompilationUnit typeRoot;

    public JavaCompilationUnit(ICompilationUnit typeRoot, String fileName, String relativePath, String fullPath, Package pkg) {
        super(fileName, relativePath, fullPath, pkg);
        this.typeRoot = typeRoot;
    }

    @Override
    public ICompilationUnit getTypeRoot() {
        return typeRoot;
    }

    @Override
    public IJavaElement toJavaElement(final Declaration ceylonDeclaration) {
        return new CeylonToJavaMatcher(this).searchInClass(ceylonDeclaration);
    }
}
