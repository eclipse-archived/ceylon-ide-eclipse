package com.redhat.ceylon.eclipse.code.search;

import static com.redhat.ceylon.eclipse.ui.CeylonPlugin.PLUGIN_ID;

import java.util.Set;

import org.eclipse.ui.IEditorPart;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.Value;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.util.FindAssignmentsVisitor;

public class FindAssignmentsAction extends AbstractFindAction {

	public FindAssignmentsAction() {}
	
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
        return declaration instanceof Value;
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