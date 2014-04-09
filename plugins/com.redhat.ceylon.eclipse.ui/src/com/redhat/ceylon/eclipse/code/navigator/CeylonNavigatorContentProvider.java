package com.redhat.ceylon.eclipse.code.navigator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.core.PackageFragmentRoot;
import org.eclipse.jdt.internal.core.util.WeakHashSet;
import org.eclipse.jdt.internal.ui.packageview.ClassPathContainer;
import org.eclipse.jdt.internal.ui.packageview.ClassPathContainer.RequiredProjectWrapper;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.navigator.CommonViewer;
import org.eclipse.ui.navigator.ICommonContentExtensionSite;
import org.eclipse.ui.navigator.IPipelinedTreeContentProvider2;
import org.eclipse.ui.navigator.PipelinedShapeModification;
import org.eclipse.ui.navigator.PipelinedViewerUpdate;
import org.eclipse.ui.progress.UIJob;

import com.redhat.ceylon.cmr.api.RepositoryManager;
import com.redhat.ceylon.cmr.impl.NodeUtils;
import com.redhat.ceylon.compiler.typechecker.model.Module;
import com.redhat.ceylon.compiler.typechecker.model.Package;
import com.redhat.ceylon.eclipse.core.builder.CeylonBuilder;
import com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.RootFolderType;
import com.redhat.ceylon.eclipse.core.model.ICeylonModelListener;
import com.redhat.ceylon.eclipse.core.model.JDTModule;

