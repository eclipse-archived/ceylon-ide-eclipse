package com.redhat.ceylon.eclipse.core.model;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jdt.core.JavaModelException;

import com.redhat.ceylon.compiler.loader.ModelResolutionException;
import com.redhat.ceylon.compiler.loader.ModelLoader.DeclarationType;
import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.Package;
import com.redhat.ceylon.compiler.typechecker.model.Unit;

public abstract class JavaUnit extends IdeUnit implements IJavaModelAware, IResourceAware {
    
    public JavaUnit(String fileName, String relativePath, String fullPath, Package pkg) {
        setFilename(fileName);
        setRelativePath(relativePath);
        setFullPath(fullPath);
        setPackage(pkg);
    }
    
    @Override
    public IFile getFileResource() {
        if (getTypeRoot() != null) {
            try {
                return (IFile) getTypeRoot().getCorrespondingResource();
            } catch (JavaModelException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public IProject getProjectResource() {
        if (getFileResource() != null) {
            return (IProject) getTypeRoot().getJavaProject().getProject();
        }
        return null;
    }

    @Override
    public IProject getProject() {
        return getProjectResource();
    }

    @Override
    public IFolder getRootFolderResource() {
        if (getFileResource() != null) {
            try {
                IPackageFragmentRoot root = (IPackageFragmentRoot) getTypeRoot().getAncestor(IJavaElement.PACKAGE_FRAGMENT_ROOT);
                if (root != null) {
                    return (IFolder) root.getCorrespondingResource();
                }
            } catch (JavaModelException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public void remove() {
        Package p = getPackage();
        p.removeUnit(this);
        assert (p.getModule() instanceof JDTModule);
        JDTModule module = (JDTModule) p.getModule();
        for (JDTModule moduleInReferencingProject : module.getModuleInReferencingProjects()) {
        	moduleInReferencingProject.removedOriginalUnit(getRelativePath());
        }
    }

    public void update() {
        remove();
        Package p = getPackage();
        assert p.getModule() instanceof JDTModule;
        JDTModule module = (JDTModule) p.getModule();
        JDTModelLoader modelLoader = module.getModelLoader();
        
        ITypeRoot typeRoot = getTypeRoot();
        IType primaryType = typeRoot.findPrimaryType();
        if (primaryType != null && modelLoader != null) {
            try {
                Declaration d = modelLoader.convertToDeclaration(module, 
                        primaryType.getFullyQualifiedName(), DeclarationType.TYPE);
                Unit newUnit = d.getUnit();
                assert newUnit instanceof JavaUnit;
                JavaUnit newJavaUnit = (JavaUnit) newUnit;
                newJavaUnit.getDependentsOf().addAll(getDependentsOf());
                for (JDTModule moduleInReferencingProject : module.getModuleInReferencingProjects()) {
                    moduleInReferencingProject.addedOriginalUnit(getRelativePath());
                }
                
            } catch(ModelResolutionException e) {
                e.printStackTrace();
            }
        }
    }
}
