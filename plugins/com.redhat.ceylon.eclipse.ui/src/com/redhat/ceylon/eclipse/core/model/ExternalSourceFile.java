package com.redhat.ceylon.eclipse.core.model;

import java.util.Stack;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.Package;
import com.redhat.ceylon.compiler.typechecker.model.Scope;
import com.redhat.ceylon.compiler.typechecker.model.Value;
import com.redhat.ceylon.eclipse.core.typechecker.ExternalPhasedUnit;
import com.redhat.ceylon.eclipse.util.SingleSourceUnitPackage;

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
