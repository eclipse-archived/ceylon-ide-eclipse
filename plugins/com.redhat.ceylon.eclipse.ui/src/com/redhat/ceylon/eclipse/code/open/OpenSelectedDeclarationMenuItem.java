package com.redhat.ceylon.eclipse.code.open;


import static com.redhat.ceylon.eclipse.ui.CeylonPlugin.PLUGIN_ID;
import static com.redhat.ceylon.eclipse.ui.ICeylonResources.GOTO;

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.actions.CompoundContributionItem;

import com.redhat.ceylon.eclipse.code.editor.DynamicMenuItem;
import com.redhat.ceylon.eclipse.ui.CeylonPlugin;

public class OpenSelectedDeclarationMenuItem extends CompoundContributionItem {
    
    private static final ImageDescriptor GOTO_IMAGE = CeylonPlugin.getInstance()
    		.getImageRegistry().getDescriptor(GOTO);

	public OpenSelectedDeclarationMenuItem() {}
    
    public OpenSelectedDeclarationMenuItem(String id) {
        super(id);
    }
    
    @Override
    protected IContributionItem[] getContributionItems() {
        return new IContributionItem[] {
                new DynamicMenuItem(PLUGIN_ID + ".action.openSelectedDeclaration", 
                        "Go To Selected Declaration",
                        new OpenSelectedDeclarationHandler().isEnabled(), 
                        GOTO_IMAGE)
            };
    }
    
}
