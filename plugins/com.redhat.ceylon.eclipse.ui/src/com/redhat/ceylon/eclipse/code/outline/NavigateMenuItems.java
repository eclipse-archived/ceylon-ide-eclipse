package com.redhat.ceylon.eclipse.code.outline;


import static com.redhat.ceylon.eclipse.code.editor.DynamicMenuItem.collapseMenuItems;
import static com.redhat.ceylon.eclipse.ui.CeylonResources.LAST_EDIT;
import static com.redhat.ceylon.eclipse.ui.CeylonResources.NEXT_ANN;
import static com.redhat.ceylon.eclipse.ui.CeylonResources.PREV_ANN;
import static com.redhat.ceylon.eclipse.util.EditorUtil.getCurrentEditor;

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.actions.CompoundContributionItem;
import org.eclipse.ui.internal.texteditor.TextEditorPlugin;

import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.code.editor.DynamicMenuItem;
import com.redhat.ceylon.eclipse.code.open.SelectedDeclarationMenuItems;
import com.redhat.ceylon.eclipse.ui.CeylonPlugin;

public class NavigateMenuItems extends CompoundContributionItem {
    
    private static final ImageRegistry imageRegistry = CeylonPlugin.getInstance()
            .getImageRegistry();
    
    private static final ImageDescriptor EDIT = imageRegistry.getDescriptor(LAST_EDIT);
    private static final ImageDescriptor NEXT = imageRegistry.getDescriptor(NEXT_ANN);
    private static final ImageDescriptor PREV = imageRegistry.getDescriptor(PREV_ANN);
    
    public NavigateMenuItems() {}
    
    public NavigateMenuItems(String id) {
        super(id);
    }
    
    @Override
    public IContributionItem[] getContributionItems() {
        IContributionItem[] items = getItems(getCurrentEditor());
        if (collapseMenuItems(getParent())) {
            MenuManager submenu = new MenuManager("Navigate");
            submenu.setActionDefinitionId(CeylonEditor.NAVIGATE_MENU_ID);
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
        boolean enabled= TextEditorPlugin.getDefault().getLastEditPosition() != null;
        IContributionItem[] items = new SelectedDeclarationMenuItems().getContributionItems();
        return new IContributionItem[] {
                items[0], items[1], items[2], items[3], items[4], items[5], items[6],
                new Separator(),
                new DynamicMenuItem("org.eclipse.ui.navigate.next", 
                        "Ne&xt Annotation", true, NEXT),
                new DynamicMenuItem("org.eclipse.ui.navigate.previous", 
                        "Pre&vious Annotation", true, PREV),
                new Separator(),
                new DynamicMenuItem("org.eclipse.ui.edit.text.gotoLastEditPosition", 
                        "Last Edit Lo&cation", enabled, EDIT),
                new DynamicMenuItem("org.eclipse.ui.edit.text.goto.line", 
                        "&Go to Line...", true),
                new DynamicMenuItem("com.redhat.ceylon.eclipse.ui.editor.gotoMatchingFence", 
                        "Go to &Matching Bracket", true)
        };
    }

}
