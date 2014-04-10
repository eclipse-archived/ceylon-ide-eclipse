package com.redhat.ceylon.eclipse.code.navigator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.jdt.core.IPackageFragmentRoot;

import com.redhat.ceylon.eclipse.core.builder.CeylonBuilder;
import com.redhat.ceylon.eclipse.core.model.JDTModule;

public class ExternalModuleNode extends ModuleNode {
    private RepositoryNode repositoryNode;
    private List<IPackageFragmentRoot> binaryArchives = new ArrayList<>();
    
    public ExternalModuleNode(RepositoryNode repositoryNode, String moduleSignature) {
        super(moduleSignature);
        this.repositoryNode = repositoryNode;
    }

    public List<IPackageFragmentRoot> getBinaryArchives() {
        return binaryArchives;
    }

    @Override
    protected JDTModule searchBySignature(String signature) {
        for (JDTModule module : CeylonBuilder.getProjectExternalModules(repositoryNode.project)) {
            if (module.getSignature().equals(signature)) {
                return module;
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
    
    
}