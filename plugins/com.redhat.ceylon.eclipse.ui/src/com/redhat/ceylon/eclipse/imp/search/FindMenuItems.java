package com.redhat.ceylon.eclipse.imp.search;


import static com.redhat.ceylon.eclipse.imp.editor.Util.getCurrentEditor;

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.actions.CompoundContributionItem;

import com.redhat.ceylon.eclipse.imp.editor.DynamicMenuItem;

public class FindMenuItems extends CompoundContributionItem {
    
    public FindMenuItems() {}
    
    public FindMenuItems(String id) {
        super(id);
    }
    
    @Override
    protected IContributionItem[] getContributionItems() {
        /*IEditorPart editor = getCurrentEditor();
        if (editor instanceof UniversalEditor) {
            UniversalEditor universalEditor = (UniversalEditor) editor;*/
            IEditorPart editor = getCurrentEditor();
            return new IContributionItem[] {
                    //new Separator(),
                    new DynamicMenuItem("com.redhat.ceylon.eclipse.ui.action.findReferences", "Find References",
                            new FindReferencesAction(editor).isEnabled()),
                    new DynamicMenuItem("com.redhat.ceylon.eclipse.ui.action.findRefinements", "Find Refinements",
                            new FindRefinementsAction(editor).isEnabled()),
                    new DynamicMenuItem("com.redhat.ceylon.eclipse.ui.action.findSubtypes", "Find Subtypes",
                            new FindSubtypesAction(editor).isEnabled()),
                    new Separator()
                };
        /*}
        else {
            return new IContributionItem[0];
        }*/
    }
    
}
