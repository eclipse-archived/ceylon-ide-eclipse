package com.redhat.ceylon.eclipse.imp.editorActionContributions;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.search.ui.ISearchQuery;
import org.eclipse.search.ui.ISearchResult;
import org.eclipse.search.ui.text.AbstractTextSearchResult;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.ui.FindReferenceVisitor;
import com.redhat.ceylon.eclipse.imp.parser.CeylonParseController;
import com.redhat.ceylon.eclipse.imp.refactoring.FindContainerVisitor;

class FindReferencesSearchQuery implements ISearchQuery {
	
	private final CeylonParseController cpc;
	private final Declaration referencedDeclaration;
	private final IFile file;
	private final FindReferenceVisitor frv;
	private AbstractTextSearchResult result = new CeylonSearchResult(this);

	FindReferencesSearchQuery(CeylonParseController cpc, Declaration referencedDeclaration, IFile file) {
		this.cpc = cpc;
		this.referencedDeclaration = referencedDeclaration;
		this.file = file;
		frv = new FindReferenceVisitor(referencedDeclaration);
	}

	@Override
	public IStatus run(IProgressMonitor monitor) throws OperationCanceledException {
		frv.visit(cpc.getRootNode());
		//TODO: should really add these as we find them:
		for (Node node: frv.getNodes()) {
			FindContainerVisitor fcv = new FindContainerVisitor(node);
			cpc.getRootNode().visit(fcv);
			result.addMatch(new CeylonSearchMatch(fcv.getDeclaration(), file, 
					node.getStartIndex(), node.getStopIndex()-node.getStartIndex()+1,
					node.getToken()));
		}
		return Status.OK_STATUS;
	}

	@Override
	public ISearchResult getSearchResult() {
		return result;
	}

	@Override
	public String getLabel() {
		return "Displaying " + frv.getNodes().size() + 
				" references to '" + referencedDeclaration.getName() + "'";
	}

	@Override
	public boolean canRunInBackground() {
		return true;
	}

	@Override
	public boolean canRerun() {
		return false;
	}
}