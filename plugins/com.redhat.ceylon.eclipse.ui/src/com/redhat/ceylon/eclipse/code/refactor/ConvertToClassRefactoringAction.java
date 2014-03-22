package com.redhat.ceylon.eclipse.code.refactor;

import static com.redhat.ceylon.eclipse.ui.CeylonPlugin.PLUGIN_ID;

import org.eclipse.ltk.ui.refactoring.RefactoringWizard;
import org.eclipse.ui.IEditorPart;

public class ConvertToClassRefactoringAction extends AbstractRefactoringAction {
    public ConvertToClassRefactoringAction(IEditorPart editor) {
        super("ConvertToClass.", editor);
        setActionDefinitionId(PLUGIN_ID + ".action.convertToClass");
    }
    
    @Override
    public Refactoring createRefactoring() {
        return new ConvertToClassRefactoring(getTextEditor());
    }
    
    @Override
    public RefactoringWizard createWizard(Refactoring refactoring) {
        return new ConvertToClassWizard((ConvertToClassRefactoring) refactoring);
    }
    
    @Override
    public String message() {
        return "No declaration name selected";
    }

    public String currentName() {
        return ((ConvertToClassRefactoring) refactoring).getDeclaration().getName();
    }
    
    public boolean isShared() {
        return ((ConvertToClassRefactoring) refactoring).getDeclaration().isShared();
    }
    
}
