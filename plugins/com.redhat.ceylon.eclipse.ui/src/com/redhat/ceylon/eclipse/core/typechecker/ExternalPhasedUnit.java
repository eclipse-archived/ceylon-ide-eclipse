package com.redhat.ceylon.eclipse.core.typechecker;

import java.util.List;

import org.antlr.runtime.CommonToken;

import com.redhat.ceylon.compiler.typechecker.TypeChecker;
import com.redhat.ceylon.compiler.typechecker.analyzer.ModuleManager;
import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.io.VirtualFile;
import com.redhat.ceylon.compiler.typechecker.model.Package;
import com.redhat.ceylon.compiler.typechecker.model.Unit;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.CompilationUnit;
import com.redhat.ceylon.eclipse.core.model.ExternalSourceFile;

public class ExternalPhasedUnit extends IdePhasedUnit {
    public ExternalPhasedUnit(VirtualFile unitFile, VirtualFile srcDir,
            CompilationUnit cu, Package p, ModuleManager moduleManager,
            TypeChecker typeChecker, List<CommonToken> tokenStream) {
        super(unitFile, srcDir, cu, p, moduleManager, typeChecker, tokenStream);
    }
    
    public ExternalPhasedUnit(PhasedUnit other) {
        super(other);
    }

    @Override
    protected Unit createUnit() {
        return new ExternalSourceFile(this);
    }
    
    @Override
    public ExternalSourceFile getUnit() {
        return (ExternalSourceFile) super.getUnit();
    }

}
