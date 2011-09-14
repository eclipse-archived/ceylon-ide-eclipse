package com.redhat.ceylon.eclipse.imp.editorActionContributions;

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.search.ui.ISearchQuery;
import org.eclipse.search.ui.ISearchResult;
import org.eclipse.search.ui.text.AbstractTextSearchResult;

import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.eclipse.imp.builder.CeylonBuilder;
import com.redhat.ceylon.eclipse.util.SearchVisitor;

class CeylonSearchQuery implements ISearchQuery {
	
	private final String string;
    private final String[] projects;
	private AbstractTextSearchResult result = new CeylonSearchResult(this);
    private int count = 0;
    private final boolean caseSensitive;
    private final boolean includeReferences;
    private final boolean includeDeclarations;

	CeylonSearchQuery(String string, String[] projects,
			boolean includeReferences, boolean includeDeclarations,
			boolean caseSensitive) {
		this.string = string;
		this.projects = projects;
		this.caseSensitive = caseSensitive;
		this.includeDeclarations = includeDeclarations;
		this.includeReferences = includeReferences;
	}

	@Override
	public IStatus run(IProgressMonitor monitor) throws OperationCanceledException {
	    List<PhasedUnit> units = projects==null ? 
	                CeylonBuilder.getUnits() : 
	                CeylonBuilder.getUnits(projects);
        for (PhasedUnit pu: units) {
	        SearchVisitor sv = new SearchVisitor( new SearchVisitor.Matcher() {
	            @Override
	            public boolean matches(String string) {
	                String pattern = CeylonSearchQuery.this.string;
	                //TODO: do a proper pattern match!
                    if (caseSensitive) {
	                    return string.contains(pattern);
	                }
	                else {
	                    return string.toLowerCase()
	                        .contains(pattern.toLowerCase());
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
            pu.getCompilationUnit().visit(sv);
    		//TODO: should really add these as we find them:
    		for (Node node: sv.getNodes()) {
    			FindContainerVisitor fcv = new FindContainerVisitor(node);
    			pu.getCompilationUnit().visit(fcv);
    			result.addMatch(new CeylonSearchMatch(fcv.getDeclaration(), 
    			        CeylonBuilder.getFile(pu), 
    					node.getStartIndex(), node.getStopIndex()-node.getStartIndex()+1,
    					node.getToken()));
    		}
    		count+=sv.getNodes().size();
        }
		return Status.OK_STATUS;
	}

	@Override
	public ISearchResult getSearchResult() {
		return result;
	}

	@Override
	public String getLabel() {
		String label = "Displaying " + count + 
				" matches of '" + string + "'";
		if (projects!=null && projects.length!=0) {
		    label += " in project ";
		    for (String project: projects) {
		        label += project + ", ";
		    }
		    label = label.substring(0, label.length()-2);
		}
        return label;
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