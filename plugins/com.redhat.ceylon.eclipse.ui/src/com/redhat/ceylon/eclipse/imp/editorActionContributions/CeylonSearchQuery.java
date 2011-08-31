package com.redhat.ceylon.eclipse.imp.editorActionContributions;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.search.ui.ISearchQuery;
import org.eclipse.search.ui.ISearchResult;
import org.eclipse.search.ui.text.AbstractTextSearchResult;

import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.ui.SearchVisitor;
import com.redhat.ceylon.eclipse.imp.parser.CeylonParseController;
import com.redhat.ceylon.eclipse.imp.refactoring.FindContainerVisitor;

class CeylonSearchQuery implements ISearchQuery {
	
	private final CeylonParseController cpc;
	private final String string;
	private final IFile file;
	private final SearchVisitor sv;
	private AbstractTextSearchResult result = new CeylonSearchResult(this);

	CeylonSearchQuery(CeylonParseController cpc, String string, IFile file,
			final boolean includeReferences, final boolean includeDeclarations,
			final boolean caseSensitive) {
		this.cpc = cpc;
		this.string = string;
		this.file = file;
		sv = new SearchVisitor( new SearchVisitor.Matcher() {
			@Override
			public boolean matches(String string) {
				if (caseSensitive) {
					return string.contains(CeylonSearchQuery.this.string);
				}
				else {
					return string.toLowerCase()
						.contains(CeylonSearchQuery.this.string.toLowerCase());
				}
			}
			@Override
			public boolean includeReferences() {
				return includeReferences;
			}
			@Override
			public boolean includeDeclarations() {
				return includeDeclarations;
			}
		});
	}

	@Override
	public IStatus run(IProgressMonitor monitor) throws OperationCanceledException {
		sv.visit(cpc.getRootNode());
		//TODO: should really add these as we find them:
		for (Node node: sv.getNodes()) {
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
		return "Displaying " + sv.getNodes().size() + 
				" matches of '" + string + "'";
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