package com.redhat.ceylon.eclipse.code.navigator;

import static com.redhat.ceylon.model.typechecker.model.ModelUtil.formatPath;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.internal.core.PackageFragment;
import org.eclipse.jdt.internal.core.PackageFragmentRoot;

import com.redhat.ceylon.model.typechecker.model.Module;
import com.redhat.ceylon.model.typechecker.model.Modules;
import com.redhat.ceylon.eclipse.core.builder.CeylonBuilder;
import com.redhat.ceylon.ide.common.model.BaseIdeModule;

public class SourceModuleNode extends PackageFragment implements ModuleNode {
    private IPackageFragmentRoot sourceFolder;
    private IPackageFragment mainPackageFragment;
    private Set<IPackageFragment> packageFragments = new LinkedHashSet<>();
    private List<IFile> resourceChildren = new ArrayList<>();
    protected String moduleSignature;
        
    public List<IFile> getResourceChildren() {
        return resourceChildren;
    }

    public IPackageFragmentRoot getSourceFolder() {
        return sourceFolder;
    }

    public static SourceModuleNode createSourceModuleNode(IPackageFragmentRoot sourceFolder, String moduleSignature) {
        BaseIdeModule module = moduleFromSignatureAndProject(moduleSignature, sourceFolder.getJavaProject().getProject());
        String[] packageName;
        if (module.getIsDefaultModule()) {
            packageName = new String[0];
        } else {
            packageName = module.getName().toArray(new String[0]);
        }
        return new SourceModuleNode(sourceFolder, moduleSignature, packageName);
    }
    
    private SourceModuleNode(IPackageFragmentRoot sourceFolder, String moduleSignature, String[] packageName) {
        super((PackageFragmentRoot)sourceFolder, packageName);
        this.moduleSignature = moduleSignature;
        this.sourceFolder = sourceFolder;
        mainPackageFragment = sourceFolder.getPackageFragment(formatPath(Arrays.asList(packageName)));
    }
    
    public IProject getProject() {
        return sourceFolder.getJavaProject().getProject();
    }

    public IPackageFragment getMainPackageFragment() {
        return mainPackageFragment;
    }

    public Collection<IPackageFragment> getPackageFragments() {
        return packageFragments;
    }
    

    
    private static BaseIdeModule moduleFromSignatureAndProject(String signature, IProject project) {
        Modules modules = CeylonBuilder.getProjectModules(project);
        if (modules != null) {
            for (Module module : modules.getListOfModules()) {
                if (! (module instanceof BaseIdeModule)) {
                    continue;
                }
                BaseIdeModule jdtModule = (BaseIdeModule) module;
                if (jdtModule.getIsProjectModule() || jdtModule.getIsDefaultModule()) {
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

    @Override
    public BaseIdeModule getModule() {
        return moduleFromSignatureAndProject(moduleSignature, getProject());
    }

    @Override
    public String getSignature() {
        return moduleSignature;
    }
}