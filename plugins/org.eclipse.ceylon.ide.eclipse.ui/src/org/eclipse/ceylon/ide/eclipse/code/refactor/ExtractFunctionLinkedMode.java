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
import static org.eclipse.ceylon.model.typechecker.model.ModelUtil.isTypeUnknown;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.ui.refactoring.RefactoringWizard;
import org.eclipse.swt.widgets.Shell;

import org.eclipse.ceylon.compiler.typechecker.tree.Tree;
import org.eclipse.ceylon.compiler.typechecker.tree.Tree.CompilationUnit;
import org.eclipse.ceylon.ide.eclipse.code.editor.CeylonEditor;
import org.eclipse.ceylon.model.typechecker.model.Type;

public final class ExtractFunctionLinkedMode 
        extends ExtractLinkedMode {
        
    private final EclipseExtractFunctionRefactoring refactoring;
    
    public ExtractFunctionLinkedMode(CeylonEditor editor) {
        super(editor);
        this.refactoring = 
                refactorJ2C()
                    .newExtractFunctionRefactoring(editor);
    }
    
    public ExtractFunctionLinkedMode(CeylonEditor editor, Tree.Declaration target) {
        super(editor);
        this.refactoring = 
                refactorJ2C()
                    .newExtractFunctionRefactoring(editor, target);
    }
    
    @Override
    protected int performInitialChange(IDocument document) {
        try {
            NullProgressMonitor pm = new NullProgressMonitor();
            refactoring.createChange(pm).perform(pm);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
    	return refactoring.getNameProposals();
    }
    
    @Override
    protected void addLinkedPositions(IDocument document,
            CompilationUnit rootNode, int adjust) {
        
        addNamePosition(document, 
                refactoring.getRefRegion().getOffset(),
                refactoring.getRefRegion().getLength(),
                refactoring.getDupeRegions());
        
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
        try {
            return refactoring.forceWizardMode() ||
                    //yew, truly terrible hack!!
                    ((Refactoring) refactoring)
                        .createChange(null) 
                            instanceof CompositeChange;
        } catch (Exception e) {
            return false;
        }
    }
    
    @Override
    protected String getActionName() {
        return PLUGIN_ID + ".action.extractFunction";
    }
    
    @Override
    protected void openPreview() {
        new ExtractFunctionRefactoringAction(editor) {
            @Override
            public Refactoring createRefactoring() {
                return (Refactoring) 
                    ExtractFunctionLinkedMode.this.refactoring;
            }
            @Override
            public RefactoringWizard createWizard(Refactoring refactoring) {
                return new ExtractFunctionWizard(refactoring) {
                    @Override
                    protected void addUserInputPages() {}
                };
            }
        }.run();
    }

    @Override
    protected void openDialog() {
        new ExtractFunctionRefactoringAction(editor) {
            @Override
            public Refactoring createRefactoring() {
                return (Refactoring) 
                    ExtractFunctionLinkedMode.this.refactoring;
            }
        }.run();
    }
    
    @Override
    public boolean canBeInferred() {
        return refactoring.canBeInferred();
    }
    
    @Override
    protected String getKind() {
        return "function";
    }

    public static void selectExpressionAndStart(
            final CeylonEditor editor) {
        final Shell shell = editor.getSite().getShell();
        if (editor.getSelection().getLength()>0) {
            new SelectContainerPopup(shell, 0, editor,
                    "Extract Function To") {
                @Override void finish() {
                    new ExtractFunctionLinkedMode(editor, getResult()).start();
                }
                @Override boolean isEnabled() {
                    return new refactorJ2C().newExtractFunctionRefactoring(editor).getEnabled();
                }
            }
            .open();
        }
        else {
            new SelectExpressionPopup(shell, 0, editor,
                    "Extract Function") {
                @Override void finish() {
                    new SelectContainerPopup(shell, 0, editor,
                            "Extract Function To") {
                        @Override void finish() {
                            new ExtractFunctionLinkedMode(editor, getResult()).start();
                        }
                        @Override boolean isEnabled() {
                            return new refactorJ2C().newExtractFunctionRefactoring(editor).getEnabled();
                        }
                    }
                    .open();
                }
            }
            .open();
        }
    }

    @Override
    protected void setReturnType(Type type) {
        // TODO this.refactoring.setType(type);
        this.refactoring.setExplicitType();
    }

}
