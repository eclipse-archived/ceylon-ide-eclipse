package com.redhat.ceylon.eclipse.code.refactor;

import static com.redhat.ceylon.eclipse.code.editor.DynamicMenuItem.collapseMenuItems;
import static com.redhat.ceylon.eclipse.code.editor.EditorUtil.getCurrentEditor;
import static com.redhat.ceylon.eclipse.ui.CeylonPlugin.PLUGIN_ID;

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.actions.CompoundContributionItem;

import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.code.editor.DynamicMenuItem;

public class RefactorMenuItems extends CompoundContributionItem {
    
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
                //new Separator(),
                new DynamicMenuItem(PLUGIN_ID + ".action.rename", "Re&name...",
                        editor!=null && new RenameRefactoringAction(editor).isEnabled(), 
                        AbstractRefactoring.RENAME),
                new DynamicMenuItem(PLUGIN_ID + ".action.changeParameters", "Change &Parameters...",
                        editor!=null && new ChangeParametersRefactoringAction(editor).isEnabled(),
                        AbstractRefactoring.REORDER),
                new Separator(),
                new DynamicMenuItem(PLUGIN_ID + ".action.inline", "Inlin&e...",
                        editor!=null && new InlineRefactoringAction(editor).isEnabled(),
                        AbstractRefactoring.COMP_CHANGE),
                new DynamicMenuItem(PLUGIN_ID + ".action.extractValue", 
                        "Extract &Value...",
                        editor!=null && new ExtractValueRefactoringAction(editor).isEnabled(),
                        AbstractRefactoring.CHANGE),
                new DynamicMenuItem(PLUGIN_ID + ".action.extractFunction", 
                        "Extract &Function...",
                        editor!=null && new ExtractFunctionRefactoringAction(editor).isEnabled(),
                        AbstractRefactoring.CHANGE),
                new DynamicMenuItem(PLUGIN_ID + ".action.extractParameter", 
                        "Extract Parameter...",
                        editor!=null && new ExtractParameterRefactoringAction(editor).isEnabled(),
                        AbstractRefactoring.CHANGE),
                new DynamicMenuItem(PLUGIN_ID + ".action.collectParameters", "Collect Parameters...",
                        editor!=null && new CollectParametersRefactoringAction(editor).isEnabled(),
                        AbstractRefactoring.COMP_CHANGE),
                new Separator(),
                new DynamicMenuItem(PLUGIN_ID + ".action.moveOut", 
                        "Move &Out...",
                        editor!=null && new MoveOutRefactoringAction(editor).isEnabled(),
                        AbstractRefactoring.CHANGE),
                new DynamicMenuItem(PLUGIN_ID + ".action.makeReceiver", 
                        "&Make Receiver...",
                        editor!=null && new MakeReceiverRefactoringAction(editor).isEnabled(),
                        AbstractRefactoring.CHANGE),
                new Separator(),
                new DynamicMenuItem(PLUGIN_ID + ".action.moveDeclarationToNewUnit", 
                        "Move to New Source File...", new MoveToNewUnitHandler().isEnabled(), 
                        AbstractRefactoring.MOVE),
                new DynamicMenuItem(PLUGIN_ID + ".action.moveDeclarationToUnit", 
                        "Move to Source File...", new MoveToUnitHandler().isEnabled(), 
                        AbstractRefactoring.MOVE),
            };
    }
    
}
