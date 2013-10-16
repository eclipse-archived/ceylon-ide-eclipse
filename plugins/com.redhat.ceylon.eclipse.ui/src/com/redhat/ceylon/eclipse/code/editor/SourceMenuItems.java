package com.redhat.ceylon.eclipse.code.editor;


import static com.redhat.ceylon.eclipse.code.editor.DynamicMenuItem.collapseMenuItems;
import static com.redhat.ceylon.eclipse.code.editor.Util.getCurrentEditor;
import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.imageRegistry;
import static com.redhat.ceylon.eclipse.ui.CeylonPlugin.PLUGIN_ID;
import static com.redhat.ceylon.eclipse.ui.CeylonResources.ADD_COMMENT;
import static com.redhat.ceylon.eclipse.ui.CeylonResources.REMOVE_COMMENT;
import static com.redhat.ceylon.eclipse.ui.CeylonResources.TERMINATE_STATEMENT;
import static com.redhat.ceylon.eclipse.ui.CeylonResources.TOGGLE_COMMENT;

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.actions.CompoundContributionItem;

public class SourceMenuItems extends CompoundContributionItem {
    
    public static ImageDescriptor TERMINATE = imageRegistry.getDescriptor(TERMINATE_STATEMENT);
    public static ImageDescriptor ADD = imageRegistry.getDescriptor(ADD_COMMENT);
    public static ImageDescriptor REMOVE = imageRegistry.getDescriptor(REMOVE_COMMENT);
    public static ImageDescriptor TOGGLE = imageRegistry.getDescriptor(TOGGLE_COMMENT);
    
    public SourceMenuItems() {}
    
    public SourceMenuItems(String id) {
        super(id);
    }
    
    @Override
    public IContributionItem[] getContributionItems() {
        IContributionItem[] items = getItems(getCurrentEditor());
        if (collapseMenuItems(getParent())) {
            MenuManager submenu = new MenuManager("Source");
            submenu.setActionDefinitionId(CeylonEditor.SOURCE_MENU_ID);
            for (IContributionItem item: items) {
                submenu.add(item);
            }
            return new IContributionItem[] { submenu };
        }
        else {
            return items;
        }
    }

    private IContributionItem[] getItems(IEditorPart editor) {
        return new IContributionItem[] {
                new DynamicMenuItem(PLUGIN_ID + ".editor.toggleComment", "Togg&le Comment", true, TOGGLE),
                new DynamicMenuItem(PLUGIN_ID + ".editor.addBlockComment", "Add Block Comment", true, ADD),
                new DynamicMenuItem(PLUGIN_ID + ".editor.removeBlockComment", "Remove Block Comment", true, REMOVE),
                new Separator(),
                new DynamicMenuItem(PLUGIN_ID + ".editor.terminateStatement", "&Terminate Statement", true, TERMINATE)
            };
    }

}
