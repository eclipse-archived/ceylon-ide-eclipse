/********************************************************************************
 * Copyright (c) {date} Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.code.refactor;

import static org.eclipse.ceylon.ide.eclipse.java2ceylon.Java2CeylonProxies.refactorJ2C;
import static org.eclipse.ceylon.ide.eclipse.ui.CeylonPlugin.PLUGIN_ID;
import static org.eclipse.ceylon.ide.eclipse.util.CeylonHelper.toJavaStringArray;
import static org.eclipse.ceylon.model.typechecker.model.ModelUtil.isTypeUnknown;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.ltk.ui.refactoring.RefactoringWizard;
import org.eclipse.swt.widgets.Shell;

import org.eclipse.ceylon.compiler.typechecker.tree.Tree.CompilationUnit;
import org.eclipse.ceylon.ide.eclipse.code.correct.correctJ2C;
import org.eclipse.ceylon.ide.eclipse.code.editor.CeylonEditor;
import org.eclipse.ceylon.ide.eclipse.platform.platformJ2C;
import org.eclipse.ceylon.ide.common.refactoring.ExtractParameterRefactoring;
import org.eclipse.ceylon.model.typechecker.model.Type;

public final class ExtractParameterLinkedMode 
        extends ExtractLinkedMode {
        
    private final ExtractParameterRefactoring<IRegion> refactoring;
    
    public ExtractParameterLinkedMode(CeylonEditor editor) {
        super(editor);
        this.refactoring = refactorJ2C().newExtractParameterRefactoring(editor);
    }
    
    @Override
    protected int performInitialChange(IDocument document) {
        org.eclipse.ceylon.ide.common.platform.TextChange change
            = new platformJ2C().newChange("Extract Parameter", 
                        new correctJ2C().newDocument(document));
        refactoring.build(change);
        change.apply();
        return 0;
    }
    
    @Override
    protected boolean canStart() {
        return refactoring.getEnabled();
    }
    
    @Override
    protected int getNameOffset() {
        return refactoring.getDecRegion().getOffset();
    }
    
    @Override
    protected int getTypeOffset() {
        return refactoring.getTypeRegion().getOffset();
    }
    
    @Override
    protected int getExitPosition(int selectionOffset, int adjust) {
        return refactoring.getRefRegion().getOffset();
    }
    
    @Override
    protected String[] getNameProposals() {
    	return toJavaStringArray(refactoring.getNameProposals());
    }
    
    @Override
    protected void addLinkedPositions(IDocument document,
            CompilationUnit rootNode, int adjust) {
        
        addNamePosition(document, 
                refactoring.getRefRegion().getOffset(),
                refactoring.getRefRegion().getLength());
        
        Type type = refactoring.getType();
        if (!isTypeUnknown(type)) {
            addTypePosition(document, type, 
                    refactoring.getTypeRegion().getOffset(), 
                    refactoring.getTypeRegion().getLength());
        }
        
    }
    
    @Override
    protected String getName() {
        return refactoring.getNewName();
    }
    
    @Override
    protected void setName(String name) {
        refactoring.setNewName(name);
    }
    
    @Override
    protected boolean forceWizardMode() {
        return refactoring.getForceWizardMode();
    }
    
    @Override
    protected String getActionName() {
        return PLUGIN_ID + ".action.extractParameter";
    }
    
    @Override
    protected void openPreview() {
        new RenameRefactoringAction(editor) {
            @Override
            public Refactoring createRefactoring() {
                return (Refactoring) ExtractParameterLinkedMode.this.refactoring;
            }
            @Override
            public RefactoringWizard createWizard(Refactoring refactoring) {
                return new ExtractParameterWizard(refactoring) {
                    @Override
                    protected void addUserInputPages() {}
                };
            }
        }.run();
    }

    @Override
    protected void openDialog() {
        new ExtractParameterRefactoringAction(editor) {
            @Override
            public Refactoring createRefactoring() {
                return (Refactoring) ExtractParameterLinkedMode.this.refactoring;
            }
        }.run();
    }
    
    @Override
    protected String getKind() {
        return "parameter";
    }

    public static void selectExpressionAndStart(
            final CeylonEditor editor) {
        if (editor.getSelection().getLength()>0) {
            new ExtractParameterLinkedMode(editor).start();
        }
        else {
            Shell shell = editor.getSite().getShell();
            new SelectExpressionPopup(shell, 0, editor,
                    "Extract Parameter") {
                @Override void finish() {
                    new ExtractParameterLinkedMode(editor).start();
                }
            }
            .open();
        }
    }
    
    @Override
    protected void setReturnType(Type type) {
        this.refactoring.setType(type);
    }

}
