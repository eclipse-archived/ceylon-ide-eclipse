package com.redhat.ceylon.eclipse.code.outline;

import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.DECORATIONS;
import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.getDecorationAttributes;
import static com.redhat.ceylon.eclipse.ui.CeylonPlugin.imageRegistry;

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
