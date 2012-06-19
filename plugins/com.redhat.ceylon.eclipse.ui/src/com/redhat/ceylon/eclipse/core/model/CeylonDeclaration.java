package com.redhat.ceylon.eclipse.core.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import com.redhat.ceylon.compiler.loader.ModelLoader.DeclarationType;
import com.redhat.ceylon.compiler.loader.mirror.ClassMirror;
import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.Method;
import com.redhat.ceylon.compiler.typechecker.model.Parameter;
import com.redhat.ceylon.compiler.typechecker.model.ParameterList;
import com.redhat.ceylon.compiler.typechecker.model.ProducedType;
import com.redhat.ceylon.compiler.typechecker.model.TypedDeclaration;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.Type;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;
import com.redhat.ceylon.eclipse.core.model.loader.JDTModelLoader;

public class CeylonDeclaration {

    
    private PhasedUnit phasedUnit;
    private Tree.Declaration astDeclaration;
    private Declaration modelDeclaration = null;
    private boolean isSourceToCompile = true;
    private JDTModelLoader modelLoader = null;

    public CeylonDeclaration(JDTModelLoader modelLoader, PhasedUnit phasedUnit, Tree.Declaration astDeclaration, boolean isSourceToCompile) {
        this.phasedUnit = phasedUnit;
        this.astDeclaration = astDeclaration;
        this.isSourceToCompile = isSourceToCompile;
        this.modelLoader = modelLoader;
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
