package com.redhat.ceylon.eclipse.core.vfs;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;

import com.redhat.ceylon.compiler.typechecker.io.VirtualFile;
import com.redhat.ceylon.ide.common.vfs.FileVirtualFile;
import com.redhat.ceylon.ide.common.vfs.ResourceVirtualFile;
import com.redhat.ceylon.ide.common.vfs.FolderVirtualFile;

public class vfsJ2C {
    public static ResourceVirtualFile<IResource, IFolder, IFile> createVirtualResource(IResource resource) {
        return eclipseVFS_.get_().createVirtualResource(resource);
    }

    public static FileVirtualFile<IResource, IFolder, IFile> createVirtualFile(IFile file) {
        return eclipseVFS_.get_().createVirtualFile(file);
    }
    
    public static FileVirtualFile<IResource, IFolder, IFile> createVirtualFile(IProject project, IPath path) {
        return eclipseVFS_.get_().createVirtualFileFromProject(project, path);
    }
    
    public static FolderVirtualFile<IResource, IFolder, IFile> createVirtualFolder(IFolder folder) {
        return eclipseVFS_.get_().createVirtualFolder(folder);
    }
    
    public static FolderVirtualFile<IResource, IFolder, IFile> createVirtualFolder(IProject project, IPath path) {
        return eclipseVFS_.get_().createVirtualFolderFromProject(project, path);
    }
    
    public static boolean instanceOfIFileVirtualFile(VirtualFile file) {
        return file instanceof IFileVirtualFile;
    }

    public static FileVirtualFile<IResource, IFolder, IFile> getIFileVirtualFile(VirtualFile file) {
        return (IFileVirtualFile) file;
    }

    public static boolean instanceOfIFolderVirtualFile(VirtualFile file) {
        return file instanceof IFolderVirtualFile;
    }

    public static FolderVirtualFile<IResource, IFolder, IFile> getIFolderVirtualFile(VirtualFile file) {
        return (IFolderVirtualFile) file;
    }

}
