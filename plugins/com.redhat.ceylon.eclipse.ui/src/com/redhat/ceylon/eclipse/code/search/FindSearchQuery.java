package com.redhat.ceylon.eclipse.code.search;

import static org.eclipse.jdt.core.search.SearchPattern.createPattern;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.core.search.SearchPattern;
import org.eclipse.jdt.core.search.SearchRequestor;
import org.eclipse.jdt.internal.corext.util.SearchUtils;
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
import com.redhat.ceylon.compiler.typechecker.model.Method;
import com.redhat.ceylon.compiler.typechecker.model.Module;
import com.redhat.ceylon.compiler.typechecker.model.Referenceable;
import com.redhat.ceylon.compiler.typechecker.model.Value;
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
        
        if (referencedDeclaration instanceof Declaration) {
            findJavaReferences(monitor);
        }
        
        monitor.done();
        referencedDeclaration = null;
        return Status.OK_STATUS;
    }
    
    private void findJavaReferences(IProgressMonitor pm) {
        SearchEngine searchEngine = new SearchEngine();
        String pattern;
        int sort;
        Declaration declaration = (Declaration) referencedDeclaration;
        String container = 
                declaration.getContainer()
                           .getQualifiedNameString()
                           .replace("::", ".");
        final String name = declaration.getName();
        if (declaration instanceof Method) {
            pattern = container + '.' + name;
            sort = IJavaSearchConstants.METHOD;
        }
        else if (declaration instanceof Value) {
            //TODO: setters!
            pattern = container + '.' + "get" + 
                    Character.toUpperCase(name.charAt(0)) +
                    name.substring(1);
            sort = IJavaSearchConstants.METHOD;
        }
        else {
            pattern = container + '.' + name;
            sort = IJavaSearchConstants.CLASS_AND_INTERFACE;
        }
        IProject[] referencingProjects = project.getReferencingProjects();
        IProject[] projects = new IProject[referencingProjects.length+1];
        projects[0] = project;
        System.arraycopy(referencingProjects, 0, projects, 1, referencingProjects.length);
        SearchRequestor requestor = new NewSearchResultCollector(result, true);
        try {
            SearchPattern searchPattern = createPattern(pattern, sort, limitTo(), 
                    SearchPattern.R_EXACT_MATCH);
            searchEngine.search(searchPattern, 
                    SearchUtils.getDefaultSearchParticipants(),
                    SearchEngine.createJavaSearchScope(projects), 
                    requestor, pm);
        }
        catch (CoreException e) {
            e.printStackTrace();
        }
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
                    FindContainerVisitor fcv = 
                            new FindContainerVisitor(node);
                    cu.visit(fcv);
                    Tree.StatementOrArgument c = 
                            fcv.getStatementOrArgument();
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