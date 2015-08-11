package com.redhat.ceylon.eclipse.code.refactor;

import org.eclipse.ui.IEditorPart;

import com.redhat.ceylon.ide.common.refactoring.ExtractValueRefactoring;

public class refactorJ2C {
    public static ExtractValueRefactoring newExtractValueRefactoring(IEditorPart editorPart) {
        return new EclipseExtractValueRefactoring(editorPart);
    }
    public static ExtractLinkedModeEnabled toExtractLinkedModeEnabled(ExtractValueRefactoring refactoring) {
        return (ExtractLinkedModeEnabled) refactoring;
    }
}
