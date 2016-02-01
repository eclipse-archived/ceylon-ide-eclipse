package com.redhat.ceylon.eclipse.core.model;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;

import com.redhat.ceylon.eclipse.core.typechecker.EditedPhasedUnit;
import com.redhat.ceylon.eclipse.core.typechecker.ProjectPhasedUnit;

public class EditedSourceFile extends ModifiableSourceFile {
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
    public IProject getResourceProject() {
        EditedPhasedUnit pu = getPhasedUnit();
        return pu==null ? null : pu.getResourceProject();
    }

    
    @Override
    public IFile getResourceFile() {
        EditedPhasedUnit pu = getPhasedUnit();
        return pu==null ? null : pu.getResourceFile();
    }

    @Override
    public IFolder getResourceRootFolder() {
        EditedPhasedUnit pu = getPhasedUnit();
        return pu==null ? null : pu.getResourceRootFolder();
    }
}
