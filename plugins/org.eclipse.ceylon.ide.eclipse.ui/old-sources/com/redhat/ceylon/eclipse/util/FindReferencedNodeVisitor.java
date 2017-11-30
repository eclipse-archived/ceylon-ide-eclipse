/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 1.0 which is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.util;

import org.eclipse.ceylon.model.typechecker.model.Declaration;
import org.eclipse.ceylon.model.typechecker.model.Function;
import org.eclipse.ceylon.model.typechecker.model.Referenceable;
import org.eclipse.ceylon.model.typechecker.model.Setter;
import org.eclipse.ceylon.common.Backends;
import org.eclipse.ceylon.compiler.typechecker.tree.Node;
import org.eclipse.ceylon.compiler.typechecker.tree.Tree;
import org.eclipse.ceylon.compiler.typechecker.tree.Visitor;
import org.eclipse.ceylon.compiler.typechecker.tree.Tree.FunctionArgument;

@Deprecated
class FindReferencedNodeVisitor extends Visitor {
    
    private final Referenceable declaration;
    private Node declarationNode;
    
    FindReferencedNodeVisitor(Referenceable declaration) {
        this.declaration = declaration;
    }
    
    Node getDeclarationNode() {
        return declarationNode;
    }
    
    private boolean isDeclaration(Declaration dec) {
        if (dec!=null && dec.equals(declaration)) {
            if (dec.isNative()) {
                Declaration d = (Declaration) declaration;
                Backends backends = d.getNativeBackends();
                if (!dec.getNativeBackends().equals(backends)) {
                    return false;
                }
            }
            if (declaration instanceof Function) {
                Function method = (Function) declaration;
                if (method.isOverloaded()) {
                    return method==dec;
                }
            }
            return true;
        }
        else {
            return false;
        }
    }
    
    @Override
    public void visit(Tree.ModuleDescriptor that) {
        super.visit(that);
        Referenceable m = that.getImportPath().getModel();
        if (m!=null && m.equals(declaration)) {
            declarationNode = that;
        }
    }
    
    @Override
    public void visit(Tree.PackageDescriptor that) {
        super.visit(that);
        Referenceable p = that.getImportPath().getModel();
        if (p!=null && p.equals(declaration)) {
            declarationNode = that;
        }
    }
    
    @Override
    public void visit(Tree.Declaration that) {
        if (isDeclaration(that.getDeclarationModel())) {
            declarationNode = that;
        }
        super.visit(that);
    }
    
    @Override
    public void visit(Tree.Constructor that) {
        if (isDeclaration(that.getConstructor())) {
            declarationNode = that;
        }
        super.visit(that);
    }
    
    @Override
    public void visit(Tree.Enumerated that) {
        if (isDeclaration(that.getEnumerated())) {
            declarationNode = that;
        }
        super.visit(that);
    }
    
    @Override
    public void visit(Tree.AttributeSetterDefinition that) {
        Setter setter = that.getDeclarationModel();
        Declaration param = 
                setter.getDirectMember(setter.getName(), 
                        null, false);
        if (isDeclaration(param)) {
            declarationNode = that;
        }
        super.visit(that);
    }
    
    @Override
    public void visit(Tree.ObjectDefinition that) {
        if (isDeclaration(that.getAnonymousClass())) {
            declarationNode = that;
        }
        super.visit(that);
    }
    
    @Override
    public void visit(Tree.ObjectExpression that) {
        if (isDeclaration(that.getAnonymousClass())) {
            declarationNode = that;
        }
        super.visit(that);
    }
    
    @Override
    public void visit(Tree.SpecifierStatement that) {
        if (that.getRefinement()) {
            if (isDeclaration(that.getDeclaration())) {
                declarationNode = that;
            }
        }
        super.visit(that);
    }
    
    @Override
    public void visit(FunctionArgument that) {
        if (isDeclaration(that.getDeclarationModel())) {
            declarationNode = that;
        }
        super.visit(that);
    }
    
    @Override
    public void visit(Tree.InitializerParameter that) {
        if (isDeclaration(that.getParameterModel().getModel())) {
            declarationNode = that;
        }
        super.visit(that);
    }
    
    public void visitAny(Node node) {
        if (declarationNode==null ||
                declarationNode 
                    instanceof Tree.InitializerParameter) {
            super.visitAny(node);
        }
    }
    
}
