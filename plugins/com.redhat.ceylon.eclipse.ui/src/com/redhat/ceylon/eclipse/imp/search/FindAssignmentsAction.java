package com.redhat.ceylon.eclipse.imp.search;

import java.util.Set;

import org.eclipse.ui.IEditorPart;

import com.redhat.ceylon.compiler.typechecker.model.Getter;
import com.redhat.ceylon.compiler.typechecker.model.Parameter;
import com.redhat.ceylon.compiler.typechecker.model.Value;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.util.FindAssignmentsVisitor;

public class FindAssignmentsAction extends AbstractFindAction {

    public FindAssignmentsAction(IEditorPart editor) {
		super("Find Assignments", editor);
		setActionDefinitionId("com.redhat.ceylon.eclipse.ui.action.findAssignments");
	}
	
    @Override
    boolean isValidSelection() {
        return declaration instanceof Value ||
                declaration instanceof Parameter ||
                declaration instanceof Getter;
    }

	@Override
	public FindSearchQuery createSearchQuery() {
	    return new FindSearchQuery(declaration, project) {
	        @Override
	        protected Set<Node> getNodes(Tree.CompilationUnit cu) {
	            FindAssignmentsVisitor frv = new FindAssignmentsVisitor(declaration);
	            cu.visit(frv);
	            return frv.getNodes();
	        }
	        @Override
	        protected String labelString() {
	            return "assigments to";
	        }
        };
	}

}