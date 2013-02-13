package com.redhat.ceylon.eclipse.core.model;

import org.eclipse.jdt.core.ICompilationUnit;

public class JavaCompilationUnit extends JavaUnit {
    ICompilationUnit compilationUnitElement;

    public JavaCompilationUnit(ICompilationUnit typeRoot) {
        super();
        compilationUnitElement = typeRoot;
    }

    @Override
    public ICompilationUnit getJavaElement() {
        return compilationUnitElement;
    }
}
