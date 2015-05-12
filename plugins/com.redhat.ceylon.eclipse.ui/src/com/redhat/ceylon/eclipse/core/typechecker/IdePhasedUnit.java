package com.redhat.ceylon.eclipse.core.typechecker;

import java.lang.ref.WeakReference;
import java.util.List;

import org.antlr.runtime.CommonToken;

import com.redhat.ceylon.compiler.typechecker.TypeChecker;
import com.redhat.ceylon.model.typechecker.util.ModuleManager;
import com.redhat.ceylon.compiler.typechecker.analyzer.ModuleSourceMapper;
import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.context.TypecheckerUnit;
import com.redhat.ceylon.compiler.typechecker.io.VirtualFile;
import com.redhat.ceylon.model.typechecker.model.Package;
import com.redhat.ceylon.model.typechecker.model.Unit;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.CompilationUnit;

public abstract class IdePhasedUnit extends PhasedUnit {

    protected WeakReference<TypeChecker> typeCheckerRef = null;

    public IdePhasedUnit(VirtualFile unitFile, VirtualFile srcDir,
            CompilationUnit cu, Package p, ModuleManager moduleManager,
            ModuleSourceMapper moduleSourceMapper,
            TypeChecker typeChecker, List<CommonToken> tokenStream) {
        super(unitFile, srcDir, cu, p, moduleManager, moduleSourceMapper, typeChecker.getContext(), tokenStream);
        typeCheckerRef = new WeakReference<TypeChecker>(typeChecker);
    }
    
    public IdePhasedUnit(PhasedUnit other) {
        super(other);
        if (other instanceof IdePhasedUnit) {
            typeCheckerRef = new WeakReference<TypeChecker>(((IdePhasedUnit) other).getTypeChecker());
        }
    }

    public TypeChecker getTypeChecker() {
        return typeCheckerRef.get();
    }
    
    protected TypecheckerUnit createUnit() {
        TypecheckerUnit oldUnit = getUnit();
        TypecheckerUnit newUnit = newUnit();
        if (oldUnit != null) {
            newUnit.setFilename(oldUnit.getFilename());
            newUnit.setFullPath(oldUnit.getFullPath());
            newUnit.setRelativePath(oldUnit.getRelativePath());
            newUnit.setPackage(oldUnit.getPackage());
            newUnit.getDependentsOf().addAll(oldUnit.getDependentsOf());
        }
        return newUnit;
    }
    

    protected abstract TypecheckerUnit newUnit();
}
