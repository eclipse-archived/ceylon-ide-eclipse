package com.redhat.ceylon.eclipse.imp.editorActionContributions;

import java.util.Set;

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

abstract class FindSearchQuery implements ISearchQuery {
	
	private final Declaration referencedDeclaration;
	private final IProject project;
	private AbstractTextSearchResult result = new CeylonSearchResult(this);
	private int count = 0;

	FindSearchQuery(Declaration referencedDeclaration, IProject project) {
		this.referencedDeclaration = referencedDeclaration;
		this.project = project;
	}

	@Override
	public IStatus run(IProgressMonitor monitor) throws OperationCanceledException {
	    for (PhasedUnit pu: CeylonBuilder.getUnits(project)) {
	        Set<Node> nodes = getNodes(pu);
	        //TODO: should really add these as we find them:
            for (Node node: nodes) {
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
    		count+=nodes.size();
        }
		return Status.OK_STATUS;
	}

    protected abstract Set<Node> getNodes(PhasedUnit pu);
    
    protected abstract String labelString();

	@Override
	public ISearchResult getSearchResult() {
		return result;
	}

	@Override
	public String getLabel() {
		return "Displaying " + count + " " + labelString() + 
		        " '" + referencedDeclaration.getName() + "'";
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