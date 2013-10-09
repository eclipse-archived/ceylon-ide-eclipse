package com.redhat.ceylon.eclipse.code.outline;

import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.IWorkbenchPartSite;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.TypeDeclaration;

public final class CeylonHierarchyContentProvider 
        implements ITreeContentProvider {
	
	private final IWorkbenchPartSite site;
	
	private HierarchyMode mode = HierarchyMode.HIERARCHY;
	private Declaration declaration;
	private CeylonHierarchyBuilder builder;
	
	CeylonHierarchyContentProvider(IWorkbenchPartSite site) {
		this.site = site;
	}

    @Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		if (newInput!=null && newInput!=oldInput) {
			HierarchyInput rootNode = (HierarchyInput) newInput;
			declaration = rootNode.declaration;
			if (declaration==null) {
			    builder = null;
			}
			else {
			    try {
			        builder = new CeylonHierarchyBuilder(declaration, 
			        		rootNode.typeChecker);
			        site.getWorkbenchWindow().run(true, true, builder);
			    } 
			    catch (Exception e) {
			        e.printStackTrace();
			    }
			}
		}
	}

	boolean isShowingRefinements() {
		return !(declaration instanceof TypeDeclaration);
	}
    
	@Override
	public void dispose() {}

	@Override
	public boolean hasChildren(Object element) {
	    return getChildren(element).length>0;
	}

	@Override
	public Object getParent(Object element) {
	    return null;
	}

	@Override
	public Object[] getElements(Object inputElement) {
	    return getChildren(inputElement);
	}

	@Override
	public CeylonHierarchyNode[] getChildren(Object parentElement) {
	    if (parentElement instanceof HierarchyInput) {
	    	switch (mode) {
	    	case HIERARCHY:
		        return new CeylonHierarchyNode[] { builder.getHierarchyRoot() };		    		
	    	case SUPERTYPES:
	    		return new CeylonHierarchyNode[] { builder.getSupertypesRoot() };
	    	case SUBTYPES:
	    		return new CeylonHierarchyNode[] { builder.getSubtypesRoot() };
	    	default:
	    		throw new RuntimeException();
	    	}
	    }
	    else if (parentElement instanceof CeylonHierarchyNode) {
	    	List<CeylonHierarchyNode> children = ((CeylonHierarchyNode) parentElement).getChildren();
			CeylonHierarchyNode[] array = children.toArray(new CeylonHierarchyNode[children.size()]);
			Arrays.sort(array);
            return array;
	    }
	    else {
	    	return null;
	    }
	}

	Declaration getDeclaration() {
		return declaration;
	}

	void setDeclaration(Declaration declaration) {
		this.declaration = declaration;
	}

	CeylonHierarchyBuilder getBuilder() {
		return builder;
	}

	void setBuilder(CeylonHierarchyBuilder builder) {
		this.builder = builder;
	}

	HierarchyMode getMode() {
		return mode;
	}

	void setMode(HierarchyMode mode) {
		this.mode = mode;
	}
}