/********************************************************************************
 * Copyright (c) {date} Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.java2ceylon;

import org.eclipse.jface.text.IRegion;
import org.eclipse.ui.IEditorPart;

import org.eclipse.ceylon.compiler.typechecker.tree.Tree;
import org.eclipse.ceylon.ide.eclipse.code.refactor.EclipseExtractFunctionRefactoring;
import org.eclipse.ceylon.ide.common.refactoring.ExtractParameterRefactoring;
import org.eclipse.ceylon.ide.common.refactoring.ExtractValueRefactoring;
import org.eclipse.ceylon.ide.common.refactoring.InlineRefactoring;

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