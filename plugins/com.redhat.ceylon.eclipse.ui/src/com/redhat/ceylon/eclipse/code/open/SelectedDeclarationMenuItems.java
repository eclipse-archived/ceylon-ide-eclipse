package com.redhat.ceylon.eclipse.code.open;


import static com.redhat.ceylon.eclipse.ui.CeylonPlugin.PLUGIN_ID;
import static com.redhat.ceylon.eclipse.ui.CeylonResources.GOTO;
import static com.redhat.ceylon.eclipse.ui.CeylonResources.HIERARCHY;

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.actions.CompoundContributionItem;

import com.redhat.ceylon.eclipse.code.editor.DynamicMenuItem;
import com.redhat.ceylon.eclipse.ui.CeylonPlugin;

public class SelectedDeclarationMenuItems extends CompoundContributionItem {
    
    private static ImageRegistry imageRegistry = CeylonPlugin.getInstance()
            .getImageRegistry();
    
    private static final ImageDescriptor GOTO_IMAGE = imageRegistry.getDescriptor(GOTO);
    private static final ImageDescriptor HIERARCHY_IMAGE = imageRegistry.getDescriptor(HIERARCHY);

    public SelectedDeclarationMenuItems() {}
    
    public SelectedDeclarationMenuItems(String id) {
        super(id);
    }
    
    @Override
    protected IContributionItem[] getContributionItems() {
        boolean enabled = new OpenSelectedDeclarationHandler().isEnabled();
        return new IContributionItem[] {
                new DynamicMenuItem(PLUGIN_ID + ".action.openSelectedDeclaration", 
                        "Go To Selected Declaration",
                        enabled, GOTO_IMAGE),
                new DynamicMenuItem(PLUGIN_ID + ".action.showInHierarchyView", 
                        "Show In Type &Hierarchy View",
                        enabled, HIERARCHY_IMAGE)
        };
    }
    
}
