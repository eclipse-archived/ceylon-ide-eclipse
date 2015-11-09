//package com.redhat.ceylon.eclipse.core.model;
//
//import org.eclipse.core.runtime.IProgressMonitor;
//import org.eclipse.jdt.core.ICompilationUnit;
//import org.eclipse.jdt.core.IJavaElement;
//
//import com.redhat.ceylon.model.typechecker.model.Declaration;
//import com.redhat.ceylon.model.typechecker.model.Package;
//
//public class JavaCompilationUnit extends JavaUnit {
//    ICompilationUnit typeRoot;
//    CeylonToJavaMatcher ceylonToJavaMatcher;
//    
//    public JavaCompilationUnit(ICompilationUnit typeRoot, String fileName, String relativePath, String fullPath, Package pkg) {
//        super(fileName, relativePath, fullPath, pkg);
//        this.typeRoot = typeRoot;
//        ceylonToJavaMatcher = new CeylonToJavaMatcher(typeRoot);
//    }
//
//    @Override
//    public ICompilationUnit getTypeRoot() {
//        return typeRoot;
//    }
//
//    @Override
//    public IJavaElement toJavaElement(Declaration ceylonDeclaration, IProgressMonitor monitor) {
//        return ceylonToJavaMatcher.searchInClass(ceylonDeclaration, monitor);
//    }
//
//    @Override
//    public IJavaElement toJavaElement(Declaration ceylonDeclaration) {
//        return ceylonToJavaMatcher.searchInClass(ceylonDeclaration, null);
//    }
//
//    @Override
//    public String getSourceFileName() {
//        return getFilename();
//    }
//    
//    @Override
//    public String getSourceRelativePath() {
//        return getRelativePath();
//    }
//
//    @Override
//    public String getSourceFullPath() {
//        return getFullPath();
//    }
//}
