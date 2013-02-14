package com.redhat.ceylon.eclipse.core.model;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;

import com.redhat.ceylon.eclipse.core.typechecker.ProjectPhasedUnit;
import com.redhat.ceylon.eclipse.core.vfs.ResourceVirtualFile;

public class ProjectSourceFile extends SourceFile {

    public ProjectSourceFile(ProjectPhasedUnit phasedUnit) {
        super(phasedUnit);
    }
    
    @Override
    public ProjectPhasedUnit getPhasedUnit() {
        return (ProjectPhasedUnit) super.getPhasedUnit();
    }

    public IProject getProject() {
        return getPhasedUnit().getProject();
    }

    
    @Override
    public IResource getResource() {
        return ((ResourceVirtualFile) (getPhasedUnit().getUnitFile())).getResource();
    }
}
