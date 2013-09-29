package com.redhat.ceylon.eclipse.code.search;

import static com.redhat.ceylon.eclipse.ui.CeylonPlugin.PLUGIN_ID;

import java.util.Collections;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.ui.IEditorPart;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.TypeParameter;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.util.FindReferenceVisitor;
import com.redhat.ceylon.eclipse.util.FindRefinementsVisitor;

public class FindRefinementsAction extends AbstractFindAction {

	private static final class Query extends FindSearchQuery {
        private Query(Declaration referencedDeclaration, IProject project) {
            super(referencedDeclaration, project);
        }

        @Override
        protected Set<Node> getNodes(Tree.CompilationUnit cu,
                Declaration referencedDeclaration) {
            Declaration declaration = new FindReferenceVisitor(referencedDeclaration).getDeclaration();
            FindRefinementsVisitor frv = new FindRefinementsVisitor(declaration);
            cu.visit(frv);
            Set<Tree.Declaration> nodes = frv.getDeclarationNodes();
            return Collections.<Node>unmodifiableSet(nodes);
        }

        @Override
        protected String labelString() {
            return "refinements of";
        }
    }

    public FindRefinementsAction() {}
	
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
        declaration = new FindReferenceVisitor(declaration).getDeclaration();
        return declaration!=null && 
                declaration.isClassOrInterfaceMember() &&
                !(declaration instanceof TypeParameter);
    }

    @Override
    public FindSearchQuery createSearchQuery() {
        return new Query(declaration, project);
    }
}