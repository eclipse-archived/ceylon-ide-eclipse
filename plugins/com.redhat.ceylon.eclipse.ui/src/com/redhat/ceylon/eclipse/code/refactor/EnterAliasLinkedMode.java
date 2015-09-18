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

import static com.redhat.ceylon.eclipse.code.correct.LinkedModeCompletionProposal.getNameProposals;
import static com.redhat.ceylon.eclipse.ui.CeylonPlugin.PLUGIN_ID;
import static com.redhat.ceylon.eclipse.util.DocLinks.hasPackage;
import static com.redhat.ceylon.eclipse.util.DocLinks.nameRegion;

import org.eclipse.jdt.internal.ui.refactoring.RefactoringExecutionHelper;
import org.eclipse.jdt.ui.refactoring.RefactoringSaveHelper;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.link.LinkedPosition;
import org.eclipse.jface.text.link.LinkedPositionGroup;
import org.eclipse.jface.text.link.ProposalPosition;
import org.eclipse.ltk.core.refactoring.DocumentChange;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.ui.refactoring.RefactoringWizard;

import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.Identifier;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.util.EditorUtil;
import com.redhat.ceylon.eclipse.util.Escaping;
import com.redhat.ceylon.model.typechecker.model.Declaration;


public class EnterAliasLinkedMode extends RefactorLinkedMode {

    protected LinkedPosition namePosition;
    protected LinkedPositionGroup linkedPositionGroup;
    
    private EnterAliasRefactoring refactoring;

    private final class LinkedPositionsVisitor 
            extends Visitor {
        private final int adjust;
        private final IDocument document;
        private final LinkedPositionGroup linkedPositionGroup;
        int i=2;

        private LinkedPositionsVisitor(int adjust, IDocument document,
                LinkedPositionGroup linkedPositionGroup) {
            this.adjust = adjust;
            this.document = document;
            this.linkedPositionGroup = linkedPositionGroup;
        }

        @Override
        public void visit(Tree.StaticMemberOrTypeExpression that) {
            super.visit(that);
            addLinkedPosition(that.getIdentifier(), 
                    that.getDeclaration());
        }
        
        @Override
        public void visit(Tree.SimpleType that) {
            super.visit(that);
            addLinkedPosition(that.getIdentifier(), 
                    that.getDeclarationModel());
        }

        @Override
        public void visit(Tree.MemberLiteral that) {
            super.visit(that);
            addLinkedPosition(that.getIdentifier(), 
                    that.getDeclaration());
        }
        
        @Override
        public void visit(Tree.DocLink that) {
            super.visit(that);
            Declaration base = that.getBase();
            if (base!=null && !hasPackage(that)) {
                Region region = nameRegion(that, 0);
                addLinkedPosition(region.getOffset(), 
                        region.getLength(), base);
            }
        }

        private void addLinkedPosition(Identifier id, Declaration d) {
            if (id!=null) {
                addLinkedPosition(id.getStartIndex(), 
                        id.getText().length(), d);
            }
        }
        private void addLinkedPosition(int pos, int len, Declaration d) {
            if (d!=null && refactoring.isReference(d)) {
                try {
                    int offset = getOriginalSelection().x;
                    int seq;
                    if (offset<pos || offset>pos+len) {
                        seq = i++;
                    }
                    else {
                        seq = 1; //the selected node
                    }
                    linkedPositionGroup.addPosition(new LinkedPosition(document, 
                            pos+adjust, len, seq));
                }
                catch (BadLocationException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public EnterAliasLinkedMode(CeylonEditor editor) {
        super(editor);
        refactoring = new EnterAliasRefactoring(editor);
    }
    
    @Override
    protected String getName() {
        return refactoring.getNewName();
    }

    @Override
    public String getHintTemplate() {
        return "Enter alias for " + 
                linkedPositionGroup.getPositions().length + 
                " occurrences of '" + 
                refactoring.getElement().getDeclarationModel().getName() + 
                "' {0}";
    }
    
    @Override
    protected int performInitialChange(IDocument document) {
        DocumentChange change = 
                new DocumentChange("Enter Alias", document);
        int result = refactoring.renameInFile(change);
        EditorUtil.performChange(change);
        return result;
    }
    
    @Override
    protected String getNewNameFromNamePosition() {
        try {
            return namePosition.getContent();
        }
        catch (BadLocationException e) {
            return getInitialName();
        }
    }
    
    @Override
    protected void setupLinkedPositions(IDocument document, int adjust)
            throws BadLocationException {
        linkedPositionGroup = new LinkedPositionGroup();
        
        Tree.ImportMemberOrType element = refactoring.getElement();
        int offset;
        Tree.Alias alias = element.getAlias();
        if (alias == null) {
            offset = element.getIdentifier().getStartIndex();
        }
        else {
            offset = alias.getStartIndex();
        }
        String originalName = getInitialName();
        namePosition = new ProposalPosition(document, offset, 
                originalName.length(), 0,
                getNameProposals(offset, 0, 
                        new String[]{element.getDeclarationModel().getName()},
                        originalName));
        
        linkedPositionGroup.addPosition(namePosition);
        editor.getParseController().getLastCompilationUnit()
                .visit(new LinkedPositionsVisitor(adjust, document, 
                        linkedPositionGroup));
        linkedModeModel.addGroup(linkedPositionGroup);
    }

    private boolean isEnabled() {
        String newName = getNewNameFromNamePosition();
        return !getInitialName().equals(newName) &&
                newName.matches("^\\w(\\w|\\d)*$") &&
                !Escaping.KEYWORDS.contains(newName);
    }

    @Override
    public void done() {
        if (isEnabled()) {
            try {
//                hideEditorActivity();
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
//            finally {
//                unhideEditorActivity();
//            }
            super.done();
        }
        else {
            super.cancel();
        }
    }

    @Override
    protected String getActionName() {
        return PLUGIN_ID + ".action.enterAlias";
    }

    @Override
    protected void setName(String name) {
        refactoring.setNewName(name);
    }

    @Override
    protected boolean canStart() {
        return refactoring.getEnabled();
    }

    @Override
    protected void openPreview() {
        new EnterAliasRefactoringAction(editor) {
            @Override
            public Refactoring createRefactoring() {
                return EnterAliasLinkedMode.this.refactoring;
            }
            @Override
            public RefactoringWizard createWizard(Refactoring refactoring) {
                return new EnterAliasWizard((EnterAliasRefactoring) refactoring) {
                    @Override
                    protected void addUserInputPages() {}
                };
            }
        }.run();
    }

    @Override
    protected void openDialog() {
        new EnterAliasRefactoringAction(editor) {
            @Override
            public AbstractRefactoring createRefactoring() {
                return EnterAliasLinkedMode.this.refactoring;
            }
        }.run();
    }
    
}
