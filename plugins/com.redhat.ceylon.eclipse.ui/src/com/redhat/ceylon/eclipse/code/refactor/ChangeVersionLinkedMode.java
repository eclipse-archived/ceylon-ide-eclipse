package com.redhat.ceylon.eclipse.code.refactor;

/*******************************************************************************
 * Copyright (c) 2005, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

import static com.redhat.ceylon.eclipse.ui.CeylonPlugin.PLUGIN_ID;

import java.util.List;

import org.eclipse.jdt.internal.ui.refactoring.RefactoringExecutionHelper;
import org.eclipse.jdt.ui.refactoring.RefactoringSaveHelper;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.link.LinkedPosition;
import org.eclipse.jface.text.link.LinkedPositionGroup;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.ui.refactoring.RefactoringWizard;

import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.Identifier;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.ImportModule;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;


public class ChangeVersionLinkedMode extends RefactorLinkedMode {

    private final Tree.ImportPath module;
    private final Tree.QuotedLiteral version;
    
    private final ChangeVersionRefactoring refactoring;
    protected LinkedPosition versionPosition;
    protected LinkedPositionGroup linkedPositionGroup;

    private final class LinkedPositionsVisitor 
            extends Visitor {
        private final int adjust;
        private final IDocument document;
        private final LinkedPositionGroup linkedPositionGroup;
        int i=1;

        private LinkedPositionsVisitor(int adjust, IDocument document,
                LinkedPositionGroup linkedPositionGroup) {
            this.adjust = adjust;
            this.document = document;
            this.linkedPositionGroup = linkedPositionGroup;
        }

        @Override
        public void visit(ImportModule that) {
            super.visit(that);
            addLinkedPosition(document, that.getVersion(), 
                    that.getImportPath());
        }
        
        boolean eq(Tree.ImportPath x, Tree.ImportPath y) {
            List<Identifier> xids = x.getIdentifiers();
            List<Identifier> yids = y.getIdentifiers();
            if (xids.size()!=yids.size()) {
                return false;
            }
            for (int i=0; i<xids.size(); i++) {
                if (!xids.get(0).equals(yids.get(i))) {
                    return false;
                }
            }
            return true;
        }
        
        private void addLinkedPosition(final IDocument document,
                Tree.QuotedLiteral version, Tree.ImportPath path) {
            if (version!=null && path!=null && eq(module, path)) {
                try {
                    int pos = version.getStartIndex()+adjust+1;
                    int len = version.getText().length()-2;
                    linkedPositionGroup.addPosition(new LinkedPosition(document, 
                            pos, len, i++));
                }
                catch (BadLocationException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public ChangeVersionLinkedMode(Tree.QuotedLiteral version, 
            Tree.ImportPath module, CeylonEditor editor) {
        super(editor);
        this.module = module;
        this.version = version;
        this.refactoring = new ChangeVersionRefactoring(editor);
    }

    @Override
    protected String getName() {
        String quoted = version.getText();
        return quoted.substring(1, quoted.length()-1);
    }
    
    @Override
    protected void setName(String version) {
        refactoring.setNewVersion(version);
    }
    
    @Override
    protected String getActionName() {
        return PLUGIN_ID + ".action.changeVersion";
    }

    @Override
    public String getHintTemplate() {
        return "Enter new version for " + refactoring.getCount() + 
                " occurrences of \"" + getName() + "\" {0}";
    }
    
    private void addLinkedPositions(final IDocument document, Tree.CompilationUnit rootNode, 
            final int adjust, final LinkedPositionGroup linkedPositionGroup) 
                    throws BadLocationException {
        versionPosition = new LinkedPosition(document, 
                version.getStartIndex()+1, 
                getInitialName().length(), 0);
        linkedPositionGroup.addPosition(versionPosition);
        rootNode.visit(new LinkedPositionsVisitor(adjust, document, linkedPositionGroup));
    }
    
    private boolean isEnabled() {
        return !getNewNameFromNamePosition().isEmpty();
    }
    
    @Override
    protected boolean canStart() {
        return refactoring.getEnabled();
    }
    
    @Override
    public void done() {
        if (isEnabled()) {
            try {
                setName(getNewNameFromNamePosition());
                revertChanges();
                if (isShowPreview()) {
                    openPreview();
                }
                else {
                    new RefactoringExecutionHelper(refactoring,
                            RefactoringStatus.WARNING,
                            RefactoringSaveHelper.SAVE_ALL,
                            editor.getSite().getShell(),
                            editor.getSite().getWorkbenchWindow())
                        .perform(false, true);
                }
            } 
            catch (Exception e) {
                e.printStackTrace();
            }
            super.done();
        }
        else {
            super.cancel();
        }
    }
    
    @Override
    protected void openPreview() {
        new ChangeVersionRefactoringAction(editor) {
            @Override
            public Refactoring createRefactoring() {
                return ChangeVersionLinkedMode.this.refactoring;
            }
            @Override
            public RefactoringWizard createWizard(Refactoring refactoring) {
                return new ChangeVersionWizard((AbstractRefactoring) refactoring) {
                    @Override
                    protected void addUserInputPages() {}
                };
            }
        }.run();
    }

    @Override
    protected void openDialog() {
        new ChangeVersionRefactoringAction(editor) {
            @Override
            public AbstractRefactoring createRefactoring() {
                return ChangeVersionLinkedMode.this.refactoring;
            }
        }.run();
    }
    
    @Override
    protected String getNewNameFromNamePosition() {
        try {
            return versionPosition.getContent();
        }
        catch (BadLocationException e) {
            return getInitialName();
        }
    }

    @Override
    protected void setupLinkedPositions(final IDocument document, final int adjust)
            throws BadLocationException {
        linkedPositionGroup = new LinkedPositionGroup();        
        addLinkedPositions(document, 
                editor.getParseController().getLastCompilationUnit(), 
                adjust, linkedPositionGroup);
        linkedModeModel.addGroup(linkedPositionGroup);
    }
    
}
