/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.core.vfs;

import static org.eclipse.ceylon.ide.eclipse.java2ceylon.Java2CeylonProxies.platformJ2C;
import static org.eclipse.ceylon.ide.eclipse.java2ceylon.Java2CeylonProxies.utilJ2C;
import static org.eclipse.ceylon.ide.eclipse.util.CeylonHelper.td;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;

import org.eclipse.ceylon.compiler.typechecker.io.VirtualFile;
import org.eclipse.ceylon.ide.eclipse.java2ceylon.VfsJ2C;
import org.eclipse.ceylon.ide.common.platform.VfsServices;
import org.eclipse.ceylon.ide.common.vfs.FileVirtualFile;
import org.eclipse.ceylon.ide.common.vfs.FolderVirtualFile;
import org.eclipse.ceylon.ide.common.vfs.ResourceVirtualFile;

public class vfsJ2C implements VfsJ2C {
    @Override
    public VfsServices<IProject, IResource, IFolder, IFile> services() {
        return platformJ2C().platformServices().vfs(td(IProject.class), td(IResource.class), td(IFolder.class), td(IFile.class));
    }

    @Override
    public ResourceVirtualFile<IProject, IResource, IFolder, IFile> createVirtualResource(
            IResource resource, IProject project) {
        return services().createVirtualResource(resource, project);
    }

    @Override
    public FileVirtualFile<IProject, IResource, IFolder, IFile> createVirtualFile(IFile file, IProject project) {
        return services().createVirtualFile(file, project);
    }
    
    @Override
    public FileVirtualFile<IProject, IResource, IFolder, IFile> createVirtualFile(IProject project, IPath path) {
        return services().createVirtualFileFromProject(project, utilJ2C().fromEclipsePath(path));
    }
    
    @Override
    public FolderVirtualFile<IProject, IResource, IFolder, IFile> createVirtualFolder(IFolder folder, IProject project) {
        return services().createVirtualFolder(folder, project);
    }
    
    @Override
    public FolderVirtualFile<IProject, IResource, IFolder, IFile> createVirtualFolder(IProject project, IPath path) {
        return services().createVirtualFolderFromProject(project, utilJ2C().fromEclipsePath(path));
    }
    
    @Override
    public boolean instanceOfIFileVirtualFile(VirtualFile file) {
        return file instanceof IFileVirtualFile;
    }

    @Override
    public FileVirtualFile<IProject, IResource, IFolder, IFile> getIFileVirtualFile(VirtualFile file) {
        return (IFileVirtualFile) file;
    }

    @Override
    public boolean instanceOfIFolderVirtualFile(VirtualFile file) {
        return file instanceof IFolderVirtualFile;
    }

    @Override
    public FolderVirtualFile<IProject, IResource, IFolder, IFile> getIFolderVirtualFile(VirtualFile file) {
        return (IFolderVirtualFile) file;
    }
}
