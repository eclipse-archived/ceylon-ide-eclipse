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

import org.eclipse.ceylon.compiler.typechecker.tree.Node;
import org.eclipse.ceylon.compiler.typechecker.tree.Tree;
import org.eclipse.ceylon.compiler.typechecker.tree.Visitor;

@Deprecated
public class FindBodyContainerVisitor extends Visitor {
    Node node;
    Tree.Declaration declaration;
    Tree.Declaration currentDeclaration;
    public Tree.Declaration getDeclarationNode() {
        return declaration;
    }
    public FindBodyContainerVisitor(Node node) {
        this.node=node;
    }
    @Override
    public void visit(Tree.ObjectDefinition that) {
        Tree.Declaration d = currentDeclaration;
        currentDeclaration = that;
        super.visit(that);
        currentDeclaration = d;
    }
    @Override
    public void visit(Tree.AttributeGetterDefinition that) {
        Tree.Declaration d = currentDeclaration;
        currentDeclaration = that;
        super.visit(that);
        currentDeclaration = d;
    }
    @Override
    public void visit(Tree.AttributeSetterDefinition that) {
        Tree.Declaration d = currentDeclaration;
        currentDeclaration = that;
        super.visit(that);
        currentDeclaration = d;
    }
    @Override
    public void visit(Tree.MethodDefinition that) {
        Tree.Declaration d = currentDeclaration;
        currentDeclaration = that;
        super.visit(that);
        currentDeclaration = d;
    }
    @Override
    public void visit(Tree.Constructor that) {
        Tree.Declaration d = currentDeclaration;
        currentDeclaration = that;
        super.visit(that);
        currentDeclaration = d;
    }
    @Override
    public void visit(Tree.ClassDefinition that) {
        Tree.Declaration d = currentDeclaration;
        currentDeclaration = that;
        super.visit(that);
        currentDeclaration = d;
    }
    @Override
    public void visit(Tree.InterfaceDefinition that) {
        Tree.Declaration d = currentDeclaration;
        currentDeclaration = that;
        super.visit(that);
        currentDeclaration = d;
    }
    @Override
    public void visitAny(Node node) {
        if (this.node==node) {
            declaration=currentDeclaration;
        }
        if (declaration==null) {
            super.visitAny(node);
        }
    }
}