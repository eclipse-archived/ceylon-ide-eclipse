package com.redhat.ceylon.eclipse.code.refactor;

import static com.redhat.ceylon.eclipse.code.editor.DynamicMenuItem.collapseMenuItems;
import static com.redhat.ceylon.eclipse.code.editor.Util.getCurrentEditor;

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.actions.CompoundContributionItem;

import com.redhat.ceylon.eclipse.code.editor.DynamicMenuItem;
import com.redhat.ceylon.eclipse.code.imports.CleanImportsHandler;

public class RefactorMenuItems extends CompoundContributionItem {
    
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
                new DynamicMenuItem("com.redhat.ceylon.eclipse.ui.action.rename", "Rename...",
                        editor!=null && new RenameRefactoringAction(editor).isEnabled(), 
                        AbstractRefactoring.RENAME),
                new DynamicMenuItem("com.redhat.ceylon.eclipse.ui.action.moveDeclarationToNewUnit", 
                        "Move to New Unit...", new MoveDeclarationHandler().isEnabled(), 
                        AbstractRefactoring.MOVE),
                new Separator(),
                new DynamicMenuItem("com.redhat.ceylon.eclipse.ui.action.inline", "Inline...",
                        editor!=null && new InlineRefactoringAction(editor).isEnabled(),
                        AbstractRefactoring.COMP_CHANGE),
                new DynamicMenuItem("com.redhat.ceylon.eclipse.ui.action.extractValue", 
                        "Extract Value...",
                        editor!=null && new ExtractValueRefactoringAction(editor).isEnabled(),
                        AbstractRefactoring.CHANGE),
                new DynamicMenuItem("com.redhat.ceylon.eclipse.ui.action.extractFunction", 
                        "Extract Function...",
                        editor!=null && new ExtractFunctionRefactoringAction(editor).isEnabled(),
                        AbstractRefactoring.CHANGE),
                /*new DynamicMenuItem("com.redhat.ceylon.eclipse.ui.action.convertToClass", 
                                "Convert to Class...",
                                editor!=null && new ConvertToClassRefactoringAction(editor).isEnabled()),*/
                new Separator(),
                new DynamicMenuItem("com.redhat.ceylon.eclipse.ui.action.cleanImports", 
                        "Clean Imports", new CleanImportsHandler().isEnabled(), 
                        AbstractRefactoring.DELETE_IMPORT)
            };
    }
    
}
