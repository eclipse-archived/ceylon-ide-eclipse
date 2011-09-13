package com.redhat.ceylon.eclipse.imp.editorActionContributions;

import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.imp.editor.UniversalEditor;

import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.eclipse.util.FindReferenceVisitor;

class FindReferencesAction extends FindAction {

	FindReferencesAction(UniversalEditor editor) {
		super("Find References", editor);
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