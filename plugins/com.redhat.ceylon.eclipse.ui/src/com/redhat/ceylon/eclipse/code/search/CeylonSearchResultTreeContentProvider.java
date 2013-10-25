package com.redhat.ceylon.eclipse.code.search;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.search.ui.text.AbstractTextSearchResult;

import com.redhat.ceylon.compiler.typechecker.TypeChecker;
import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.context.PhasedUnits;
import com.redhat.ceylon.compiler.typechecker.io.VirtualFile;
import com.redhat.ceylon.compiler.typechecker.model.Module;
import com.redhat.ceylon.compiler.typechecker.model.Package;
import com.redhat.ceylon.compiler.typechecker.model.Unit;
import com.redhat.ceylon.eclipse.core.builder.CeylonBuilder;
import com.redhat.ceylon.eclipse.core.model.loader.JDTModule;

class CeylonSearchResultTreeContentProvider implements
    CeylonStructuredContentProvider, ITreeContentProvider {
	
	static final int LEVEL_FILE=0;
	static final int LEVEL_PACKAGE=1;
	static final int LEVEL_FOLDER=2;
	static final int LEVEL_PROJECT=3;
	
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

    public Object getParent(Object element) {
        if (element instanceof IProject ||
            element instanceof IJavaProject) {
            return null;
        }
        if (element instanceof IPackageFragment) {
        	if (level==LEVEL_PACKAGE) {
        		return null;
        	}
            IJavaElement container=((IPackageFragment)element).getParent();
            while (container instanceof IPackageFragment) {
                container = container.getParent();
            }
            return container; //the IPackageFragmentRoot
        }
        if (element instanceof IPackageFragmentRoot) {
        	if (level==LEVEL_FOLDER) {
        		return null;
        	}
            return ((IPackageFragmentRoot)element).getJavaProject().getProject();
        }
        if (element instanceof IResource) {
        	if (level==LEVEL_FILE) {
        		return null;
        	}
            IContainer parent = ((IResource) element).getParent();
            IJavaElement javaElement = JavaCore.create(parent);
            if (javaElement instanceof IPackageFragment ||
                javaElement instanceof IPackageFragmentRoot) {
                return javaElement;
            }
            else {
                return parent;
            }
        }
        if (element instanceof Unit) {
        	if (level==LEVEL_FILE) {
        		return null;
        	}
            return ((Unit) element).getPackage();
        }
        if (element instanceof Package) {
        	if (level==LEVEL_PACKAGE) {
        		return null;
        	}
            return ((Package) element).getModule();
        }
        if (element instanceof Module) {
        	if (level==LEVEL_PACKAGE) {
        		return null;
        	}
            return null;
        }
        if (element instanceof CeylonElement) {
            IFile file = ((CeylonElement) element).getFile();
            if (file!=null) {
                return file;
            }
            VirtualFile virtualFile = ((CeylonElement) element).getVirtualFile();
            for (TypeChecker tc: CeylonBuilder.getTypeCheckers()) {
                PhasedUnit phasedUnit = tc.getPhasedUnits().getPhasedUnit(virtualFile);
                if (phasedUnit!=null) {
                    return phasedUnit.getUnit();
                }
                
                for (Module m : tc.getContext().getModules().getListOfModules()) {
                    if (m instanceof JDTModule) {
                        JDTModule module = (JDTModule) m;
                        if (module.isArchive()) {
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