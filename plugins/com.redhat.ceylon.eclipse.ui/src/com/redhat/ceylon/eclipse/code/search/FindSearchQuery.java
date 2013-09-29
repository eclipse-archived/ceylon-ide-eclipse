package com.redhat.ceylon.eclipse.code.search;

import static com.redhat.ceylon.eclipse.code.parse.CeylonSourcePositionLocator.getIdentifyingNode;

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
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.CompilationUnit;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.code.editor.Util;
import com.redhat.ceylon.eclipse.code.parse.CeylonParseController;
import com.redhat.ceylon.eclipse.core.builder.CeylonBuilder;

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
		for (TypeChecker tc: CeylonBuilder.getTypeCheckers()) {
			findInUnits(tc.getPhasedUnits());
			for (PhasedUnits units: tc.getPhasedUnitsOfDependencies()) {
				//TODO: eliminate dupe hits in repos!!!
				findInUnits(units);
			}
        }
		referencedDeclaration = null;
		return Status.OK_STATUS;
	}
	
	public void findInUnits(PhasedUnits units) {
		for (PhasedUnit pu: units.getPhasedUnits()) {
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
					node = getIdentifyingNode(node);
					Tree.StatementOrArgument c = fcv.getStatementOrArgument();
					if (c!=null) {
					    result.addMatch(new CeylonSearchMatch(c, 
					            pu.getUnitFile(), 
					            node.getStartIndex(), 
					            node.getStopIndex()-node.getStartIndex()+1,
					            node.getToken()));
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