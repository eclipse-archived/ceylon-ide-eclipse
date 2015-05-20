package com.redhat.ceylon.eclipse.core.typechecker;

import java.lang.ref.WeakReference;
import java.util.List;

import org.antlr.runtime.CommonToken;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;

import com.redhat.ceylon.compiler.typechecker.TypeChecker;
import com.redhat.ceylon.compiler.typechecker.analyzer.ModuleSourceMapper;
import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.context.TypecheckerUnit;
import com.redhat.ceylon.compiler.typechecker.io.VirtualFile;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.CompilationUnit;
import com.redhat.ceylon.eclipse.core.model.EditedSourceFile;
import com.redhat.ceylon.eclipse.core.model.ProjectSourceFile;
import com.redhat.ceylon.model.typechecker.model.Declaration;
import com.redhat.ceylon.model.typechecker.model.Overloadable;
import com.redhat.ceylon.model.typechecker.model.Package;
import com.redhat.ceylon.model.typechecker.model.Unit;
import com.redhat.ceylon.model.typechecker.util.ModuleManager;

public class EditedPhasedUnit extends IdePhasedUnit {
    WeakReference<ProjectPhasedUnit> savedPhasedUnitRef;
    
    public EditedPhasedUnit(VirtualFile unitFile, VirtualFile srcDir,
            CompilationUnit cu, Package p, ModuleManager moduleManager,
            ModuleSourceMapper moduleSourceMapper,
            TypeChecker typeChecker, List<CommonToken> tokenStream, ProjectPhasedUnit savedPhasedUnit) {
        super(unitFile, srcDir, cu, p, moduleManager, moduleSourceMapper, typeChecker, tokenStream);
        savedPhasedUnitRef = new WeakReference<ProjectPhasedUnit>(savedPhasedUnit);
        if (savedPhasedUnit!=null) {
            savedPhasedUnit.addWorkingCopy(this);
        }
    }
    
    public EditedPhasedUnit(PhasedUnit other) {
        super(other);
    }

    @Override
    public TypecheckerUnit newUnit() {
        return new EditedSourceFile(this);
    }
    
    @Override
    public EditedSourceFile getUnit() {
        return (EditedSourceFile) super.getUnit();
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
    
    @Override
    protected boolean reuseExistingDescriptorModels() {
        return true;
    }
    
    @Override
    public boolean shouldIgnoreOverload(Declaration overload,
            Declaration currentDeclaration) {
        Unit currentdeclarationUnit = currentDeclaration.getUnit();
        if (currentdeclarationUnit instanceof EditedSourceFile) {
            ProjectSourceFile projectSourceFile = ((EditedSourceFile)currentdeclarationUnit).getOriginalSourceFile();
            for (Declaration modelDecl : projectSourceFile.getDeclarations()) {
                if (modelDecl.equals(currentDeclaration)) {
                    return overload == modelDecl;
                }
            }
            
            for (Declaration modelDecl : projectSourceFile.getPackage().getMembers()) {
                if (modelDecl.isNative() && modelDecl instanceof Overloadable) {
                    for (Declaration anyOverload : ((Overloadable)modelDecl).getOverloads()) {
                        if (anyOverload.equals(currentDeclaration) 
                                && projectSourceFile.equals(anyOverload.getUnit())) {
                            return overload == anyOverload;
                        }
                    }
                }
            }
        }
        return false;
    }
}
