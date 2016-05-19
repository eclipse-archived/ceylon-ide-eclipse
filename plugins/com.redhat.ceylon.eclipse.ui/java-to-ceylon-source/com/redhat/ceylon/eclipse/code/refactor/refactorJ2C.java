package com.redhat.ceylon.eclipse.code.refactor;

import org.eclipse.jface.text.IRegion;
import org.eclipse.ui.IEditorPart;

import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.java2ceylon.RefactorJ2C;
import com.redhat.ceylon.ide.common.refactoring.ExtractParameterRefactoring;
import com.redhat.ceylon.ide.common.refactoring.ExtractValueRefactoring;
import com.redhat.ceylon.ide.common.refactoring.InlineRefactoring;

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
