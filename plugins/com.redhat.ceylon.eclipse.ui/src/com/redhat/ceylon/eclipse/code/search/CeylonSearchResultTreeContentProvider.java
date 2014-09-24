package com.redhat.ceylon.eclipse.code.search;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.search.ui.text.AbstractTextSearchResult;

import com.redhat.ceylon.compiler.typechecker.TypeChecker;
import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.io.VirtualFile;
import com.redhat.ceylon.compiler.typechecker.model.Module;
import com.redhat.ceylon.compiler.typechecker.model.Modules;
import com.redhat.ceylon.compiler.typechecker.model.Package;
import com.redhat.ceylon.compiler.typechecker.model.Unit;
import com.redhat.ceylon.eclipse.core.builder.CeylonBuilder;
import com.redhat.ceylon.eclipse.core.model.JDTModule;

class CeylonSearchResultTreeContentProvider implements
    CeylonStructuredContentProvider, ITreeContentProvider {
    
    static final int LEVEL_FILE=0;
    static final int LEVEL_PACKAGE=1;
    static final int LEVEL_MODULE=2;
    static final int LEVEL_FOLDER=3;
    static final int LEVEL_PROJECT=4;
    
    private final TreeViewer viewer;
    private CeylonSearchResult result;
    private CeylonSearchResultPage page;
    private Map<Object, Set<Object>> childrenMap;
    private int level;

    CeylonSearchResultTreeContentProvider(TreeViewer viewer, 
            CeylonSearchResultPage page) {
        this.viewer = viewer;
        this.page = page;
    }

    public Object[] getElements(Object inputElement) {
        Object[] children = getChildren(inputElement);
        int elementLimit = getElementLimit();
        if (elementLimit!=-1 && elementLimit<children.length) {
            Object[] limitedChildren= new Object[elementLimit];
            System.arraycopy(children, 0, limitedChildren, 0, elementLimit);
            return limitedChildren;
        }
        return children;
    }
    
    private int getElementLimit() {
        return page.getElementLimit();
    }

    public void dispose() {}
    
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        if (newInput instanceof AbstractTextSearchResult) {
            initialize((AbstractTextSearchResult) newInput);
        }
    }
    
    private synchronized void initialize(AbstractTextSearchResult result) {
        this.result = (CeylonSearchResult) result;
        childrenMap = new HashMap<Object, Set<Object>>();
        if (result!=null) {
            Object[] elements= result.getElements();
            for (int i= 0; i < elements.length; i++) {
                insert(elements[i],  false);
            }
        }
    }

    private void insert(Object child, boolean refreshViewer) {
        Object parent = getParent(child);
        while (parent!=null) {
            if (insertChild(parent, child)) {
                if (refreshViewer) viewer.add(parent, child);
            } 
            else {
                if (refreshViewer) viewer.refresh(parent);
                return;
            }
            child = parent;
            parent = getParent(child);
        }
        if (insertChild(result, child)) {
            if (refreshViewer) viewer.add(result, child);
        }
    }

    /**
     * returns true if the child already was a child of parent.
     * 
     * @param parent
     * @param child
     * @return Returns <code>trye</code> if the child was added
     */
    private boolean insertChild(Object parent, Object child) {
        Set<Object> children = childrenMap.get(parent);
        if (children==null) {
            children = new HashSet<Object>();
            childrenMap.put(parent, children);
        }
        return children.add(child);
    }

    private void remove(Object element, boolean refreshViewer) {
        // precondition here:  fResult.getMatchCount(child) <= 0
    
        if (hasChildren(element)) {
            if (refreshViewer) viewer.refresh(element);
        } 
        else {
            if (result.getMatchCount(element) == 0) {
                childrenMap.remove(element);
                Object parent= getParent(element);
                if (parent != null) {
                    removeFromSiblings(element, parent);
                    remove(parent, refreshViewer);
                } 
                else {
                    removeFromSiblings(element, result);
                    if (refreshViewer) viewer.refresh();
                }
            } 
            else {
                if (refreshViewer) {
                    viewer.refresh(element);
                }
            }
        }
    }

    private void removeFromSiblings(Object element, Object parent) {
        Set<Object> siblings= childrenMap.get(parent);
        if (siblings != null) {
            siblings.remove(element);
        }
    }

    public Object[] getChildren(Object parentElement) {
        Set<Object> children= childrenMap.get(parentElement);
        if (children == null) {
            return new Object[0];
        }
        else {
            return children.toArray();
        }
    }

    public boolean hasChildren(Object element) {
        return getChildren(element).length > 0;
    }

    public synchronized void elementsChanged(Object[] updatedElements) {
        for (int i= 0; i < updatedElements.length; i++) {
            if (result.getMatchCount(updatedElements[i]) > 0) {
                insert(updatedElements[i], true);
            }
            else {
                remove(updatedElements[i], true);
            }
        }
    }

    public Object getParent(Object child) {
        if (child instanceof IProject) {
            return null;
        }
        if (child instanceof IPackageFragmentRoot) {
            if (level==LEVEL_FOLDER) {
                return null;
            }
            IPackageFragmentRoot sourceFolder = (IPackageFragmentRoot) child;
            return sourceFolder.getJavaProject().getProject();
        }
        if (child instanceof WithSourceFolder) {
            Object element = ((WithSourceFolder) child).element;
            IPackageFragmentRoot sourceFolder = 
                    ((WithSourceFolder) child).sourceFolder;
            if (element instanceof Unit) {
                if (level==LEVEL_FILE) {
                    return null;
                }
                Package pack = ((Unit) element).getPackage();
                return new WithSourceFolder(pack, sourceFolder);
            }
            if (element instanceof Package) {
                if (level==LEVEL_PACKAGE) {
                    return null;
                }
                Module mod = ((Package) element).getModule();
                return new WithSourceFolder(mod, sourceFolder);
            }
            if (element instanceof Module) {
                if (level==LEVEL_MODULE) {
                    return null;
                }
                return sourceFolder==null ? 
                        ArchiveMatches.INSTANCE : sourceFolder;
            }
            if (element instanceof IFile) {
                IJavaElement packageFragment = 
                        JavaCore.create(((IFile) element).getParent());
                return new WithSourceFolder(packageFragment, sourceFolder);
            }
            return sourceFolder;
        }
        if (child instanceof IJavaElement) {
            IJavaElement javaElement = (IJavaElement) child;
            
            IFile file = (IFile) javaElement.getResource();
            if (file!=null) {
                IContainer parent = file.getParent();
                javaElement = JavaCore.create(parent);
                return new WithSourceFolder(file, 
                        getSourceFolder(javaElement));
            }
        }
        if (child instanceof CeylonElement) {
            CeylonElement ceylonElement = (CeylonElement) child;
            
            IPackageFragmentRoot sourceFolder = null;
            IFile file = ceylonElement.getFile();
            if (file!=null) {
                IContainer parent = file.getParent();
                IJavaElement javaElement = JavaCore.create(parent);
                sourceFolder = getSourceFolder(javaElement);
            }
            
            VirtualFile virtualFile = ceylonElement.getVirtualFile();
            Unit unit = getUnit(virtualFile);
            if (unit!=null) {
                return new WithSourceFolder(unit, sourceFolder);
            }
            else {
                return null;
            }
        }
        
        return null;
    }

    private static Unit getUnit(VirtualFile virtualFile) {
        for (TypeChecker tc: CeylonBuilder.getTypeCheckers()) {
            PhasedUnit phasedUnit = 
                    tc.getPhasedUnits().getPhasedUnit(virtualFile);
            if (phasedUnit!=null) {
                return phasedUnit.getUnit();
            }
            
            Modules modules = tc.getContext().getModules();
            for (Module m: modules.getListOfModules()) {
                if (m instanceof JDTModule) {
                    JDTModule module = (JDTModule) m;
                    if (module.isCeylonArchive()) {
                        phasedUnit = module.getPhasedUnit(virtualFile);
                        if (phasedUnit!=null) {
                            return phasedUnit.getUnit();
                        }
                    }
                }
            }
        }
        return null;
    }

    private IPackageFragmentRoot getSourceFolder(IJavaElement javaElement) {
        if (javaElement instanceof IPackageFragment) {
            while (javaElement instanceof IPackageFragment) {
                javaElement = javaElement.getParent();
            }
        }
        if (javaElement instanceof IPackageFragmentRoot) {
            return (IPackageFragmentRoot) javaElement;
        }
        return null;
    }
    
    @Override
    public void clear() {
        initialize(result);
        viewer.refresh();
    }
    
    @Override
    public void setLevel(int grouping) {
        this.level = grouping;
        initialize(result);
        viewer.refresh();
    }
    
}