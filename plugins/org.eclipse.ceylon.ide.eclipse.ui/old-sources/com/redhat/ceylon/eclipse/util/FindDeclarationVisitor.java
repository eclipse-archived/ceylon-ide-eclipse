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

import org.eclipse.ceylon.compiler.typechecker.tree.Node;
import org.eclipse.ceylon.compiler.typechecker.tree.Tree;
import org.eclipse.ceylon.compiler.typechecker.tree.Visitor;

@Deprecated
public class FindDeclarationVisitor 
        extends Visitor {

    private final Node term;
    private Tree.Declaration declaration;
    private Tree.Declaration current;

    public FindDeclarationVisitor(Node term) {
        this.term = term;
    }

    public Tree.Declaration getDeclarationNode() {
        return declaration;
    }

    @Override
    public void visit(Tree.Declaration that) {
        Tree.Declaration outer = current;
        current = that;
        super.visit(that);
        current = outer;
    }
    
    @Override
    public void visitAny(Node node) {
        if (node == term) {
            declaration = current;
        }
        if (declaration == null) {
            super.visitAny(node);
        }
    }

}