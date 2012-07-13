/*******************************************************************************
* Copyright (c) 2007 IBM Corporation.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*    Robert Fuhrer (rfuhrer@watson.ibm.com) - initial API and implementation

*******************************************************************************/

package com.redhat.ceylon.eclipse.code.outline;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Tree;

public abstract class OutlineContentProviderBase 
        implements ITreeContentProvider {
	
	private OutlineInformationControl fInfoControl;
	boolean fShowInheritedMembers;

	protected OutlineContentProviderBase(OutlineInformationControl oic) {
	    this(oic, false);
	}

	/**
	 * Creates a new Outline content provider.
	 *
	 * @param showInheritedMembers <code>true</code> iff inherited members are shown
	 */
	protected OutlineContentProviderBase(OutlineInformationControl oic, 
			boolean showInheritedMembers) {
	    fShowInheritedMembers= showInheritedMembers;
	    fInfoControl= oic;
	}

	public void setInfoControl(OutlineInformationControl infoControl) {
	    fInfoControl= infoControl;
	}

	public boolean isShowingInheritedMembers() {
	    return fShowInheritedMembers;
	}

	public void toggleShowInheritedMembers() {
	    if (fInfoControl == null) return;
	    Tree tree= fInfoControl.getTreeViewer().getTree();
	    tree.setRedraw(false);
	    fShowInheritedMembers= !fShowInheritedMembers;
	    fInfoControl.getTreeViewer().refresh();
	    fInfoControl.getTreeViewer().expandToLevel(2);
	    // reveal selection
	    Object selectedElement= fInfoControl.getSelectedElement();
	    if (selectedElement != null)
		fInfoControl.getTreeViewer().reveal(selectedElement);
	    tree.setRedraw(true);
	}

	public Object[] getChildren(Object element) { // left here as a placeholder for the commented-out code below
//	    if (fShowOnlyMainType) {
//		if (element instanceof ICompilationUnit) {
//		    element= getMainType((ICompilationUnit) element);
//		} else if (element instanceof IClassFile) {
//		    element= getMainType((IClassFile) element);
//		}
//		if (element == null)
//		    return NO_CHILDREN;
//	    }
//	    if (fShowInheritedMembers && element instanceof IType) {
//		IType type= (IType) element;
//		if (type.getDeclaringType() == null) {
//		    ITypeHierarchy th= getSuperTypeHierarchy(type);
//		    if (th != null) {
//			List children= new ArrayList();
//			IType[] superClasses= th.getAllSupertypes(type);
//			children.addAll(Arrays.asList(super.getChildren(type)));
//			for(int i= 0, scLength= superClasses.length; i < scLength; i++)
//			    children.addAll(Arrays.asList(super.getChildren(superClasses[i])));
//			return children.toArray();
//		    }
//		}
//	    }
//	    return super.getChildren(element);
	    return OutlineInformationControl.NO_CHILDREN;
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}

	public void dispose() {}

	public boolean hasChildren(Object element) {
	    Object[] children= getChildren(element);
	
	    return (children != null) && children.length > 0;
	}

	public Object[] getElements(Object inputElement) {
	    return getChildren(inputElement);
	}
    }