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

import static org.eclipse.ceylon.ide.eclipse.java2ceylon.Java2CeylonProxies.modelJ2C;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;

import org.eclipse.ceylon.ide.common.model.BaseCeylonProject;
import org.eclipse.ceylon.ide.common.model.BaseIdeModule;
import org.eclipse.ceylon.model.cmr.Repository;

public class RepositoryNode {
    private String displayString;
    private List<ExternalModuleNode> modules = new ArrayList<>();
    final IProject project;
    
    public RepositoryNode(IProject project, String displayString) {
        this.displayString = displayString;
        this.project = project;
    }
    
    public List<ExternalModuleNode> getModules() {
        return modules;
    }
    
    public Repository getRepository() {
        BaseCeylonProject ceylonProject = modelJ2C().ceylonModel().getProject(project);
        if (ceylonProject != null) {
            for (Repository r : ceylonProject.getRepositoryManager().getRepositories()) {
                if (displayString.equals(r.getDisplayString())) {
                    return r;
                }
            }
        }
        return null;
    }
    
    public void addModule(BaseIdeModule module) {
        modules.add(new ExternalModuleNode(this, module.getSignature()));
    }

    public String getDisplayString() {
        return displayString;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((displayString == null) ? 0 : displayString.hashCode());
        result = prime * result
                + ((project == null) ? 0 : project.hashCode());
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
        RepositoryNode other = (RepositoryNode) obj;
        if (displayString == null) {
            if (other.displayString != null)
                return false;
        } else if (!displayString.equals(other.displayString))
            return false;
        if (project == null) {
            if (other.project != null)
                return false;
        } else if (!project.equals(other.project))
            return false;
        return true;
    }
    
    
}