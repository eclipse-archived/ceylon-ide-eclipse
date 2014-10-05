package com.redhat.ceylon.eclipse.code.search;

import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getProjectTypeChecker;
import static com.redhat.ceylon.eclipse.util.JavaSearch.createSearchPattern;
import static com.redhat.ceylon.eclipse.util.JavaSearch.getProjectsToSearch;
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
import org.eclipse.jface.text.IRegion;
import org.eclipse.search.ui.ISearchQuery;
import org.eclipse.search.ui.ISearchResult;
import org.eclipse.search.ui.text.AbstractTextSearchResult;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;

import com.redhat.ceylon.compiler.typechecker.TypeChecker;
import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
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
import com.redhat.ceylon.eclipse.util.DocLinks;
import com.redhat.ceylon.eclipse.util.EditorUtil;

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
        monitor.beginTask("Searching for " + labelString() + " '" + name + "'", 
                estimateWork(monitor));
        if (monitor.isCanceled()) {
            return Status.CANCEL_STATUS;
        }
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
        Package pack = getPackage();
        if (pack==null) return;
        for (IProject project: getProjectsToSearch(this.project)) {
            if (CeylonNature.isEnabled(project)) {
                TypeChecker typeChecker = getProjectTypeChecker(project);
                findInUnits(typeChecker.getPhasedUnits().getPhasedUnits(), monitor);
                monitor.worked(1);
                if (monitor.isCanceled()) {
                    throw new OperationCanceledException();
                }
                Modules modules = typeChecker.getContext().getModules();
                for (Module m: modules.getListOfModules()) {
                    if (m instanceof JDTModule) {
                        JDTModule module = (JDTModule) m;
                        if (module.isCeylonArchive() && 
                                !module.isProjectModule() && 
                                module.getArtifact()!=null) {
                            String archivePath = module.getArtifact().getAbsolutePath();
                            if (searchedArchives.add(archivePath) &&
                                    searchedArchives.add(module.getSourceArchivePath()) && 
                                    m.getAllPackages().contains(pack)) {
                                findInUnits(module.getPhasedUnits(), monitor);
                                monitor.worked(1);
                                if (monitor.isCanceled()) {
                                    throw new OperationCanceledException();
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private Package getPackage() {
        if (referencedDeclaration instanceof Declaration) {
            return referencedDeclaration.getUnit().getPackage();
        }
        else if (referencedDeclaration instanceof Package) {
            return (Package) referencedDeclaration;
        }
        else if (referencedDeclaration instanceof Module) {
            return ((Module) referencedDeclaration).getRootPackage();
        }
        else {
            return null;
        }
    }
    
    private int estimateWork(IProgressMonitor monitor) {
        int work = 0;
        Set<String> searchedArchives = new HashSet<String>();
        Package pack = getPackage();
        if (pack==null) return 0;
        for (IProject project: getProjectsToSearch(this.project)) {
            if (CeylonNature.isEnabled(project)) {
                work++;
                Modules modules = getProjectTypeChecker(project).getContext().getModules();
                for (Module m: modules.getListOfModules()) {
                    if (m instanceof JDTModule) {
                        JDTModule module = (JDTModule) m;
                        if (module.isCeylonArchive() && 
                                !module.isProjectModule() && 
                                module.getArtifact()!=null) { 
                            String archivePath = module.getArtifact().getAbsolutePath();
                            if (searchedArchives.add(archivePath) &&
                                    searchedArchives.add(module.getSourceArchivePath()) && 
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
    
    private void findJavaReferences(IProgressMonitor monitor) {
        Declaration declaration = (Declaration) referencedDeclaration;
        SearchPattern searchPattern = createSearchPattern(declaration, limitTo());
        if (searchPattern==null) return;
        runSearch(monitor, new SearchEngine(), searchPattern, getProjectsToSearch(project), 
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
    
    private void findInUnits(Iterable<? extends PhasedUnit> units, 
            IProgressMonitor monitor) {
        for (PhasedUnit pu: units) {
            monitor.subTask("Searching source file " + pu.getUnitFile().getPath());
            CompilationUnit cu = getRootNode(pu);
            Set<Node> nodes = getNodes(cu, referencedDeclaration);
            //TODO: should really add these as we find them:
            for (Node node: nodes) {
                if (node.getToken()==null) {
                    //a synthetic node inserted in the tree
                }
                else {
                    CeylonSearchMatch match = 
                            CeylonSearchMatch.create(node, cu, pu.getUnitFile());
                    if (node instanceof Tree.DocLink) {
                        Tree.DocLink link = (Tree.DocLink) node;
                        if (link.getBase().equals(referencedDeclaration)) {
                            IRegion r = DocLinks.nameRegion(link, 0);
                            match.setOffset(r.getOffset());
                            match.setLength(r.getLength());
                        }
                        else {
                            for (Declaration d: link.getQualified()) {
                                if (d.equals(referencedDeclaration)) {
                                    IRegion r = DocLinks.nameRegion(link, 0);
                                    match.setOffset(r.getOffset());
                                    match.setLength(r.getLength());
                                }
                            }
                        }
                    }
                    result.addMatch(match);
                    count++;
                }
            }
            if (monitor.isCanceled()) {
                throw new OperationCanceledException();
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