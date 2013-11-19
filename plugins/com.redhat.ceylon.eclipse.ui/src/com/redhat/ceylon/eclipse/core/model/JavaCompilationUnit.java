package com.redhat.ceylon.eclipse.core.model;

import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.ICompilationUnit;

import com.redhat.ceylon.compiler.typechecker.model.Package;

public class JavaCompilationUnit extends JavaUnit {
    ICompilationUnit compilationUnitElement;

    public JavaCompilationUnit(ICompilationUnit typeRoot, String fileName, String relativePath, String fullPath, Package pkg) {
        super(fileName, relativePath, fullPath, pkg);
        compilationUnitElement = typeRoot;
    }

    @Override
    public ICompilationUnit getJavaElement() {
        return compilationUnitElement;
    }
}
