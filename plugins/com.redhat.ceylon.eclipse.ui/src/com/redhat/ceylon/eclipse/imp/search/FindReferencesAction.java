package com.redhat.ceylon.eclipse.imp.search;

import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.imp.editor.UniversalEditor;

import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.eclipse.util.FindReferenceVisitor;

public class FindReferencesAction extends FindAction {

    public FindReferencesAction(UniversalEditor editor) {
		super("Find References", editor);
		setActionDefinitionId("com.redhat.ceylon.eclipse.ui.action.findReferences");
	}
	
    @Override
    boolean isValidSelection(Declaration selectedDeclaration) {
        return selectedDeclaration!=null;
    }

	@Override
	public FindSearchQuery createSearchQuery(final Declaration declaration, IProject project) {
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