package com.redhat.ceylon.eclipse.code.search;

import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getProjectTypeChecker;
import static java.util.regex.Pattern.CASE_INSENSITIVE;
import static java.util.regex.Pattern.compile;
import static org.eclipse.core.resources.ResourcesPlugin.getWorkspace;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
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
import com.redhat.ceylon.compiler.typechecker.model.Module;
import com.redhat.ceylon.compiler.typechecker.model.Modules;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.code.parse.CeylonParseController;
import com.redhat.ceylon.eclipse.core.builder.CeylonBuilder;
import com.redhat.ceylon.eclipse.core.builder.CeylonNature;
import com.redhat.ceylon.eclipse.core.model.JDTModule;
import com.redhat.ceylon.eclipse.core.vfs.IFileVirtualFile;
import com.redhat.ceylon.eclipse.util.EditorUtil;
import com.redhat.ceylon.eclipse.util.SearchVisitor;

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
    private final boolean archives;
    private IWorkbenchPage page;

    CeylonSearchQuery(String string, String[] projects, IResource[] resources,
            boolean includeReferences, boolean includeDeclarations,
            boolean caseSensitive, boolean regex, boolean archives) {
        this.string = string;
        this.projects = projects;
        this.caseSensitive = caseSensitive;
        this.includeDeclarations = includeDeclarations;
        this.includeReferences = includeReferences;
        this.regex = regex;
        this.resources = resources;
        this.archives = archives;
        this.page = EditorUtil.getActivePage();
    }
    
    private Collection<IProject> getProjectsToSearch() {
        if (projects==null) {
            return CeylonBuilder.getProjects();
        }
        List<IProject> result = new ArrayList<IProject>();
        for (String name: projects) {
            IProject project = getWorkspace().getRoot().getProject(name);
//            if (CeylonNature.isEnabled(project)) {
                result.add(project);
//            }
        }
        return result;
    }
    
    @Override
    public IStatus run(IProgressMonitor monitor) throws OperationCanceledException {
        monitor.beginTask("Searching in Ceylon source", 
                estimateWork(monitor));
        if (monitor.isCanceled()) {
            return Status.CANCEL_STATUS;
        }
        search(monitor);
        monitor.done();
        return Status.OK_STATUS;
    }

    private void search(IProgressMonitor monitor) {
        Set<String> searchedArchives = new HashSet<String>();
        for (IProject project: getProjectsToSearch()) {
            if (CeylonNature.isEnabled(project)) {
                TypeChecker typeChecker = getProjectTypeChecker(project);
                findInUnits(monitor, typeChecker.getPhasedUnits().getPhasedUnits());
                monitor.worked(1);
                if (monitor.isCanceled()) {
                    throw new OperationCanceledException();
                }
                if (archives) {
                    Modules modules = getProjectTypeChecker(project).getContext().getModules();
                    for (Module m: modules.getListOfModules()) {
                        if (m instanceof JDTModule) {
                            JDTModule module = (JDTModule) m;
                            if (module.isCeylonArchive() && 
                                    !module.isProjectModule() && 
                                    module.getArtifact()!=null) { 
                                String archivePath = module.getArtifact().getAbsolutePath();
                                if (searchedArchives.add(archivePath) &&
                                        searchedArchives.add(module.getSourceArchivePath())) {
                                    findInUnits(monitor, module.getPhasedUnits());
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
    
    private void findInUnits(IProgressMonitor monitor, List<? extends PhasedUnit> units) {
        for (final PhasedUnit pu: units) {
            if (isWithinSelection(pu)) {
                final Tree.CompilationUnit cu = getRootNode(pu);
                monitor.subTask("Searching source file " + pu.getUnitFile().getPath());
                SearchVisitor sv = new SearchVisitor(new PatternMatcher()) {
                    @Override
                    public void matchingNode(Node node) {
                        CeylonSearchMatch match = 
                                CeylonSearchMatch.create(node, cu, pu.getUnitFile());
                        result.addMatch(match);
                        count++;
                    }
                    @Override
                    public void matchingRegion(Node node, IRegion region) {
                        CeylonSearchMatch match = 
                                CeylonSearchMatch.create(node, cu, pu.getUnitFile());
                        match.setOffset(region.getOffset());
                        match.setLength(region.getLength());
                        result.addMatch(match);
                    }
                };
                cu.visit(sv);
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
                work++;
                if (archives) {
                    Modules modules = getProjectTypeChecker(project).getContext().getModules();
                    for (Module m: modules.getListOfModules()) {
                        if (m instanceof JDTModule) {
                            JDTModule module = (JDTModule) m;
                            if (module.isCeylonArchive() && 
                                    !module.isProjectModule() && 
                                    module.getArtifact()!=null) { 
                                String archivePath = module.getArtifact().getAbsolutePath();
                                if (searchedArchives.add(archivePath) &&
                                        searchedArchives.add(module.getSourceArchivePath())) {
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
                CeylonParseController cpc = ((CeylonEditor)editor).getParseController();
                if ( /*editor.isDirty() &&*/
                        pu.getUnit().equals(cpc.getRootNode().getUnit()) ) {
                    return cpc.getRootNode();
                }
            }
        }
        return pu.getCompilationUnit();
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