/********************************************************************************
 * Copyright (c) {date} Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.code.outline;

import static org.eclipse.ceylon.ide.eclipse.code.outline.CeylonLabelProvider.DECORATIONS;
import static org.eclipse.ceylon.ide.eclipse.code.outline.CeylonLabelProvider.getDecorationAttributes;
import static org.eclipse.ceylon.ide.eclipse.ui.CeylonPlugin.imageRegistry;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;

/**
 * This class works, but we don't use it because it decorates the
 * images asynchronously, which looks just horrible in the UI.
 * However, we're still using its static utility methods directly
 * from CeylonLabelProvider.
 *
 */
class CeylonLabelDecorator implements ILightweightLabelDecorator {
    
    private CeylonLabelDecorator() {}
    
    @Override
    public void addListener(ILabelProviderListener listener) {}
    
    @Override
    public void dispose() {}

    @Override
    public boolean isLabelProperty(Object element, String property) {
        return false;
    }

    @Override
    public void removeListener(ILabelProviderListener listener) {}

    @Override
    public void decorate(Object element, IDecoration decoration) {
        int adornmentFlags = getDecorationAttributes(element);
        for (DecorationDescriptor d: DECORATIONS) {
            if (d.hasDecoration(adornmentFlags)) {
                ImageDescriptor id = 
                        imageRegistry()
                            .getDescriptor(d.getImageKey());
                decoration.addOverlay(id, 
                        d.getQuadrant());
            }
        }
    }

}
