package com.redhat.ceylon.eclipse.vfs;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;

import com.redhat.ceylon.compiler.typechecker.io.VirtualFile;

public class IFileVirtualFile extends ResourceVirtualFile {

    public IFileVirtualFile(IProject project, IPath path) {
        super(project, path);
    }

    IFileVirtualFile(IResource resource) {
        super(resource);
    }

    @Override
    protected IResource createResourceFromIPath() {
        return project.getFolder(path);
    }
    
    @Override
    public boolean isFolder() {
        return false;
    }

    @Override
    public InputStream getInputStream() {
        IFile file = (IFile) resource;
        try {
            return file.getContents(true);
        } catch (CoreException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<VirtualFile> getChildren() {
        return new ArrayList<VirtualFile>();
    }
    
}
