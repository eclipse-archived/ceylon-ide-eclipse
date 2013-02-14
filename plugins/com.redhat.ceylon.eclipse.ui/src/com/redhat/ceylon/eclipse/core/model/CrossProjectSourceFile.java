package com.redhat.ceylon.eclipse.core.model;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;

import com.redhat.ceylon.eclipse.core.typechecker.CrossProjectPhasedUnit;
import com.redhat.ceylon.eclipse.core.typechecker.ProjectPhasedUnit;

public class CrossProjectSourceFile extends SourceFile implements IResourceAware {
    public CrossProjectSourceFile(CrossProjectPhasedUnit phasedUnit) {
        super(phasedUnit);
    }

    @Override
    public IProject getProjectResource() {
        ProjectPhasedUnit ppu = getPhasedUnit().getOriginalProjectPhasedUnit();
        return ppu != null ? ppu.getProjectResource() : null;
    }

    @Override
    public IFolder getRootFolderResource() {
        ProjectPhasedUnit ppu = getPhasedUnit().getOriginalProjectPhasedUnit();
        return ppu != null ? ppu.getSourceFolderResource() : null;
    }

    @Override
    public IFile getFileResource() {
        ProjectPhasedUnit ppu = getPhasedUnit().getOriginalProjectPhasedUnit();
        return ppu != null ? ppu.getSourceFileResource() : null;
    }

    @Override
    public CrossProjectPhasedUnit getPhasedUnit() {
        return (CrossProjectPhasedUnit) super.getPhasedUnit();
    }
    
    public ProjectSourceFile getOriginalSourceFile() {
        ProjectPhasedUnit ppu = getPhasedUnit().getOriginalProjectPhasedUnit();
        return ppu != null ? (ProjectSourceFile) ppu.getUnit() : null;
    }
}
