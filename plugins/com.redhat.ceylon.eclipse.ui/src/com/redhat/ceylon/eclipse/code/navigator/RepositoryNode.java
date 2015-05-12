package com.redhat.ceylon.eclipse.code.navigator;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;

import com.redhat.ceylon.eclipse.core.builder.CeylonBuilder;
import com.redhat.ceylon.eclipse.core.model.JDTModule;
import com.redhat.ceylon.model.cmr.Repository;

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
        for (Repository r : CeylonBuilder.getProjectRepositoryManager(project).getRepositories()) {
            if (displayString.equals(r.getDisplayString())) {
                return r;
            }
        }
        return null;
    }
    
    public void addModule(JDTModule module) {
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