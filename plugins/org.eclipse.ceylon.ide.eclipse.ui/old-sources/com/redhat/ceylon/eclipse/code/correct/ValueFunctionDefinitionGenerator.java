/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.code.correct;

import static org.eclipse.ceylon.ide.eclipse.code.correct.CorrectionUtil.defaultValue;
import static org.eclipse.ceylon.ide.eclipse.code.correct.ImportProposals.importProposals;
import static org.eclipse.ceylon.ide.eclipse.ui.CeylonResources.LOCAL_ATTRIBUTE;
import static org.eclipse.ceylon.ide.eclipse.ui.CeylonResources.LOCAL_METHOD;
import static org.eclipse.ceylon.model.typechecker.model.ModelUtil.isTypeUnknown;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import org.eclipse.swt.graphics.Image;

import org.eclipse.ceylon.compiler.typechecker.tree.Node;
import org.eclipse.ceylon.compiler.typechecker.tree.Tree;
import org.eclipse.ceylon.compiler.typechecker.tree.Tree.AssignmentOp;
import org.eclipse.ceylon.compiler.typechecker.tree.Tree.CompilationUnit;
import org.eclipse.ceylon.compiler.typechecker.tree.Tree.MemberOrTypeExpression;
import org.eclipse.ceylon.compiler.typechecker.tree.Tree.SpecifierStatement;
import org.eclipse.ceylon.compiler.typechecker.tree.Tree.UnaryOperatorExpression;
import org.eclipse.ceylon.model.typechecker.model.Declaration;
import org.eclipse.ceylon.model.typechecker.model.Type;
import org.eclipse.ceylon.model.typechecker.model.TypeParameter;
import org.eclipse.ceylon.model.typechecker.model.Unit;

class ValueFunctionDefinitionGenerator extends DefinitionGenerator {
    
    private final String brokenName;
    private final MemberOrTypeExpression node;
    private final CompilationUnit rootNode;
    private final String desc;
    private final Image image;
    private final Type returnType;
    private final LinkedHashMap<String, Type> parameters;
    private final Boolean isVariable;
    
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

    private ValueFunctionDefinitionGenerator(
            String brokenName, 
            Tree.MemberOrTypeExpression node, 
            Tree.CompilationUnit rootNode,
            String desc,
            Image image,
            Type returnType,
            LinkedHashMap<String, Type> paramTypes,
            Boolean isVariable) {
        this.brokenName = brokenName;
        this.node = node;
        this.rootNode = rootNode;
        this.desc = desc;
        this.image = image;
        this.returnType = returnType;
        this.parameters = paramTypes;
        this.isVariable = isVariable;
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
        return true;
    }
    
    private String generateInternal(
            String indent, String delim, boolean isFormal) {
        StringBuffer def = new StringBuffer();
        boolean isVoid = returnType==null;
        Unit unit = node.getUnit();
        if (parameters!=null) {            
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

            if (isVoid) {
                def.append("void");
            }
            else {
                if (isTypeUnknown(returnType)) {
                    def.append("function");
                }
                else {
                    def.append(returnType.asSourceCodeString(unit));
                }
            }
            def.append(" ")
            .append(brokenName).append(typeParamDef);
            appendParameters(parameters, def,
                    unit.getAnythingDeclaration());
            def.append(typeParamConstDef);
            if(isFormal){
                def.append(";");
            }
            else if (isVoid) {
                def.append(" {}");
            }
            else {
                //removed because it's ugly for parameters:
                //delim + indent + defIndent + defIndent +
                def.append(" => ")
                .append(defaultValue(unit, returnType))
                .append(";");
            }
        }
        else {
            if(isVariable){
                def.append("variable ");
            }
            if (isVoid) {
                def.append("Anything");
            }
            else {
                if (isTypeUnknown(returnType)) {
                    def.append("value");
                }
                else {
                    def.append(returnType.asSourceCodeString(unit));
                }
            }
            def.append(" ")
               .append(brokenName);
            if(!isFormal){
                def.append(" = ")
                   .append(defaultValue(unit, returnType));
            }
            def.append(";");
        }
        return def.toString();
    }
    
    Set<Declaration> getImports() {
        Set<Declaration> imports = 
                new HashSet<Declaration>();
        importProposals().importType(imports, returnType, rootNode);
        if (parameters!=null) {
            importProposals().importTypes(imports, parameters.values(), rootNode);
        }
        return imports;
    }
    
    static ValueFunctionDefinitionGenerator create(
            String brokenName, 
            Tree.MemberOrTypeExpression node, 
            Tree.CompilationUnit rootNode) {
        boolean isUpperCase = 
                Character.isUpperCase(
                        brokenName.codePointAt(0));
        if (isUpperCase) return null;
        FindValueFunctionVisitor fav = 
                new FindValueFunctionVisitor(node);
        rootNode.visit(fav);
        Type et = fav.expectedType;
        final boolean isVoid = et==null;
        Type returnType = isVoid ? null : 
            node.getUnit().denotableType(et);
        StringBuilder params = new StringBuilder();
        LinkedHashMap<String, Type> paramTypes = 
                getParameters(fav);
        if (paramTypes!=null) {         
            String desc = 
                    "function '" + brokenName + params + "'";
            return new ValueFunctionDefinitionGenerator(
                    brokenName, node, rootNode, desc, 
                    LOCAL_METHOD, returnType, paramTypes, 
                    null);
        }
        else {
            String desc = 
                    "value '" + brokenName + "'";
            return new ValueFunctionDefinitionGenerator(
                    brokenName, node, rootNode, desc, 
                    LOCAL_ATTRIBUTE, returnType, null, 
                    fav.isVariable);
        }
    }
    
    private static class FindValueFunctionVisitor 
            extends FindArgumentsVisitor{

        boolean isVariable = false;
        
        FindValueFunctionVisitor(MemberOrTypeExpression smte) {
            super(smte);
        }
        
        @Override
        public void visit(AssignmentOp that) {
            Tree.AssignmentOp ao = (Tree.AssignmentOp) that;
            isVariable = ao.getLeftTerm() == smte;
            super.visit(that);
        }
        
        @Override
        public void visit(UnaryOperatorExpression that) {
            Tree.UnaryOperatorExpression uoe = 
                    (Tree.UnaryOperatorExpression) that;
            isVariable = uoe.getTerm() == smte;
            super.visit(that);
        }

        @Override
        public void visit(SpecifierStatement that) {
            Tree.SpecifierStatement ss = 
                    (Tree.SpecifierStatement) that;
            isVariable = ss.getBaseMemberExpression() == smte;
            super.visit(that);
        }
        
    }

}