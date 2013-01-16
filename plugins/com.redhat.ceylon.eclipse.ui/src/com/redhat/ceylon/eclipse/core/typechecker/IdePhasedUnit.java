package com.redhat.ceylon.eclipse.core.typechecker;

import java.util.List;

import org.antlr.runtime.CommonToken;

import com.redhat.ceylon.compiler.typechecker.TypeChecker;
import com.redhat.ceylon.compiler.typechecker.analyzer.ModuleManager;
import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.io.VirtualFile;
import com.redhat.ceylon.compiler.typechecker.model.Package;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.CompilationUnit;

public class IdePhasedUnit extends PhasedUnit {

    private TypeChecker typeChecker;

    public IdePhasedUnit(VirtualFile unitFile, VirtualFile srcDir,
            CompilationUnit cu, Package p, ModuleManager moduleManager,
            TypeChecker typeChecker, List<CommonToken> tokenStream) {
        super(unitFile, srcDir, cu, p, moduleManager, typeChecker.getContext(), tokenStream);
        this.typeChecker = typeChecker;
    }
    
    public IdePhasedUnit(PhasedUnit other) {
        super(other);
        if (other instanceof IdePhasedUnit) {
            typeChecker = ((IdePhasedUnit) other).typeChecker;
        }
    }

    public TypeChecker getTypeChecker() {
        return typeChecker;
    }
}
