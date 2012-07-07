package com.redhat.ceylon.eclipse.code.open;


import static com.redhat.ceylon.eclipse.ui.CeylonPlugin.PLUGIN_ID;

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.ui.actions.CompoundContributionItem;

import com.redhat.ceylon.eclipse.code.editor.DynamicMenuItem;

public class OpenSelectedDeclarationMenuItem extends CompoundContributionItem {
    
    public OpenSelectedDeclarationMenuItem() {}
    
    public OpenSelectedDeclarationMenuItem(String id) {
        super(id);
    }
    
    @Override
    protected IContributionItem[] getContributionItems() {
        return new IContributionItem[] {
                new DynamicMenuItem(PLUGIN_ID + ".action.openSelectedDeclaration", 
                        "Open Selected Declaration",
                        new OpenSelectedDeclarationHandler().isEnabled())
            };
    }
    
}
