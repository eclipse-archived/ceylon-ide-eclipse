package com.redhat.ceylon.eclipse.java2ceylon;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;

import com.redhat.ceylon.compiler.typechecker.io.VirtualFile;
import com.redhat.ceylon.ide.common.model.CeylonProjects;
import com.redhat.ceylon.ide.common.vfs.FileVirtualFile;
import com.redhat.ceylon.ide.common.vfs.FolderVirtualFile;
import com.redhat.ceylon.ide.common.vfs.ResourceVirtualFile;

public interface VfsJ2C {

    CeylonProjects<IProject, IResource, IFolder, IFile>.VirtualFileSystem eclipseVFS();

    ResourceVirtualFile<IResource, IFolder, IFile> createVirtualResource(
            IResource resource);

    FileVirtualFile<IResource, IFolder, IFile> createVirtualFile(IFile file);

    FileVirtualFile<IResource, IFolder, IFile> createVirtualFile(
            IProject project, IPath path);

    FolderVirtualFile<IResource, IFolder, IFile> createVirtualFolder(
            IFolder folder);

    FolderVirtualFile<IResource, IFolder, IFile> createVirtualFolder(
            IProject project, IPath path);

    boolean instanceOfIFileVirtualFile(VirtualFile file);

    FileVirtualFile<IResource, IFolder, IFile> getIFileVirtualFile(
            VirtualFile file);

    boolean instanceOfIFolderVirtualFile(VirtualFile file);

    FolderVirtualFile<IResource, IFolder, IFile> getIFolderVirtualFile(
            VirtualFile file);

}