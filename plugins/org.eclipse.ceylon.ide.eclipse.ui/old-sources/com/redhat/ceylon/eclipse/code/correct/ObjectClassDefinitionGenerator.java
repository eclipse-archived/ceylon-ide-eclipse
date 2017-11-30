/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 1.0 which is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.code.correct;

import static org.eclipse.ceylon.ide.eclipse.code.complete.CodeCompletions.getRefinementTextFor;
import static org.eclipse.ceylon.ide.eclipse.code.complete.CompletionUtil.overloads;
import static org.eclipse.ceylon.ide.eclipse.code.complete.RefinementCompletionProposal.getRefinedProducedReference;
import static org.eclipse.ceylon.ide.eclipse.code.correct.ImportProposals.importProposals;
import static org.eclipse.ceylon.ide.eclipse.ui.CeylonResources.LOCAL_ATTRIBUTE;
import static org.eclipse.ceylon.ide.eclipse.ui.CeylonResources.LOCAL_CLASS;
import static org.eclipse.ceylon.ide.eclipse.java2ceylon.Java2CeylonProxies.utilJ2C;
import static org.eclipse.ceylon.model.typechecker.model.ModelUtil.intersectionType;
import static org.eclipse.ceylon.model.typechecker.model.ModelUtil.isTypeUnknown;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import org.eclipse.swt.graphics.Image;

import org.eclipse.ceylon.compiler.typechecker.tree.Node;
import org.eclipse.ceylon.compiler.typechecker.tree.Tree;
import org.eclipse.ceylon.compiler.typechecker.tree.Tree.CompilationUnit;
import org.eclipse.ceylon.compiler.typechecker.tree.Tree.MemberOrTypeExpression;
import org.eclipse.ceylon.model.typechecker.model.Class;
import org.eclipse.ceylon.model.typechecker.model.Declaration;
import org.eclipse.ceylon.model.typechecker.model.DeclarationWithProximity;
import org.eclipse.ceylon.model.typechecker.model.Interface;
import org.eclipse.ceylon.model.typechecker.model.Reference;
import org.eclipse.ceylon.model.typechecker.model.Type;
import org.eclipse.ceylon.model.typechecker.model.TypeDeclaration;
import org.eclipse.ceylon.model.typechecker.model.TypeParameter;
import org.eclipse.ceylon.model.typechecker.model.Unit;

class ObjectClassDefinitionGenerator extends DefinitionGenerator {
    
    private final String brokenName;
    private final boolean isUpperCase;
    private final MemberOrTypeExpression node;
    private final CompilationUnit rootNode;
    private final String desc;
    private final Image image;
    private final Type returnType;
    private final LinkedHashMap<String, Type> parameters;
   

    @Override
    String getBrokenName() {
        return brokenName;
    }
    
    @Override
    Type getReturnType() {
        return returnType;
    }
    
    @Override
    LinkedHashMap<String, Type> getParameters() {
        return parameters;
    }
    
    @Override
    String getDescription() {
        return desc;
    }
    
    @Override
    Image getImage() {
        return image;
    }
    
    @Override
    Tree.CompilationUnit getRootNode() {
        return rootNode;
    }

    @Override
    Node getNode() {
        return node;
    }

    private ObjectClassDefinitionGenerator(
            String brokenName, 
            Tree.MemberOrTypeExpression node, 
            Tree.CompilationUnit rootNode,
            String desc,
            Image image,
            Type returnType,
            LinkedHashMap<String, Type> paramTypes) {
        this.brokenName = brokenName;
        this.isUpperCase = 
                Character.isUpperCase(
                        brokenName.codePointAt(0));
        this.node = node;
        this.rootNode = rootNode;
        this.desc = desc;
        this.image = image;
        this.returnType = returnType;
        this.parameters = paramTypes;
    }
        
    String generateShared(String indent, String delim) {
        return "shared " + 
                generateInternal(indent, delim, false);
    }
    
    String generate(String indent, String delim) {
        return generateInternal(indent, delim, false);
    }
    
    String generateSharedFormal(String indent, String delim) {
        return "shared formal "+ 
                generateInternal(indent, delim, true);
    }
    
    boolean isFormalSupported(){
        return isClassGenerator();
    }
    
