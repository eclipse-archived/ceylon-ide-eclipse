package org.eclipse.ceylon.ide.eclipse.core.typechecker;

import java.util.List;

import org.antlr.runtime.CommonToken;

import org.eclipse.ceylon.compiler.typechecker.TypeChecker;
import org.eclipse.ceylon.compiler.typechecker.analyzer.ModuleSourceMapper;
import org.eclipse.ceylon.compiler.typechecker.context.PhasedUnit;
import org.eclipse.ceylon.compiler.typechecker.context.TypecheckerUnit;
import org.eclipse.ceylon.compiler.typechecker.io.VirtualFile;
import org.eclipse.ceylon.compiler.typechecker.tree.Tree.CompilationUnit;
import org.eclipse.ceylon.ide.eclipse.core.model.ExternalSourceFile;
import org.eclipse.ceylon.model.typechecker.model.Package;
import org.eclipse.ceylon.model.typechecker.util.ModuleManager;

public class ExternalPhasedUnit extends IdePhasedUnit {
    public ExternalPhasedUnit(VirtualFile unitFile, VirtualFile srcDir,
            CompilationUnit cu, Package p, ModuleManager moduleManager,
            ModuleSourceMapper moduleSourceMapper,
            TypeChecker typeChecker, List<CommonToken> tokenStream) {
        super(unitFile, srcDir, cu, p, moduleManager, moduleSourceMapper, typeChecker, tokenStream);
    }
    
    public ExternalPhasedUnit(PhasedUnit other) {
        super(other);
    }

    @Override
    protected TypecheckerUnit newUnit() {
        return new ExternalSourceFile(this);
    }
    
    @Override
    public ExternalSourceFile getUnit() {
        return (ExternalSourceFile) super.getUnit();
    }

}
