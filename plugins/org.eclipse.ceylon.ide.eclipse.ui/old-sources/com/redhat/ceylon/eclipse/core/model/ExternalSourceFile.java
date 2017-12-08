/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 1.0 which is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.core.model;

import java.util.Stack;

import org.eclipse.ceylon.model.typechecker.model.Declaration;
import org.eclipse.ceylon.model.typechecker.model.Package;
import org.eclipse.ceylon.model.typechecker.model.Scope;
import org.eclipse.ceylon.model.typechecker.model.Value;
import org.eclipse.ceylon.ide.eclipse.core.typechecker.ExternalPhasedUnit;
import org.eclipse.ceylon.ide.eclipse.util.SingleSourceUnitPackage;

/*
 * Used when the external declarations come from a source archive that doesn't have any binary version,
 *     or the binary car isn't taken in account (current behavior) => this unit will contain declarations
 *   
 */
public class ExternalSourceFile extends SourceFile {

    public ExternalSourceFile(ExternalPhasedUnit phasedUnit) {
        super(phasedUnit);
    }

    @Override
    public ExternalPhasedUnit getPhasedUnit() {
        return (ExternalPhasedUnit) super.getPhasedUnit();
    }
    
    public boolean isBinaryDeclarationSource() {
        JDTModule module = getModule();
        return module.isCeylonBinaryArchive() 
                && (getPackage() instanceof SingleSourceUnitPackage);

    }
    
    public Declaration retrieveBinaryDeclaration(Declaration sourceDeclaration) {
        if (! this.equals(sourceDeclaration.getUnit())) {
            return null;
        }
        Declaration binaryDeclaration = null;
        if (isBinaryDeclarationSource()) {
            SingleSourceUnitPackage sourceUnitPackage = (SingleSourceUnitPackage) getPackage();
            Package binaryPackage = sourceUnitPackage.getModelPackage();
            Stack<Declaration> ancestors = new Stack<>();
            Scope container = sourceDeclaration.getContainer();
            while (container instanceof Declaration) {
                Declaration ancestor = (Declaration) container;
                ancestors.push(ancestor);
                container = ancestor.getContainer();
            }
            if (container.equals(sourceUnitPackage)) {
                Scope curentBinaryScope = binaryPackage;
                while (! ancestors.isEmpty()) {
                    Declaration binaryAncestor = curentBinaryScope.getDirectMember(ancestors.pop().getName(), null, false);
                    if (binaryAncestor instanceof Value) {
                        binaryAncestor = ((Value) binaryAncestor).getTypeDeclaration();
                    }
                    if (binaryAncestor instanceof Scope) {
                        curentBinaryScope = (Scope) binaryAncestor;
                    } else {
                        break;
                    }
                }
                if (curentBinaryScope != null) {
                    binaryDeclaration = curentBinaryScope.getDirectMember(sourceDeclaration.getName(), null, false);
                }
            }
        }
        return binaryDeclaration;
    }
}
