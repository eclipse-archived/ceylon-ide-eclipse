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
public class FindStatementVisitor extends Visitor {
    
    private final Node term;
    private Tree.Statement statement;
    private Tree.Statement currentStatement;
    private final boolean toplevel;
    private boolean currentlyToplevel=true;
    private boolean resultIsToplevel;
    private boolean inParameter;
    
    public Tree.Statement getStatement() {
        return statement;
    }
    
    public boolean isToplevel() {
        return resultIsToplevel;
    }
    
    public FindStatementVisitor(Node term, boolean toplevel) {
        this.term = term;
        this.toplevel = toplevel;
    }
    
    @Override
    public void visit(Tree.Parameter that) {
        boolean oip = inParameter;
        inParameter = true;
        super.visit(that);
        inParameter = oip;
    }

    @Override
    public void visit(Tree.Statement that) {
        if ((!toplevel || currentlyToplevel) && !inParameter) {
            if (!(that instanceof Tree.Variable ||
                    that instanceof Tree.TypeConstraint ||
                    that instanceof Tree.TypeParameterDeclaration)) {
                currentStatement = that;
                resultIsToplevel = currentlyToplevel;
            }
        }
        boolean octl = currentlyToplevel;
        currentlyToplevel = false;
        super.visit(that);
        currentlyToplevel = octl;
    }
    
    @Override
    public void visitAny(Node node) {
        if (node==term) {
            statement = currentStatement;
            resultIsToplevel = currentlyToplevel;
        }
        if (statement==null) {
            super.visitAny(node);
        }
    }
    
}