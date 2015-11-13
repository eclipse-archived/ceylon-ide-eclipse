package com.redhat.ceylon.eclipse.code.search;

import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getProjectTypeChecker;
import static com.redhat.ceylon.eclipse.util.EditorUtil.getActivePage;
import static com.redhat.ceylon.ide.common.util.toJavaString_.toJavaString;
import static java.util.regex.Pattern.CASE_INSENSITIVE;
import static java.util.regex.Pattern.compile;
import static org.eclipse.core.resources.ResourcesPlugin.getWorkspace;
import static com.redhat.ceylon.eclipse.java2ceylon.Java2CeylonProxies.vfsJ2C;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.text.IRegion;
import org.eclipse.search.ui.ISearchQuery;
import org.eclipse.search.ui.ISearchResult;
import org.eclipse.search.ui.text.AbstractTextSearchResult;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;

import com.redhat.ceylon.compiler.typechecker.TypeChecker;
import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.io.VirtualFile;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.code.parse.CeylonParseController;
import com.redhat.ceylon.eclipse.core.builder.CeylonBuilder;
import com.redhat.ceylon.eclipse.core.builder.CeylonNature;
import com.redhat.ceylon.eclipse.util.Filters;
import com.redhat.ceylon.eclipse.util.SearchVisitor;
import com.redhat.ceylon.ide.common.model.BaseIdeModule;
import com.redhat.ceylon.ide.common.model.CeylonProject;
import com.redhat.ceylon.ide.common.model.IdeModule;
import com.redhat.ceylon.model.typechecker.model.Module;
import com.redhat.ceylon.model.typechecker.model.Modules;

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

        @Override
        public boolean includeTypes() {
            return includeTypes;
        }

        @Override
        public boolean includeImports() {
            return includeImports;
        }

        @Override
        public boolean includeDoc() {
            return includeDoc;
        }

        @Override
        public int kinds() {
            return kinds;
        }
    }

    private AbstractTextSearchResult result = 
            new CeylonSearchResult(this);
    
    private final String string;
    private final String[] projects;
    private final IResource[] resources;
    private int count = 0;
    private final boolean caseSensitive;
    private final boolean regex;
    private final boolean includeReferences;
    private final boolean includeTypes;
    private final boolean includeDeclarations;
    private final boolean includeImports;
    private final boolean includeDoc;
    private final boolean sources;
    private final boolean archives;
    private final int kinds;
    private IWorkbenchPage page;
    
    CeylonSearchQuery(String string, 
            String[] projects, IResource[] resources,
            boolean includeReferences, 
            boolean includeTypes,
            boolean includeDeclarations,
            boolean includeImports,
            boolean includeDoc,
            boolean caseSensitive, boolean regex, 
            boolean sources, boolean archives,
            int kinds) {
        this.string = string;
        this.projects = projects;
        this.includeTypes = includeTypes;
        this.includeImports = includeImports;
        this.includeDoc = includeDoc;
        this.caseSensitive = caseSensitive;
        this.includeDeclarations = includeDeclarations;
        this.includeReferences = includeReferences;
        this.regex = regex;
        this.resources = resources;
        this.sources = sources;
        this.archives = archives;
        this.kinds = kinds;
        this.page = getActivePage();
    }
    
    private Collection<IProject> getProjectsToSearch() {
        if (projects==null) {
            return CeylonBuilder.getProjects();
        }
        List<IProject> result = 
                new ArrayList<IProject>();
        for (String name: projects) {
            IProject project = 
                    getWorkspace()
                        .getRoot()
                        .getProject(name);
            if (project!=null) {
//            if (CeylonNature.isEnabled(project)) {
                result.add(project);
//            }
            }
        }
        return result;
    }
    
    @Override
    public IStatus run(IProgressMonitor monitor) 
            throws OperationCanceledException {
        monitor.beginTask("Searching in Ceylon source", 
                estimateWork(monitor));
        if (monitor.isCanceled()) {
            return Status.CANCEL_STATUS;
        }
        search(monitor);
        monitor.done();
        return Status.OK_STATUS;
    }
    
    private Filters filters = new Filters();

    private void search(IProgressMonitor monitor) {
        result.removeAll();
        count = 0;
        Set<String> searchedArchives = new HashSet<String>();
        Collection<IProject> projectsToSearch = getProjectsToSearch();
        for (IProject project: projectsToSearch) {
            if (CeylonNature.isEnabled(project)) {
                TypeChecker typeChecker = 
                        getProjectTypeChecker(project);
                if (sources) {
                    List<PhasedUnit> phasedUnits = 
                            typeChecker.getPhasedUnits()
                                .getPhasedUnits();
                    findInUnits(monitor, phasedUnits);
                    monitor.worked(1);
                    if (monitor.isCanceled()) {
                        throw new OperationCanceledException();
                    }
                }
                if (archives) {
                    Modules modules = 
                            typeChecker.getContext()
                                .getModules();
                    for (Module m: modules.getListOfModules()) {
                        if (m instanceof IdeModule &&
                                !filters.isFiltered(m)) {
                            IdeModule<IProject,IResource,IFolder,IFile> module = (IdeModule<IProject,IResource,IFolder,IFile>) m;
                            if (module.getIsCeylonArchive() && 
                                    !module.getIsProjectModule() && 
                                    module.getArtifact()!=null) { 

                                CeylonProject<IProject,IResource,IFolder,IFile> originalProject = module.getOriginalProject();
                                if (originalProject != null 
                                        && projectsToSearch.contains(originalProject.getIdeArtifact())) {
                                    continue;
                                }
                                
                                String archivePath = 
                                        module.getArtifact()
                                            .getAbsolutePath();
                                String sourceArchivePath = 
                                        toJavaString(module.getSourceArchivePath());
                                if (searchedArchives.add(archivePath) &&
                                    searchedArchives.add(sourceArchivePath)) {
                                    findInUnits(monitor, 
                                            module.getPhasedUnitsAsJavaList());
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
    }
    
    private void findInUnits(IProgressMonitor monitor, 
            List<? extends PhasedUnit> units) {
        for (final PhasedUnit pu: units) {
            if (isWithinSelection(pu)) {
                final Tree.CompilationUnit rootNode = 
                        getRootNode(pu);
                monitor.subTask("Searching source file " + 
                pu.getUnitFile().getPath());
                SearchVisitor sv = 
                        new SearchVisitor(
                                new PatternMatcher()) {
                    @Override
                    public void matchingNode(Node node) {
                        CeylonSearchMatch match = 
                                CeylonSearchMatch.create(
                                        node, rootNode, 
                                        pu.getUnitFile());
                        result.addMatch(match);
                        count++;
                    }
                    @Override
                    public void matchingRegion(Node node, 
                            IRegion region) {
                        CeylonSearchMatch match = 
                                CeylonSearchMatch.create(
                                        node, rootNode, 
                                        pu.getUnitFile());
                        match.setOffset(region.getOffset());
                        match.setLength(region.getLength());
                        result.addMatch(match);
                    }
                };
                rootNode.visit(sv);
                if (monitor.isCanceled()) {
                    throw new OperationCanceledException();
                }
            }
        }
    }

    private int estimateWork(IProgressMonitor monitor) {
        int work = 0;
        Set<String> searchedArchives = new HashSet<String>();
        for (IProject project: getProjectsToSearch()) {
            if (CeylonNature.isEnabled(project)) {
                if (sources) {
                    work++;
                }
                if (archives) {
                    Modules modules = 
                            getProjectTypeChecker(project)
                                    .getContext()
                                    .getModules();
                    for (Module m: modules.getListOfModules()) {
                        if (m instanceof BaseIdeModule) {
                            BaseIdeModule module = (BaseIdeModule) m;
                            if (module.getIsCeylonArchive() && 
                                    !module.getIsProjectModule() && 
                                    module.getArtifact()!=null) { 
                                String archivePath = 
                                        module.getArtifact()
                                            .getAbsolutePath();
                                String sourceArchivePath = 
                                        toJavaString(module.getSourceArchivePath());
                                if (searchedArchives.add(archivePath) &&
                                    searchedArchives.add(sourceArchivePath)) {
                                    work++;
                                }
                            }
                        }
                    }
                }
            }
        }
        return work;
    }
    
    //TODO: copy/pasted from FindSearchQuery!
    Tree.CompilationUnit getRootNode(PhasedUnit pu) {
        for (IEditorPart editor: page.getDirtyEditors()) {
            if (editor instanceof CeylonEditor) {
                CeylonEditor ce = (CeylonEditor) editor;
                CeylonParseController cpc = 
                        ce.getParseController();
                if ( /*editor.isDirty() &&*/
                        pu.getUnit().equals(cpc.getLastCompilationUnit().getUnit()) ) {
                    return cpc.getLastCompilationUnit();
                }
            }
        }
        return pu.getCompilationUnit();
    }
    
    public boolean isWithinSelection(PhasedUnit pu) {
        if (filters.isFiltered(pu.getPackage())) {
            return false;
        }
        if (resources==null) {
            return true;
        }
        else {
            for (IResource r: resources) {
                VirtualFile unitFile = pu.getUnitFile();
                if (vfsJ2C().instanceOfIFileVirtualFile(unitFile)) {
                    IPath loc = 
                            vfsJ2C().getIFileVirtualFile(unitFile).getNativeResource().getLocation();
                    if (r.getLocation().isPrefixOf(loc)) {
                        return true;
                    }
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
        return true;
    }
}