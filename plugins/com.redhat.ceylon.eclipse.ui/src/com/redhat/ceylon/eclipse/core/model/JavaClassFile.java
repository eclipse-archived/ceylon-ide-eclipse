package com.redhat.ceylon.eclipse.core.model;

import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.IJavaElement;

import com.redhat.ceylon.model.typechecker.model.Declaration;
import com.redhat.ceylon.model.typechecker.model.Package;

public class JavaClassFile extends JavaUnit {

    IClassFile classFileElement;
    
    public JavaClassFile(IClassFile typeRoot, String fileName, String relativePath, String fullPath, Package pkg) {
        super(fileName, relativePath, fullPath, pkg);
        classFileElement = typeRoot;
    }

    @Override
    public IClassFile getTypeRoot() {
        return classFileElement;
    }

    @Override
    public IJavaElement toJavaElement(Declaration ceylonDeclaration) {
        return new CeylonToJavaMatcher(this).searchInClass(ceylonDeclaration);
    }
}
