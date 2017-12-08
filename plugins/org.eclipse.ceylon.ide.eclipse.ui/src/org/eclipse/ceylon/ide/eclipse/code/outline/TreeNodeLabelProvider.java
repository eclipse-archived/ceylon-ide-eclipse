/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 1.0 which is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.code.outline;

import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.TreeNode;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.graphics.Image;

public class TreeNodeLabelProvider 
        extends StyledCellLabelProvider 
        implements DelegatingStyledCellLabelProvider.IStyledLabelProvider, 
                   ILabelProvider {
    
    private CeylonLabelProvider ceylonLabelProvider;
    
    public TreeNodeLabelProvider(CeylonLabelProvider ceylonLabelProvider) {
        this.ceylonLabelProvider = ceylonLabelProvider;
    }
    
    public TreeNodeLabelProvider() {
        this(new CeylonLabelProvider());
    }

    protected Object unwrap(Object element) {
        return ((TreeNode) element).getValue();
    }

    @Override
    public String getText(Object element) {
        return ceylonLabelProvider.getText(unwrap(element));
    }

    @Override
    public StyledString getStyledText(Object element) {
        return ceylonLabelProvider.getStyledText(unwrap(element));
    }

    @Override
    public Image getImage(Object element) {
        return ceylonLabelProvider.getImage(unwrap(element));
    }
    
    @Override
    public void addListener(ILabelProviderListener listener) {
        ceylonLabelProvider.addListener(listener);
    }
    
    @Override
    public void removeListener(ILabelProviderListener listener) {
        ceylonLabelProvider.removeListener(listener);
    }

    @Override
    public boolean isLabelProperty(Object element, String property) {
        return false;
    }
    
    @Override
    public void update(ViewerCell cell) {
        Object element = cell.getElement();
        StyledString styledText = getStyledText(element);
        cell.setText(styledText.toString());
        cell.setStyleRanges(styledText.getStyleRanges());
        cell.setImage(getImage(element));
        super.update(cell);
    }
    
}
