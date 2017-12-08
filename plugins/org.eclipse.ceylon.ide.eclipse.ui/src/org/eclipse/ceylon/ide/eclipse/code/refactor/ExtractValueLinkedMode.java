/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 1.0 which is available at
 * http://www.eclipse.org/legal/epl-v10.html.
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
import org.eclipse.ceylon.ide.common.refactoring.ExtractValueRefactoring;
import org.eclipse.ceylon.model.typechecker.model.Type;

public final class ExtractValueLinkedMode 
        extends ExtractLinkedMode {
        
    private final ExtractValueRefactoring<IRegion> refactoring;
    
    public ExtractValueLinkedMode(CeylonEditor editor) {
        super(editor);
        this.refactoring = 
                refactorJ2C()
                    .newExtractValueRefactoring(editor);
    }
    
    @Override
    protected int performInitialChange(IDocument document) {
        org.eclipse.ceylon.ide.common.platform.TextChange change
            = new platformJ2C().newChange("Extract Value", 
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
        return refactoring.getRefRegion() != null
                ? refactoring.getRefRegion().getOffset()
                : -1;
    }
    
    @Override
    protected String[] getNameProposals() {
    	return toJavaStringArray(refactoring.getNameProposals());
    }
    
    @Override
    protected void addLinkedPositions(IDocument document,
            CompilationUnit rootNode, int adjust) {
        
        if (refactoring.getRefRegion() != null) {
            addNamePosition(document, 
                    refactoring.getRefRegion().getOffset(),
                    refactoring.getRefRegion().getLength(),
                    refactoring.getDupeRegions());
        } else {
            addNamePosition(document, 
                    refactoring.getDecRegion().getOffset(),
                    refactoring.getDecRegion().getLength(),
                    refactoring.getDupeRegions());
        }
        
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
        return PLUGIN_ID + ".action.extractValue";
    }
    
    @Override
    protected void openPreview() {
        new RenameRefactoringAction(editor) {
            @Override
            public Refactoring createRefactoring() {
                return (Refactoring) 
                    ExtractValueLinkedMode.this.refactoring;
            }
            @Override
            public RefactoringWizard createWizard(Refactoring refactoring) {
                return new ExtractValueWizard(refactoring) {
                    @Override
                    protected void addUserInputPages() {}
                };
            }
        }.run();
    }

    @Override
    protected void openDialog() {
        new ExtractValueRefactoringAction(editor) {
            @Override
            public Refactoring createRefactoring() {
                return (Refactoring) 
                    ExtractValueLinkedMode.this.refactoring;
            }
        }.run();
    }
    
    @Override
    public boolean canBeInferred() {
        return refactoring.getCanBeInferred();
    }
    
    @Override
    protected String getKind() {
        return refactoring.getExtractsFunction() ? "function" : "value";
    }

    public static void selectExpressionAndStart(
            final CeylonEditor editor) {
        if (editor.getSelection().getLength()>0) {
            new ExtractValueLinkedMode(editor).start();
        }
        else {
            Shell shell = editor.getSite().getShell();
            new SelectExpressionPopup(shell, 0, editor,
                    "Extract Value") {
                @Override void finish() {
                    new ExtractValueLinkedMode(editor).start();
                }
            }
            .open();
        }
    }
    
    @Override
    protected void setReturnType(Type type) {
        this.refactoring.setType(type);
        this.refactoring.setExplicitType(type!=null);
    }

}