    private String generateInternal(String indent, 
            String delim, boolean isFormal) {
        StringBuffer def = new StringBuffer();
        boolean isVoid = returnType==null;
        if (isClassGenerator()) {
            List<TypeParameter> typeParams = 
                    new ArrayList<TypeParameter>();
            StringBuilder typeParamDef = 
                    new StringBuilder();
            StringBuilder typeParamConstDef = 
                    new StringBuilder();
            appendTypeParams(typeParams, 
                    typeParamDef, typeParamConstDef, 
                    returnType);
            appendTypeParams(typeParams, 
                    typeParamDef, typeParamConstDef, 
                    parameters.values());
            if (typeParamDef.length() > 0) {
                typeParamDef.insert(0, "<");
                typeParamDef.setLength(typeParamDef.length() - 1);
                typeParamDef.append(">");
            }
            String defIndent = utilJ2C().indents().getDefaultIndent();
            String supertype = isVoid ? null : 
                supertypeDeclaration(returnType);
            def.append("class ")
                .append(brokenName)
                .append(typeParamDef);
            appendParameters(parameters, def, 
                    getDefaultedSupertype());
            if (supertype!=null) {
                def.append(delim)
                    .append(indent)
                    .append(defIndent)
                    .append(defIndent)
                    .append(supertype);
            }
            def.append(typeParamConstDef);
            def.append(" {").append(delim);
            if (!isVoid) {
                appendMembers(indent, delim, def, defIndent);
            }
            def.append(indent).append("}");            
        }
        else if (isObjectGenerator()) {
            String defIndent = utilJ2C().indents().getDefaultIndent();
            String supertype = isVoid ? null : 
                supertypeDeclaration(returnType);
            def.append("object ").append(brokenName);
            if (supertype!=null) {
                def.append(delim)
                    .append(indent)
                    .append(defIndent)
                    .append(defIndent)
                    .append(supertype);
            }
            def.append(" {").append(delim);
            if (!isVoid) {
                appendMembers(indent, delim, def, defIndent);
            }
            def.append(indent).append("}");
        }
        else {
            return null;
        }
        return def.toString();
    }
    
    private boolean isClassGenerator(){
        return isUpperCase && parameters!=null;
    }
    
    private boolean isObjectGenerator(){
        return !isUpperCase && parameters==null;
    }
    
    Set<Declaration> getImports() {
        Set<Declaration> imports = new HashSet<Declaration>();
        importProposals().importType(imports, returnType, rootNode);
        if (parameters!=null) {
            importProposals().importTypes(imports, parameters.values(), rootNode);
        }
        if (returnType!=null) {
            importMembers(imports);            
        }
        return imports;
    }

    private void importMembers(Set<Declaration> imports) {
        //TODO: this is a major copy/paste from appendMembers() below
        TypeDeclaration td = getDefaultedSupertype();
        Set<String> ambiguousNames = new HashSet<String>();
        Unit unit = rootNode.getUnit();
        Collection<DeclarationWithProximity> members = 
                td.getMatchingMemberDeclarations(unit, null, 
                        "", 0)
                    .values();
        for (DeclarationWithProximity dwp: members) {
            Declaration dec = dwp.getDeclaration();
            for (Declaration d: overloads(dec)) {
                if (d.isFormal() /*&& td.isInheritedFromSupertype(d)*/) {
                    importProposals().importSignatureTypes(d, rootNode, imports);
                    ambiguousNames.add(d.getName());
                }
            }
        }
        for (TypeDeclaration superType: 
                td.getSupertypeDeclarations()) {
            for (Declaration m: superType.getMembers()) {
                if (m.isShared()) {
                    Declaration r = 
                            td.getMember(m.getName(), 
                                    null, false);
                    if (r==null || 
                            !r.refines(m) && 
//                                !r.getContainer().equals(ut) && 
                            !ambiguousNames.add(m.getName())) {
                        importProposals().importSignatureTypes(m, rootNode, imports);
                    }
                }
            }
        }
    }

    private void appendMembers(
            String indent, String delim, 
            StringBuffer def, String defIndent) {
        TypeDeclaration td = getDefaultedSupertype();
        Set<String> ambiguousNames = new HashSet<String>();
        Unit unit = rootNode.getUnit();
        Collection<DeclarationWithProximity> members = 
                td.getMatchingMemberDeclarations(unit, null, 
                        "", 0)
                    .values();
        for (DeclarationWithProximity dwp: members) {
            Declaration dec = dwp.getDeclaration();
            if (ambiguousNames.add(dec.getName())) {
                for (Declaration d: overloads(dec)) {
                    if (d.isFormal() /*&& td.isInheritedFromSupertype(d)*/) {
                        appendRefinementText(indent, delim, 
                                def, defIndent, d);
                    }
                }
            }
        }
        for (TypeDeclaration superType: 
                td.getSupertypeDeclarations()) {
            for (Declaration m: superType.getMembers()) {
                if (m.isShared()) {
                    Declaration r = 
                            td.getMember(m.getName(), 
                                    null, false);
                    if ((r==null || 
                            !r.refines(m)) && 
//                            !r.getContainer().equals(ut)) && 
                            ambiguousNames.add(m.getName())) {
                        appendRefinementText(indent, delim, 
                                def, defIndent, m);
                    }
                }
            }
        }
    }

    private TypeDeclaration getDefaultedSupertype() {
        if (isNotBasic(returnType)) {
            return returnType.getDeclaration();
        }
        else {
            Unit unit = rootNode.getUnit();
            return intersectionType(returnType, 
                    unit.getBasicType(), unit)
                        .getDeclaration();
        }
    }

