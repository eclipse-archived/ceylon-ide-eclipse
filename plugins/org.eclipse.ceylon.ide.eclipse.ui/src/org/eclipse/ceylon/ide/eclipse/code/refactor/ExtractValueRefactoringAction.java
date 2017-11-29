/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.code.refactor;

import org.eclipse.ltk.ui.refactoring.RefactoringWizard;
import org.eclipse.ui.IEditorPart;

import static org.eclipse.ceylon.ide.eclipse.java2ceylon.Java2CeylonProxies.refactorJ2C;


public class ExtractValueRefactoringAction extends AbstractRefactoringAction {
    
    public ExtractValueRefactoringAction(IEditorPart editor) {
        super(editor);
    }
    
    @Override
    public Refactoring createRefactoring() {
        return (Refactoring) refactorJ2C().newExtractValueRefactoring(editor);
    }
    
    @Override
    public RefactoringWizard createWizard(Refactoring refactoring) {
        return new ExtractValueWizard(refactoring);
    }
    
    @Override
    public String message() {
        return "No expression selected";
    }

}
