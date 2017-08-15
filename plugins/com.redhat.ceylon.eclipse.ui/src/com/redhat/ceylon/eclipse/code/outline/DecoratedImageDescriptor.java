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

import static org.eclipse.jface.viewers.IDecoration.BOTTOM_LEFT;
import static org.eclipse.jface.viewers.IDecoration.BOTTOM_RIGHT;
import static org.eclipse.jface.viewers.IDecoration.TOP_LEFT;
import static org.eclipse.jface.viewers.IDecoration.TOP_RIGHT;
import static org.eclipse.jface.viewers.IDecoration.UNDERLAY;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.resource.CompositeImageDescriptor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;

import com.redhat.ceylon.eclipse.ui.CeylonPlugin;

/**
 * A {@link DecoratedImageDescriptor} consists of a base image and several adornments. The adornments
 * are computed according to the flags either passed during creation or set via the method
 * {@link #setAdornments(int)}.
 * <p>
 * N.B. The JDT version of this code specially handles certain combinations of flags using a "merged"
 * decoration (e.g. synchronized + overrides) by checking for those first. We could duplicate that
 * functionality for each of the quadrants, if someone wanted us to. The DecorationDescriptor and
 * much of the logic is already prepared for that, but we'd just need to change the behavior of the
 * various drawXXX() methods.
 * 
 * <p>
 * For now, we assume the decorations in each quadrant are mutually exclusive.
 * 
 * <p>
 * This class may be instantiated; it is not intended to be subclassed.
 * </p>
 *
 * @since 0.1
 */
public class DecoratedImageDescriptor extends CompositeImageDescriptor {
    private ImageDescriptor fBaseImage;
    private int fFlags;
    private boolean fSmallSize;
//    private Point fSize;
    private final ImageRegistry imageRegistry = CeylonPlugin.imageRegistry();

    /**
     * Creates a new SourceEntityImageDescriptor.
     * 
     * @param baseImage an image descriptor used as the base image
     * @param flags flags indicating which adornments are to be rendered. See {@link #setAdornments(int)}
     *  for valid values.
     * @param size the size of the resulting image
     */
    public DecoratedImageDescriptor(ImageDescriptor baseImage, int flags, boolean smallSize/*, Point size*/) {
        fBaseImage= baseImage;
        this.fSmallSize = smallSize/*, Point size*/;
        Assert.isNotNull(fBaseImage);
        fFlags= flags;
        Assert.isTrue(fFlags >= 0);
//        fSize= size;
//        Assert.isNotNull(fSize);
    }

    /**
     * Sets the descriptors adornments. Valid values are: {@link #ABSTRACT}, {@link #FINAL},
     * {@link #SYNCHRONIZED}, {@link #STATIC}, {@link #RUNNABLE}, {@link #CEYLON_WARNING}, 
     * {@link #CEYLON_ERROR}, {@link #OVERRIDES}, {@link #IMPLEMENTS}, {@link #CONSTRUCTOR},
     * {@link #DEPRECATED}, {@link #VOLATILE}, {@link #TRANSIENT} or any combination of those.
     * 
     * @param adornments the image descriptors adornments
     */
    public void setAdornments(int adornments) {
        Assert.isTrue(adornments >= 0);
        fFlags= adornments;
    }

    /**
     * Returns the current adornments.
     * 
     * @return the current adornments
     */
    public int getAdornments() {
        return fFlags;
    }

//    /**
//     * Sets the size of the image created by calling {@link #createImage()}.
//     * 
//     * @param size the size of the image returned from calling {@link #createImage()}
//     */
//    public void setImageSize(Point size) {
//        Assert.isNotNull(size);
//        Assert.isTrue(size.x >= 0 && size.y >= 0);
//        fSize= size;
//    }

//    /**
//     * Returns the size of the image created by calling {@link #createImage()}.
//     * 
//     * @return the size of the image created by calling {@link #createImage()}
//     */
//    public Point getImageSize() {
//        return new Point(fSize.x, fSize.y);
//    }

    @Override
    protected Point getSize() {
        ImageData imageData = imageData(fBaseImage);
        int width = imageData.width;
        int height = imageData.height;
        if (!fSmallSize) {
            width = width * 24 / 16;
        }
        return new Point(width, height);
    }

