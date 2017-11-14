/********************************************************************************
 * Copyright (c) {date} Red Hat Inc. and/or its affiliates and others
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

public class FindDocumentableVisitor extends Visitor {

    private final Node term;
    private Tree.StatementOrArgument documentableNode;
    private Tree.StatementOrArgument currentDocumentableNode;

    public FindDocumentableVisitor(Node term) {
        this.term = term;
    }

    public Tree.StatementOrArgument getDocumentableNode() {
        return documentableNode;
    }

    @Override
    public void visit(Tree.StatementOrArgument that) {
        boolean isDocumentable = isDocumentable(that);
        Tree.StatementOrArgument originalDocumentableNode = null;
        if (isDocumentable) {
            originalDocumentableNode = currentDocumentableNode;
            currentDocumentableNode = that;
        }
        super.visit(that);
        if (isDocumentable) {
            currentDocumentableNode = originalDocumentableNode;
        }
    }
    
    @Override
    public void visit(Tree.Body that) {
        currentDocumentableNode = null;
        super.visit(that);
    }

    @Override
    public void visitAny(Node node) {
        if (node == term) {
            documentableNode = currentDocumentableNode;
        }
        if (documentableNode == null) {
            super.visitAny(node);
        }
    }
    
    private boolean isDocumentable(Tree.StatementOrArgument that) {
        if (that instanceof Tree.ClassOrInterface || 
                that instanceof Tree.AnyAttribute || 
                that instanceof Tree.AnyMethod || 
                that instanceof Tree.Constructor ||
                that instanceof Tree.ObjectDefinition || 
                that instanceof Tree.ModuleDescriptor || 
                that instanceof Tree.PackageDescriptor ||
                that instanceof Tree.Assertion) {
            return true;
        }
        return false;
    }

}