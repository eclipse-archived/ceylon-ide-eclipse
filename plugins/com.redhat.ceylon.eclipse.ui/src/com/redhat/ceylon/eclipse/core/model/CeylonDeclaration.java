package com.redhat.ceylon.eclipse.core.model;

import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;

public class CeylonDeclaration {

    
    private PhasedUnit phasedUnit;
    private Tree.Declaration astDeclaration;

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
}
