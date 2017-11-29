/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.code.modulesearch;

import static org.eclipse.ceylon.cmr.ceylon.CeylonUtils.repoManager;
import static org.eclipse.ceylon.ide.eclipse.java2ceylon.Java2CeylonProxies.modelJ2C;
import static org.eclipse.ceylon.ide.eclipse.util.ModuleQueries.getModuleQuery;
import static org.eclipse.ceylon.ide.eclipse.util.ModuleQueries.getModuleVersionQuery;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.resources.IProject;

import org.eclipse.ceylon.cmr.api.ArtifactContext;
import org.eclipse.ceylon.cmr.api.ModuleQuery;
import org.eclipse.ceylon.cmr.api.ModuleSearchResult;
import org.eclipse.ceylon.cmr.api.ModuleSearchResult.ModuleDetails;
import org.eclipse.ceylon.cmr.api.ModuleVersionArtifact;
import org.eclipse.ceylon.cmr.api.ModuleVersionDetails;
import org.eclipse.ceylon.cmr.api.ModuleVersionQuery;
import org.eclipse.ceylon.cmr.api.ModuleVersionResult;
import org.eclipse.ceylon.cmr.api.RepositoryManager;
import org.eclipse.ceylon.common.Backend;
import org.eclipse.ceylon.common.Backends;
import org.eclipse.ceylon.common.Versions;
import org.eclipse.ceylon.ide.eclipse.util.EclipseLogger;
import org.eclipse.ceylon.ide.common.model.BaseCeylonProject;
import org.eclipse.ceylon.ide.common.modulesearch.ModuleNode;
import org.eclipse.ceylon.ide.common.modulesearch.ModuleVersionNode;

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
        query.setJvmBinaryMajor(Versions.JVM_BINARY_MAJOR_VERSION);
        query.setJvmBinaryMinor(Versions.JVM_BINARY_MINOR_VERSION);
        query.setJsBinaryMajor(Versions.JS_BINARY_MAJOR_VERSION);
        query.setJsBinaryMinor(Versions.JS_BINARY_MINOR_VERSION);
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
                query.setJvmBinaryMajor(Versions.JVM_BINARY_MAJOR_VERSION);
                query.setJvmBinaryMinor(Versions.JVM_BINARY_MINOR_VERSION);
                query.setJsBinaryMajor(Versions.JS_BINARY_MAJOR_VERSION);
                query.setJsBinaryMinor(Versions.JS_BINARY_MINOR_VERSION);
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
        BaseCeylonProject baseCeylonProject = modelJ2C().ceylonModel().getProject(selectedProject);

        if (baseCeylonProject != null) {
            repositoryManager = baseCeylonProject.getRepositoryManager();
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
                boolean forJava = false, forJs = false;
                for (ModuleVersionArtifact moduleVersionArtifact : version.getArtifactTypes()) {
                    String suffix = moduleVersionArtifact.getSuffix().toLowerCase();
                    if (suffix.equals(ArtifactContext.CAR) || 
                        suffix.equals(ArtifactContext.JAR)) {
                        forJava = true;
                    }
                    if (suffix.equals(ArtifactContext.JS)) {
                        forJs = true;
                    }
                }
                if (forJava && !forJs) {
                    versionNode.setNativeBackend(Backends.fromAnnotation(Backend.Java.nativeAnnotation));
                }
                if (!forJava && forJs) {
                    versionNode.setNativeBackend(Backends.fromAnnotation(Backend.JavaScript.nativeAnnotation));
                }
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
