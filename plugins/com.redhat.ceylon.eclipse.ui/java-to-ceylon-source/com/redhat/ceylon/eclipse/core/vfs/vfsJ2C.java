package com.redhat.ceylon.eclipse.core.vfs;

import static com.redhat.ceylon.eclipse.java2ceylon.Java2CeylonProxies.*;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;

import com.redhat.ceylon.compiler.typechecker.io.VirtualFile;
import com.redhat.ceylon.eclipse.java2ceylon.VfsJ2C;
import com.redhat.ceylon.ide.common.model.CeylonProjects;
import com.redhat.ceylon.ide.common.vfs.FileVirtualFile;
import com.redhat.ceylon.ide.common.vfs.ResourceVirtualFile;
import com.redhat.ceylon.ide.common.vfs.FolderVirtualFile;

public class vfsJ2C implements VfsJ2C {
    @Override
    public CeylonProjects<IProject,IResource, IFolder, IFile>.VirtualFileSystem eclipseVFS() {
        return modelJ2C().ceylonModel().getVfs();
    }
    
    @Override
    public ResourceVirtualFile<IResource, IFolder, IFile> createVirtualResource(IResource resource) {
        return eclipseVFS().createVirtualResource(resource);
    }

    @Override
    public FileVirtualFile<IResource, IFolder, IFile> createVirtualFile(IFile file) {
        return eclipseVFS().createVirtualFile(file);
    }
    
    @Override
    public FileVirtualFile<IResource, IFolder, IFile> createVirtualFile(IProject project, IPath path) {
        return eclipseVFS().createVirtualFileFromProject(project, utilJ2C().fromEclipsePath(path));
    }
    
    @Override
    public FolderVirtualFile<IResource, IFolder, IFile> createVirtualFolder(IFolder folder) {
        return eclipseVFS().createVirtualFolder(folder);
    }
    
    @Override
    public FolderVirtualFile<IResource, IFolder, IFile> createVirtualFolder(IProject project, IPath path) {
        return eclipseVFS().createVirtualFolderFromProject(project, utilJ2C().fromEclipsePath(path));
    }
    
    @Override
    public boolean instanceOfIFileVirtualFile(VirtualFile file) {
        return file instanceof IFileVirtualFile;
    }

    @Override
    public FileVirtualFile<IResource, IFolder, IFile> getIFileVirtualFile(VirtualFile file) {
        return (IFileVirtualFile) file;
    }

    @Override
    public boolean instanceOfIFolderVirtualFile(VirtualFile file) {
        return file instanceof IFolderVirtualFile;
    }

    @Override
    public FolderVirtualFile<IResource, IFolder, IFile> getIFolderVirtualFile(VirtualFile file) {
        return (IFolderVirtualFile) file;
    }

}
