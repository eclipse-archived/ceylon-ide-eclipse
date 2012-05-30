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

public class CeylonSourceFile extends PhasedUnit {

    private TypeChecker typeChecker;
    private boolean validatingTree = false; 
    private boolean scanningDeclarations = false; 
    private boolean scanningTypeDeclarations = false; 
    private boolean validatingRefinement = false; 
    private boolean analysingTypes = false; 
    private boolean analyzingFlow = false; 
    
    public CeylonSourceFile(VirtualFile unitFile, VirtualFile srcDir,
            CompilationUnit cu, Package p, ModuleManager moduleManager,
            TypeChecker typeChecker, List<CommonToken> tokenStream) {
        super(unitFile, srcDir, cu, p, moduleManager, typeChecker.getContext(), tokenStream);
        this.typeChecker = typeChecker;
    }
    
    public CeylonSourceFile(PhasedUnit other) {
        super(other);
        if (other instanceof CeylonSourceFile) {
            typeChecker = ((CeylonSourceFile) other).typeChecker;
        }
    }

    @Override
    public void validateTree() {
        if (! isTreeValidated() && ! validatingTree) {
            validatingTree = true;
            super.validateTree();
        }
    }

    @Override
    public synchronized void scanDeclarations() {
        if (! isDeclarationsScanned() && !scanningDeclarations) {
            scanningDeclarations = true;
            for (PhasedUnit phasedUnit : typeChecker.getPhasedUnits().getPhasedUnits()) {
                phasedUnit.validateTree();
            }
            super.scanDeclarations();
        }
    }

    @Override
    public synchronized void scanTypeDeclarations() {
        if (! isTypeDeclarationsScanned() && ! scanningTypeDeclarations) {
            scanningTypeDeclarations = true; 
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
    }

    @Override
    public synchronized void validateRefinement() {
        if (! isRefinementValidated() && validatingRefinement) {
            validatingRefinement = true;
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
    }

    @Override
    public synchronized void analyseTypes() {
        if (! isFullyTyped() && ! analysingTypes) {
            analysingTypes = true;
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
    }

    @Override
    public synchronized void analyseFlow() {
        if (! isFlowAnalyzed() && ! analyzingFlow) {
            analyzingFlow = true;
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
}
