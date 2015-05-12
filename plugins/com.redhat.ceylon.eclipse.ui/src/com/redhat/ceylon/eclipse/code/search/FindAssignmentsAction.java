package com.redhat.ceylon.eclipse.code.search;

import static com.redhat.ceylon.eclipse.ui.CeylonPlugin.PLUGIN_ID;

import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorPart;

import com.redhat.ceylon.model.typechecker.model.ClassOrInterface;
import com.redhat.ceylon.model.typechecker.model.Declaration;
import com.redhat.ceylon.model.typechecker.model.MethodOrValue;
import com.redhat.ceylon.model.typechecker.model.Referenceable;
import com.redhat.ceylon.model.typechecker.model.TypeAlias;
import com.redhat.ceylon.model.typechecker.model.TypeParameter;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.util.FindAssignmentsVisitor;

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
            return frv.getNodes();
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
        return declaration instanceof MethodOrValue ||
        		declaration instanceof ClassOrInterface ||
        		declaration instanceof TypeAlias ||
        		declaration instanceof TypeParameter;
    }

    @Override
    public FindSearchQuery createSearchQuery() {
        return new Query((Declaration) declaration, project);
    }

}