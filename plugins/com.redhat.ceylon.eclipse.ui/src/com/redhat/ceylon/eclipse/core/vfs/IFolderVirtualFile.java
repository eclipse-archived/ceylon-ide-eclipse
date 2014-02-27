package com.redhat.ceylon.eclipse.core.vfs;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;

import com.redhat.ceylon.compiler.typechecker.io.VirtualFile;

public class IFolderVirtualFile extends ResourceVirtualFile {
    public IFolderVirtualFile(IProject project, IPath path) {
        super(project, path);
    }

    IFolderVirtualFile(IFolder resource) {
        super(resource);
    }
    
    protected IFolder createResourceFromIPath() {
        return project.getFolder(path);
    }

    @Override
    public boolean isFolder() {
        return true;
    }

    @Override
    public InputStream getInputStream() {
        return null;
    }

    @Override
    public List<VirtualFile> getChildren() {
        List<VirtualFile> children = new ArrayList<VirtualFile>();
        IFolder folder = (IFolder) resource;
        try {
            for (IResource childResource : folder.members())
            {
                children.add(ResourceVirtualFile.createResourceVirtualFile(childResource));
            }
        } catch (CoreException e) {
            e.printStackTrace();
        }
        
        return children;
    }
    
    public IFolder getFolder() {
        return (IFolder) resource;
    }
}
