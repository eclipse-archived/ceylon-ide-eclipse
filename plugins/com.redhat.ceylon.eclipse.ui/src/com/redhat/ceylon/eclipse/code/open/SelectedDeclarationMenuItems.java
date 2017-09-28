package org.eclipse.ceylon.ide.eclipse.code.open;


import static org.eclipse.ceylon.ide.eclipse.ui.CeylonPlugin.PLUGIN_ID;
import static org.eclipse.ceylon.ide.eclipse.ui.CeylonPlugin.imageRegistry;
import static org.eclipse.ceylon.ide.eclipse.ui.CeylonResources.CEYLON_DEFAULT_REFINEMENT;
import static org.eclipse.ceylon.ide.eclipse.ui.CeylonResources.CEYLON_HIERARCHY;
import static org.eclipse.ceylon.ide.eclipse.ui.CeylonResources.CEYLON_OUTLINE;
import static org.eclipse.ceylon.ide.eclipse.ui.CeylonResources.CEYLON_REFS;
import static org.eclipse.ceylon.ide.eclipse.ui.CeylonResources.CEYLON_SOURCE;
import static org.eclipse.ceylon.ide.eclipse.ui.CeylonResources.GOTO;

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.actions.CompoundContributionItem;

import org.eclipse.ceylon.ide.eclipse.code.editor.DynamicMenuItem;
import org.eclipse.ceylon.ide.eclipse.ui.CeylonResources;

public class SelectedDeclarationMenuItems extends CompoundContributionItem {
    
    private static final ImageDescriptor GOTO_IMAGE = 
            imageRegistry().getDescriptor(GOTO);
    private static final ImageDescriptor REFINED_IMAGE = 
            imageRegistry().getDescriptor(CEYLON_DEFAULT_REFINEMENT);
    private static final ImageDescriptor HIERARCHY_IMAGE = 
            imageRegistry().getDescriptor(CeylonResources.HIERARCHY);
    private static final ImageDescriptor OUTLINE = 
            imageRegistry().getDescriptor(CEYLON_OUTLINE);
    private static final ImageDescriptor HIERARCHY = 
            imageRegistry().getDescriptor(CEYLON_HIERARCHY);
    private static final ImageDescriptor CODE = 
            imageRegistry().getDescriptor(CEYLON_SOURCE);
    private static final ImageDescriptor REFS = 
            imageRegistry().getDescriptor(CEYLON_REFS);

    public SelectedDeclarationMenuItems() {}
    
    public SelectedDeclarationMenuItems(String id) {
        super(id);
    }
    
    @Override
    public IContributionItem[] getContributionItems() {
        boolean selectedEnabled = 
                new OpenSelectedDeclarationHandler()
                    .isEnabled();
        boolean refinedEnabled = 
                new OpenRefinedDeclarationHandler()
                    .isEnabled();
        return new IContributionItem[] {
                new DynamicMenuItem(PLUGIN_ID + ".action.openSelectedDeclaration", 
                        "Go to Selected Declaration",
                        selectedEnabled, GOTO_IMAGE),
                new DynamicMenuItem(PLUGIN_ID + ".action.openRefinedDeclaration", 
                        "Go to Refined Declaration", //TODO: mnemonic!
                        refinedEnabled, REFINED_IMAGE),
                new DynamicMenuItem(PLUGIN_ID + ".action.showInHierarchyView", 
                        "&Show in Type Hierarchy View",
                        selectedEnabled, HIERARCHY_IMAGE),
                new Separator(),
                new DynamicMenuItem(PLUGIN_ID + ".editor.showOutline", 
                        "Quick &Outline", true, OUTLINE),
                new DynamicMenuItem(PLUGIN_ID + ".editor.hierarchy", 
                        "Quick &Hierarchy", true, HIERARCHY),
                new DynamicMenuItem(PLUGIN_ID + ".editor.findReferences", 
                        "Quick &Find References", true, REFS),
                new DynamicMenuItem(PLUGIN_ID + ".editor.code", 
                        "&Peek Definition", true, CODE),
        };
    }
    
}