public class CeylonNavigatorContentProvider implements
        IPipelinedTreeContentProvider2, ICeylonModelListener {
    
    @SuppressWarnings("unchecked")
    @Override
    public void getPipelinedChildren(Object aParent, Set theCurrentChildren) {
        if (aParent instanceof IJavaProject) {
            aParent = ((IJavaProject) aParent).getProject();
        }

        if (aParent instanceof IProject) {
            IProject project = (IProject) aParent;

            Map<String, RepositoryNode> repositories = getProjectRepositoryNodes(project);

            List<Object> toRemove = new ArrayList<>();
            for (Object child : theCurrentChildren) {
                if (child instanceof ClassPathContainer) {
                    toRemove.add(child);
                    ClassPathContainer cpContainer = (ClassPathContainer) child;
                    for (IAdaptable entry : cpContainer.getChildren()) {
                        if (entry instanceof IPackageFragmentRoot) {
                            IPackageFragmentRoot pfr = (IPackageFragmentRoot) entry;
                            for (RepositoryNode rn : repositories.values()) {
                                for (ExternalModuleNode emn : rn.getModules()) {
                                    if (emn.getModule()
                                            .getPackageFragmentRoots()
                                            .contains(pfr)) {
                                        emn.getBinaryArchives().add(pfr);
                                    }
                                }
                            }
                        }
                        if (entry instanceof RequiredProjectWrapper) {
                            System.out.print("");
                        }
                    }
                }
            }

            theCurrentChildren.removeAll(toRemove);
            for (RepositoryNode repoNode : repositories.values()) {
                if (hasChildren(repoNode)) {
                    theCurrentChildren.add(repoNode);
                }
            }
        }
        
        if (aParent instanceof IPackageFragmentRoot) {
            IPackageFragmentRoot root = (IPackageFragmentRoot) aParent;
            if (CeylonBuilder.isSourceFolder(root)) {
                Map<String, SourceModuleNode> moduleNodes = getSourceDirectoryModules(root);
                
                List<Object> toRemove = new ArrayList<Object>();
                for (Object child : theCurrentChildren) {
                    if (child instanceof IPackageFragment) {
                        toRemove.add(child);
                        IFolder pkgFolder = (IFolder) ((IPackageFragment) child).getResource();
                        Package pkg = CeylonBuilder.getPackage(pkgFolder);
                        if (pkg != null) {
                            String signature = pkg.getModule().getSignature();
                            SourceModuleNode moduleNode = moduleNodes.get(signature);
                            if (moduleNode != null) {
                                moduleNode.getPackageFragments().add((IPackageFragment) child);
                            }
                        }
                    } else {
                        if (child instanceof IFile) {
                            toRemove.add(child);
                        }                        
                    }
                }
                theCurrentChildren.removeAll(toRemove);
                for (SourceModuleNode moduleNode : moduleNodes.values()) {
                    if (hasChildren(moduleNode)) {
                        theCurrentChildren.add(moduleNode);
                    }
                }
            }
        }

        if (aParent instanceof IPackageFragment) {
            IPackageFragment pkgFragment = (IPackageFragment) aParent;
            IPackageFragmentRoot root = (IPackageFragmentRoot) pkgFragment.getAncestor(IJavaElement.PACKAGE_FRAGMENT_ROOT);
            if (root != null) {
                IFolder rootFolder = null;
                try {
                    rootFolder = (IFolder) root.getCorrespondingResource();
                } catch (JavaModelException e) {
                    e.printStackTrace();
                }
                if (rootFolder != null && RootFolderType.SOURCE.equals(CeylonBuilder.getRootFolderType(root))) {
                    if (pkgFragment.isDefaultPackage()) {
                        try {
                            for (IResource r : rootFolder.members()) {
                                if (r instanceof IFile && ! JavaCore.isJavaLikeFileName(r.getName())) {
                                    theCurrentChildren.add((IFile)r);
                                }
                            }
                        } catch (CoreException e) {
                            e.printStackTrace();
                        }
                    } else {
                        JDTModule fragmentModule = CeylonBuilder.getModule(pkgFragment);
                        if (fragmentModule != null) {
                            for (Iterator<Object> itr = theCurrentChildren.iterator(); itr.hasNext(); ) {
                                Object child = itr.next();
                                if (child instanceof IPackageFragment) {
                                    IPackageFragment childPkg = (IPackageFragment) child;
                                    if (! fragmentModule.equals(CeylonBuilder.getModule(childPkg))) {
                                        itr.remove();
                                    }                                    
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private synchronized Map<String, RepositoryNode> getProjectRepositoryNodes(IProject project) {
        RepositoryManager repoManager = CeylonBuilder.getProjectRepositoryManager(project);
        Map<String, RepositoryNode> repositories = new LinkedHashMap<>();
        for (String displayString : repoManager.getRepositoriesDisplayString()) {
            repositories.put(displayString, new RepositoryNode(project, displayString));
        }
        RepositoryNode unknownRepositoryNode = new RepositoryNode(project, NodeUtils.UNKNOWN_REPOSITORY);
        repositories.put(NodeUtils.UNKNOWN_REPOSITORY, unknownRepositoryNode);
        
        for (JDTModule externalModule : CeylonBuilder.getProjectExternalModules(project)) {
            String repoDisplayString = externalModule.getRepositoryDisplayString();
            if (repositories.containsKey(repoDisplayString)) {
                repositories.get(repoDisplayString).addModule(externalModule);
            } else {
                unknownRepositoryNode.addModule(externalModule);
            }
        }
        return repositories;
    }

    private synchronized Map<String, SourceModuleNode> getSourceDirectoryModules(IPackageFragmentRoot sourceRoot) {
        Map<String, SourceModuleNode> sourceDirectoryModules = new LinkedHashMap<>();

        for (Module m : CeylonBuilder.getProjectSourceModules(sourceRoot.getJavaProject().getProject())) {
            if (m instanceof JDTModule) {
                JDTModule module = (JDTModule) m;
                if (module.getPackageFragmentRoots().contains(sourceRoot)) {
                    String signature = module.getSignature();
                    SourceModuleNode sourceModuleNode = sourceDirectoryModules.get(signature);
                    if (sourceModuleNode == null) {
                        sourceModuleNode = new SourceModuleNode(sourceRoot, signature);
                        sourceDirectoryModules.put(signature, sourceModuleNode);
                    }
                }
            }
        }
        return sourceDirectoryModules;
    }
    
    @Override
    public void getPipelinedElements(Object anInput, Set theCurrentElements) {
        System.out.print("");
    }

    @Override
    public Object getPipelinedParent(Object anObject, Object aSuggestedParent) {
        if (anObject instanceof IPackageFragmentRoot) {
            IPackageFragmentRoot pfr = (IPackageFragmentRoot) anObject;
            if (aSuggestedParent instanceof ClassPathContainer) {
                IProject project = pfr.getJavaProject().getProject();
                Map<String, RepositoryNode> repositories = getProjectRepositoryNodes(project);
                for (RepositoryNode rn : repositories.values()) {
                    for (ExternalModuleNode emn : rn.getModules()) {
                        if (emn.getModule()
                                .getPackageFragmentRoots()
                                .contains(pfr)) {
                            return rn;
                        }
                    }
                }
                return null;
            }
        }

        if (anObject instanceof IPackageFragment) {
            IPackageFragment pkgFragment = (IPackageFragment) anObject;
            IPackageFragmentRoot root = (IPackageFragmentRoot) pkgFragment.getAncestor(IJavaElement.PACKAGE_FRAGMENT_ROOT);
            Map<String, SourceModuleNode> moduleNodes = getSourceDirectoryModules(root);
            if (CeylonBuilder.isSourceFolder(root)) {
                if (aSuggestedParent instanceof IPackageFragmentRoot) {
                    JDTModule module = CeylonBuilder.getModule(pkgFragment);
                    if (module != null) {
                        return moduleNodes.get(module.getSignature());
                    }
                }
                if (aSuggestedParent instanceof IPackageFragment) {
                    JDTModule module = CeylonBuilder.getModule(pkgFragment);
                    if (module != null) {
                        JDTModule parentModule = CeylonBuilder.getModule((IPackageFragment)aSuggestedParent);
                        if (! module.equals(parentModule)) {
                            String signature = module.getSignature();
                            return moduleNodes.get(signature);
                        }
                    }
                }
            }
        }
        

        if (anObject instanceof IFile && aSuggestedParent instanceof IPackageFragmentRoot) {
            IPackageFragmentRoot root = (IPackageFragmentRoot) aSuggestedParent;
            try {
                for (IJavaElement je : root.getChildren()) {
                    if (((IPackageFragment)je).isDefaultPackage()) {
                        return je;
                    }
                }
            } catch (JavaModelException e) {
                e.printStackTrace();
            }
        }
        
        return aSuggestedParent;
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.navigator.IPipelinedTreeContentProvider#interceptAdd(org.eclipse.ui.navigator.PipelinedShapeModification)
     */
    @Override
    public PipelinedShapeModification interceptAdd(
            PipelinedShapeModification aShapeModification) {
        Object aParent = aShapeModification.getParent();
                Set changedChildren = aShapeModification.getChildren();
                
                /*
                 * IProject - ClassPathContainer * => remove the modification and refresh project
                 */
                    
                if (aParent instanceof IProject) {
                    for (Object child : changedChildren) {
                        if (child instanceof ClassPathContainer) {
                            aShapeModification.getChildren().clear();
                            scheduleRefresh(aParent);
                            return aShapeModification;
                        }
                    }
                }
                
        /*
         *       ClassPathContainer - IPackageFragmentRoot * =>
         *           Calculate the parent module for each root. 
         *               - If only on parent module => add ExternalModuleNode - IPackageFragmentRoot (What happens if the module didn't exist before ??)
         *               - Else refresh on project
         * 
         */
                
                if (aParent instanceof ClassPathContainer) {
                    replaceParentOrScheduleRefresh(aShapeModification, aParent, changedChildren, 
                            ((ClassPathContainer)aParent).getJavaProject().getProject());
                    return aShapeModification;
                }
        
                /*        
                IPackageFragmentRoot - IPackageFragment * =>
                    Calculate the parent source module for each fragment. 
                        - If only on parent module => add sourceModule - IPackageFragment * (What happens if the module didn't exist before ??)
                        - Else refresh on the IPackageFragmentRoot
        
                IPackageFragmentRoot - IFile * => add defaultPackage - IFile (What happens if the default module wasn't displayed before ??)
                 */
                
                if (aParent instanceof IPackageFragmentRoot) {
                    IPackageFragmentRoot root = (IPackageFragmentRoot) aParent;
                    if (CeylonBuilder.isSourceFolder(root)) {
                        replaceParentOrScheduleRefresh(aShapeModification, aParent,
                                changedChildren, aParent);
                    }
        
                    return aShapeModification;
                }
        
                return aShapeModification;
    }

    private void replaceParentOrScheduleRefresh(
            PipelinedShapeModification shapeModification, Object parent,
            Set addedChildren, Object nodeToRefresh) {
        Object newParent = null;
        
        for (Object child : addedChildren) {
            Object currentParent = getPipelinedParent(child, parent);
            if (currentParent == null) {
                currentParent = getParent(child);
            }
            
            if (newParent == null) {
                newParent = currentParent;
            } else {
                if (! newParent.equals(currentParent)) {
                    // Several new parents
                    // Cancel the addition and refresh the project
                    newParent = null;
                    break;
                }                            
            }
        }
        if (newParent == null) {
            shapeModification.getChildren().clear();
            scheduleRefresh(nodeToRefresh);
        } else {
            shapeModification.setParent(newParent);
        }
    }

    private void scheduleRefresh(final Object aParent) {
        if (viewer != null) {
            UIJob refreshJob = new UIJob("Refresh Viewer") {
                
                @Override
                public IStatus runInUIThread(IProgressMonitor monitor) {
                    viewer.refresh(aParent);
                    return Status.OK_STATUS;
                }
            };
            refreshJob.setSystem(true);
            refreshJob.schedule();
        }
    }

    @Override
    public PipelinedShapeModification interceptRemove(
            PipelinedShapeModification aRemoveModification) {        
        return aRemoveModification;
    }

    @Override
    public boolean interceptRefresh(
            PipelinedViewerUpdate aRefreshSynchronization) {
        ClassPathContainer aClassPathContainer = null;
        for (Object target : aRefreshSynchronization.getRefreshTargets()) {
            if (target instanceof ClassPathContainer) {
                aClassPathContainer = (ClassPathContainer)target;
                break;
            }
        }
        if (aClassPathContainer != null) {
            aRefreshSynchronization.getRefreshTargets().clear();            
            aRefreshSynchronization.getRefreshTargets().addAll(getProjectRepositoryNodes(aClassPathContainer.getJavaProject().getProject()).values());
            return true;
        }
        return false;
    }

    @Override
    public boolean interceptUpdate(PipelinedViewerUpdate anUpdateSynchronization) {
        return false;
    }

    @Override
    public void init(ICommonContentExtensionSite aConfig) {
        CeylonBuilder.addModelListener(this);
    }
    
    @Override
    public Object[] getElements(Object inputElement) {
        System.out.print("");
        return new Object[0];
    }

    @Override
    public Object[] getChildren(Object parentElement) {
        if (parentElement instanceof RepositoryNode) {
            return ((RepositoryNode)parentElement).getModules().toArray();
        }
        if (parentElement instanceof ExternalModuleNode) {
            return ((ExternalModuleNode)parentElement).getBinaryArchives().toArray();
        }
        if (parentElement instanceof SourceModuleNode) {
            return ((SourceModuleNode)parentElement).getPackageFragments().toArray();
        }
        return new Object[0];
    }

    @Override
    public Object getParent(Object element) {
        if (element instanceof RepositoryNode) {
            return ((RepositoryNode)element).project;
        }
        if (element instanceof ExternalModuleNode) {
            return ((ExternalModuleNode)element).getRepositoryNode();
        }
        if (element instanceof SourceModuleNode) {
            return ((SourceModuleNode)element).getSourceFolder();
        }
        return null;
    }

    @Override
    public boolean hasChildren(Object element) {
        if (element instanceof RepositoryNode) {
            return ! ((RepositoryNode)element).getModules().isEmpty();
        }
        if (element instanceof ExternalModuleNode) {
            return ! ((ExternalModuleNode)element).getBinaryArchives().isEmpty();
        }
        if (element instanceof SourceModuleNode) {
            SourceModuleNode sourceModuleNode = (SourceModuleNode) element;
            if (sourceModuleNode.getModule().isDefaultModule()) {
                for (IPackageFragment pkgFragment : sourceModuleNode.getPackageFragments()) {
                    if (defaultModuleHasFiles(pkgFragment)) {
                        return true;
                    }
                }
                return false;
            }
            return sourceModuleNode.getPackageFragments().size() > 0;
        }
        return false;
    }
    
    private boolean defaultModuleHasFiles(IPackageFragment pkgFragment) {
        IFolder folder;
        try {
            folder = (IFolder) pkgFragment.getCorrespondingResource();
            if (folder != null) {
                Package p = CeylonBuilder.getPackage(folder);
                if (p != null) {
                    if (! p.getModule().isDefault()) {
                        return false;
                    }
                    for (IResource childResource : folder.members()) {
                        if (childResource instanceof IFile) {
                            return true;
                        }
                    }                    
                    for (IJavaElement child : pkgFragment.getChildren()) {
                        if (child instanceof IPackageFragment) {
                            if (defaultModuleHasFiles((IPackageFragment)child)) {
                                return true;
                            }
                        }
                    }
                    return false;
                }
            }
        } catch (JavaModelException e1) {
            e1.printStackTrace();
        } catch (CoreException e) {
            e.printStackTrace();
        }
        return true;
    }
    
    @Override
    public void dispose() {
        CeylonBuilder.removeModelListener(this);
    }

    private StructuredViewer viewer = null;
    @Override
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        this.viewer = (StructuredViewer) viewer;
    }

    @Override
    public void restoreState(IMemento aMemento) {
        // TODO Auto-generated method stub

    }

    @Override
    public void saveState(IMemento aMemento) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean hasPipelinedChildren(Object anInput,
            boolean currentHasChildren) {
        if (anInput instanceof IPackageFragment) {
            IPackageFragment pkgFragment = (IPackageFragment) anInput;
            IPackageFragmentRoot root = (IPackageFragmentRoot) pkgFragment.getAncestor(IJavaElement.PACKAGE_FRAGMENT_ROOT);
            if (pkgFragment.isDefaultPackage() && root != null) {
                IFolder rootFolder = null;
                try {
                    rootFolder = (IFolder) root.getCorrespondingResource();
                } catch (JavaModelException e) {
                    e.printStackTrace();
                }
                
                if (rootFolder != null && CeylonBuilder.isSourceFolder(root)) {
                    try {
                        for (IResource r : rootFolder.members()) {
                            if (r instanceof IFile) {
                                return true;
                            }
                        }
                    } catch (JavaModelException e) {
                        e.printStackTrace();
                    } catch (CoreException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return currentHasChildren;
    }

    @Override
    public void modelParsed(IProject project) {
        if (project != null) {
            try {
                for (IPackageFragmentRoot pfr : JavaCore.create(project).getAllPackageFragmentRoots()) {
                    if (CeylonBuilder.isSourceFolder(pfr)) {
                        scheduleRefresh(pfr);
                    }
                }
            } catch (JavaModelException e) {
                e.printStackTrace();
            }
        }
    }

}
