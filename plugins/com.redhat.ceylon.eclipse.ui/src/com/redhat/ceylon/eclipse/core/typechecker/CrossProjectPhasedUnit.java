package com.redhat.ceylon.eclipse.core.typechecker;

import java.lang.ref.WeakReference;
import java.util.List;

import org.antlr.runtime.CommonToken;
import org.eclipse.core.resources.IProject;

import com.redhat.ceylon.compiler.typechecker.TypeChecker;
import com.redhat.ceylon.compiler.typechecker.analyzer.ModuleManager;
import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.io.VirtualFile;
import com.redhat.ceylon.compiler.typechecker.model.Package;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.CompilationUnit;
import com.redhat.ceylon.eclipse.core.builder.CeylonBuilder;

public class CrossProjectPhasedUnit extends ExternalPhasedUnit {

    private WeakReference<IProject> originalProjectRef = null;
    private WeakReference<ProjectPhasedUnit> originalProjectPhasedUnitRef = null;
    
    public CrossProjectPhasedUnit(CrossProjectPhasedUnit other) {
        super(other);
        originalProjectPhasedUnitRef = new WeakReference<ProjectPhasedUnit>(other.getOriginalProjectPhasedUnit());
    }

    public CrossProjectPhasedUnit(VirtualFile unitFile, VirtualFile srcDir,
            CompilationUnit cu, Package p, ModuleManager moduleManager,
            TypeChecker typeChecker, List<CommonToken> tokenStream, IProject originalProject) {
        super(unitFile, srcDir, cu, p, moduleManager, typeChecker, tokenStream);
        originalProjectRef = new WeakReference<IProject>(originalProject);
    }
    
    public ProjectPhasedUnit getOriginalProjectPhasedUnit() {
        if (originalProjectPhasedUnitRef == null) {
            IProject project = originalProjectRef.get();
            if (project != null) {
                TypeChecker originalTypeChecker = CeylonBuilder.getProjectTypeChecker(project);
                if (originalTypeChecker != null) {
                    ProjectPhasedUnit originalProjectPhasedUnit = (ProjectPhasedUnit) originalTypeChecker.getPhasedUnitFromRelativePath(getPathRelativeToSrcDir());
                    if (originalProjectPhasedUnit != null) {
                        originalProjectPhasedUnitRef = new WeakReference<ProjectPhasedUnit>(originalProjectPhasedUnit);
                    }
                }
            }
        }
        return originalProjectPhasedUnitRef != null ? originalProjectPhasedUnitRef.get() : null;
    }
}
