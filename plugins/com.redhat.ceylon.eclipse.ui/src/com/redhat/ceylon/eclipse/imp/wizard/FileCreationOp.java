package com.redhat.ceylon.eclipse.imp.wizard;

import static java.util.Collections.reverse;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jface.operation.IRunnableWithProgress;

class FileCreationOp implements IRunnableWithProgress {

    private final IPackageFragmentRoot sourceDir;
    private final IPackageFragment packageFragment;
    private final String unitName;
    private final boolean includePreamble;
    private final String contents;
    
    private  IFile result;
    
    IFile getResult() {
        return result;
    }
    
    FileCreationOp(IPackageFragmentRoot sourceDir,
            IPackageFragment packageFragment, String unitName,
            boolean includePreamble, String contents) {
        this.sourceDir = sourceDir;
        this.packageFragment = packageFragment;
        this.unitName = unitName;
        this.includePreamble = includePreamble;
        this.contents = contents;
    }
    
    public void run(IProgressMonitor monitor) {
        IPath path = packageFragment.getPath().append(unitName + ".ceylon");
        IProject project = sourceDir.getJavaProject().getProject();
        InputStream his = getHeader(project);
        result = project.getFile(path.makeRelativeTo(project.getFullPath()));

        List<IFolder> resourcesToCreate = new LinkedList<IFolder>();
        IContainer parent = result.getParent();
        while (!parent.exists() && (parent instanceof IFolder)) {
            resourcesToCreate.add((IFolder)parent);
            parent = parent.getParent();
        }
        reverse(resourcesToCreate);
        
        try {
            for (IFolder pkg : resourcesToCreate) {
                pkg.create(false, false, monitor);
            }
            result.create(his, false, monitor);
            result.appendContents(new ByteArrayInputStream(contents.getBytes()), 
                    false, false, monitor);
            parent.refreshLocal(IResource.DEPTH_ZERO, monitor);
            for (IFolder pkg : resourcesToCreate) {
                pkg.refreshLocal(IResource.DEPTH_ZERO, monitor);
            }
        }
        catch (CoreException ce) {
            ce.printStackTrace();
        }
        finally {
            try {
                his.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    private InputStream getHeader(IProject project) {
        IFile header = project.getFile("header.ceylon");
        InputStream his = new ByteArrayInputStream(new byte[0]);
        if (includePreamble && header.exists() && 
                header.isAccessible()) {
            try {
                his = header.getContents();
            }
            catch (CoreException e) {
                e.printStackTrace();
            }
            
        }
        return his;
    }
}