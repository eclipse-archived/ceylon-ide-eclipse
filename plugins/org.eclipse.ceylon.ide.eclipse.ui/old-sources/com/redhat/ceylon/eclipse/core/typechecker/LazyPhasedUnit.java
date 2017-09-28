package org.eclipse.ceylon.ide.eclipse.core.typechecker;

import java.util.List;

import org.antlr.runtime.CommonToken;

import org.eclipse.ceylon.compiler.typechecker.TypeChecker;
import org.eclipse.ceylon.model.typechecker.util.ModuleManager;
import org.eclipse.ceylon.compiler.typechecker.analyzer.ModuleSourceMapper;
import org.eclipse.ceylon.compiler.typechecker.context.PhasedUnit;
import org.eclipse.ceylon.compiler.typechecker.context.PhasedUnits;
import org.eclipse.ceylon.compiler.typechecker.io.VirtualFile;
import org.eclipse.ceylon.model.typechecker.model.Package;
import org.eclipse.ceylon.compiler.typechecker.tree.Tree.CompilationUnit;

public abstract class LazyPhasedUnit extends IdePhasedUnit {

    private TypeChecker typeChecker;
    private boolean validatingTree = false; 
    private boolean scanningDeclarations = false; 
    private boolean scanningTypeDeclarations = false; 
    private boolean validatingRefinement = false; 
    private boolean analysingTypes = false; 
    private boolean analyzingFlow = false; 
    
    public LazyPhasedUnit(VirtualFile unitFile, VirtualFile srcDir,
            CompilationUnit cu, Package p, ModuleManager moduleManager, ModuleSourceMapper moduleSourceMapper,
            TypeChecker typeChecker, List<CommonToken> tokenStream) {
        super(unitFile, srcDir, cu, p, moduleManager, moduleSourceMapper, typeChecker, tokenStream);
        this.typeChecker = typeChecker;
    }
    
    public LazyPhasedUnit(PhasedUnit other) {
        super(other);
        if (other instanceof LazyPhasedUnit) {
            typeChecker = ((LazyPhasedUnit) other).typeChecker;
        }
    }

    @Override
    public void validateTree() {
        if (! isTreeValidated() && ! validatingTree) {
            validatingTree = true;
            super.validateTree();
            validatingTree = false;
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
            scanningDeclarations = false;
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
            scanningTypeDeclarations = false; 
        }
    }

    @Override
    public synchronized void validateRefinement() {
        if (! isRefinementValidated() && ! validatingRefinement) {
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
            validatingRefinement = false;
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
            analysingTypes = false;
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
            analyzingFlow = false;
        }
    }
}
