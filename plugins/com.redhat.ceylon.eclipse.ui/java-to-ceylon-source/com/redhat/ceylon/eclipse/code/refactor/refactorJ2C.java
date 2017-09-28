package org.eclipse.ceylon.ide.eclipse.code.refactor;

import org.eclipse.jface.text.IRegion;
import org.eclipse.ui.IEditorPart;

import org.eclipse.ceylon.compiler.typechecker.tree.Tree;
import org.eclipse.ceylon.ide.eclipse.code.editor.CeylonEditor;
import org.eclipse.ceylon.ide.eclipse.java2ceylon.RefactorJ2C;
import org.eclipse.ceylon.ide.common.refactoring.ExtractParameterRefactoring;
import org.eclipse.ceylon.ide.common.refactoring.ExtractValueRefactoring;
import org.eclipse.ceylon.ide.common.refactoring.InlineRefactoring;

public class refactorJ2C implements RefactorJ2C {
    @Override
    public ExtractValueRefactoring<IRegion> 
    newExtractValueRefactoring(IEditorPart editorPart) {
        return new EclipseExtractValueRefactoring((CeylonEditor) editorPart);
    }
    @Override
    public ExtractParameterRefactoring<IRegion> 
    newExtractParameterRefactoring(IEditorPart editorPart) {
        return new EclipseExtractParameterRefactoring((CeylonEditor) editorPart);
    }
    @Override
    public EclipseExtractFunctionRefactoring 
    newExtractFunctionRefactoring(IEditorPart editorPart) {
        return new EclipseExtractFunctionRefactoring((CeylonEditor) editorPart);
    }
    @Override
    public EclipseExtractFunctionRefactoring 
    newExtractFunctionRefactoring(IEditorPart editorPart, Tree.Declaration target) {
        return new EclipseExtractFunctionRefactoring((CeylonEditor) editorPart, target);
    }
    @Override
    public InlineRefactoring
    newInlineRefactoring(IEditorPart editorPart) {
        return newEclipseInlineRefactoring_.newEclipseInlineRefactoring((CeylonEditor) editorPart);
    }
}
