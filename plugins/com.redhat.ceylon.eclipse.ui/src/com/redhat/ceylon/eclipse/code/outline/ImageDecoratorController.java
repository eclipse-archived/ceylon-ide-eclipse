/*******************************************************************************
* Copyright (c) 2009 IBM Corporation.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*    Robert Fuhrer (rfuhrer@watson.ibm.com) - initial API and implementation
*******************************************************************************/

package com.redhat.ceylon.eclipse.code.outline;

import static com.redhat.ceylon.eclipse.code.outline.DecorationDescriptor.Quadrant.BOTTOM_LEFT;
import static com.redhat.ceylon.eclipse.ui.CeylonResources.CEYLON_ERR;
import static com.redhat.ceylon.eclipse.ui.CeylonResources.CEYLON_WARN;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Point;

import com.redhat.ceylon.eclipse.ui.CeylonPlugin;

/**
 * This class manages the language's <code>IEntityImageDecorator</code> service, if any.
 * If none is defined, this controller provides support for error/warning decorations
 * for resources.
 * @author rfuhrer@watson.ibm.com
 */
public class ImageDecoratorController {
    /**
     * Flag to render the warning decoration. Must be distinct from all language-specific
     * decoration attribute values.
     */
    public final static int WARNING= 1 << 0;
    
    /**
     * Flag to render the error decoration. Must be distinct from all language-specific
     * decoration attribute values.
     */
    public final static int ERROR= 1 << 1;

    private static final ImageRegistry registry = CeylonPlugin.getInstance().getImageRegistry();
    public final static DecorationDescriptor WARNING_DECORATION= new DecorationDescriptor(WARNING, registry.getDescriptor(CEYLON_WARN), BOTTOM_LEFT);
    public final static DecorationDescriptor ERROR_DECORATION= new DecorationDescriptor(ERROR, registry.getDescriptor(CEYLON_ERR), BOTTOM_LEFT);

    private final CeylonEntityImageDecorator fDecorator;

    private final List<DecorationDescriptor> topLeftDecorations= new LinkedList<DecorationDescriptor>();
    private final List<DecorationDescriptor> topRightDecorations= new LinkedList<DecorationDescriptor>();
    private final List<DecorationDescriptor> bottomLeftDecorations= new LinkedList<DecorationDescriptor>();
    private final List<DecorationDescriptor> bottomRightDecorations= new LinkedList<DecorationDescriptor>();

    public ImageDecoratorController() {
        fDecorator= new CeylonEntityImageDecorator();

        if (fDecorator != null) {
            DecorationDescriptor[] allDescs= fDecorator.getAllDecorations();

            for(int i= 0; i < allDescs.length; i++) {
                DecorationDescriptor desc= allDescs[i];
                if (desc.quadrant == DecorationDescriptor.Quadrant.BOTTOM_LEFT) {
                    bottomLeftDecorations.add(desc);
                } else if (desc.quadrant == DecorationDescriptor.Quadrant.BOTTOM_RIGHT) {
                    bottomRightDecorations.add(desc);
                } else if (desc.quadrant == DecorationDescriptor.Quadrant.TOP_LEFT) {
                    topLeftDecorations.add(desc);
                } else if (desc.quadrant == DecorationDescriptor.Quadrant.TOP_RIGHT) {
                    topRightDecorations.add(desc);
                }
            }
        }
        bottomRightDecorations.add(WARNING_DECORATION);
        bottomRightDecorations.add(ERROR_DECORATION);
    }

    public SourceEntityImageDescriptor getImageDescriptor(ImageDescriptor baseImage, Object entity, Point size) {
        int attrs= fDecorator.getDecorationAttributes(entity);
        return new SourceEntityImageDescriptor(baseImage, attrs, size, this);
    }

    public List<DecorationDescriptor> getBottomLeftDecorations() {
        return bottomLeftDecorations;
    }

    public List<DecorationDescriptor> getBottomRightDecorations() {
        return bottomRightDecorations;
    }

    public List<DecorationDescriptor> getTopLeftDecorations() {
        return topLeftDecorations;
    }

    public List<DecorationDescriptor> getTopRightDecorations() {
        return topRightDecorations;
    }
}
