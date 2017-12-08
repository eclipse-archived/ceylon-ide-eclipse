/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 1.0 which is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.java2ceylon;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;

import org.eclipse.ceylon.compiler.typechecker.io.VirtualFile;
import org.eclipse.ceylon.ide.common.platform.VfsServices;
import org.eclipse.ceylon.ide.common.vfs.FileVirtualFile;
import org.eclipse.ceylon.ide.common.vfs.FolderVirtualFile;
import org.eclipse.ceylon.ide.common.vfs.ResourceVirtualFile;

public interface VfsJ2C {

    VfsServices<IProject, IResource, IFolder, IFile> services();

    ResourceVirtualFile<IProject, IResource, IFolder, IFile> createVirtualResource(
            IResource resource, IProject project);

    FileVirtualFile<IProject, IResource, IFolder, IFile> createVirtualFile(IFile file, IProject project);

    FileVirtualFile<IProject, IResource, IFolder, IFile> createVirtualFile(
            IProject project, IPath path);

    FolderVirtualFile<IProject, IResource, IFolder, IFile> createVirtualFolder(
            IFolder folder, IProject project);

    FolderVirtualFile<IProject, IResource, IFolder, IFile> createVirtualFolder(
            IProject project, IPath path);

    boolean instanceOfIFileVirtualFile(VirtualFile file);

    FileVirtualFile<IProject, IResource, IFolder, IFile> getIFileVirtualFile(
            VirtualFile file);

    boolean instanceOfIFolderVirtualFile(VirtualFile file);

    FolderVirtualFile<IProject, IResource, IFolder, IFile> getIFolderVirtualFile(
            VirtualFile file);

}