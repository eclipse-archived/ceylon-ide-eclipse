package com.redhat.ceylon.eclipse.code.modulesearch;

import static com.redhat.ceylon.cmr.ceylon.CeylonUtils.repoManager;
import static com.redhat.ceylon.eclipse.util.ModuleQueries.getModuleQuery;
import static com.redhat.ceylon.eclipse.util.ModuleQueries.getModuleVersionQuery;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.resources.IProject;

import com.redhat.ceylon.cmr.api.ModuleQuery;
import com.redhat.ceylon.cmr.api.ModuleSearchResult;
import com.redhat.ceylon.cmr.api.ModuleSearchResult.ModuleDetails;
import com.redhat.ceylon.cmr.api.ModuleVersionDetails;
import com.redhat.ceylon.cmr.api.ModuleVersionQuery;
import com.redhat.ceylon.cmr.api.ModuleVersionResult;
import com.redhat.ceylon.cmr.api.RepositoryManager;
import com.redhat.ceylon.common.Versions;
import com.redhat.ceylon.eclipse.core.builder.CeylonBuilder;
import com.redhat.ceylon.eclipse.util.EclipseLogger;

public class ModuleSearchManager {
    
    private List<ModuleNode> modules;
    private String lastQuery;
    private ModuleSearchResult lastResult;
    private ModuleSearchViewPart moduleSearchViewPart;
    private RepositoryManager defaultRepositoryManager;
    
    public ModuleSearchManager(ModuleSearchViewPart moduleSearchViewPart) {
        this.moduleSearchViewPart = moduleSearchViewPart;
        this.defaultRepositoryManager = repoManager().logger(new EclipseLogger()).isJDKIncluded(true).buildManager();
    }
    
    public void searchModules(final String query) {
        final RepositoryManager repositoryManager = getRepositoryManager();
        final IProject project = moduleSearchViewPart.getSelectedProject();
        new ModuleSearchJobTemplate("Searching modules in repositories") {
            @Override
            protected void onRun() {
                lastQuery = query.trim();
                lastResult = repositoryManager.searchModules(newModuleQuery(lastQuery, project));
                modules = convertResult(lastResult.getResults());
            }
            @Override
            protected void onFinish() {
                moduleSearchViewPart.update(true);
            }
        }.schedule();
    }

    public void fetchNextModules() {
        final RepositoryManager repositoryManager = getRepositoryManager();
        final IProject project = moduleSearchViewPart.getSelectedProject();
        new ModuleSearchJobTemplate("Searching modules in repositories") {
            @Override
            protected void onRun() {
                ModuleQuery moduleQuery = newModuleQuery(lastQuery, project);
                moduleQuery.setPagingInfo(lastResult.getNextPagingInfo());
                lastResult = repositoryManager.searchModules(moduleQuery);
                modules.addAll(convertResult(lastResult.getResults()));
            }
            @Override
            protected void onFinish() {
                moduleSearchViewPart.update(false);
            }
        }.schedule();
    }
    
    private ModuleQuery newModuleQuery(String search, IProject project) {
        ModuleQuery query = getModuleQuery(search, project);
        query.setBinaryMajor(Versions.JVM_BINARY_MAJOR_VERSION);
        query.setCount(20l);
        return query;
    }

    public void fetchDocumentation(final String moduleName, final String moduleVersion) {
        final ModuleVersionNode versionNode = getVersionNode(moduleName, moduleVersion);
        if (versionNode == null) {
            return;
        }
        
        final RepositoryManager repositoryManager = getRepositoryManager();
        final IProject project = moduleSearchViewPart.getSelectedProject();
        new ModuleSearchJobTemplate("Loading module documentation") {
            @Override
            protected void onRun() {
                ModuleVersionQuery query = getModuleVersionQuery(moduleName, moduleVersion, project);
                query.setBinaryMajor(Versions.JVM_BINARY_MAJOR_VERSION);
                ModuleVersionResult result = repositoryManager.completeVersions(query);
                if (result != null) {
                    ModuleVersionDetails detail = result.getVersions().get(moduleVersion);
                    if (detail != null) {
                        versionNode.setFilled(true);
                        versionNode.setDoc(detail.getDoc());
                        versionNode.setLicense(detail.getLicense());
                        versionNode.setAuthors(detail.getAuthors());
                    }
                }
            }
            @Override
            protected void onFinish() {
                moduleSearchViewPart.updateDoc();
            }
        }.schedule();        
    }
    
    public RepositoryManager getRepositoryManager() {
        RepositoryManager repositoryManager = defaultRepositoryManager;

        IProject selectedProject = moduleSearchViewPart.getSelectedProject();
        if (selectedProject != null) {
            repositoryManager = CeylonBuilder.getProjectRepositoryManager(selectedProject);
        }

        return repositoryManager;
    }
    
    public String getLastQuery() {
        return lastQuery;
    }
    
    public List<ModuleNode> getModules() {
        return modules;
    }
    
    public ModuleVersionNode getVersionNode(String moduleName, String moduleVersion) {
        ModuleVersionNode result = null;
        if (modules != null) {
            for (ModuleNode moduleNode : modules) {
                if (moduleNode.getName().equals(moduleName)) {
                    for (ModuleVersionNode versionNode : moduleNode.getVersions()) {
                        if (versionNode.getVersion().equals(moduleVersion)) {
                            result = versionNode;
                            break;
                        }
                    }
                    break;
                }
            }
        }
        return result;
    }

    public boolean canFetchNext() {
        if (modules != null && !modules.isEmpty() && lastResult != null) {
            return lastResult.getHasMoreResults();
        }
        return false;
    }

    public void remove(List<?> selectedElements) {
        modules.removeAll(selectedElements);
        if( modules.isEmpty() ) {
            modules = null;
        }
    }

    public void clear() {
        modules = null;
    }

    public static List<ModuleNode> convertResult(Collection<ModuleDetails> details) {
        List<ModuleNode> moduleNodes = new ArrayList<ModuleNode>(details.size());
        for (ModuleDetails detail : details) {
            List<ModuleVersionNode> versionNodes = new ArrayList<ModuleVersionNode>(detail.getVersions().size());
            ModuleNode moduleNode = new ModuleNode(detail.getName(), versionNodes);
            for(ModuleVersionDetails version : detail.getVersions().descendingSet()){
                ModuleVersionNode versionNode = new ModuleVersionNode(moduleNode, version.getVersion());
                if (version.equals(detail.getLastVersion())) {
                    versionNode.setFilled(true);
                    versionNode.setDoc(detail.getDoc());
                    versionNode.setLicense(detail.getLicense());
                    versionNode.setAuthors(detail.getAuthors());
                }
                versionNodes.add(versionNode);
            }
            moduleNodes.add(moduleNode);
        }
        return moduleNodes;
    }

}
