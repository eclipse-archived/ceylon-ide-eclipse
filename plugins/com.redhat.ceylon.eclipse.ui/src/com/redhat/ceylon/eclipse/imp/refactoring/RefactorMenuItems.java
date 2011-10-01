package com.redhat.ceylon.eclipse.imp.refactoring;

import static com.redhat.ceylon.eclipse.imp.editor.Util.getCurrentEditor;

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.actions.CompoundContributionItem;

import com.redhat.ceylon.eclipse.imp.editor.DynamicMenuItem;

public class RefactorMenuItems extends CompoundContributionItem {
    
    public RefactorMenuItems() {}
    
    public RefactorMenuItems(String id) {
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
                    new DynamicMenuItem("com.redhat.ceylon.eclipse.ui.action.rename", "Rename...",
                            editor!=null && new RenameRefactoringAction(editor).isEnabled()),
                    new Separator(),
                    new DynamicMenuItem("com.redhat.ceylon.eclipse.ui.action.inline", "Inline...",
                            editor!=null && new InlineRefactoringAction(editor).isEnabled()),
                    new DynamicMenuItem("com.redhat.ceylon.eclipse.ui.action.extractValue", 
                            "Extract Value...",
                            editor!=null && new ExtractValueRefactoringAction(editor).isEnabled()),
                    new DynamicMenuItem("com.redhat.ceylon.eclipse.ui.action.extractFunction", 
                            "Extract Function...",
                            editor!=null && new ExtractFunctionRefactoringAction(editor).isEnabled()),
                    new Separator(),
                    new DynamicMenuItem("com.redhat.ceylon.eclipse.ui.action.convertToNamedArguments", 
                            "Convert to Named Arguments...",
                            editor!=null && new ConvertToNamedArgumentsRefactoringAction(editor).isEnabled()),
                };
        /*}
        else {
            return new IContributionItem[0];
        }*/
    }
    
}
