package com.redhat.ceylon.eclipse.core.typechecker;

import java.util.List;

import org.antlr.runtime.CommonToken;

import com.redhat.ceylon.compiler.typechecker.TypeChecker;
import com.redhat.ceylon.compiler.typechecker.analyzer.ModuleSourceMapper;
import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.io.VirtualFile;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.CompilationUnit;
import com.redhat.ceylon.eclipse.core.model.IResourceAware;
import com.redhat.ceylon.model.typechecker.model.Package;
import com.redhat.ceylon.model.typechecker.util.ModuleManager;

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