    private void appendRefinementText(
            String indent, String delim,
            StringBuffer def, String defIndent, 
            Declaration d) {
        Reference pr = 
                getRefinedProducedReference(returnType, d);
        Unit unit = node.getUnit();
        String text = 
                getRefinementTextFor(d, pr, unit, false, 
                        null, "", false);
        if (parameters!=null &&
                parameters.containsKey(d.getName())) {
            text = text.substring(0, text.indexOf(" =>")) + ";";
        }
        def.append(indent)
            .append(defIndent)
            .append(text)
            .append(delim);
    }

    static ObjectClassDefinitionGenerator create(
            String brokenName, 
            Tree.MemberOrTypeExpression node, 
            Tree.CompilationUnit rootNode) {
        boolean isUpperCase = 
                Character.isUpperCase(
                        brokenName.charAt(0));
        FindArgumentsVisitor fav = 
                new FindArgumentsVisitor(node);
        rootNode.visit(fav);
        Unit unit = node.getUnit();
        Type returnType = 
                unit.denotableType(fav.expectedType);
        StringBuilder params = new StringBuilder();
        LinkedHashMap<String,Type> paramTypes = getParameters(fav);
        if (returnType!=null) {
            if (unit.isOptionalType(returnType)) {
                returnType = returnType.eliminateNull();
            }
            if (returnType.isObject() || 
                returnType.isAnything()) {
                returnType = null;
            }
        }
        if (!isValidSupertype(returnType)) {
            return null;
        }
        if (paramTypes!=null && isUpperCase) {
            String supertype = 
                    supertypeDeclaration(returnType);
            if (supertype==null) {
                supertype = "";
            }
            String desc = 
                    "class '" + 
                    brokenName + params + supertype + "'";
            return new ObjectClassDefinitionGenerator(
                    brokenName, node, rootNode, desc, 
                    LOCAL_CLASS, returnType, paramTypes);
        }
        else if (paramTypes==null && !isUpperCase) {
            String desc = "object '" + brokenName + "'";
            return new ObjectClassDefinitionGenerator(
                    brokenName, node, rootNode, desc, 
                    LOCAL_ATTRIBUTE, returnType, null);
        }
        else {
            return null;
        }
    }

    private static String supertypeDeclaration(Type returnType) {
        if (isTypeUnknown(returnType)) {
            return null;
        }
        else {
            if (returnType.isClass()) {
                return " extends " + 
                        returnType.asString() + "()"; //TODO: supertype arguments!
            }
            else if (returnType.isInterface()) {
                return " satisfies " + 
                        returnType.asString();
            }
            else if (returnType.isIntersection()) {
                String extendsClause = "";
                StringBuilder satisfiesClause = 
                        new StringBuilder();
                for (Type st: returnType.getSatisfiedTypes()) {
                    if (st.isClass()) {
                        extendsClause = 
                                " extends " + st.asString() + "()"; //TODO: supertype arguments!
                    }
                    else if (st.isInterface()) {
                        if (satisfiesClause.length()==0) {
                            satisfiesClause.append(" satisfies ");
                        }
                        else {
                            satisfiesClause.append(" & ");
                        }
                        satisfiesClause.append(st.asString());
                    }
                }
                return extendsClause + satisfiesClause;
            }
            else {
                return null;
            }
        }
    }

    private static boolean isValidSupertype(Type returnType) {
        if (isTypeUnknown(returnType)) {
            return true;
        }
        else {
            if (returnType.getCaseTypes()!=null) {
                return false;
            }
            TypeDeclaration rtd = 
                    returnType.getDeclaration();
            if (returnType.isClass()) {
                return !rtd.isFinal();
            }
            else if (returnType.isInterface()) {
                Interface cd = 
                        rtd.getUnit()
                            .getCallableDeclaration();
                return !rtd.equals(cd);
            }
            else if (returnType.isIntersection()) {
                for (Type st: returnType.getSatisfiedTypes()) {
                    if (!isValidSupertype(st)) {
                        return false;
                    }
                }
                return true;
            }
            else {
                return false;
            }
        }
    }

    private static boolean isNotBasic(Type returnType) {
        if (isTypeUnknown(returnType)) {
            return false;
        }
        else {
            TypeDeclaration rtd = 
                    returnType.getDeclaration();
            Class bd = 
                    rtd.getUnit()
                        .getBasicDeclaration();
            if (returnType.isClass()) {
                return rtd.inherits(bd);
            }
            else if (returnType.isInterface()) {
                return false;
            }
            else if (returnType.isIntersection()) {
                for (Type st: returnType.getSatisfiedTypes()) {
                    if (st.isClass()) {
                        return rtd.inherits(bd);
                    }
                }
                return false;
            }
            else {
                return false;
            }
        }
    }

}