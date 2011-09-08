package com.redhat.ceylon.eclipse.imp.editorActionContributions;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.search.ui.ISearchQuery;
import org.eclipse.search.ui.ISearchResult;
import org.eclipse.search.ui.text.AbstractTextSearchResult;

import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.eclipse.imp.builder.CeylonBuilder;
import com.redhat.ceylon.eclipse.util.FindReferenceVisitor;

class FindReferencesSearchQuery implements ISearchQuery {
	
	private final Declaration referencedDeclaration;
	private final IProject project;
	private final FindReferenceVisitor frv;
	private AbstractTextSearchResult result = new CeylonSearchResult(this);
	private int count = 0;

	FindReferencesSearchQuery(Declaration referencedDeclaration, IProject project) {
		this.referencedDeclaration = referencedDeclaration;
		this.project = project;
		frv = new FindReferenceVisitor(referencedDeclaration);
	}

	@Override
	public IStatus run(IProgressMonitor monitor) throws OperationCanceledException {
	    for (PhasedUnit pu: CeylonBuilder.getUnits(project)) {
	        pu.getCompilationUnit().visit(frv);
	        //TODO: should really add these as we find them:
    		for (Node node: frv.getNodes()) {
    			FindContainerVisitor fcv = new FindContainerVisitor(node);
    			pu.getCompilationUnit().visit(fcv);
                if (node.getToken()==null) {
                    //a synthetic node inserted in the tree
                }
                else {
        			result.addMatch(new CeylonSearchMatch(fcv.getDeclaration(), 
        			        CeylonBuilder.getFile(pu), 
        					node.getStartIndex(), node.getStopIndex()-node.getStartIndex()+1,
        					node.getToken()));
                }
    		}
    		count+=frv.getNodes().size();
    		frv.getNodes().clear();
        }
		return Status.OK_STATUS;
	}

	@Override
	public ISearchResult getSearchResult() {
		return result;
	}

	@Override
	public String getLabel() {
		return "Displaying " + count + " references to '" + 
		        referencedDeclaration.getName() + "'";
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