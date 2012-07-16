package com.redhat.ceylon.eclipse.code.search;

import static com.redhat.ceylon.eclipse.ui.CeylonPlugin.PLUGIN_ID;

import java.util.Set;

import org.eclipse.ui.IEditorPart;

import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.util.FindReferenceVisitor;

public class FindReferencesAction extends AbstractFindAction {

	public FindReferencesAction() {}
	
    public FindReferencesAction(IEditorPart editor) {
		super("Find References", editor);
		setActionDefinitionId(PLUGIN_ID + ".action.findReferences");
	}
	
    @Override
    boolean isValidSelection() {
        return declaration!=null;
    }

	@Override
	public FindSearchQuery createSearchQuery() {
	    return new FindSearchQuery(declaration, project) {
	        @Override
	        protected Set<Node> getNodes(Tree.CompilationUnit cu) {
	            FindReferenceVisitor frv = new FindReferenceVisitor(declaration);
	            cu.visit(frv);
	            return frv.getNodes();
	        }
	        @Override
	        protected String labelString() {
	            return "references to";
	        }
        };
	}

}