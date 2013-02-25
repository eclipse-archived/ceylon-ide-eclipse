package com.redhat.ceylon.eclipse.core.typechecker;

import java.lang.ref.WeakReference;
import java.util.List;

import org.antlr.runtime.CommonToken;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;

import com.redhat.ceylon.compiler.typechecker.TypeChecker;
import com.redhat.ceylon.compiler.typechecker.analyzer.ModuleManager;
import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.io.VirtualFile;
import com.redhat.ceylon.compiler.typechecker.model.Package;
import com.redhat.ceylon.compiler.typechecker.model.Unit;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.CompilationUnit;
import com.redhat.ceylon.eclipse.core.model.EditedSourceFile;

public class EditedPhasedUnit extends IdePhasedUnit {
    WeakReference<ProjectPhasedUnit> savedPhasedUnitRef;
    
    public EditedPhasedUnit(VirtualFile unitFile, VirtualFile srcDir,
            CompilationUnit cu, Package p, ModuleManager moduleManager,
            TypeChecker typeChecker, List<CommonToken> tokenStream, ProjectPhasedUnit savedPhasedUnit) {
        super(unitFile, srcDir, cu, p, moduleManager, typeChecker, tokenStream);
        savedPhasedUnitRef = new WeakReference<ProjectPhasedUnit>(savedPhasedUnit);
    }
    
    public EditedPhasedUnit(PhasedUnit other) {
        super(other);
    }

    @Override
    public Unit createUnit() {
        return new EditedSourceFile(this);
    }

    public ProjectPhasedUnit getOriginalPhasedUnit() {
        return savedPhasedUnitRef.get();
    }
    
    public IFile getSourceFileResource() {
        return getOriginalPhasedUnit() == null ? null
                : getOriginalPhasedUnit().getSourceFileResource();
    }
    

    public IFolder getSourceFolderResource() {
        return getOriginalPhasedUnit() == null ? null
                : getOriginalPhasedUnit().getSourceFolderResource();
    }
    

    public IProject getProjectResource() {
        return getOriginalPhasedUnit() == null ? null
                : getOriginalPhasedUnit().getProjectResource();
    }
}
