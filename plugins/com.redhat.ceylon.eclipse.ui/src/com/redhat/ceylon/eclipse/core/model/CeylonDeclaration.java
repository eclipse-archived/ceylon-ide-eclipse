package com.redhat.ceylon.eclipse.core.model;

import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;

public class CeylonDeclaration {

    
    private PhasedUnit phasedUnit;
    private Tree.Declaration astDeclaration;
    private Declaration modelDeclaration = null;

    public CeylonDeclaration(PhasedUnit phasedUnit, Tree.Declaration astDeclaration) {
        this.phasedUnit = phasedUnit;
        this.astDeclaration = astDeclaration;
    }

    public PhasedUnit getPhasedUnit() {
        return phasedUnit;
    }

    public Tree.Declaration getAstDeclaration() {
        return astDeclaration;
    }

    public Declaration getModelDeclaration() {
        if (modelDeclaration != null) {
            return modelDeclaration;
        }
        if (! phasedUnit.isDeclarationsScanned()) {
            phasedUnit.scanDeclarations();
        }
        if (! phasedUnit.isTypeDeclarationsScanned()) {
            phasedUnit.scanTypeDeclarations();
        }
        if (! phasedUnit.isRefinementValidated()) {
            phasedUnit.validateRefinement();
        }
        if (! phasedUnit.isFullyTyped()) {
            phasedUnit.analyseTypes();
        }
        modelDeclaration = astDeclaration.getDeclarationModel();
        return modelDeclaration;
    }
}