    private ImageData imageData(ImageDescriptor descriptor) {
        ImageData imageData = 
                descriptor.getImageData(getZoomLevel());
        if (imageData==null) {
            imageData = descriptor.getImageData(100);
        }
        return imageData;
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof DecoratedImageDescriptor) {
            DecoratedImageDescriptor other = 
                    (DecoratedImageDescriptor) object;
            return fBaseImage.equals(other.fBaseImage) 
                && fFlags == other.fFlags 
                && fSmallSize==other.fSmallSize;
        }
        else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return fBaseImage.hashCode() | fFlags;
    }

    @Override
    protected void drawCompositeImage(int width, int height) {
        ImageData bg = getImageData(fBaseImage);

        drawUnderlay();
        
        drawImage(bg, (width-bg.width)/2, 0);

        drawTopLeft(); // conventionally not used
        drawTopRight();
        drawBottomRight();
        drawBottomLeft();
    }

    private ImageData getImageData(ImageDescriptor descriptor) {
        ImageData data = imageData(descriptor);
        if (data == null) {
            data = DEFAULT_IMAGE_DATA;
            System.err.println("Image data not available: " + descriptor.toString());
        }
        return data;
    }

    private void addUnderlayImage(ImageDescriptor desc, Point pos) {
        ImageData data= getImageData(desc);
        int x = (pos.x - data.width)/2;
        if (x >= 0) {
            drawImage(data, x, pos.y);
        }
    }

    private void addTopLeftImage(ImageDescriptor desc, Point pos) {
        ImageData data= getImageData(desc);
        drawImage(data, pos.x, pos.y);
    }

    private void addTopRightImage(ImageDescriptor desc, Point pos) {
        ImageData data= getImageData(desc);
        int x= pos.x - data.width;
        if (x >= 0) {
            drawImage(data, x, pos.y);
            pos.x= x;
        }
    }

    private void addBottomRightImage(ImageDescriptor desc, Point pos) {
        ImageData data= getImageData(desc);
        int x= pos.x - data.width;
        int y= pos.y - data.height;
        if (x >= 0 && y >= 0) {
            drawImage(data, x, y);
            pos.x= x;
        }
    }

    private void addBottomLeftImage(ImageDescriptor desc, Point pos) {
        ImageData data= getImageData(desc);
        int x= pos.x;
        int y= pos.y - data.height;
        if (x + data.width < getSize().x && y >= 0) {
            drawImage(data, x, y);
            pos.x= x + data.width;
        }
    }

    private void drawUnderlay() {
        Point pos= new Point(getSize().x, 0);
        for(DecorationDescriptor d: CeylonLabelProvider.DECORATIONS) {
            if (d.getQuadrant()==UNDERLAY && d.hasDecoration(fFlags)) {
                addUnderlayImage(imageRegistry.getDescriptor(d.getImageKey()), pos);
            }
        }
    }

    private void drawTopRight() {
        Point pos= new Point(getSize().x, 0);
        for(DecorationDescriptor d: CeylonLabelProvider.DECORATIONS) {
            if (d.getQuadrant()==TOP_RIGHT && d.hasDecoration(fFlags)) {
                addTopRightImage(imageRegistry.getDescriptor(d.getImageKey()), pos);
            }
        }
    }

    private void drawTopLeft() {
        Point pos= new Point(0, 0);
        for(DecorationDescriptor d: CeylonLabelProvider.DECORATIONS) {
            if (d.getQuadrant()==TOP_LEFT && d.hasDecoration(fFlags)) {
                addTopLeftImage(imageRegistry.getDescriptor(d.getImageKey()), pos);
            }
        }
    }

    private void drawBottomRight() {
        Point size= getSize();
        Point pos= new Point(size.x, size.y);

        // Were we to support "merged" decorators, we'd need to do something like the following instead:
        //
//      int flags= fFlags;
//      for(DecorationDescriptor d: fController.getBottomRightDecorations()) {
//          if ((flags & d.mask) != 0) {
//              addBottomRightImage(d.getImageDescriptor(), pos);
//          }
//          flags &= ~d.mask;
//      }

        for(DecorationDescriptor d: CeylonLabelProvider.DECORATIONS) {
            if (d.getQuadrant()==BOTTOM_RIGHT && d.hasDecoration(fFlags)) {
                addBottomRightImage(imageRegistry.getDescriptor(d.getImageKey()), pos);
            }
        }
    }

    private void drawBottomLeft() {
        Point pos= new Point(0, getSize().y);
        for (DecorationDescriptor d: CeylonLabelProvider.DECORATIONS) {
            if (d.getQuadrant()==BOTTOM_LEFT && d.hasDecoration(fFlags)) {
                addBottomLeftImage(imageRegistry.getDescriptor(d.getImageKey()), pos);
            }
        }
    }
}
