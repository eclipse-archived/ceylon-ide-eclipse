package com.redhat.ceylon.eclipse.core.model;

import java.util.List;

import org.antlr.runtime.CommonToken;

import com.redhat.ceylon.compiler.typechecker.TypeChecker;
import com.redhat.ceylon.compiler.typechecker.analyzer.ModuleManager;
import com.redhat.ceylon.compiler.typechecker.context.Context;
import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.context.PhasedUnits;
import com.redhat.ceylon.compiler.typechecker.io.VirtualFile;
import com.redhat.ceylon.compiler.typechecker.model.Package;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.CompilationUnit;

public class CeylonCompilationUnit extends PhasedUnit {

    private TypeChecker typeChecker;
    
    public CeylonCompilationUnit(VirtualFile unitFile, VirtualFile srcDir,
            CompilationUnit cu, Package p, ModuleManager moduleManager,
            Context context, List<CommonToken> tokenStream, TypeChecker typeChecker) {
        super(unitFile, srcDir, cu, p, moduleManager, context, tokenStream);
        this.typeChecker = typeChecker;
    }
    
    public CeylonCompilationUnit(PhasedUnit other) {
        super(other);
        if (other instanceof CeylonCompilationUnit) {
            typeChecker = ((CeylonCompilationUnit) other).typeChecker;
        }
    }

    @Override
    public synchronized void validateTree() {
        super.validateTree();
    }

    @Override
    public synchronized void scanDeclarations() {
        for (PhasedUnit phasedUnit : typeChecker.getPhasedUnits().getPhasedUnits()) {
            phasedUnit.validateTree();
        }
        super.scanDeclarations();
    }

    @Override
    public synchronized void scanTypeDeclarations() {
        for (PhasedUnits phasedUnits : typeChecker.getPhasedUnitsOfDependencies()) {
            for (PhasedUnit phasedUnit : phasedUnits.getPhasedUnits()) {
                phasedUnit.scanDeclarations();
            }
        }
        for (PhasedUnit phasedUnit : typeChecker.getPhasedUnits().getPhasedUnits()) {
            phasedUnit.scanDeclarations();
        }
        super.scanTypeDeclarations();
    }

    @Override
    public synchronized void validateRefinement() {
        for (PhasedUnits phasedUnits : typeChecker.getPhasedUnitsOfDependencies()) {
            for (PhasedUnit phasedUnit : phasedUnits.getPhasedUnits()) {
                phasedUnit.scanTypeDeclarations();
            }
        }
        for (PhasedUnit phasedUnit : typeChecker.getPhasedUnits().getPhasedUnits()) {
            phasedUnit.scanTypeDeclarations();
        }
        super.validateRefinement();
    }

    @Override
    public synchronized void analyseTypes() {
        for (PhasedUnits phasedUnits : typeChecker.getPhasedUnitsOfDependencies()) {
            for (PhasedUnit phasedUnit : phasedUnits.getPhasedUnits()) {
                phasedUnit.validateRefinement();
            }
        }
        for (PhasedUnit phasedUnit : typeChecker.getPhasedUnits().getPhasedUnits()) {
            phasedUnit.validateRefinement();
        }
        super.analyseTypes();
    }

    @Override
    public synchronized void analyseFlow() {
        for (PhasedUnits phasedUnits : typeChecker.getPhasedUnitsOfDependencies()) {
            for (PhasedUnit phasedUnit : phasedUnits.getPhasedUnits()) {
                phasedUnit.analyseTypes();
            }
        }
        for (PhasedUnit phasedUnit : typeChecker.getPhasedUnits().getPhasedUnits()) {
            phasedUnit.analyseTypes();
        }
        super.analyseFlow();
    }
}
