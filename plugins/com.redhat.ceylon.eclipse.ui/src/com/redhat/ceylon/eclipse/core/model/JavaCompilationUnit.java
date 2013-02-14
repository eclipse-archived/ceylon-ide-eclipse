package com.redhat.ceylon.eclipse.core.model;

import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.ITypeRoot;

public class JavaCompilationUnit extends JavaUnit {

    @Override
    public ICompilationUnit getJavaElement() {
        return null;
    }

    @Override
    public IResource getResource() {
        return null;
    }

}
