package com.redhat.ceylon.eclipse.code.open;


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
                new DynamicMenuItem("com.redhat.ceylon.eclipse.ui.action.openSelectedDeclaration", 
                        "Open Selected Declaration",
                        new OpenSelectedDeclarationHandler().isEnabled())
            };
    }
    
}
