package com.redhat.ceylon.eclipse.code.search;

import static com.redhat.ceylon.eclipse.ui.CeylonPlugin.PLUGIN_ID;

import java.util.Collections;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorPart;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.Referenceable;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.util.FindReferencesVisitor;
import com.redhat.ceylon.eclipse.util.FindRefinementsVisitor;

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
            Set<Tree.StatementOrArgument> nodes = frv.getDeclarationNodes();
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