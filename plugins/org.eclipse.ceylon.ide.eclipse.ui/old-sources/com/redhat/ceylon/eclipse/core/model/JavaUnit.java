/********************************************************************************
 * Copyright (c) {date} Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.core.model;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;

import org.eclipse.ceylon.ide.common.model.BaseIdeModule;
import org.eclipse.ceylon.ide.common.model.IResourceAware;
import org.eclipse.ceylon.ide.common.model.IdeUnit;
import org.eclipse.ceylon.ide.common.util.toJavaIterable_;
import org.eclipse.ceylon.model.typechecker.model.Package;

public abstract class JavaUnit extends IdeUnit implements IJavaModelAware, IResourceAware {
    
    public JavaUnit(String fileName, 
            String relativePath, String fullPath, 
            Package pkg) {
        setFilename(fileName);
        setRelativePath(relativePath);
        setFullPath(fullPath);
        setPackage(pkg);
    }
    
    @Override
    public IFile getResourceFile() {
        if (getTypeRoot() != null) {
            try {
                return (IFile) 
                        getTypeRoot()
                            .getCorrespondingResource();
            } catch (JavaModelException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public IProject getResourceProject() {
        if (getResourceFile() != null) {
            return (IProject) 
                    getTypeRoot()
                        .getJavaProject()
                        .getProject();
        }
        return null;
    }

    @Override
    public IProject getProject() {
        return getResourceProject();
    }

    @Override
    public IFolder getResourceRootFolder() {
        if (getResourceFile() != null) {
            try {
                IPackageFragmentRoot root = 
                        (IPackageFragmentRoot) 
                            getTypeRoot()
                                .getAncestor(IJavaElement.PACKAGE_FRAGMENT_ROOT);
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
        assert (p.getModule() instanceof BaseIdeModule);
        BaseIdeModule module = (BaseIdeModule) p.getModule();
        for (BaseIdeModule moduleInReferencingProject :
                toJavaIterable_.toJavaIterable(
                        BaseIdeModule.$TypeDescriptor$, 
                        module.getModuleInReferencingProjects())) {
        	moduleInReferencingProject.removedOriginalUnit(getRelativePath());
        }
    }
}
