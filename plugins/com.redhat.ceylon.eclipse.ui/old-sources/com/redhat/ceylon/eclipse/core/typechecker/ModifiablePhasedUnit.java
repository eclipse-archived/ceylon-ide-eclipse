package org.eclipse.ceylon.ide.eclipse.core.typechecker;

import java.util.List;

import org.antlr.runtime.CommonToken;

import org.eclipse.ceylon.compiler.typechecker.TypeChecker;
import org.eclipse.ceylon.compiler.typechecker.analyzer.ModuleSourceMapper;
import org.eclipse.ceylon.compiler.typechecker.context.PhasedUnit;
import org.eclipse.ceylon.compiler.typechecker.io.VirtualFile;
import org.eclipse.ceylon.compiler.typechecker.tree.Tree.CompilationUnit;
import org.eclipse.ceylon.ide.eclipse.core.model.IResourceAware;
import org.eclipse.ceylon.model.typechecker.model.Package;
import org.eclipse.ceylon.model.typechecker.util.ModuleManager;

public abstract class ModifiablePhasedUnit extends IdePhasedUnit implements
        IResourceAware {

    public ModifiablePhasedUnit(PhasedUnit other) {
        super(other);
    }

    public ModifiablePhasedUnit(
            VirtualFile unitFile,
            VirtualFile srcDir,
            CompilationUnit cu, Package p, ModuleManager moduleManager,
            ModuleSourceMapper moduleSourceMapper, TypeChecker typeChecker,
            List<CommonToken> tokenStream) {
        super(unitFile, srcDir, cu, p, moduleManager, moduleSourceMapper, typeChecker, tokenStream);
    }
}
