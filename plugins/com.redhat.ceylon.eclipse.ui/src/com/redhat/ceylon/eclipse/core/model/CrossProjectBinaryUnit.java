package com.redhat.ceylon.eclipse.core.model;

import java.lang.ref.WeakReference;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IClassFile;

import com.redhat.ceylon.compiler.typechecker.TypeChecker;
import com.redhat.ceylon.eclipse.core.builder.CeylonBuilder;
import com.redhat.ceylon.eclipse.core.typechecker.CrossProjectPhasedUnit;
import com.redhat.ceylon.eclipse.core.typechecker.ProjectPhasedUnit;
import com.redhat.ceylon.model.typechecker.model.Package;

public class CrossProjectBinaryUnit extends CeylonBinaryUnit implements ICrossProjectReference {
    private WeakReference<ProjectPhasedUnit> originalProjectPhasedUnitRef = new WeakReference<ProjectPhasedUnit>(null);

    public CrossProjectBinaryUnit(IClassFile typeRoot, String fileName, String relativePath, String fullPath, Package pkg) {
        super(typeRoot, fileName, relativePath, fullPath, pkg);
    }
    
    @Override
    public IProject getResourceProject() {
        CrossProjectPhasedUnit pu = getPhasedUnit();
        ProjectPhasedUnit ppu = pu!=null ? pu.getOriginalProjectPhasedUnit() : null;
        return ppu != null ? ppu.getResourceProject() : null;
    }

    @Override
    public IFolder getResourceRootFolder() {
        CrossProjectPhasedUnit pu = getPhasedUnit();
        ProjectPhasedUnit ppu = pu!=null ? pu.getOriginalProjectPhasedUnit() : null;
        return ppu != null ? ppu.getResourceRootFolder() : null;
    }

    @Override
    public IFile getResourceFile() {
        ProjectPhasedUnit ppu = 
                getPhasedUnit()
                    .getOriginalProjectPhasedUnit();
        return ppu != null ? ppu.getResourceFile() : null;
    }

    @Override
    public CrossProjectPhasedUnit getPhasedUnit() {
        return (CrossProjectPhasedUnit) super.getPhasedUnit();
    }
    
    public ProjectSourceFile getOriginalSourceFile() {
        ProjectPhasedUnit ppu = getOriginalPhasedUnit();
        return ppu != null ? (ProjectSourceFile) ppu.getUnit() : null;
    }

    @Override
    public ProjectPhasedUnit getOriginalPhasedUnit() {
        ProjectPhasedUnit originalPhasedUnit = originalProjectPhasedUnitRef.get();
        if (originalPhasedUnit == null) {
            JDTModule module = getModule();        
            IProject originalProject = module.getOriginalProject();
            if (originalProject != null) {
                TypeChecker originalTypeChecker = CeylonBuilder.getProjectTypeChecker(originalProject);
                if (originalTypeChecker != null) {
                    String sourceRelativePath = module.toSourceUnitRelativePath(getRelativePath());
                    originalPhasedUnit = (ProjectPhasedUnit) originalTypeChecker.getPhasedUnitFromRelativePath(sourceRelativePath);
                    if (originalPhasedUnit != null) {
                        originalProjectPhasedUnitRef = new WeakReference<ProjectPhasedUnit>(originalPhasedUnit);
                    }
                }
            }
        }

        return originalPhasedUnit;
    }
}
