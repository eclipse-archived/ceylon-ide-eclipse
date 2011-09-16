package com.redhat.ceylon.eclipse.imp.editor;

import static com.redhat.ceylon.eclipse.util.Util.getCurrentEditor;

import org.eclipse.imp.editor.UniversalEditor;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.actions.CompoundContributionItem;

import com.redhat.ceylon.eclipse.imp.refactoring.ConvertToNamedArgumentsRefactoringAction;
import com.redhat.ceylon.eclipse.imp.refactoring.ExtractFunctionRefactoringAction;
import com.redhat.ceylon.eclipse.imp.refactoring.ExtractValueRefactoringAction;
import com.redhat.ceylon.eclipse.imp.refactoring.InlineRefactoringAction;
import com.redhat.ceylon.eclipse.imp.refactoring.RenameRefactoringAction;

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
                            new RenameRefactoringAction(editor).isEnabled()),
                    new Separator(),
                    new DynamicMenuItem("com.redhat.ceylon.eclipse.ui.action.inline", "Inline...",
                            new InlineRefactoringAction(editor).isEnabled()),
                    new DynamicMenuItem("com.redhat.ceylon.eclipse.ui.action.extractValue", 
                            "Extract Value...",
                            new ExtractValueRefactoringAction(editor).isEnabled()),
                    new DynamicMenuItem("com.redhat.ceylon.eclipse.ui.action.extractFunction", 
                            "Extract Function...",
                            new ExtractFunctionRefactoringAction(editor).isEnabled()),
                    new Separator(),
                    new DynamicMenuItem("com.redhat.ceylon.eclipse.ui.action.convertToNamedArguments", 
                            "Convert To Named Arguments...",
                            new ConvertToNamedArgumentsRefactoringAction(editor).isEnabled()),
                };
        /*}
        else {
            return new IContributionItem[0];
        }*/
    }
    
}
