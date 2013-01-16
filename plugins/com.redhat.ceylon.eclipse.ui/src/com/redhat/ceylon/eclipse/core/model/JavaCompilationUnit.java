package com.redhat.ceylon.eclipse.core.model;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.ITypeRoot;

public class JavaCompilationUnit extends JavaUnit {

    @Override
    public ICompilationUnit getJavaElement() {
        return null;
    }

    @Override
    public IFile getFileResource() {
        return null;
    }

    @Override
    public IProject getProjectResource() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public IFolder getRootFolderResource() {
        // TODO Auto-generated method stub
        return null;
    }

}
