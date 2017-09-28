package org.eclipse.ceylon.ide.eclipse.code.refactor;

import static org.eclipse.ceylon.ide.eclipse.code.editor.DynamicMenuItem.collapseMenuItems;
import static org.eclipse.ceylon.ide.eclipse.ui.CeylonPlugin.PLUGIN_ID;
import static org.eclipse.ceylon.ide.eclipse.ui.CeylonPlugin.imageRegistry;
import static org.eclipse.ceylon.ide.eclipse.ui.CeylonResources.CEYLON_CHANGE;
import static org.eclipse.ceylon.ide.eclipse.ui.CeylonResources.CEYLON_COMPOSITE_CHANGE;
import static org.eclipse.ceylon.ide.eclipse.ui.CeylonResources.CEYLON_IMPORT;
import static org.eclipse.ceylon.ide.eclipse.ui.CeylonResources.CEYLON_MOVE;
import static org.eclipse.ceylon.ide.eclipse.ui.CeylonResources.CEYLON_RENAME;
import static org.eclipse.ceylon.ide.eclipse.ui.CeylonResources.CEYLON_REORDER;
import static org.eclipse.ceylon.ide.eclipse.util.EditorUtil.getCurrentEditor;
import static org.eclipse.ceylon.ide.eclipse.java2ceylon.Java2CeylonProxies.refactorJ2C;

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.actions.CompoundContributionItem;

import org.eclipse.ceylon.ide.eclipse.code.editor.CeylonEditor;
import org.eclipse.ceylon.ide.eclipse.code.editor.DynamicMenuItem;

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
                        editorAvailable(editor) 
                            && new RenameRefactoring(editor).getEnabled(), 
                        RENAME),
                new DynamicMenuItem(PLUGIN_ID + ".action.enterAlias", 
                        "Enter Import &Alias...",
                        editorAvailable(editor) 
                            && new EnterAliasRefactoring(editor).getEnabled(), 
                        IMPORT),
                new Separator(),
                new DynamicMenuItem(PLUGIN_ID + ".action.changeParameters", 
                        "Change Parameter &List...",
                        editorAvailable(editor) 
                            && new ChangeParametersRefactoring(editor).getEnabled(),
                        REORDER),
                new Separator(),
                new DynamicMenuItem(PLUGIN_ID + ".action.inline", 
                        "&Inline...",
                        editorAvailable(editor) 
                            && newEclipseInlineRefactoring_.newEclipseInlineRefactoring(editor) != null,
                        COMP_CHANGE),
                new DynamicMenuItem(PLUGIN_ID + ".action.extractValue", 
                        "Extract &Value...",
                        editorAvailable(editor) 
                            && refactorJ2C().newExtractValueRefactoring(editor).getEnabled(),
                        CHANGE),
                new DynamicMenuItem(PLUGIN_ID + ".action.extractFunction", 
                        "Extract &Function...",
                        editorAvailable(editor) 
                            && refactorJ2C().newExtractFunctionRefactoring(editor).getEnabled(),
                        CHANGE),
                new DynamicMenuItem(PLUGIN_ID + ".action.extractInterface", 
                        "Extract Interface...",
                        editorAvailable(editor) 
                            && new ExtractInterfaceRefactoring(editor).getEnabled(),
                        CHANGE),
                new DynamicMenuItem(PLUGIN_ID + ".action.extractParameter", 
                        "Extract &Parameter...",
                        editorAvailable(editor) 
                            && refactorJ2C().newExtractParameterRefactoring(editor).getEnabled(),
                        CHANGE),
                new DynamicMenuItem(PLUGIN_ID + ".action.collectParameters", 
                        "&Collect Parameters...",
                        editorAvailable(editor) 
                            && new CollectParametersRefactoring(editor).getEnabled(),
                        COMP_CHANGE),
                new DynamicMenuItem(PLUGIN_ID + ".action.createAlias", 
                        "Introduce &Type Alias...",
                        editorAvailable(editor) 
                            && new AliasRefactoring(editor).getEnabled(), 
                        COMP_CHANGE),
                new Separator(),
                new DynamicMenuItem(PLUGIN_ID + ".action.invertBoolean", 
                        "Invert &Boolean...",
                        editorAvailable(editor) 
                            && new InvertBooleanRefactoring(editor).getEnabled(),
                       COMP_CHANGE),
                new Separator(),
                new DynamicMenuItem(PLUGIN_ID + ".action.moveOut", 
                        "Move &Out...",
                        editorAvailable(editor) 
                            && new MoveOutRefactoring(editor).getEnabled(),
                        RefactorMenuItems.COMP_CHANGE),
                new DynamicMenuItem(PLUGIN_ID + ".action.makeReceiver", 
                        "Make R&eceiver...",
                        editorAvailable(editor) 
                            && new MakeReceiverRefactoring(editor).getEnabled(),
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

    private static boolean editorAvailable(IEditorPart editor) {
        if (editor instanceof CeylonEditor) {
            CeylonEditor ceylonEditor = (CeylonEditor) editor;
            return ceylonEditor.getParseController()
                    .getTypecheckedRootNode()!=null;
        }
        else {
            return false;
        }
    }
    
}
