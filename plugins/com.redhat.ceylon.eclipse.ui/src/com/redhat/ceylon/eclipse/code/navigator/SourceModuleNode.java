package com.redhat.ceylon.eclipse.code.navigator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.jdt.core.IBuffer;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaModel;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IOpenable;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.WorkingCopyOwner;

import com.redhat.ceylon.compiler.typechecker.model.Module;
import com.redhat.ceylon.compiler.typechecker.model.Modules;
import com.redhat.ceylon.eclipse.core.builder.CeylonBuilder;
import com.redhat.ceylon.eclipse.core.model.JDTModule;

public class SourceModuleNode extends ModuleNode implements IPackageFragment {
    private IPackageFragmentRoot sourceFolder;
    private IPackageFragment mainPackageFragment;
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
        if (module.isDefaultModule()) {
            mainPackageFragment = sourceFolder.getPackageFragment("");
        } else {
            mainPackageFragment = sourceFolder.getPackageFragment(module.getNameAsString());
        }
        if (mainPackageFragment != null && module.equals(CeylonBuilder.getModule(mainPackageFragment))) {
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

	@Override
	public IJavaElement[] getChildren() throws JavaModelException {
	    return mainPackageFragment.getChildren();
	}

	@Override
	public boolean hasChildren() throws JavaModelException {
	    return mainPackageFragment.hasChildren();
	}

	@Override
	public boolean exists() {
	    return mainPackageFragment.exists();
	}

	@Override
	public IJavaElement getAncestor(int ancestorType) {
	    return mainPackageFragment.getAncestor(ancestorType);
	}

	@Override
    public String getAttachedJavadoc(IProgressMonitor monitor)
            throws JavaModelException {
	    return mainPackageFragment.getAttachedJavadoc(monitor);
	}

	@Override
	public IResource getCorrespondingResource() throws JavaModelException {
	    return mainPackageFragment.getCorrespondingResource();
	}

	@Override
	public int getElementType() {
	    return mainPackageFragment.getElementType();
	}

	@Override
	public String getHandleIdentifier() {
	    return mainPackageFragment.getHandleIdentifier();
	}

	@Override
	public IJavaModel getJavaModel() {
	    return mainPackageFragment.getJavaModel();
	}

	@Override
	public IJavaProject getJavaProject() {
	    return mainPackageFragment.getJavaProject();
	}

	@Override
	public IOpenable getOpenable() {
	    return mainPackageFragment.getOpenable();
	}

	@Override
	public IJavaElement getParent() {
	    return mainPackageFragment.getParent();
	}

	@Override
	public IPath getPath() {
	    return mainPackageFragment.getPath();
	}

	@Override
	public IJavaElement getPrimaryElement() {
	    return mainPackageFragment.getPrimaryElement();
	}

	@Override
	public IResource getResource() {
	    return mainPackageFragment.getResource();
	}

	@Override
	public ISchedulingRule getSchedulingRule() {
	    return mainPackageFragment.getSchedulingRule();
	}

	@Override
	public IResource getUnderlyingResource() throws JavaModelException {
	    return mainPackageFragment.getUnderlyingResource();
	}

	@Override
	public boolean isReadOnly() {
	    return mainPackageFragment.isReadOnly();
	}

	@Override
	public boolean isStructureKnown() throws JavaModelException {
	    return mainPackageFragment.isStructureKnown();
	}

	@Override
	public Object getAdapter(@SuppressWarnings("rawtypes") Class adapter) {
	    return mainPackageFragment.getAdapter(adapter);
	}

	@Override
	public void close() throws JavaModelException {
	    mainPackageFragment.close();
	}

	@Override
	public String findRecommendedLineSeparator() throws JavaModelException {
	    return mainPackageFragment.findRecommendedLineSeparator();
	}

	@Override
	public IBuffer getBuffer() throws JavaModelException {
	    return mainPackageFragment.getBuffer();
	}

	@Override
	public boolean hasUnsavedChanges() throws JavaModelException {
	    return mainPackageFragment.hasUnsavedChanges();
	}

	@Override
	public boolean isConsistent() throws JavaModelException {
	    return mainPackageFragment.isConsistent();
	}

	@Override
	public boolean isOpen() {
	    return mainPackageFragment.isOpen();
	}

	@Override
    public void makeConsistent(IProgressMonitor progress)
            throws JavaModelException {
	    mainPackageFragment.makeConsistent(progress);
	}

	@Override
	public void open(IProgressMonitor progress) throws JavaModelException {
	    mainPackageFragment.open(progress);
	}

	@Override
    public void save(IProgressMonitor progress, boolean force)
            throws JavaModelException {
	    mainPackageFragment.save(progress, force);        
	}

	@Override
    public void copy(IJavaElement container, IJavaElement sibling,
            String rename, boolean replace, IProgressMonitor monitor)
            throws JavaModelException {
			    mainPackageFragment.copy(container, sibling, rename, replace, monitor);
			}

	@Override
    public void delete(boolean force, IProgressMonitor monitor)
            throws JavaModelException {
	    mainPackageFragment.delete(force, monitor);
	}

	@Override
    public void move(IJavaElement container, IJavaElement sibling,
            String rename, boolean replace, IProgressMonitor monitor)
            throws JavaModelException {
			    mainPackageFragment.move(container, sibling, rename, replace, monitor);
			}

	@Override
	public void rename(String name, boolean replace, IProgressMonitor monitor)
			throws JavaModelException {
			    mainPackageFragment.rename(name, replace, monitor);
			}

	@Override
	public boolean containsJavaResources() throws JavaModelException {
	    return mainPackageFragment.containsJavaResources();
	}

	@Override
    public ICompilationUnit createCompilationUnit(String name, String contents,
            boolean force, IProgressMonitor monitor) throws JavaModelException {
			    return mainPackageFragment.createCompilationUnit(name, contents, force, monitor);
			}

	@Override
	public IClassFile getClassFile(String name) {
	    return mainPackageFragment.getClassFile(name);
	}

	@Override
	public IClassFile[] getClassFiles() throws JavaModelException {
	    return mainPackageFragment.getClassFiles();
	}

	@Override
	public ICompilationUnit getCompilationUnit(String name) {
	    return mainPackageFragment.getCompilationUnit(name);
	}

	@Override
	public ICompilationUnit[] getCompilationUnits() throws JavaModelException {
	    return mainPackageFragment.getCompilationUnits();
	}

	@Override
    public ICompilationUnit[] getCompilationUnits(WorkingCopyOwner owner)
            throws JavaModelException {
	    return mainPackageFragment.getCompilationUnits(owner);
	}

	@Override
	public String getElementName() {
	    return mainPackageFragment.getElementName();
	}

	@Override
	public int getKind() throws JavaModelException {
	    return mainPackageFragment.getKind();
	}

	@Override
	public Object[] getNonJavaResources() throws JavaModelException {
	    return mainPackageFragment.getNonJavaResources();
	}

	@Override
	public boolean hasSubpackages() throws JavaModelException {
	    return mainPackageFragment.hasSubpackages();
	}

	@Override
	public boolean isDefaultPackage() {
	    return mainPackageFragment.isDefaultPackage();
	}
}