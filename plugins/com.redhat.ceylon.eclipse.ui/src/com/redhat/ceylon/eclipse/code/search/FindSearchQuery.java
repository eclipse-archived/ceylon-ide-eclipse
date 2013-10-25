package com.redhat.ceylon.eclipse.code.search;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.search.ui.ISearchQuery;
import org.eclipse.search.ui.ISearchResult;
import org.eclipse.search.ui.text.AbstractTextSearchResult;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;

import com.redhat.ceylon.compiler.typechecker.TypeChecker;
import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.context.PhasedUnits;
import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.Module;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.CompilationUnit;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.code.editor.Util;
import com.redhat.ceylon.eclipse.code.parse.CeylonParseController;
import com.redhat.ceylon.eclipse.core.builder.CeylonBuilder;
import com.redhat.ceylon.eclipse.core.model.loader.JDTModule;

abstract class FindSearchQuery implements ISearchQuery {
	
	private Declaration referencedDeclaration;
	//private final IProject project;
	private AbstractTextSearchResult result = new CeylonSearchResult(this);
	private int count = 0;
	private IWorkbenchPage page;
	private String name;
	
	FindSearchQuery(Declaration referencedDeclaration, IProject project) {
		this.referencedDeclaration = referencedDeclaration;
		//this.project = project;
		this.page = Util.getActivePage();
		name = referencedDeclaration.getName();
	}
	
	@Override
	public IStatus run(IProgressMonitor monitor) throws OperationCanceledException {
	    //List<PhasedUnit> units = Ceylon Builder.getUnits(project);
		//if (units==null) units = CeylonBuilder.getUnits();
		//List<PhasedUnit> units = getUnits();
		Set<String> searchedArchives = new HashSet<String>();
	    for (TypeChecker tc: CeylonBuilder.getTypeCheckers()) {
			findInUnits(tc.getPhasedUnits());
			for (Module m : tc.getContext().getModules().getListOfModules()) {
			    if (m instanceof JDTModule) {
			        JDTModule module = (JDTModule) m;
			        String archivePath = null;
			        if (module.isArchive() && 
			                !searchedArchives.contains(archivePath = module.getArtifact().getAbsolutePath())) {
	                    findInUnits(module.getPhasedUnits());
	                    searchedArchives.add(archivePath);
			        }
			    }
			}
        }
		referencedDeclaration = null;
		return Status.OK_STATUS;
	}
	
	public void findInUnits(PhasedUnits units) {
	    findInUnits(units.getPhasedUnits());
	}
	
    public void findInUnits(Iterable<? extends PhasedUnit> units) {
        for (PhasedUnit pu: units) {
            CompilationUnit cu = getRootNode(pu);
            Set<Node> nodes = getNodes(cu, referencedDeclaration);
            //TODO: should really add these as we find them:
            for (Node node: nodes) {
                if (node.getToken()==null) {
                    //a synthetic node inserted in the tree
                }
                else {
                    FindContainerVisitor fcv = new FindContainerVisitor(node);
                    cu.visit(fcv);
                    Tree.StatementOrArgument c = fcv.getStatementOrArgument();
                    if (c!=null) {
                        result.addMatch(new CeylonSearchMatch(c, pu.getUnitFile(), node));
                    }
                }
            }
            count+=nodes.size();
        }
    }

	Tree.CompilationUnit getRootNode(PhasedUnit pu) {
        for (IEditorPart editor: page.getDirtyEditors()) {
            if (editor instanceof CeylonEditor) {
                CeylonParseController cpc = ((CeylonEditor)editor).getParseController();
                if ( /*editor.isDirty() &&*/
                        pu.getUnit().equals(cpc.getRootNode().getUnit()) ) {
                    return cpc.getRootNode();
                }
            }
        }
        return pu.getCompilationUnit();
    }
    
    protected abstract Set<Node> getNodes(Tree.CompilationUnit cu, 
            Declaration referencedDeclaration);
    
    protected abstract String labelString();

	@Override
	public ISearchResult getSearchResult() {
		return result;
	}
	
	@Override
	public String getLabel() {
		return "Displaying " + count + " " + labelString() + 
                " '" + name + "'";
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