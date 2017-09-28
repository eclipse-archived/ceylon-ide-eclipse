package org.eclipse.ceylon.ide.eclipse.code.refactor;

import static org.eclipse.ceylon.ide.eclipse.java2ceylon.Java2CeylonProxies.refactorJ2C;

import org.eclipse.ltk.ui.refactoring.RefactoringWizard;
import org.eclipse.ui.IEditorPart;

public class ExtractParameterRefactoringAction extends AbstractRefactoringAction {
    
    public ExtractParameterRefactoringAction(IEditorPart editor) {
        super(editor);
    }
    
    @Override
    public Refactoring createRefactoring() {
        return (Refactoring) refactorJ2C().newExtractParameterRefactoring(editor);
    }
    
    @Override
    public RefactoringWizard createWizard(Refactoring refactoring) {
        return new ExtractParameterWizard(refactoring);
    }
    
    @Override
    public String message() {
        return "No expression selected";
    }

}
