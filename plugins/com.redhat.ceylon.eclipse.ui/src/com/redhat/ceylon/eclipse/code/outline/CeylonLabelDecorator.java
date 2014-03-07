package com.redhat.ceylon.eclipse.code.outline;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;

import com.redhat.ceylon.eclipse.ui.CeylonPlugin;

/**
 * This class works, but we don't use it because it decorates the
 * images asynchronously, which looks just horrible in the UI.
 * However, we're still using its static utility methods directly
 * from CeylonLabelProvider.
 *
 */
class CeylonLabelDecorator implements ILightweightLabelDecorator {
    
    private CeylonLabelDecorator() {}
    
    private final ImageRegistry imageRegistry = CeylonPlugin.getInstance().getImageRegistry();
    
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
        int adornmentFlags = CeylonLabelProvider.getDecorationAttributes(element);
        for (DecorationDescriptor d: CeylonLabelProvider.DECORATIONS) {
            if (d.hasDecoration(adornmentFlags)) {
                decoration.addOverlay(imageRegistry.getDescriptor(d.getImageKey()), 
                        d.getQuadrant());
            }
        }
    }

}
