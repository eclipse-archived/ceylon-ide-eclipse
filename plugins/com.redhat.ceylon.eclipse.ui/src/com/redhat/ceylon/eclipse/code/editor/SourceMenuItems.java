package com.redhat.ceylon.eclipse.code.editor;


import static com.redhat.ceylon.eclipse.code.editor.DynamicMenuItem.collapseMenuItems;
import static com.redhat.ceylon.eclipse.code.editor.DynamicMenuItem.isContextMenu;
import static com.redhat.ceylon.eclipse.code.editor.Util.getCurrentEditor;
import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.imageRegistry;
import static com.redhat.ceylon.eclipse.ui.CeylonPlugin.PLUGIN_ID;
import static com.redhat.ceylon.eclipse.ui.CeylonResources.ADD_COMMENT;
import static com.redhat.ceylon.eclipse.ui.CeylonResources.CORRECT_INDENT;
import static com.redhat.ceylon.eclipse.ui.CeylonResources.QUICK_ASSIST;
import static com.redhat.ceylon.eclipse.ui.CeylonResources.REMOVE_COMMENT;
import static com.redhat.ceylon.eclipse.ui.CeylonResources.SHIFT_LEFT;
import static com.redhat.ceylon.eclipse.ui.CeylonResources.SHIFT_RIGHT;
import static com.redhat.ceylon.eclipse.ui.CeylonResources.TERMINATE_STATEMENT;
import static com.redhat.ceylon.eclipse.ui.CeylonResources.TOGGLE_COMMENT;

import java.util.Arrays;

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.actions.CompoundContributionItem;
import org.eclipse.ui.texteditor.ITextEditorActionDefinitionIds;

public class SourceMenuItems extends CompoundContributionItem {
    
    public static ImageDescriptor TERMINATE = imageRegistry.getDescriptor(TERMINATE_STATEMENT);
    public static ImageDescriptor ADD = imageRegistry.getDescriptor(ADD_COMMENT);
    public static ImageDescriptor REMOVE = imageRegistry.getDescriptor(REMOVE_COMMENT);
    public static ImageDescriptor TOGGLE = imageRegistry.getDescriptor(TOGGLE_COMMENT);
    public static ImageDescriptor CORRECT = imageRegistry.getDescriptor(CORRECT_INDENT);
    public static ImageDescriptor LEFT = imageRegistry.getDescriptor(SHIFT_LEFT);
    public static ImageDescriptor RIGHT = imageRegistry.getDescriptor(SHIFT_RIGHT);
    public static ImageDescriptor FIX = imageRegistry.getDescriptor(QUICK_ASSIST);
    
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
        else if (isContextMenu(getParent())) {
            IContributionItem[] copy = Arrays.copyOf(items, items.length+1);
            copy[items.length] = new Separator();
            return copy;
        }
        else {
            return items;            
        }
    }

    private IContributionItem[] getItems(IEditorPart editor) {
        return new IContributionItem[] {
                new DynamicMenuItem(ITextEditorActionDefinitionIds.QUICK_ASSIST, "&Quick Fix/Assist", true, FIX),
                new Separator(),
                new DynamicMenuItem(PLUGIN_ID + ".editor.terminateStatement", "&Terminate Statement", true, TERMINATE),
                new Separator(),
                new DynamicMenuItem(PLUGIN_ID + ".editor.correctIndentation", "Correct &Indentation", true, CORRECT),
                new Separator(),
                new DynamicMenuItem(ITextEditorActionDefinitionIds.SHIFT_LEFT, "Shift &Left", true, LEFT),
                new DynamicMenuItem(ITextEditorActionDefinitionIds.SHIFT_RIGHT, "Shift &Right", true, RIGHT),
                new Separator(),
                new DynamicMenuItem(PLUGIN_ID + ".editor.toggleComment", "Togg&le Comment", true, TOGGLE),
                new DynamicMenuItem(PLUGIN_ID + ".editor.addBlockComment", "Add Block Comment", true, ADD),
                new DynamicMenuItem(PLUGIN_ID + ".editor.removeBlockComment", "Remove Block Comment", true, REMOVE)
            };
    }

}
