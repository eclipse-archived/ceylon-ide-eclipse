/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.core.model.mirror;

import org.eclipse.ceylon.compiler.typechecker.context.PhasedUnit;
import org.eclipse.ceylon.compiler.typechecker.tree.Tree;
import org.eclipse.ceylon.model.typechecker.model.Declaration;

@Deprecated
public class SourceDeclarationHolder {

    
    private PhasedUnit phasedUnit;
    private Tree.Declaration astDeclaration;
    private Declaration modelDeclaration = null;
    private boolean isSourceToCompile = true;

    public SourceDeclarationHolder(PhasedUnit phasedUnit, Tree.Declaration astDeclaration, boolean isSourceToCompile) {
        this.phasedUnit = phasedUnit;
        this.astDeclaration = astDeclaration;
        this.isSourceToCompile = isSourceToCompile;
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
        
        if (isSourceToCompile) {
            modelDeclaration = astDeclaration.getDeclarationModel();
        }
        
        if (phasedUnit.isScanningDeclarations()) {
            return null;
        }
        
        if (!phasedUnit.isDeclarationsScanned()) {
            phasedUnit.scanDeclarations();
        }
        if (!phasedUnit.isTypeDeclarationsScanned()) {
            phasedUnit.scanTypeDeclarations();
        }
        
        modelDeclaration = astDeclaration.getDeclarationModel();
        
        return modelDeclaration;
    }

    public boolean isSourceToCompile() {
        return isSourceToCompile;
    }

    public void setSourceToCompile(boolean isSourceToCompile) {
        this.isSourceToCompile = isSourceToCompile;
    }
}
