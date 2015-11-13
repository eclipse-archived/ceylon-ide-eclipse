package com.redhat.ceylon.eclipse.code.refactor;

import static com.redhat.ceylon.eclipse.code.editor.DynamicMenuItem.collapseMenuItems;
import static com.redhat.ceylon.eclipse.ui.CeylonPlugin.PLUGIN_ID;
import static com.redhat.ceylon.eclipse.ui.CeylonPlugin.imageRegistry;
import static com.redhat.ceylon.eclipse.ui.CeylonResources.CEYLON_CHANGE;
import static com.redhat.ceylon.eclipse.ui.CeylonResources.CEYLON_COMPOSITE_CHANGE;
import static com.redhat.ceylon.eclipse.ui.CeylonResources.CEYLON_IMPORT;
import static com.redhat.ceylon.eclipse.ui.CeylonResources.CEYLON_MOVE;
import static com.redhat.ceylon.eclipse.ui.CeylonResources.CEYLON_RENAME;
import static com.redhat.ceylon.eclipse.ui.CeylonResources.CEYLON_REORDER;
import static com.redhat.ceylon.eclipse.util.EditorUtil.getCurrentEditor;
import static com.redhat.ceylon.eclipse.java2ceylon.Java2CeylonProxies.refactorJ2C;

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.actions.CompoundContributionItem;

import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.code.editor.DynamicMenuItem;

public class RefactorMenuItems extends CompoundContributionItem {
    
    private static ImageDescriptor RENAME = 
            imageRegistry().getDescriptor(CEYLON_RENAME);
    private static ImageDescriptor CHANGE = 
            imageRegistry().getDescriptor(CEYLON_CHANGE);
    private static ImageDescriptor COMP_CHANGE = 
            imageRegistry().getDescriptor(CEYLON_COMPOSITE_CHANGE);
    private static ImageDescriptor MOVE = 
            imageRegistry().getDescriptor(CEYLON_MOVE);
    private static ImageDescriptor IMPORT = 
            imageRegistry().getDescriptor(CEYLON_IMPORT);
    private static ImageDescriptor REORDER = 
            imageRegistry().getDescriptor(CEYLON_REORDER);

    public RefactorMenuItems() {}
    
    public RefactorMenuItems(String id) {
        super(id);
    }
    
    @Override
    public IContributionItem[] getContributionItems() {
        IContributionItem[] items = getItems(getCurrentEditor());
        if (collapseMenuItems(getParent())) {
            MenuManager submenu = new MenuManager("Refactor");
            submenu.setActionDefinitionId(CeylonEditor.REFACTOR_MENU_ID);
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
                new DynamicMenuItem(PLUGIN_ID + ".action.rename", 
                        "&Rename...",
                        editor!=null && new RenameRefactoring(editor).getEnabled(), 
                        RENAME),
                new DynamicMenuItem(PLUGIN_ID + ".action.enterAlias", 
                        "Enter Import &Alias...",
                        editor!=null && new EnterAliasRefactoring(editor).getEnabled(), 
                        IMPORT),
                new Separator(),
                new DynamicMenuItem(PLUGIN_ID + ".action.changeParameters", 
                        "Change Parameter &List...",
                        editor!=null && new ChangeParametersRefactoring(editor).getEnabled(),
                        REORDER),
                new Separator(),
                new DynamicMenuItem(PLUGIN_ID + ".action.inline", 
                        "&Inline...",
                        editor!=null && new InlineRefactoring(editor).getEnabled(),
                        COMP_CHANGE),
                new DynamicMenuItem(PLUGIN_ID + ".action.extractValue", 
                        "Extract &Value...",
                        editor!=null && refactorJ2C().newExtractValueRefactoring(editor).getEnabled(),
                        CHANGE),
                new DynamicMenuItem(PLUGIN_ID + ".action.extractFunction", 
                        "Extract &Function...",
                        editor!=null && new ExtractFunctionRefactoring(editor).getEnabled(),
                        CHANGE),
                new DynamicMenuItem(PLUGIN_ID + ".action.extractInterface", 
                        "Extract Interface...",
                        editor!=null && new ExtractInterfaceRefactoring(editor).getEnabled(),
                        CHANGE),
                new DynamicMenuItem(PLUGIN_ID + ".action.extractParameter", 
                        "Extract &Parameter...",
                        editor!=null && new ExtractParameterRefactoring(editor).getEnabled(),
                        CHANGE),
                new DynamicMenuItem(PLUGIN_ID + ".action.collectParameters", 
                        "&Collect Parameters...",
                        editor!=null && new CollectParametersRefactoring(editor).getEnabled(),
                        COMP_CHANGE),
                new DynamicMenuItem(PLUGIN_ID + ".action.createAlias", 
                        "Introduce &Type Alias...",
                        editor!=null && new AliasRefactoring(editor).getEnabled(), 
                        COMP_CHANGE),
                new Separator(),
                new DynamicMenuItem(PLUGIN_ID + ".action.invertBoolean", 
                        "Invert &Boolean...",
                        editor!=null && new InvertBooleanRefactoring(editor).getEnabled(),
                       COMP_CHANGE),
                new Separator(),
                new DynamicMenuItem(PLUGIN_ID + ".action.moveOut", 
                        "Move &Out...",
                        editor!=null && new MoveOutRefactoring(editor).getEnabled(),
                        RefactorMenuItems.COMP_CHANGE),
                new DynamicMenuItem(PLUGIN_ID + ".action.makeReceiver", 
                        "Make R&eceiver...",
                        editor!=null && new MakeReceiverRefactoring(editor).getEnabled(),
                        COMP_CHANGE),
                new Separator(),
                new DynamicMenuItem(PLUGIN_ID + ".action.moveDeclarationToNewUnit", 
                        "Move to New &Source File...", 
                        new MoveToNewUnitHandler().isEnabled(), 
                        MOVE),
                new DynamicMenuItem(PLUGIN_ID + ".action.moveDeclarationToUnit", 
                        "&Move to Source File...", 
                        new MoveToUnitHandler().isEnabled(), 
                        MOVE),
        };
    }
    
}
