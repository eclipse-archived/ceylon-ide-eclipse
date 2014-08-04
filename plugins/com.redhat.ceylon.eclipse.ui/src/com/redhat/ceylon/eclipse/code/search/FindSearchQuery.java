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
import com.redhat.ceylon.compiler.typechecker.model.Module;
import com.redhat.ceylon.compiler.typechecker.model.Referenceable;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.CompilationUnit;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.code.parse.CeylonParseController;
import com.redhat.ceylon.eclipse.core.builder.CeylonBuilder;
import com.redhat.ceylon.eclipse.core.model.JDTModule;
import com.redhat.ceylon.eclipse.util.EditorUtil;

abstract class FindSearchQuery implements ISearchQuery {
    
    private Referenceable referencedDeclaration;
    //private final IProject project;
    private AbstractTextSearchResult result = new CeylonSearchResult(this);
    private int count = 0;
    private IWorkbenchPage page;
    private String name;
    
    FindSearchQuery(Referenceable referencedDeclaration, IProject project) {
        this.referencedDeclaration = referencedDeclaration;
        //this.project = project;
        this.page = EditorUtil.getActivePage();
        name = referencedDeclaration.getNameAsString();
    }
    
    @Override
    public IStatus run(IProgressMonitor monitor) throws OperationCanceledException {
        //List<PhasedUnit> units = Ceylon Builder.getUnits(project);
        //if (units==null) units = CeylonBuilder.getUnits();
        //List<PhasedUnit> units = getUnits();
        int work = 0;
        Set<String> searchedArchives = new HashSet<String>();
        for (TypeChecker tc: CeylonBuilder.getTypeCheckers()) {
            work+=1;
            for (Module m : tc.getContext().getModules().getListOfModules()) {
                if (m instanceof JDTModule) {
                    JDTModule module = (JDTModule) m;
                    if (module.isCeylonArchive() && module.getArtifact() != null) { 
                        String archivePath = module.getArtifact().getAbsolutePath();
                        if (!searchedArchives.contains(archivePath)) {
                            work++;
                        }
                    }
                }
            }
        }
        monitor.beginTask("Searching for " + labelString() + " '" + name + "'", work);
        searchedArchives = new HashSet<String>();
        for (TypeChecker tc: CeylonBuilder.getTypeCheckers()) {
            findInUnits(tc.getPhasedUnits());
            monitor.worked(1);
            for (Module m : tc.getContext().getModules().getListOfModules()) {
                if (m instanceof JDTModule) {
                    JDTModule module = (JDTModule) m;
                    if (module.isCeylonArchive() && module.getArtifact() != null) { 
                        String archivePath = module.getArtifact().getAbsolutePath();
                        if (!searchedArchives.contains(archivePath)) {
                            findInUnits(module.getPhasedUnits());
                            searchedArchives.add(archivePath);
                            monitor.worked(1);
                        }
                    }
                }
            }
        }
        monitor.done();
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
            Referenceable referencedDeclaration);
    
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