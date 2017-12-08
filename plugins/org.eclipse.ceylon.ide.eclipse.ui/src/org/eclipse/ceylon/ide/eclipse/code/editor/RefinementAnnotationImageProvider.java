/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 1.0 which is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.code.editor;

import static org.eclipse.ceylon.ide.eclipse.ui.CeylonResources.CEYLON_DEFAULT_REFINEMENT;
import static org.eclipse.ceylon.ide.eclipse.ui.CeylonResources.CEYLON_FORMAL_REFINEMENT;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.texteditor.IAnnotationImageProvider;

import org.eclipse.ceylon.ide.eclipse.ui.CeylonPlugin;

public class RefinementAnnotationImageProvider implements IAnnotationImageProvider {
    
    private static Image DEFAULT = 
            CeylonPlugin.imageRegistry()
                .get(CEYLON_DEFAULT_REFINEMENT);
    private static Image FORMAL = 
            CeylonPlugin.imageRegistry()
                .get(CEYLON_FORMAL_REFINEMENT);
    
    @Override
    public Image getManagedImage(Annotation annotation) {
        RefinementAnnotation ra = (RefinementAnnotation) annotation;
        return ra.getDeclaration().isFormal() ? FORMAL : DEFAULT;
    }
    
    @Override
    public String getImageDescriptorId(Annotation annotation) {
        return null;
    }
    
    @Override
    public ImageDescriptor getImageDescriptor(String imageDescritporId) {
        return null;
    }
    
}
