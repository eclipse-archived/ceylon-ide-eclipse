package com.redhat.ceylon.eclipse.imp.editorActionContributions;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.search.ui.ISearchQuery;
import org.eclipse.search.ui.ISearchResult;
import org.eclipse.search.ui.text.AbstractTextSearchResult;
import org.eclipse.search.ui.text.Match;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.ui.FindReferenceVisitor;
import com.redhat.ceylon.eclipse.imp.parser.CeylonParseController;

class FindReferencesSearchQuery implements ISearchQuery {
	
	private final CeylonParseController cpc;
	private final Declaration d;
	private final Node node;
	private final IFile file;
	private final FindReferenceVisitor frv;
	AbstractTextSearchResult result = new CeylonSearchResult(this);

	FindReferencesSearchQuery(CeylonParseController cpc, Declaration d, Node node, IFile file) {
		this.cpc = cpc;
		this.d = d;
		this.node = node;
		this.file = file;
		frv = new FindReferenceVisitor(d);
	}

	@Override
	public IStatus run(IProgressMonitor monitor) throws OperationCanceledException {
		frv.visit(cpc.getRootNode());
		//TODO: should really add these as we find them:
		for (Node n: frv.getNodes()) {
			result.addMatch(new Match(file, n.getStartIndex(), 
					n.getStopIndex()-n.getStartIndex()+1));
		}
		return Status.OK_STATUS;
	}

	@Override
	public ISearchResult getSearchResult() {
		return result;
	}

	@Override
	public String getLabel() {
		return frv.getNodes().size() + " references to " + d.getName();
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