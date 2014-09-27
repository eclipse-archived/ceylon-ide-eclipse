package com.redhat.ceylon.eclipse.code.search;

import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getProjectTypeChecker;
import static com.redhat.ceylon.eclipse.util.JavaSearch.createSearchPattern;
import static com.redhat.ceylon.eclipse.util.JavaSearch.runSearch;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.core.search.SearchMatch;
import org.eclipse.jdt.core.search.SearchPattern;
import org.eclipse.jdt.internal.ui.search.NewSearchResultCollector;
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
import com.redhat.ceylon.compiler.typechecker.model.Modules;
import com.redhat.ceylon.compiler.typechecker.model.Package;
import com.redhat.ceylon.compiler.typechecker.model.Referenceable;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.CompilationUnit;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.code.parse.CeylonParseController;
import com.redhat.ceylon.eclipse.core.builder.CeylonNature;
import com.redhat.ceylon.eclipse.core.model.JDTModule;
import com.redhat.ceylon.eclipse.util.EditorUtil;
import com.redhat.ceylon.eclipse.util.JavaSearch;

abstract class FindSearchQuery implements ISearchQuery {
    
    private Referenceable referencedDeclaration;
    //private final IProject project;
    private AbstractTextSearchResult result = new CeylonSearchResult(this);
    private int count = 0;
    private IWorkbenchPage page;
    private String name;
    private IProject project;
    
    FindSearchQuery(Referenceable referencedDeclaration, IProject project) {
        this.referencedDeclaration = referencedDeclaration;
        this.project = project;
        //this.project = project;
        this.page = EditorUtil.getActivePage();
        name = referencedDeclaration.getNameAsString();
    }
    
    @Override
    public IStatus run(IProgressMonitor monitor) throws OperationCanceledException {
        int work = estimateWork(monitor);
        monitor.beginTask("Searching for " + labelString() + " '" + name + "'", work);
        findCeylonReferences(monitor);
        if (referencedDeclaration instanceof Declaration && project!=null) {
            findJavaReferences(monitor);
        }
        monitor.done();
        referencedDeclaration = null;
        return Status.OK_STATUS;
    }

    private void findCeylonReferences(IProgressMonitor monitor) {
        Set<String> searchedArchives = new HashSet<String>();
        Package pack = referencedDeclaration.getUnit().getPackage();
        for (IProject project: JavaSearch.getProjectsToSearch(project)) {
            if (CeylonNature.isEnabled(project)) {
                TypeChecker typeChecker = getProjectTypeChecker(project);
                findInUnits(typeChecker.getPhasedUnits());
                monitor.worked(1);
                Modules modules = typeChecker.getContext().getModules();
                for (Module m: modules.getListOfModules()) {
                    if (m instanceof JDTModule) {
                        JDTModule module = (JDTModule) m;
                        if (module.isCeylonArchive() && module.getArtifact()!=null) {
                            String archivePath = module.getArtifact().getAbsolutePath();
                            if (searchedArchives.add(archivePath) && 
                                    m.getAllPackages().contains(pack)) {
                                findInUnits(module.getPhasedUnits());
                                monitor.worked(1);
                            }
                        }
                    }
                }
            }
        }
    }

    private int estimateWork(IProgressMonitor monitor) {
        int work = 0;
        Set<String> searchedArchives = new HashSet<String>();
        Package pack = referencedDeclaration.getUnit().getPackage();
        for (IProject project: JavaSearch.getProjectsToSearch(project)) {
            if (CeylonNature.isEnabled(project)) {
                work+=1;
                Modules modules = getProjectTypeChecker(project).getContext().getModules();
                for (Module m : modules.getListOfModules()) {
                    if (m instanceof JDTModule) {
                        JDTModule module = (JDTModule) m;
                        if (module.isCeylonArchive() && module.getArtifact()!=null) { 
                            String archivePath = module.getArtifact().getAbsolutePath();
                            if (searchedArchives.add(archivePath) && 
                                    m.getAllPackages().contains(pack)) {
                                work++;
                            }
                        }
                    }
                }
            }
        }
        return work;
    }
    
    private void findJavaReferences(IProgressMonitor pm) {
        Declaration declaration = (Declaration) referencedDeclaration;
        SearchPattern searchPattern = createSearchPattern(declaration, limitTo());
        if (searchPattern==null) return;
        runSearch(pm, new SearchEngine(), searchPattern, JavaSearch.getProjectsToSearch(project), 
                new NewSearchResultCollector(result, true) {
            @Override
            public void acceptSearchMatch(SearchMatch match)
                    throws CoreException {
                super.acceptSearchMatch(match);
                IJavaElement enclosingElement= (IJavaElement) match.getElement();
                if (enclosingElement != null && 
                        match.getAccuracy() != SearchMatch.A_INACCURATE) {
                    count++;
                }
            }
            });
    }
    
    abstract int limitTo();
    
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
                    result.addMatch(CeylonSearchMatch.create(node, cu, pu.getUnitFile()));
                    count++;
                }
            }
        }
    }

    Tree.CompilationUnit getRootNode(PhasedUnit pu) {
        for (IEditorPart editor: page.getDirtyEditors()) {
            if (editor instanceof CeylonEditor) {
                CeylonParseController cpc = 
                        ((CeylonEditor)editor).getParseController();
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