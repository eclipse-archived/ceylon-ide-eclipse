package com.redhat.ceylon.eclipse.code.open;


import static com.redhat.ceylon.eclipse.ui.CeylonPlugin.PLUGIN_ID;
import static com.redhat.ceylon.eclipse.ui.CeylonResources.CEYLON_HIERARCHY;
import static com.redhat.ceylon.eclipse.ui.CeylonResources.CEYLON_OUTLINE;
import static com.redhat.ceylon.eclipse.ui.CeylonResources.CEYLON_SOURCE;
import static com.redhat.ceylon.eclipse.ui.CeylonResources.GOTO;

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.actions.CompoundContributionItem;

import com.redhat.ceylon.eclipse.code.editor.DynamicMenuItem;
import com.redhat.ceylon.eclipse.ui.CeylonPlugin;
import com.redhat.ceylon.eclipse.ui.CeylonResources;

public class SelectedDeclarationMenuItems extends CompoundContributionItem {
    
    private static ImageRegistry imageRegistry = CeylonPlugin.getInstance()
            .getImageRegistry();
    
    private static final ImageDescriptor GOTO_IMAGE = imageRegistry.getDescriptor(GOTO);
    private static final ImageDescriptor HIERARCHY_IMAGE = imageRegistry.getDescriptor(CeylonResources.HIERARCHY);
    private static final ImageDescriptor OUTLINE = imageRegistry.getDescriptor(CEYLON_OUTLINE);
    private static final ImageDescriptor HIERARCHY = imageRegistry.getDescriptor(CEYLON_HIERARCHY);
    private static final ImageDescriptor CODE = imageRegistry.getDescriptor(CEYLON_SOURCE);

    public SelectedDeclarationMenuItems() {}
    
    public SelectedDeclarationMenuItems(String id) {
        super(id);
    }
    
    @Override
    public IContributionItem[] getContributionItems() {
        boolean enabled = new OpenSelectedDeclarationHandler().isEnabled();
        return new IContributionItem[] {
                new DynamicMenuItem(PLUGIN_ID + ".action.openSelectedDeclaration", 
                        "Go to Selected &Declaration",
                        enabled, GOTO_IMAGE),
                new DynamicMenuItem(PLUGIN_ID + ".action.showInHierarchyView", 
                        "&Show in Type Hierarchy View",
                        enabled, HIERARCHY_IMAGE),
                new Separator(),
                new DynamicMenuItem(PLUGIN_ID + ".editor.showOutline", 
                        "Quick &Outline", true, OUTLINE),
                new DynamicMenuItem(PLUGIN_ID + ".editor.hierarchy", 
                        "Quick &Hierarchy", true, HIERARCHY),
                new DynamicMenuItem(PLUGIN_ID + ".editor.code", 
                        "&Peek Definition", true, CODE),
        };
    }
    
}
