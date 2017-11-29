/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.code.search;

import static org.eclipse.ceylon.ide.eclipse.ui.CeylonPlugin.PLUGIN_ID;

import java.util.Collections;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorPart;

import org.eclipse.ceylon.model.typechecker.model.Declaration;
import org.eclipse.ceylon.model.typechecker.model.Referenceable;
import org.eclipse.ceylon.compiler.typechecker.tree.Node;
import org.eclipse.ceylon.compiler.typechecker.tree.Tree;
import org.eclipse.ceylon.ide.common.util.FindReferencesVisitor;
import org.eclipse.ceylon.ide.common.util.FindRefinementsVisitor;

public class FindRefinementsAction extends AbstractFindAction {

    private static final class Query extends FindSearchQuery {
        private Query(Declaration referencedDeclaration, IProject project) {
            super(referencedDeclaration, project);
        }

        @Override
        protected Set<Node> getNodes(Tree.CompilationUnit cu,
                Referenceable referencedDeclaration) {
            //TODO: very ugly!!
            Declaration declaration = (Declaration)
                    new FindReferencesVisitor(referencedDeclaration)
                            .getDeclaration();
            FindRefinementsVisitor frv = new FindRefinementsVisitor(declaration);
            cu.visit(frv);
            Set<Tree.StatementOrArgument> nodes = frv.getDeclarationNodeSet();
            return Collections.<Node>unmodifiableSet(nodes);
        }

        @Override
        int limitTo() {
            //TODO: is this really correct?
            return IJavaSearchConstants.IGNORE_DECLARING_TYPE |
                   IJavaSearchConstants.DECLARATIONS;
        }

        @Override
        protected String labelString() {
            return "refinements of";
        }
    }

    public FindRefinementsAction() {
        super("Find Refinements");
        setActionDefinitionId(PLUGIN_ID + ".action.findRefinements");
    }
    
    public FindRefinementsAction(CeylonSearchResultPage page, ISelection selection) {
        super("Find Refinements", page, selection);
        setActionDefinitionId(PLUGIN_ID + ".action.findRefinements");
    }
    
    public FindRefinementsAction(IEditorPart editor) {
        super("Find Refinements", editor);
        setActionDefinitionId(PLUGIN_ID + ".action.findRefinements");
    }
    
    public FindRefinementsAction(IEditorPart editor, Declaration dec) {
        super("Find Refinements", editor, dec);
        setActionDefinitionId(PLUGIN_ID + ".action.findRefinements");
    }
    
    @Override
    boolean isValidSelection() {
        if (declaration==null || 
                declaration.getNameAsString()==null) {
            return false;
        }
        else {
            declaration = 
                    new FindReferencesVisitor(declaration)
                            .getDeclaration();
            return declaration instanceof Declaration;
        }
    }

    @Override
    public FindSearchQuery createSearchQuery() {
        return new Query((Declaration) declaration, project);
    }
}