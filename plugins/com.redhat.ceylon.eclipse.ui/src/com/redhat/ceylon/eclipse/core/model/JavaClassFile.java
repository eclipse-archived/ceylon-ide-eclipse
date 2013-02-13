package com.redhat.ceylon.eclipse.core.model;

import org.eclipse.jdt.core.IClassFile;

public class JavaClassFile extends JavaUnit {

    IClassFile classFileElement;
    
    public JavaClassFile(IClassFile typeRoot) {
        super();
        classFileElement = typeRoot;
    }

    @Override
    public IClassFile getJavaElement() {
        return classFileElement;
    }
}
