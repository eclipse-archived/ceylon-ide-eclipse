package com.redhat.ceylon.eclipse.core.model;

import org.eclipse.jdt.core.IClassFile;

import com.redhat.ceylon.compiler.typechecker.model.Package;

public class JavaClassFile extends JavaUnit {

    IClassFile classFileElement;
    
    public JavaClassFile(IClassFile typeRoot, String fileName, String relativePath, String fullPath, Package pkg) {
        super(fileName, relativePath, fullPath, pkg);
        classFileElement = typeRoot;
    }

    @Override
    public IClassFile getJavaElement() {
        return classFileElement;
    }
}
