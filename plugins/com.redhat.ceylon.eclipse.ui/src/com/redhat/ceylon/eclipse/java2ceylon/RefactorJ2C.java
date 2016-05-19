package com.redhat.ceylon.eclipse.java2ceylon;

import org.eclipse.jface.text.IRegion;
import org.eclipse.ui.IEditorPart;

import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.code.refactor.EclipseExtractFunctionRefactoring;
import com.redhat.ceylon.ide.common.refactoring.ExtractParameterRefactoring;
import com.redhat.ceylon.ide.common.refactoring.ExtractValueRefactoring;
import com.redhat.ceylon.ide.common.refactoring.InlineRefactoring;

public interface RefactorJ2C {

    ExtractValueRefactoring<IRegion> newExtractValueRefactoring(
            IEditorPart editorPart);

    ExtractParameterRefactoring<IRegion> newExtractParameterRefactoring(
            IEditorPart editorPart);

    EclipseExtractFunctionRefactoring newExtractFunctionRefactoring(
            IEditorPart editorPart);

    EclipseExtractFunctionRefactoring newExtractFunctionRefactoring(
            IEditorPart editorPart, Tree.Declaration target);

    InlineRefactoring newInlineRefactoring(
            IEditorPart editorPart);


}