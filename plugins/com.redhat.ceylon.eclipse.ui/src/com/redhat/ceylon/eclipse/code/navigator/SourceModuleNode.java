package com.redhat.ceylon.eclipse.code.navigator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;

import com.redhat.ceylon.compiler.typechecker.model.Module;
import com.redhat.ceylon.compiler.typechecker.model.Modules;
import com.redhat.ceylon.eclipse.core.builder.CeylonBuilder;
import com.redhat.ceylon.eclipse.core.model.JDTModule;

public class SourceModuleNode extends ModuleNode {
    private IPackageFragmentRoot sourceFolder;
    private Set<IPackageFragment> packageFragments = new LinkedHashSet<>();
    private List<IFile> resourceChildren = new ArrayList<>();
        
    public List<IFile> getResourceChildren() {
        return resourceChildren;
    }

    public IPackageFragmentRoot getSourceFolder() {
        return sourceFolder;
    }

    public SourceModuleNode(IPackageFragmentRoot sourceFolder, String moduleSignature) {
        super(moduleSignature);
        this.sourceFolder = sourceFolder;
        JDTModule module = getModule();
        IPackageFragment mainPackageFragment = null;
        if (module.isDefaultModule()) {
            mainPackageFragment = sourceFolder.getPackageFragment("");
        } else {
            mainPackageFragment = sourceFolder.getPackageFragment(module.getNameAsString());
        }
        if (mainPackageFragment != null) {
            packageFragments.add(mainPackageFragment);
        }        
    }
    
    public IProject getProject() {
        return sourceFolder.getJavaProject().getProject();
    }

    public Collection<IPackageFragment> getPackageFragments() {
        return packageFragments;
    }
    
    
    @Override
    protected JDTModule searchBySignature(String signature) {
        Modules modules = CeylonBuilder.getProjectModules(getProject());
        if (modules != null) {
            for (Module module : modules.getListOfModules()) {
                if (! (module instanceof JDTModule)) {
                    continue;
                }
                JDTModule jdtModule = (JDTModule) module;
                if (jdtModule.isProjectModule() || jdtModule.isDefaultModule()) {
                    if (jdtModule.getSignature().equals(signature)) {
                        return jdtModule;
                    }
                }
            }
        }
        return null;
    }

    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime
                * result
                + ((moduleSignature == null) ? 0 : moduleSignature
                        .hashCode());
        result = prime * result
                + ((sourceFolder == null) ? 0 : sourceFolder.hashCode());
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
        SourceModuleNode other = (SourceModuleNode) obj;
        if (moduleSignature == null) {
            if (other.moduleSignature != null)
                return false;
        } else if (!moduleSignature.equals(other.moduleSignature))
            return false;
        if (sourceFolder == null) {
            if (other.sourceFolder != null)
                return false;
        } else if (!sourceFolder.equals(other.sourceFolder))
            return false;
        return true;
    }
}