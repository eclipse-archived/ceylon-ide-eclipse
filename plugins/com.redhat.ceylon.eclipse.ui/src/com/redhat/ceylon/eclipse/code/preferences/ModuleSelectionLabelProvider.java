package com.redhat.ceylon.eclipse.code.preferences;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import com.redhat.ceylon.eclipse.code.modulesearch.ModuleNode;
import com.redhat.ceylon.eclipse.code.modulesearch.ModuleVersionNode;
import com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider;

final class ModuleSelectionLabelProvider extends
        LabelProvider {
    @Override
    public Image getImage(Object element) {
        if (element instanceof ModuleNode) {
            return CeylonLabelProvider.ARCHIVE;
        }
        else {
            return CeylonLabelProvider.VERSION;
        }
    }

    @Override
    public String getText(Object element) {
        if (element instanceof ModuleNode) {
            ModuleNode md = (ModuleNode) element;
            return md.getName() + " : " + md.getLastVersion().getVersion();
        }
        else {
            return ((ModuleVersionNode) element).getVersion();
        }
    }
}