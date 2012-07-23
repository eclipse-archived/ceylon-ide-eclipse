package com.redhat.ceylon.eclipse.code.refactor;

import static com.redhat.ceylon.eclipse.code.editor.DynamicMenuItem.collapseMenuItems;
import static com.redhat.ceylon.eclipse.code.editor.Util.getCurrentEditor;
import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.imageRegistry;
import static com.redhat.ceylon.eclipse.ui.CeylonPlugin.PLUGIN_ID;
import static com.redhat.ceylon.eclipse.ui.CeylonResources.CEYLON_CHANGE;
import static com.redhat.ceylon.eclipse.ui.CeylonResources.CEYLON_COMPOSITE_CHANGE;
import static com.redhat.ceylon.eclipse.ui.CeylonResources.CEYLON_DELETE_IMPORT;
import static com.redhat.ceylon.eclipse.ui.CeylonResources.CEYLON_MOVE;
import static com.redhat.ceylon.eclipse.ui.CeylonResources.CEYLON_RENAME;

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.actions.CompoundContributionItem;

import com.redhat.ceylon.eclipse.code.editor.DynamicMenuItem;
import com.redhat.ceylon.eclipse.code.imports.CleanImportsHandler;

public class RefactorMenuItems extends CompoundContributionItem {
    public static ImageDescriptor CHANGE = imageRegistry.getDescriptor(CEYLON_CHANGE);
    public static ImageDescriptor COMP_CHANGE = imageRegistry.getDescriptor(CEYLON_COMPOSITE_CHANGE);
    public static ImageDescriptor MOVE = imageRegistry.getDescriptor(CEYLON_MOVE);
    public static ImageDescriptor RENAME = imageRegistry.getDescriptor(CEYLON_RENAME);
    public static ImageDescriptor DELETE_IMPORT = imageRegistry.getDescriptor(CEYLON_DELETE_IMPORT);

    public RefactorMenuItems() {}
    
    public RefactorMenuItems(String id) {
        super(id);
    }
    
    @Override
    protected IContributionItem[] getContributionItems() {
        IContributionItem[] items = getItems(getCurrentEditor());
        if (collapseMenuItems(getParent())) {
            MenuManager submenu = new MenuManager("Refactor");
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
                //new Separator(),
                new DynamicMenuItem(PLUGIN_ID + ".action.rename", "Rename...",
                        editor!=null && new RenameRefactoringAction(editor).isEnabled(), 
                        RENAME),
                new DynamicMenuItem(PLUGIN_ID + ".action.moveDeclarationToNewUnit", 
                        "Move to New Unit...", new MoveDeclarationHandler().isEnabled(), 
                        MOVE),
                new Separator(),
                new DynamicMenuItem(PLUGIN_ID + ".action.inline", "Inline...",
                        editor!=null && new InlineRefactoringAction(editor).isEnabled(),
                        COMP_CHANGE),
                new DynamicMenuItem(PLUGIN_ID + ".action.extractValue", 
                        "Extract Value...",
                        editor!=null && new ExtractValueRefactoringAction(editor).isEnabled(),
                        CHANGE),
                new DynamicMenuItem(PLUGIN_ID + ".action.extractFunction", 
                        "Extract Function...",
                        editor!=null && new ExtractFunctionRefactoringAction(editor).isEnabled(),
                        CHANGE),
                /*new DynamicMenuItem(PLUGIN_ID + ".action.convertToClass", 
                                "Convert to Class...",
                                editor!=null && new ConvertToClassRefactoringAction(editor).isEnabled()),*/
                new Separator(),
                new DynamicMenuItem(PLUGIN_ID + ".action.cleanImports", 
                        "Clean Imports", new CleanImportsHandler().isEnabled(), 
                        DELETE_IMPORT)
            };
    }
    
}
