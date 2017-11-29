/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.code.navigator;

import static org.eclipse.ceylon.ide.eclipse.core.external.ExternalSourceArchiveManager.getExternalSourceArchiveManager;
import static org.eclipse.ceylon.ide.eclipse.util.InteropUtils.toJavaString;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.internal.resources.Resource;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IPackageFragmentRoot;

import org.eclipse.ceylon.ide.eclipse.core.builder.CeylonBuilder;
import org.eclipse.ceylon.ide.eclipse.core.external.CeylonArchiveFileStore;
import org.eclipse.ceylon.ide.common.model.BaseIdeModule;

public class ExternalModuleNode implements ModuleNode {
    private RepositoryNode repositoryNode;
    private List<IPackageFragmentRoot> binaryArchives = new ArrayList<>();
    protected String moduleSignature;
    
    public ExternalModuleNode(RepositoryNode repositoryNode, String moduleSignature) {
        this.moduleSignature = moduleSignature;
        this.repositoryNode = repositoryNode;
    }

    public List<IPackageFragmentRoot> getBinaryArchives() {
        return binaryArchives;
    }

    public CeylonArchiveFileStore getSourceArchive() {
        BaseIdeModule module = getModule();
        if (module != null 
                && module.getIsCeylonArchive()) {
            String sourcePathString = toJavaString(module.getSourceArchivePath());
            if (sourcePathString != null) {
                IFolder sourceArchive = getExternalSourceArchiveManager().getSourceArchive(Path.fromOSString(sourcePathString));
                if (sourceArchive != null && sourceArchive.exists()) {
                    return ((CeylonArchiveFileStore) ((Resource)sourceArchive).getStore()); // TODO : URGENT : CCE when opening the module in the navigator 
                    // after the elements are removed from the .projects file and no manual rebuild has been done !!!
                    // After : test the breakpoints.
                }
            }
        }
        return null;
    }

    public RepositoryNode getRepositoryNode() {
        return repositoryNode;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime
                * result
                + ((moduleSignature == null) ? 0 : moduleSignature
                        .hashCode());
        result = prime
                * result
                + ((repositoryNode == null) ? 0 : repositoryNode.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ExternalModuleNode other = (ExternalModuleNode) obj;
        if (moduleSignature == null) {
            if (other.moduleSignature != null)
                return false;
        } else if (!moduleSignature.equals(other.moduleSignature))
            return false;
        if (repositoryNode == null) {
            if (other.repositoryNode != null)
                return false;
        } else if (!repositoryNode.equals(other.repositoryNode))
            return false;
        return true;
    }

    /*
     * Since this method retrieves the JDTModule lazily, the result might be
     * null if a Ceylon build is being processed.
     * 
     */
    @Override
    public BaseIdeModule getModule() {
        for (BaseIdeModule module : CeylonBuilder.getProjectExternalModules(repositoryNode.project)) {
            if (module.getSignature().equals(moduleSignature)) {
                return module;
            }
        }
        return null;
    }

    @Override
    public String getSignature() {
        return moduleSignature;
    }
}