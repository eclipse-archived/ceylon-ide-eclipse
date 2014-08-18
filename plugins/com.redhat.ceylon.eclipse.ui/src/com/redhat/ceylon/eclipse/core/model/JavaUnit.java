package com.redhat.ceylon.eclipse.core.model;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;

import com.redhat.ceylon.compiler.typechecker.model.Package;

public abstract class JavaUnit extends IdeUnit implements IJavaModelAware, IResourceAware {
    
    public JavaUnit(String fileName, String relativePath, String fullPath, Package pkg) {
        setFilename(fileName);
        setRelativePath(relativePath);
        setFullPath(fullPath);
        setPackage(pkg);
    }
    
    @Override
    public IFile getFileResource() {
        if (getTypeRoot() != null) {
            try {
                return (IFile) getTypeRoot().getCorrespondingResource();
            } catch (JavaModelException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public IProject getProjectResource() {
        if (getFileResource() != null) {
            return (IProject) getTypeRoot().getJavaProject().getProject();
        }
        return null;
    }

    @Override
    public IProject getProject() {
        return getProjectResource();
    }

    @Override
    public IFolder getRootFolderResource() {
        if (getFileResource() != null) {
            try {
                IPackageFragmentRoot root = (IPackageFragmentRoot) getTypeRoot().getAncestor(IJavaElement.PACKAGE_FRAGMENT_ROOT);
                if (root != null) {
                    return (IFolder) root.getCorrespondingResource();
                }
            } catch (JavaModelException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public void remove() {
        Package p = getPackage();
        p.removeUnit(this);
        assert (p.getModule() instanceof JDTModule);
        JDTModule module = (JDTModule) p.getModule();
        module.removedOriginalUnit(getRelativePath());
    }
}
