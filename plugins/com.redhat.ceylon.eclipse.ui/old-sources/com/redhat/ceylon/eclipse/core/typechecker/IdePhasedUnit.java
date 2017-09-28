package org.eclipse.ceylon.ide.eclipse.core.typechecker;

import java.lang.ref.WeakReference;
import java.util.List;

import org.antlr.runtime.CommonToken;

import org.eclipse.ceylon.compiler.typechecker.TypeChecker;
import org.eclipse.ceylon.model.typechecker.util.ModuleManager;
import org.eclipse.ceylon.compiler.typechecker.analyzer.ModuleSourceMapper;
import org.eclipse.ceylon.compiler.typechecker.context.PhasedUnit;
import org.eclipse.ceylon.compiler.typechecker.context.TypecheckerUnit;
import org.eclipse.ceylon.compiler.typechecker.io.VirtualFile;
import org.eclipse.ceylon.model.typechecker.model.Package;
import org.eclipse.ceylon.compiler.typechecker.tree.Tree.CompilationUnit;

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
    
    protected Unit createUnit() {
        Unit oldUnit = getUnit();
        Unit newUnit = newUnit();
        if (oldUnit != null) {
            newUnit.setFilename(oldUnit.getFilename());
            newUnit.setFullPath(oldUnit.getFullPath());
            newUnit.setRelativePath(oldUnit.getRelativePath());
            newUnit.setPackage(oldUnit.getPackage());
            newUnit.getDependentsOf().addAll(oldUnit.getDependentsOf());
        }
        return newUnit;
    }
    
    protected abstract Unit newUnit();
}
