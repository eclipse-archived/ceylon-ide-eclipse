package com.redhat.ceylon.eclipse.code.modulesearch;

import static com.redhat.ceylon.cmr.ceylon.CeylonUtils.makeRepositoryManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.NavigableSet;

import com.redhat.ceylon.cmr.api.ModuleQuery;
import com.redhat.ceylon.cmr.api.ModuleQuery.Type;
import com.redhat.ceylon.cmr.api.ModuleSearchResult;
import com.redhat.ceylon.cmr.api.ModuleSearchResult.ModuleDetails;
import com.redhat.ceylon.cmr.api.ModuleVersionDetails;
import com.redhat.ceylon.cmr.api.ModuleVersionQuery;
import com.redhat.ceylon.cmr.api.ModuleVersionResult;
import com.redhat.ceylon.cmr.api.RepositoryManager;
import com.redhat.ceylon.eclipse.util.EclipseLogger;

public class ModuleSearchManager {
    
    private List<ModuleNode> modules;
    private String lastQuery;
    private ModuleSearchResult lastResult;
    private ModuleSearchViewPart moduleSearchViewPart;
    private RepositoryManager repositoryManager;
    
    public ModuleSearchManager(ModuleSearchViewPart moduleSearchViewPart) {
        this.moduleSearchViewPart = moduleSearchViewPart;
        this.repositoryManager = makeRepositoryManager(null, null, null, new EclipseLogger()); // TODO which repo manager ???
    }
    
    public void searchModules(final String query) {
        new ModuleSearchJobTemplate("Searching modules in repositories") {
            @Override
            protected void onRun() {
                lastQuery = query.trim();
                lastResult = repositoryManager.searchModules(new ModuleQuery(lastQuery, Type.JVM));
                modules = convertResult(lastResult.getResults());
            }
            @Override
            protected void onFinish() {
                moduleSearchViewPart.update(true);
            }
        }.schedule();
    }

    public void fetchNextModules() {
        new ModuleSearchJobTemplate("Searching modules in repositories") {
            @Override
            protected void onRun() {
                ModuleQuery moduleQuery = new ModuleQuery(lastQuery, Type.JVM);
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
    
    public void fetchDocumentation(final String moduleName, final String moduleVersion) {
        final ModuleVersionNode versionNode = getVersionNode(moduleName, moduleVersion);
        if (versionNode == null) {
            return;
        }
        
        new ModuleSearchJobTemplate("Loading module documentation") {
            @Override
            protected void onRun() {
                ModuleVersionQuery query = new ModuleVersionQuery(moduleName, moduleVersion, Type.JVM);
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
            return lastResult.getNextPagingInfo() != null; // TODO waiting on ceylon-module-resolver#43 ?
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

    private List<ModuleNode> convertResult(Collection<ModuleDetails> details) {
        List<ModuleNode> moduleNodes = new ArrayList<ModuleNode>(details.size());
        for (ModuleDetails detail : details) {
            Iterator<String> versionIterator = ((NavigableSet<String>) detail.getVersions()).descendingIterator(); // TODO waiting on ceylon-module-resolver#44
            List<ModuleVersionNode> versionNodes = new ArrayList<ModuleVersionNode>(detail.getVersions().size());
            ModuleNode moduleNode = new ModuleNode(detail.getName(), versionNodes);
            while (versionIterator.hasNext()) {
                String version = versionIterator.next();
                ModuleVersionNode versionNode = new ModuleVersionNode(moduleNode, version);
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