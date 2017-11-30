/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 1.0 which is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.code.complete;

import java.util.Map;

import org.eclipse.ceylon.compiler.typechecker.tree.Node;
import org.eclipse.ceylon.compiler.typechecker.tree.Tree;
import org.eclipse.ceylon.ide.eclipse.code.editor.CeylonEditor;
import org.eclipse.ceylon.ide.eclipse.java2ceylon.CompletionJ2C;
import org.eclipse.ceylon.ide.common.completion.completionManager_;
import org.eclipse.ceylon.model.typechecker.model.DeclarationWithProximity;
import org.eclipse.ceylon.model.typechecker.model.Scope;

public class completionJ2C implements CompletionJ2C {

    @Override
    public completionManager_ dummyCompletionManager() {
        return completionManager_.get_();
    }

    /* (non-Javadoc)
     * @see org.eclipse.ceylon.ide.eclipse.code.complete.CompletionJ2C#newCompletionProcessor(org.eclipse.ceylon.ide.eclipse.code.editor.CeylonEditor)
     */
    @Override
    public EclipseCompletionProcessor newCompletionProcessor(CeylonEditor editor) {
        return new CeylonCompletionProcessor(editor);
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.ceylon.ide.eclipse.code.complete.CompletionJ2C#getProposals(org.eclipse.ceylon.compiler.typechecker.tree.Node, org.eclipse.ceylon.model.typechecker.model.Scope, org.eclipse.ceylon.compiler.typechecker.tree.Tree.CompilationUnit)
     */
    @Override
    public Map<String,DeclarationWithProximity> getProposals(Node node, Scope scope, Tree.CompilationUnit rootNode) {
        return completionManager_.get_().getProposals(node, scope, "", false, rootNode, null);
    }
}
