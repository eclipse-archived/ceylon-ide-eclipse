package com.redhat.ceylon.eclipse.core.model;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;

import com.redhat.ceylon.eclipse.core.typechecker.ProjectPhasedUnit;
import com.redhat.ceylon.eclipse.core.vfs.ResourceVirtualFile;

public class ProjectSourceFile extends SourceFile implements IResourceAware {

    public ProjectSourceFile(ProjectPhasedUnit phasedUnit) {
        super(phasedUnit);
    }
    
    @Override
    public ProjectPhasedUnit getPhasedUnit() {
        return (ProjectPhasedUnit) super.getPhasedUnit();
    }

    @Override
    public IProject getProjectResource() {
        return getPhasedUnit().getProjectResource();
    }

    
    @Override
    public IFile getFileResource() {
        return (IFile)((ResourceVirtualFile) (getPhasedUnit().getUnitFile())).getResource();
    }

    @Override
    public IFolder getRootFolderResource() {
        return null;
    }
}
