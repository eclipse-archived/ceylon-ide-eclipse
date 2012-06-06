package com.redhat.ceylon.eclipse.imp.search;

import static java.util.regex.Pattern.CASE_INSENSITIVE;
import static java.util.regex.Pattern.compile;

import java.util.List;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.search.ui.ISearchQuery;
import org.eclipse.search.ui.ISearchResult;
import org.eclipse.search.ui.text.AbstractTextSearchResult;

import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.context.PhasedUnits;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.eclipse.imp.builder.CeylonBuilder;
import com.redhat.ceylon.eclipse.util.SearchVisitor;
import com.redhat.ceylon.eclipse.vfs.IFileVirtualFile;

class CeylonSearchQuery implements ISearchQuery {
	
	class PatternMatcher implements SearchVisitor.Matcher {
        Pattern pattern = compile(patternString(), flags());
        
        private String patternString() {
            return regex ? string : string
                    .replace("*", ".*").replace("?", ".");
        }
        
        private int flags() {
            return caseSensitive ? 0 : CASE_INSENSITIVE;
        }
        
        @Override
        public boolean matches(String string) {
            return pattern.matcher(string).matches();
        }
        
        @Override
        public boolean includeReferences() {
            return includeReferences;
        }
        
        @Override
        public boolean includeDeclarations() {
            return includeDeclarations;
        }
    }

    private final String string;
    private final String[] projects;
    private final IResource[] resources;
	private AbstractTextSearchResult result = new CeylonSearchResult(this);
    private int count = 0;
    private final boolean caseSensitive;
    private final boolean regex;
    private final boolean includeReferences;
    private final boolean includeDeclarations;

	CeylonSearchQuery(String string, String[] projects, IResource[] resources,
			boolean includeReferences, boolean includeDeclarations,
			boolean caseSensitive, boolean regex) {
		this.string = string;
		this.projects = projects;
		this.caseSensitive = caseSensitive;
		this.includeDeclarations = includeDeclarations;
		this.includeReferences = includeReferences;
		this.regex = regex;
		this.resources = resources;
	}

	@Override
	public IStatus run(IProgressMonitor monitor) throws OperationCanceledException {
	    List<PhasedUnit> units = projects==null ? 
	                CeylonBuilder.getUnits() : 
	                CeylonBuilder.getUnits(projects);
	    monitor.beginTask("Ceylon Search", units.size());
        if (monitor.isCanceled()) return Status.CANCEL_STATUS;

        for (final PhasedUnit pu: units) {
            if (isWithinSelection(pu)) {
                monitor.subTask("Searching source file " + pu.getUnitFile().getPath());
    	        SearchVisitor sv = new SearchVisitor(new PatternMatcher()) {
    	            @Override
    	            public void matchingNode(Node node) {
    	                FindContainerVisitor fcv = new FindContainerVisitor(node);
    	                pu.getDeclarations();
    	                pu.getCompilationUnit().visit(fcv);
    	                result.addMatch(new CeylonSearchMatch(fcv.getDeclaration(), 
    	                        CeylonBuilder.getFile(pu), 
    	                        node.getStartIndex(), node.getStopIndex()-node.getStartIndex()+1,
    	                        node.getToken()));
    	                count++;
    	            }
    	        };
                pu.getCompilationUnit().visit(sv);
            }
            monitor.worked(1);
            if (monitor.isCanceled()) return Status.CANCEL_STATUS;
        }
        monitor.done();
		return Status.OK_STATUS;
	}

    public boolean isWithinSelection(PhasedUnit pu) {
        if (resources==null) {
            return true;
        }
        else {
            for (IResource r: resources) {
                if (pu.getUnitFile() instanceof IFileVirtualFile &&
                        r.getLocation().isPrefixOf(((IFileVirtualFile) pu.getUnitFile())
                                .getResource().getLocation())) {
                    return true;
                }
            }
            return false;
        }
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