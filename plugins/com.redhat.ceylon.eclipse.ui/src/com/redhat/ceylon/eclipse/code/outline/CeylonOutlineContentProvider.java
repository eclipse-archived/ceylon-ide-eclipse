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

public class CeylonOutlineContentProvider 
        implements ITreeContentProvider {

    @Override
    public Object[] getChildren(Object element) {
        CeylonOutlineNode node = (CeylonOutlineNode) element;
        return node.getChildren().toArray();
    }

    @Override
    public void inputChanged(Viewer viewer, 
            Object oldInput, Object newInput) {}

    @Override
    public void dispose() {}

    @Override
    public boolean hasChildren(Object element) {
        Object[] children = getChildren(element);
        return children!=null && children.length > 0;
    }
    
    @Override
    public Object getParent(Object element) {
        return ((CeylonOutlineNode) element).getParent();
    }

    @Override
    public Object[] getElements(Object inputElement) {
        return getChildren(inputElement);
    }
}