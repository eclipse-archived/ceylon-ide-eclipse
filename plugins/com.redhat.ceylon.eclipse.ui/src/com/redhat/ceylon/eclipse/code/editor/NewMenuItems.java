package com.redhat.ceylon.eclipse.code.editor;


import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.CompoundContributionItem;
import org.eclipse.ui.actions.NewWizardMenu;

public class NewMenuItems extends CompoundContributionItem {
    
    public NewMenuItems() {}
    
    public NewMenuItems(String id) {
        super(id);
    }
    
    @Override
    public IContributionItem[] getContributionItems() {
        String newId = ActionFactory.NEW.getId();
        MenuManager newMenu = new MenuManager("New", newId);
        newMenu.setActionDefinitionId("org.eclipse.ui.file.newQuickMenu");
        newMenu.add(new Separator(newId));
        NewWizardMenu menu = new NewWizardMenu(PlatformUI.getWorkbench().getActiveWorkbenchWindow());
        newMenu.add(menu);
        //TODO: clean up menu
        return new IContributionItem[] { newMenu };
    }
    
}
