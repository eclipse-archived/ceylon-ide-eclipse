package com.redhat.ceylon.eclipse.core.model;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;

import com.redhat.ceylon.eclipse.core.typechecker.EditedPhasedUnit;
import com.redhat.ceylon.eclipse.core.typechecker.ProjectPhasedUnit;

public class EditedSourceFile extends SourceFile implements IResourceAware {
    public EditedSourceFile(EditedPhasedUnit phasedUnit) {
        super(phasedUnit);
    }

    @Override
    public EditedPhasedUnit getPhasedUnit() {
        return (EditedPhasedUnit) super.getPhasedUnit();
    }
    
    public ProjectSourceFile getOriginalSourceFile() {
        final EditedPhasedUnit pu = getPhasedUnit();
        ProjectPhasedUnit originalPhasedUnit = pu==null ? null : pu.getOriginalPhasedUnit();
        return originalPhasedUnit == null ? null : (ProjectSourceFile) originalPhasedUnit.getUnit();
    }
    
    @Override
    public IProject getProjectResource() {
        EditedPhasedUnit pu = getPhasedUnit();
        return pu==null ? null : pu.getProjectResource();
    }

    
    @Override
    public IFile getFileResource() {
        EditedPhasedUnit pu = getPhasedUnit();
        return pu==null ? null : pu.getFileResource();
    }

    @Override
    public IFolder getRootFolderResource() {
        EditedPhasedUnit pu = getPhasedUnit();
        return pu==null ? null : pu.getRootFolderResource();
    }
}
