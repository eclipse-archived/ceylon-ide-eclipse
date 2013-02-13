package com.redhat.ceylon.eclipse.core.model;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;

public abstract class JavaUnit extends IdeUnit implements IJavaModelAware, IResourceAware {
    @Override
    public IFile getFileResource() {
        if (getJavaElement() != null) {
            try {
                return (IFile) getJavaElement().getCorrespondingResource();
            } catch (JavaModelException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public IProject getProjectResource() {
        if (getJavaElement() != null) {
            return (IProject) getJavaElement().getJavaProject().getProject();
        }
        return null;
    }

    @Override
    public IFolder getRootFolderResource() {
        if (getJavaElement() != null) {
            try {
                IPackageFragmentRoot root = (IPackageFragmentRoot) getJavaElement().getAncestor(IJavaElement.PACKAGE_FRAGMENT_ROOT);
                if (root != null) {
                    return (IFolder) root.getCorrespondingResource();
                }
            } catch (JavaModelException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
