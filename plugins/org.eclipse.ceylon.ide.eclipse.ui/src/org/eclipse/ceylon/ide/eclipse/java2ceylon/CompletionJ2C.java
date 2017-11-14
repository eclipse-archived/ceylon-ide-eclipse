/********************************************************************************
 * Copyright (c) {date} Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.java2ceylon;

import java.util.Map;

import org.eclipse.ceylon.compiler.typechecker.tree.Node;
import org.eclipse.ceylon.compiler.typechecker.tree.Tree;
import org.eclipse.ceylon.ide.eclipse.code.complete.EclipseCompletionProcessor;
import org.eclipse.ceylon.ide.eclipse.code.editor.CeylonEditor;
import org.eclipse.ceylon.ide.common.completion.completionManager_;
import org.eclipse.ceylon.model.typechecker.model.DeclarationWithProximity;
import org.eclipse.ceylon.model.typechecker.model.Scope;

public interface CompletionJ2C {

    completionManager_ dummyCompletionManager();

    EclipseCompletionProcessor newCompletionProcessor(CeylonEditor editor);

    Map<String, DeclarationWithProximity> getProposals(Node node, Scope scope,
            Tree.CompilationUnit rootNode);

}