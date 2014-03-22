package com.redhat.ceylon.eclipse.code.refactor;

import static com.redhat.ceylon.eclipse.ui.CeylonPlugin.PLUGIN_ID;

import org.eclipse.ltk.ui.refactoring.RefactoringWizard;
import org.eclipse.ui.IEditorPart;

public class RenameRefactoringAction extends AbstractRefactoringAction {
    public RenameRefactoringAction(IEditorPart editor) {
        super("Rename.", editor);
        setActionDefinitionId(PLUGIN_ID + ".action.rename");
    }
    
    @Override
    public Refactoring createRefactoring() {
        return new RenameRefactoring(getTextEditor());
    }
    
    @Override
    public RefactoringWizard createWizard(Refactoring refactoring) {
        return new RenameWizard((RenameRefactoring) refactoring);
    }
    
    @Override
    public String message() {
        return "No declaration name selected";
    }
    
//    public String currentName() {
//        return ((RenameRefactoring) refactoring).getDeclaration().getName();
//    }
//    
}
