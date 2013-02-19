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
        ProjectPhasedUnit originalPhasedUnit = getPhasedUnit().getOriginalPhasedUnit();
        return originalPhasedUnit == null ? null : (ProjectSourceFile) originalPhasedUnit.getUnit();
    }
    
    @Override
    public IProject getProjectResource() {
        return getPhasedUnit().getProjectResource();
    }

    
    @Override
    public IFile getFileResource() {
        return getPhasedUnit().getSourceFileResource();
    }

    @Override
    public IFolder getRootFolderResource() {
        return getPhasedUnit().getSourceFolderResource();
    }
}
