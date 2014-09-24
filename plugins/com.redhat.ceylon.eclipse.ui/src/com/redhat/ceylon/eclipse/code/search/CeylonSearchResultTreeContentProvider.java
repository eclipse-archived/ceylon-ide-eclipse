package com.redhat.ceylon.eclipse.code.search;

import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.asSourceModule;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getPackage;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getUnit;
import static org.eclipse.jdt.core.IJavaElement.PACKAGE_FRAGMENT_ROOT;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.search.ui.text.AbstractTextSearchResult;

import com.redhat.ceylon.compiler.typechecker.io.VirtualFile;
import com.redhat.ceylon.compiler.typechecker.model.Module;
import com.redhat.ceylon.compiler.typechecker.model.Package;
import com.redhat.ceylon.compiler.typechecker.model.Unit;
import com.redhat.ceylon.eclipse.core.model.IResourceAware;

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
            if (element instanceof IPackageFragment) {
                if (level==LEVEL_PACKAGE) {
                    return null;
                }
                //let's see if it belongs to a Ceylon module
                Module mod = asSourceModule((IPackageFragment) element);
                if (mod!=null) {
                    return new WithSourceFolder(mod, sourceFolder);
                }
                //otherwise, it doesn't belong to a module
                if (level==LEVEL_MODULE) {
                    return null;
                }
                return sourceFolder==null ? 
                        ArchiveMatches.INSTANCE : sourceFolder;
            }
            if (element instanceof IFile) {
                if (level==LEVEL_FILE) {
                    return null;
                }
                IContainer parent = ((IFile) element).getParent();
                if (parent instanceof IFolder) {
                    //let's see if the .java files it belongs to
                    //a Ceylon package
                    Package pack = getPackage((IFolder) parent);
                    if (pack!=null) {
                        return new WithSourceFolder(pack, sourceFolder);
                    }
                }
                return new WithSourceFolder(JavaCore.create(parent), 
                        sourceFolder);
            }
            return sourceFolder;
        }
        if (child instanceof IType ||
            child instanceof IMethod ||
            child instanceof IField) {
            IJavaElement javaElement = (IJavaElement) child;
            IFile file = (IFile) javaElement.getResource();
            if (file!=null) {
                IPackageFragmentRoot sourceFolder = (IPackageFragmentRoot) 
                        javaElement.getAncestor(PACKAGE_FRAGMENT_ROOT);
                //there is never a Unit for a .java file
                return new WithSourceFolder(file, sourceFolder);
            }
        }
        
        if (child instanceof CeylonElement) {
            CeylonElement ceylonElement = (CeylonElement) child;
            IFile file = ceylonElement.getFile();
            if (file!=null) {
                //workspace .ceylon file
                IJavaElement javaElement = JavaCore.create(file.getParent());
                IPackageFragmentRoot sourceFolder = (IPackageFragmentRoot) 
                        javaElement.getAncestor(PACKAGE_FRAGMENT_ROOT);
                IResourceAware unit = getUnit(file);
                if (unit instanceof Unit) {
                    return new WithSourceFolder(unit, sourceFolder);
                }
            }
            else {
                //archive
                VirtualFile virtualFile = ceylonElement.getVirtualFile();
                Unit unit = getUnit(virtualFile);
                if (unit!=null) {
                    return new WithSourceFolder(unit, null);
                }
            }
        }
        
        return null;
    }

    /*private static Unit getUnit(VirtualFile virtualFile) {
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
    }*/
    
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