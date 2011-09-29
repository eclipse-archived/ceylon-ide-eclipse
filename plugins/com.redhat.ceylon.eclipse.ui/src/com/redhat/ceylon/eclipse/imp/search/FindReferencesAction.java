package com.redhat.ceylon.eclipse.imp.search;

import java.util.Set;

import org.eclipse.ui.IEditorPart;

import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.eclipse.util.FindReferenceVisitor;

public class FindReferencesAction extends AbstractFindAction {

    public FindReferencesAction(IEditorPart editor) {
		super("Find References", editor);
		setActionDefinitionId("com.redhat.ceylon.eclipse.ui.action.findReferences");
	}
	
    @Override
    boolean isValidSelection() {
        return declaration!=null;
    }

	@Override
	public FindSearchQuery createSearchQuery() {
	    return new FindSearchQuery(declaration, project) {
	        @Override
	        protected Set<Node> getNodes(PhasedUnit pu) {
	            FindReferenceVisitor frv = new FindReferenceVisitor(declaration);
	            pu.getCompilationUnit().visit(frv);
	            return frv.getNodes();
	        }
	        @Override
	        protected String labelString() {
	            return "references to";
	        }
        };
	}

}