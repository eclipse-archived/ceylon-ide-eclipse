package com.redhat.ceylon.eclipse.code.refactor;

import org.eclipse.ltk.ui.refactoring.RefactoringWizard;
import org.eclipse.ui.IEditorPart;

import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;

public class InlineRefactoringAction extends AbstractRefactoringAction {
    
    public InlineRefactoringAction(IEditorPart editor) {
        super(editor);
    }
    
    @Override
    public Refactoring createRefactoring() {
        return new EclipseInlineRefactoring((CeylonEditor) editor).init();
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
        return refactoring.getEnabled();
    }
}