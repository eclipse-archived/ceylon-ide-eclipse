/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.util;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.ceylon.ide.eclipse.core.model.CeylonUnit;
import org.eclipse.ceylon.model.typechecker.model.Annotation;
import org.eclipse.ceylon.model.typechecker.model.Declaration;
import org.eclipse.ceylon.model.typechecker.model.DeclarationWithProximity;
import org.eclipse.ceylon.model.typechecker.model.Import;
import org.eclipse.ceylon.model.typechecker.model.Module;
import org.eclipse.ceylon.model.typechecker.model.Type;
import org.eclipse.ceylon.model.typechecker.model.Scope;
import org.eclipse.ceylon.model.typechecker.model.TypeDeclaration;
import org.eclipse.ceylon.model.typechecker.model.Unit;

public class SingleSourceUnitPackage extends org.eclipse.ceylon.model.typechecker.model.Package {

    private org.eclipse.ceylon.model.typechecker.model.Package modelPackage;
    private String fullPathOfSourceUnitToTypecheck;

    public SingleSourceUnitPackage(org.eclipse.ceylon.model.typechecker.model.Package delegate, 
            String fullPathOfSourceUnitToTypecheck) {
        this.modelPackage = delegate;
        this.fullPathOfSourceUnitToTypecheck = fullPathOfSourceUnitToTypecheck;
        setModule(delegate.getModule());
        setName(delegate.getName());
        setShared(delegate.isShared());
    }
    
    public String getFullPathOfSourceUnitToTypecheck() {
        return fullPathOfSourceUnitToTypecheck;
    }

    private boolean mustSearchInSourceFile(Declaration modelDeclaration) {
        if (modelDeclaration == null) {
            return true;
        }
        Unit unit = modelDeclaration.getUnit();
        return mustSearchInSourceFile(unit);
    }

    private boolean mustSearchInSourceFile(Unit modelUnit) {
        if (modelUnit instanceof CeylonUnit) {
            CeylonUnit ceylonUnit = (CeylonUnit) modelUnit;
            String fullPathOfModelSourceUnit = ceylonUnit.getCeylonSourceFullPath();
            if (fullPathOfModelSourceUnit != null && fullPathOfModelSourceUnit.equals(fullPathOfSourceUnitToTypecheck)) {
                return true;
            }
        }
        return false;
    }
     
    @Override
    public Declaration getDirectMember(String name,
            List<Type> signature, boolean ellipsis) {
        Declaration modelMember = modelPackage.getDirectMember(name, signature, ellipsis);
        return mustSearchInSourceFile(modelMember)  ? super.getDirectMember(name, signature, ellipsis) : modelMember;
    }
    
    @Override
    public Declaration getMember(String name, List<Type> signature,
            boolean ellipsis) {
        Declaration modelMember = modelPackage.getMember(name, signature, ellipsis);
        return mustSearchInSourceFile(modelMember) ? super.getMember(name, signature, ellipsis) : modelMember;
    }
    
    @Override
    public List<Declaration> getMembers() {
        LinkedList<Declaration> ret = new LinkedList<Declaration>();
        for (Declaration modelDeclaration : modelPackage.getMembers()) {
            if (! mustSearchInSourceFile(modelDeclaration)) {
                ret.add(modelDeclaration);
            }
        }
        ret.addAll(super.getMembers());
        return ret;
    }
    
    @Override
    public Iterable<Unit> getUnits() {
        LinkedList<Unit> units = new LinkedList<Unit>();
        for (Unit modelUnit : modelPackage.getUnits()) {
            if (! mustSearchInSourceFile(modelUnit)) {
                units.add(modelUnit);
            }
        }
        for (Unit u : super.getUnits()) {
            units.add(u);
        }
        return units;
    }

    @Override
    public List<Annotation> getAnnotations() {
        return modelPackage.getAnnotations();
    }

    @Override
    public Scope getContainer() {
        return modelPackage.getContainer();
    }

    @Override
    public Type getDeclaringType(Declaration modelDeclaration) {
        return mustSearchInSourceFile(modelDeclaration) ? super.getDeclaringType(modelDeclaration) : modelPackage.getDeclaringType(modelDeclaration);
    }

    @Override
    public Map<String, DeclarationWithProximity> getImportableDeclarations(Unit modelUnit, 
            String startingWith, List<Import> imports, int proximity) {
        return modelPackage.getImportableDeclarations(modelUnit, startingWith, imports, proximity);
    }

    @Override
    public TypeDeclaration getInheritingDeclaration(Declaration d) {
        return modelPackage.getInheritingDeclaration(d);
    }

    @Override
    public Map<String, DeclarationWithProximity> getMatchingDeclarations(
            Unit unit, String startingWith, int proximity) {
        return super.getMatchingDeclarations(unit, startingWith, proximity);
    }

    @Override
    public Declaration getMemberOrParameter(Unit modelUnit, String name,
            List<Type> signature, boolean ellipsis) {
        Declaration modelMember = modelPackage.getMemberOrParameter(modelUnit, name, signature, ellipsis);
        return mustSearchInSourceFile(modelMember) ? super.getMemberOrParameter(modelUnit, name, signature, ellipsis) : modelMember;
    }

    @Override
    public Module getModule() {
        return modelPackage.getModule();
    }

    @Override
    public List<String> getName() {
        return modelPackage.getName();
    }

    @Override
    public String getNameAsString() {
        return modelPackage.getNameAsString();
    }

    @Override
    public String getQualifiedNameString() {
        return modelPackage.getQualifiedNameString();
    }

    @Override
    public Scope getScope() {
        return modelPackage.getScope();
    }

    @Override
    public Unit getUnit() {
        Unit modelUnit = modelPackage.getUnit();
        return mustSearchInSourceFile(modelUnit) ? 
                super.getUnit() : 
                    modelUnit;
    }

    @Override
    public boolean isInherited(Declaration d) {
        return modelPackage.isInherited(d);
    }

    @Override
    public boolean isShared() {
        return modelPackage.isShared();
    }
    
    public org.eclipse.ceylon.model.typechecker.model.Package getModelPackage() {
        return modelPackage;
    }
}
