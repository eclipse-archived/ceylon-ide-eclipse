package com.redhat.ceylon.eclipse.code.outline;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;

class ImageImageDescriptor extends ImageDescriptor {
    private Image fImage;

    public ImageImageDescriptor(Image image) {
        super();
        fImage= image;
    }

    public ImageData getImageData() {
        return fImage.getImageData();
    }

    public boolean equals(Object obj) {
        return (obj != null) && getClass().equals(obj.getClass()) && 
        		fImage.equals(((ImageImageDescriptor)obj).fImage);
    }

    public int hashCode() {
        return fImage.hashCode();
    }
}