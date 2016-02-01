package com.redhat.ceylon.eclipse.core.model;

//import java.lang.ref.WeakReference;
//
//import org.eclipse.core.resources.IFile;
//import org.eclipse.core.resources.IFolder;
//import org.eclipse.core.resources.IProject;
//import org.eclipse.core.resources.IResource;
//import org.eclipse.jdt.core.IClassFile;
//
//import com.redhat.ceylon.compiler.typechecker.TypeChecker;
//import com.redhat.ceylon.eclipse.core.builder.CeylonBuilder;
//import com.redhat.ceylon.ide.common.typechecker.CrossProjectPhasedUnit;
//import com.redhat.ceylon.ide.common.typechecker.ProjectPhasedUnit;
//import com.redhat.ceylon.model.typechecker.model.Package;
//
//public class CrossProjectBinaryUnit extends CeylonBinaryUnit implements ICrossProjectReference {
//    private WeakReference<ProjectPhasedUnit> originalProjectPhasedUnitRef = new WeakReference<ProjectPhasedUnit>(null);
//
//    public CrossProjectBinaryUnit(IClassFile typeRoot, String fileName, String relativePath, String fullPath, Package pkg) {
//        super(typeRoot, fileName, relativePath, fullPath, pkg);
//    }
//    
//    @Override
//    public IProject getResourceProject() {
//        CrossProjectPhasedUnit<IProject,IResource,IFolder,IFile> pu = getPhasedUnit();
//        ProjectPhasedUnit<IProject,IResource,IFolder,IFile> ppu = pu!=null ? pu.getOriginalProjectPhasedUnit() : null;
//        return ppu != null ? ppu.getResourceProject() : null;
//    }
//
//    @Override
//    public IFolder getResourceRootFolder() {
//        CrossProjectPhasedUnit<IProject,IResource,IFolder,IFile> pu = getPhasedUnit();
//        ProjectPhasedUnit<IProject,IResource,IFolder,IFile> ppu = pu!=null ? pu.getOriginalProjectPhasedUnit() : null;
//        return ppu != null ? ppu.getResourceRootFolder() : null;
//    }
//
//    @Override
//    public IFile getResourceFile() {
//        ProjectPhasedUnit<IProject,IResource,IFolder,IFile> ppu = 
//                getPhasedUnit()
//                    .getOriginalProjectPhasedUnit();
//        return ppu != null ? ppu.getResourceFile() : null;
//    }
//
//    @Override
//    public CrossProjectPhasedUnit<IProject,IResource,IFolder,IFile> getPhasedUnit() {
//        return (CrossProjectPhasedUnit<IProject,IResource,IFolder,IFile>) super.getPhasedUnit();
//    }
//    
//    public ProjectSourceFile getOriginalSourceFile() {
//        ProjectPhasedUnit<IProject,IResource,IFolder,IFile> ppu = getOriginalPhasedUnit();
//        return ppu != null ? (ProjectSourceFile) ppu.getUnit() : null;
//    }
//
//    @Override
//    public ProjectPhasedUnit getOriginalPhasedUnit() {
//        ProjectPhasedUnit originalPhasedUnit = originalProjectPhasedUnitRef.get();
//        if (originalPhasedUnit == null) {
//            JDTModule module = getModule();        
//            IProject originalProject = module.getOriginalProject();
//            if (originalProject != null) {
//                TypeChecker originalTypeChecker = CeylonBuilder.getProjectTypeChecker(originalProject);
//                if (originalTypeChecker != null) {
//                    String sourceRelativePath = module.toSourceUnitRelativePath(getRelativePath());
//                    originalPhasedUnit = (ProjectPhasedUnit) originalTypeChecker.getPhasedUnitFromRelativePath(sourceRelativePath);
//                    if (originalPhasedUnit != null) {
//                        originalProjectPhasedUnitRef = new WeakReference<ProjectPhasedUnit>(originalPhasedUnit);
//                    }
//                }
//            }
//        }
//
//        return originalPhasedUnit;
//    }
//}
