//package com.redhat.ceylon.eclipse.core.model;
//
//import org.eclipse.core.resources.IFile;
//import org.eclipse.core.resources.IFolder;
//import org.eclipse.core.resources.IProject;
//
//import com.redhat.ceylon.eclipse.core.typechecker.CrossProjectPhasedUnit;
//import com.redhat.ceylon.eclipse.core.typechecker.ProjectPhasedUnit;
//
//public class CrossProjectSourceFile extends ExternalSourceFile implements ICrossProjectReference, IResourceAware {
//    public CrossProjectSourceFile(CrossProjectPhasedUnit phasedUnit) {
//        super(phasedUnit);
//    }
//
//    @Override
//    public IProject getResourceProject() {
//        ProjectPhasedUnit ppu = getPhasedUnit().getOriginalProjectPhasedUnit();
//        return ppu != null ? ppu.getResourceProject() : null;
//    }
//
//    @Override
//    public IFolder getResourceRootFolder() {
//        ProjectPhasedUnit ppu = getPhasedUnit().getOriginalProjectPhasedUnit();
//        return ppu != null ? ppu.getResourceRootFolder() : null;
//    }
//
//    @Override
//    public IFile getResourceFile() {
//        ProjectPhasedUnit ppu = getPhasedUnit().getOriginalProjectPhasedUnit();
//        return ppu != null ? ppu.getResourceFile() : null;
//    }
//
//    @Override
//    public CrossProjectPhasedUnit getPhasedUnit() {
//        return (CrossProjectPhasedUnit) super.getPhasedUnit();
//    }
//    
//    public ProjectSourceFile getOriginalSourceFile() {
//        ProjectPhasedUnit ppu = getOriginalPhasedUnit();
//        return ppu != null ? (ProjectSourceFile) ppu.getUnit() : null;
//    }
//
//    @Override
//    public ProjectPhasedUnit getOriginalPhasedUnit() {
//        return getPhasedUnit().getOriginalProjectPhasedUnit();
//    }
//}
