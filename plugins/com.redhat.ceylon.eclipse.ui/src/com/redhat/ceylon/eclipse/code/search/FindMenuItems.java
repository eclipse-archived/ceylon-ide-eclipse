package com.redhat.ceylon.eclipse.code.search;


import static com.redhat.ceylon.eclipse.code.editor.DynamicMenuItem.collapseMenuItems;
import static com.redhat.ceylon.eclipse.ui.CeylonPlugin.PLUGIN_ID;
import static com.redhat.ceylon.eclipse.ui.CeylonResources.CEYLON_DECS;
import static com.redhat.ceylon.eclipse.ui.CeylonResources.CEYLON_REFS;
import static com.redhat.ceylon.eclipse.util.EditorUtil.getCurrentEditor;

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.actions.CompoundContributionItem;

import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.code.editor.DynamicMenuItem;
import com.redhat.ceylon.eclipse.ui.CeylonPlugin;

public class FindMenuItems extends CompoundContributionItem {
    
    private static ImageRegistry imageRegistry = CeylonPlugin.getInstance()
            .getImageRegistry();
    
    private static ImageDescriptor REFS = imageRegistry.getDescriptor(CEYLON_REFS);
    private static ImageDescriptor DECS = imageRegistry.getDescriptor(CEYLON_DECS);

    public FindMenuItems() {}
    
    public FindMenuItems(String id) {
        super(id);
    }
    
    @Override
    public IContributionItem[] getContributionItems() {
        IContributionItem[] items = getItems(getCurrentEditor());
        if (collapseMenuItems(getParent())) {
            MenuManager submenu = new MenuManager("Find");
            submenu.setActionDefinitionId(CeylonEditor.FIND_MENU_ID);
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
                new DynamicMenuItem(PLUGIN_ID + ".action.findReferences", 
                        "Find &References",
                        editor==null ? false : new FindReferencesAction(editor).isEnabled(), 
                                REFS),
                new DynamicMenuItem(PLUGIN_ID + ".action.findAssignments", 
                        "Find Assi&gnments",
                        editor==null ? false : new FindAssignmentsAction(editor).isEnabled(), 
                                REFS),
                new DynamicMenuItem(PLUGIN_ID + ".action.findRefinements", 
                        "Find Refi&nements",
                        editor==null ? false : new FindRefinementsAction(editor).isEnabled(), 
                                DECS),
                new DynamicMenuItem(PLUGIN_ID + ".action.findSubtypes", 
                        "Find &Subtypes",
                        editor==null ? false : new FindSubtypesAction(editor).isEnabled(), 
                                DECS),
                new Separator(),
                new DynamicMenuItem("org.eclipse.search.ui.performTextSearchWorkspace",
                        "Find &Text in Workspace", true)
        };
    }

}
