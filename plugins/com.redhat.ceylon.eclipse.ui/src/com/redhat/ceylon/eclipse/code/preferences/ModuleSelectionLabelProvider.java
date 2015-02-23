package com.redhat.ceylon.eclipse.code.preferences;

import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.MODULE;
import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.REPO;
import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.VERSION;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import com.redhat.ceylon.eclipse.code.modulesearch.ModuleNode;
import com.redhat.ceylon.eclipse.code.modulesearch.ModuleVersionNode;

final class ModuleSelectionLabelProvider extends
        LabelProvider {
    @Override
    public Image getImage(Object element) {
        if (element instanceof ModuleNode) {
            return MODULE;
        }
        else if (element instanceof ModuleVersionNode) {
            return VERSION;
        }
        else {
            return REPO;
        }
    }

    @Override
    public String getText(Object element) {
        if (element instanceof ModuleNode) {
            ModuleNode md = (ModuleNode) element;
            return md.getName() + " \"" + md.getLastVersion().getVersion() + "\"";
        }
        else if (element instanceof ModuleVersionNode) {
            return ((ModuleVersionNode) element).getVersion();
        }
        else {
            return ((ModuleCategoryNode) element).getDescription();
        }
    }
}