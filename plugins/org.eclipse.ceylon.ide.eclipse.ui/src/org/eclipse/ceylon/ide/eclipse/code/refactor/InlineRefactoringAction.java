package org.eclipse.ceylon.ide.eclipse.code.refactor;

import static org.eclipse.ceylon.ide.eclipse.java2ceylon.Java2CeylonProxies.refactorJ2C;

import org.eclipse.ltk.ui.refactoring.RefactoringWizard;
import org.eclipse.ui.IEditorPart;

public class InlineRefactoringAction extends AbstractRefactoringAction {
    
    public InlineRefactoringAction(IEditorPart editor) {
        super(editor);
    }
    
    @Override
    public Refactoring createRefactoring() {
        return (Refactoring) refactorJ2C().newInlineRefactoring(editor);
    }
    
    @Override
    public RefactoringWizard createWizard(Refactoring refactoring) {
        return new InlineWizard(refactoring);
    }
    
    @Override
    public String message() {
        return "No function or value name selected";
    }

    public String currentName() {
        return ((EclipseInlineRefactoring) refactoring).getDeclaration().getName();
    }

    public boolean isEnabled() {
        return refactoring!=null &&
                refactoring.getEnabled();
    }
}