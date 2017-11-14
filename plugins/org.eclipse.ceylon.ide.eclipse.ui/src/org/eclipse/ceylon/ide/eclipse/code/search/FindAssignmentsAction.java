/********************************************************************************
 * Copyright (c) {date} Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.code.search;

import static org.eclipse.ceylon.ide.eclipse.ui.CeylonPlugin.PLUGIN_ID;

import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorPart;

import org.eclipse.ceylon.compiler.typechecker.tree.Node;
import org.eclipse.ceylon.compiler.typechecker.tree.Tree;
import org.eclipse.ceylon.ide.common.util.FindAssignmentsVisitor;
import org.eclipse.ceylon.model.typechecker.model.ClassOrInterface;
import org.eclipse.ceylon.model.typechecker.model.Declaration;
import org.eclipse.ceylon.model.typechecker.model.FunctionOrValue;
import org.eclipse.ceylon.model.typechecker.model.Referenceable;
import org.eclipse.ceylon.model.typechecker.model.TypeAlias;
import org.eclipse.ceylon.model.typechecker.model.TypeParameter;

public class FindAssignmentsAction extends AbstractFindAction {

    private static final class Query extends FindSearchQuery {
        private Query(Declaration referencedDeclaration, IProject project) {
            super(referencedDeclaration, project);
        }

        @Override
        protected Set<Node> getNodes(Tree.CompilationUnit cu, 
                Referenceable referencedDeclaration) {
            FindAssignmentsVisitor frv = 
                    new FindAssignmentsVisitor((Declaration) referencedDeclaration);
            cu.visit(frv);
            return frv.getAssignmentNodeSet();
        }
        
        @Override
        int limitTo() {
            //this is understood by the impl of 
            //JavaSearch.createSearchPattern()
            return IJavaSearchConstants.WRITE_ACCESSES;
        }

        @Override
        protected String labelString() {
            return "assigments to";
        }
    }

    public FindAssignmentsAction() {
        super("Find Assignments");
        setActionDefinitionId(PLUGIN_ID + ".action.findAssignments");
    }
    
    public FindAssignmentsAction(CeylonSearchResultPage page, ISelection selection) {
        super("Find Assignments", page, selection);
        setActionDefinitionId(PLUGIN_ID + ".action.findAssignments");
    }
    
    public FindAssignmentsAction(IEditorPart editor) {
        super("Find Assignments", editor);
        setActionDefinitionId(PLUGIN_ID + ".action.findAssignments");
    }
    
    public FindAssignmentsAction(IEditorPart editor, Declaration dec) {
        super("Find Assignments", editor, dec);
        setActionDefinitionId(PLUGIN_ID + ".action.findAssignments");
    }
    
    @Override
    boolean isValidSelection() {
        //TODO: invalid for getters with no matching setter
        return declaration instanceof FunctionOrValue ||
        		declaration instanceof ClassOrInterface ||
        		declaration instanceof TypeAlias ||
        		declaration instanceof TypeParameter;
    }

    @Override
    public FindSearchQuery createSearchQuery() {
        return new Query((Declaration) declaration, project);
    }

}