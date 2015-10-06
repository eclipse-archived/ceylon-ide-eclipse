//package com.redhat.ceylon.eclipse.core.typechecker;
//
//import java.lang.ref.WeakReference;
//import java.util.List;
//
//import org.antlr.runtime.CommonToken;
//import org.eclipse.core.resources.IProject;
//
//import com.redhat.ceylon.compiler.typechecker.TypeChecker;
//import com.redhat.ceylon.compiler.typechecker.analyzer.ModuleSourceMapper;
//import com.redhat.ceylon.compiler.typechecker.context.TypecheckerUnit;
//import com.redhat.ceylon.compiler.typechecker.io.VirtualFile;
//import com.redhat.ceylon.compiler.typechecker.tree.Tree.CompilationUnit;
//import com.redhat.ceylon.eclipse.core.builder.CeylonBuilder;
//import com.redhat.ceylon.eclipse.core.model.CrossProjectSourceFile;
//import com.redhat.ceylon.model.typechecker.model.Package;
//import com.redhat.ceylon.model.typechecker.util.ModuleManager;
//
//public class CrossProjectPhasedUnit extends ExternalPhasedUnit {
//
//    private WeakReference<IProject> originalProjectRef = new WeakReference<IProject>(null);
//    private WeakReference<ProjectPhasedUnit> originalProjectPhasedUnitRef = new WeakReference<ProjectPhasedUnit>(null);
//    
//    public CrossProjectPhasedUnit(CrossProjectPhasedUnit other) {
//        super(other);
//        originalProjectRef = new WeakReference<IProject>(other.originalProjectRef.get());
//        originalProjectPhasedUnitRef = new WeakReference<ProjectPhasedUnit>(other.getOriginalProjectPhasedUnit());
//        
//    }
//
//    public CrossProjectPhasedUnit(VirtualFile unitFile, VirtualFile srcDir,
//            CompilationUnit cu, Package p, ModuleManager moduleManager,
//            ModuleSourceMapper moduleSourceMapper, TypeChecker typeChecker, List<CommonToken> tokenStream, IProject originalProject) {
//        super(unitFile, srcDir, cu, p, moduleManager, moduleSourceMapper, typeChecker, tokenStream);
//        originalProjectRef = new WeakReference<IProject>(originalProject);
//    }
//    
//    public ProjectPhasedUnit getOriginalProjectPhasedUnit() {
//        ProjectPhasedUnit originalPhasedUnit = originalProjectPhasedUnitRef.get(); 
//        if (originalPhasedUnit == null) {
//            IProject originalProject = originalProjectRef.get();
//            if (originalProject != null) {
//                TypeChecker originalTypeChecker = CeylonBuilder.getProjectTypeChecker(originalProject);
//                if (originalTypeChecker != null) {
//                    originalPhasedUnit = (ProjectPhasedUnit) originalTypeChecker.getPhasedUnitFromRelativePath(getPathRelativeToSrcDir());
//                    originalProjectPhasedUnitRef = new WeakReference<ProjectPhasedUnit>(originalPhasedUnit);
//                }
//            }
//        }
//        return originalPhasedUnit;
//    }
//    
//    @Override
//    protected TypecheckerUnit newUnit() {
//        return new CrossProjectSourceFile(this);
//    }
//
//    @Override
//    public CrossProjectSourceFile getUnit() {
//        return (CrossProjectSourceFile) super.getUnit();
//    }
//}
