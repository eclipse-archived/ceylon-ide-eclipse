package com.redhat.ceylon.eclipse.code.search;

import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.asSourceModule;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getPackage;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getUnit;
import static org.eclipse.jdt.core.IJavaElement.PACKAGE_FRAGMENT;
import static org.eclipse.jdt.core.IJavaElement.PACKAGE_FRAGMENT_ROOT;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IImportDeclaration;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.search.ui.text.AbstractTextSearchResult;

import com.redhat.ceylon.compiler.typechecker.io.VirtualFile;
import com.redhat.ceylon.eclipse.core.builder.CeylonBuilder;
import com.redhat.ceylon.ide.common.model.BaseIdeModule;
import com.redhat.ceylon.ide.common.model.CeylonBinaryUnit;
import com.redhat.ceylon.ide.common.model.IJavaModelAware;
import com.redhat.ceylon.ide.common.model.IResourceAware;
import com.redhat.ceylon.ide.common.model.IdeUnit;
import com.redhat.ceylon.ide.common.model.JavaClassFile;
import com.redhat.ceylon.model.typechecker.model.Module;
import com.redhat.ceylon.model.typechecker.model.Package;
import com.redhat.ceylon.model.typechecker.model.Unit;

class CeylonSearchResultTreeContentProvider implements
    CeylonStructuredContentProvider, ITreeContentProvider {
    
    static final int LEVEL_FILE=0;
    static final int LEVEL_PACKAGE=1;
    static final int LEVEL_MODULE=2;
    static final int LEVEL_FOLDER=3;
    static final int LEVEL_PROJECT=4;
    
    private static final Object[] EMPTY_ARR = new Object[0];
    
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
        return getChildren(inputElement);
    }
    
    public void dispose() {}
    
    public void inputChanged(Viewer viewer, 
            Object oldInput, Object newInput) {
        if (newInput instanceof AbstractTextSearchResult) {
            initialize((AbstractTextSearchResult) newInput);
        }
    }
    
    private synchronized void initialize(
            AbstractTextSearchResult result) {
        this.result = (CeylonSearchResult) result;
        childrenMap = new HashMap<Object, Set<Object>>();
        if (result != null) {
            Object[] elements = result.getElements();
            for (int i= 0; i<elements.length; i++) {
                Object element = elements[i];
                if (page.getDisplayedMatchCount(element) > 0) {
                    insert(null, null, element);
                }
            }
        }
    }

    public Object[] getChildren(Object parentElement) {
        Set<Object> children = 
                childrenMap.get(parentElement);
        if (children == null) {
            return EMPTY_ARR;
        }
        int limit = page.getElementLimit().intValue();
        if (limit!=-1 && limit<children.size()) {
            Object[] limitedArray = new Object[limit];
            Iterator<Object> iterator = children.iterator();
            for (int i=0; i<limit; i++) {
                limitedArray[i] = iterator.next();
            }
            return limitedArray;
        }

        return children.toArray();
    }

    public boolean hasChildren(Object element) {
        Set<Object> children = childrenMap.get(element);
        return children!=null && !children.isEmpty();
    }

    public synchronized void elementsChanged(
            Object[] updatedElements) {
        if (result!=null) {
            Set<Object> removed = new HashSet<Object>();
            Set<Object> updated = new HashSet<Object>();
            Map<Object,Set<Object>> added = 
                    new HashMap<Object,Set<Object>>();
            for (int i= 0; i<updatedElements.length; i++) {
                Object element = updatedElements[i];
                if (page.getDisplayedMatchCount(element) > 0) {
                    insert(added, updated, element);
                } else {
                    remove(removed, updated, element);
                }
            }

            viewer.remove(removed.toArray());
            for (Map.Entry<Object, Set<Object>> entry: 
                    added.entrySet()) {
                Object parent = entry.getKey();
                Set<Object> children = added.get(parent);
                viewer.add(parent, children.toArray());
            }
            for (Object element: updated) {
                viewer.refresh(element);
            }
        }
    }

    protected void insert(
            Map<Object, Set<Object>> toAdd, 
            Set<Object> toUpdate, 
            Object child) {
        Object parent = getParent(child);
        while (parent!=null) {
            if (insertChild(parent, child)) {
                if (toAdd!=null) {
                    insertInto(parent, child, toAdd);
                }
            } else {
                if (toUpdate!=null) {
                    toUpdate.add(parent);
                }
                return;
            }
            child = parent;
            parent = getParent(child);
        }
        if (insertChild(result, child)) {
            if (toAdd!=null) {
                insertInto(result, child, toAdd);
            }
        }
    }

    private boolean insertChild(Object parent, Object child) {
        return insertInto(parent, child, childrenMap);
    }

    private boolean insertInto(Object parent, Object child, 
            Map<Object, Set<Object>> map) {
        Set<Object> children = map.get(parent);
        if (children==null) {
            children = new HashSet<Object>();
            map.put(parent, children);
        }
        return children.add(child);
    }

    protected void remove(
            Set<Object> toRemove, 
            Set<Object> toUpdate, 
            Object element) {
        // precondition here:  fResult.getMatchCount(child) <= 0

        if (hasChildren(element)) {
            if (toUpdate != null) {
                toUpdate.add(element);
            }
        } else {
            if (page.getDisplayedMatchCount(element) == 0) {
                childrenMap.remove(element);
                Object parent = getParent(element);
                if (parent!=null) {
                    if (removeFromSiblings(element, parent)) {
                        remove(toRemove, toUpdate, parent);
                    }
                } else {
                    if (removeFromSiblings(element, result)) {
                        if (toRemove!=null) {
                            toRemove.add(element);
                        }
                    }
                }
            } else {
                if (toUpdate!=null) {
                    toUpdate.add(element);
                }
            }
        }
    }

    /**
     * Tries to remove the given element from the list of stored siblings.
     * 
     * @param element potential child
     * @param parent potential parent
     * @return returns true if it really was a remove (i.e. element was a child of parent).
     */
    private boolean removeFromSiblings(
            Object element, Object parent) {
        Set<Object> siblings = childrenMap.get(parent);
        if (siblings!=null) {
            return siblings.remove(element);
        } else {
            return false;
        }
    }
    
    public Object getParent(Object child) {
        CeylonSearchMatch.Type category;
        if (child instanceof CeylonSearchMatch) {
            CeylonSearchMatch match = 
                    (CeylonSearchMatch) child;
            category = match.getType();
        }
        else if (child instanceof WithCategory) {
            WithCategory wc = (WithCategory) child;
            category = wc.getCategory();
            child = wc.getItem();
        }
        else {
            category = null;
        }
        Object parent = getParentInternal(child);
        return parent == null ? category : 
            new WithCategory(parent, category);
    }

    public Object getParentInternal(Object child) {
        if (child instanceof IProject) {
            return null;
        }
        if (child instanceof IPackageFragmentRoot) {
            if (level==LEVEL_FOLDER) {
                return null;
            }
            IPackageFragmentRoot sourceFolder = 
                    (IPackageFragmentRoot) child;
            return sourceFolder.getJavaProject()
                    .getProject();
        }
        if (child instanceof WithSourceFolder) {
            WithSourceFolder wsf = (WithSourceFolder) child;
            Object element = wsf.element;
            IPackageFragmentRoot sourceFolder = 
                    wsf.sourceFolder;
            if (element instanceof Unit) {
                if (level==LEVEL_FILE) {
                    return null;
                }
                Unit unit = (Unit) element;
                Package pack = unit.getPackage();
                return new WithSourceFolder(pack, 
                        sourceFolder);
            }
            if (element instanceof Package) {
                if (level==LEVEL_PACKAGE) {
                    return null;
                }
                Package p = (Package) element;
                Module mod = p.getModule();
                return new WithSourceFolder(mod, 
                        sourceFolder);
            }
            if (element instanceof Module) {
                if (level==LEVEL_MODULE) {
                    return null;
                }
                return sourceFolder==null ? 
                        ArchiveMatches.INSTANCE : 
                            sourceFolder;
            }
            if (element instanceof IPackageFragment) {
                if (level==LEVEL_PACKAGE) {
                    return null;
                }
                //let's see if it belongs to a Ceylon module
                IPackageFragment pf = 
                        (IPackageFragment) element;
                Module mod = asSourceModule(pf);
                if (mod!=null) {
                    return new WithSourceFolder(mod, 
                            sourceFolder);
                }
                IPackageFragmentRoot pfr = 
                        (IPackageFragmentRoot) 
                        pf.getAncestor(PACKAGE_FRAGMENT_ROOT);
                if (pfr!=null && 
                        pfr.getPath().getFileExtension()!=null) {
                    return new WithSourceFolder(pfr, 
                            sourceFolder);
                }
                
                //otherwise, it doesn't belong to a module
                if (level==LEVEL_MODULE) {
                    return null;
                }
                return sourceFolder==null ? 
                        ArchiveMatches.INSTANCE : 
                            sourceFolder;
            }
            if (element instanceof IPackageFragmentRoot) {
                if (level==LEVEL_MODULE) {
                    return null;
                }
                return sourceFolder==null ? 
                        ArchiveMatches.INSTANCE : 
                            sourceFolder;
            }
            if (element instanceof IFile) {
                if (level==LEVEL_FILE) {
                    return null;
                }
                IFile file = (IFile) element;
                IContainer parent = file.getParent();
                if (parent instanceof IFolder) {
                    //let's see if the .java files it belongs to
                    //a Ceylon package
                    IFolder folder = (IFolder) parent;
                    Package pack = getPackage(folder);
                    if (pack!=null) {
                        return new WithSourceFolder(pack, 
                                sourceFolder);
                    }
                }
                return new WithSourceFolder(
                        JavaCore.create(parent), 
                        sourceFolder);
            }
            return sourceFolder;
        }
        if (child instanceof IType ||
            child instanceof IMethod ||
            child instanceof IField ||
            child instanceof IImportDeclaration) {
            IJavaElement javaElement = (IJavaElement) child;
            IJavaModelAware<IProject,ITypeRoot,IJavaElement> unit = 
                    CeylonBuilder.getUnit(javaElement);
            if (unit instanceof CeylonBinaryUnit || 
                unit instanceof JavaClassFile) {
                IdeUnit ideUnit = (IdeUnit) unit;
                Module module = 
                        ideUnit.getPackage().getModule();
                if (module instanceof BaseIdeModule) {
                    BaseIdeModule jdtModule = 
                            (BaseIdeModule) module;
                    if (jdtModule.getIsCeylonBinaryArchive()) {
                        return new WithSourceFolder(unit, null);
                    }
                }
            }
            IFile file = (IFile) javaElement.getResource();
            //there is never a Unit for a .java file, since
            //I can't figure out any way to navigate to the
            //Java source file
            if (file == null) {
                if (level==LEVEL_FILE) {
                    return null;
                }
                IPackageFragment pack = 
                        (IPackageFragment) 
                        javaElement.getAncestor(
                                PACKAGE_FRAGMENT);
                return new WithSourceFolder(pack, null);
            }
            else {
                IPackageFragmentRoot sourceFolder = 
                        (IPackageFragmentRoot) 
                        javaElement.getAncestor(
                                PACKAGE_FRAGMENT_ROOT);
                return new WithSourceFolder(file, 
                        sourceFolder);
            }
        }
        
        if (child instanceof CeylonElement) {
            CeylonElement ceylonElement = 
                    (CeylonElement) child;
            IFile file = ceylonElement.getFile();
            if (file!=null) {
                //workspace .ceylon file
                IJavaElement javaElement = 
                        JavaCore.create(file.getParent());
                IPackageFragmentRoot sourceFolder = 
                        (IPackageFragmentRoot) 
                        javaElement.getAncestor(
                                PACKAGE_FRAGMENT_ROOT);
                IResourceAware<IProject,IFolder,IFile> unit = 
                        getUnit(file);
                if (unit instanceof Unit) {
                    return new WithSourceFolder(unit, 
                            sourceFolder);
                }
            }
            else {
                //archive
                VirtualFile virtualFile = 
                        ceylonElement.getVirtualFile();
                Unit unit = getUnit(virtualFile);
                if (unit!=null) {
                    return new WithSourceFolder(unit, null);
                }
            }
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