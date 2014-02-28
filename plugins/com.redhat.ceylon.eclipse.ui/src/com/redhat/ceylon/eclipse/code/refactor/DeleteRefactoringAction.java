package com.redhat.ceylon.eclipse.code.refactor;

import static com.redhat.ceylon.eclipse.ui.CeylonPlugin.PLUGIN_ID;

import org.eclipse.ltk.ui.refactoring.RefactoringWizard;
import org.eclipse.ui.IEditorPart;

public class DeleteRefactoringAction extends AbstractRefactoringAction {
    public DeleteRefactoringAction(IEditorPart editor) {
        super("Delete.", editor);
        setActionDefinitionId(PLUGIN_ID + ".action.delete");
    }
    
    @Override
    public AbstractRefactoring createRefactoring() {
        return new DeleteRefactoring(getTextEditor());
    }
    
    @Override
    public RefactoringWizard createWizard(AbstractRefactoring refactoring) {
        return new DeleteWizard((AbstractRefactoring) refactoring);
    }
    
    @Override
    String message() {
        return "No declaration name selected";
    }
    
//    public String currentName() {
//        return ((RenameRefactoring) refactoring).getDeclaration().getName();
//    }
//    
}
