/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.code.editor;

import static org.eclipse.ceylon.ide.eclipse.code.editor.EditorActionIds.SELECT_ENCLOSING;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.text.IRegion;
import org.eclipse.ui.texteditor.ITextEditor;

import org.eclipse.ceylon.compiler.typechecker.tree.Node;
import org.eclipse.ceylon.compiler.typechecker.tree.Tree.ArgumentList;
import org.eclipse.ceylon.compiler.typechecker.tree.Tree.Body;
import org.eclipse.ceylon.compiler.typechecker.tree.Tree.CompilationUnit;
import org.eclipse.ceylon.compiler.typechecker.tree.Tree.Condition;
import org.eclipse.ceylon.compiler.typechecker.tree.Tree.ConditionList;
import org.eclipse.ceylon.compiler.typechecker.tree.Tree.ControlClause;
import org.eclipse.ceylon.compiler.typechecker.tree.Tree.Expression;
import org.eclipse.ceylon.compiler.typechecker.tree.Tree.Identifier;
import org.eclipse.ceylon.compiler.typechecker.tree.Tree.ImportMemberOrType;
import org.eclipse.ceylon.compiler.typechecker.tree.Tree.ImportMemberOrTypeList;
import org.eclipse.ceylon.compiler.typechecker.tree.Tree.ParameterList;
import org.eclipse.ceylon.compiler.typechecker.tree.Tree.SpecifierOrInitializerExpression;
import org.eclipse.ceylon.compiler.typechecker.tree.Tree.StatementOrArgument;
import org.eclipse.ceylon.compiler.typechecker.tree.Tree.Term;
import org.eclipse.ceylon.compiler.typechecker.tree.Tree.Type;
import org.eclipse.ceylon.compiler.typechecker.tree.Visitor;

class SelectEnclosingAction extends Action {
    private CeylonEditor editor;

    public SelectEnclosingAction() {
        this(null);
    }

    public SelectEnclosingAction(CeylonEditor editor) {
        super("Select Enclosing");
        setActionDefinitionId(SELECT_ENCLOSING);
        setEditor(editor);
    }

    private void setEditor(ITextEditor editor) {
        if (editor instanceof CeylonEditor) {
            this.editor = (CeylonEditor) editor;
        } 
        else {
            this.editor = null;
        }
        setEnabled(this.editor!=null);
    }
    
    private static class EnclosingVisitor extends Visitor {
        private Node result;
        private int startOffset; 
        private int endOffset;
        private EnclosingVisitor(int startOffset, int endOffset) {
            this.startOffset = startOffset;
            this.endOffset = endOffset;
        }
        private boolean expandsSelection(Node that) {
            Integer nodeStart = that.getStartIndex();
            Integer nodeStop = that.getEndIndex();
            if (nodeStart!=null && nodeStop!=null) {
                return nodeStart<startOffset && nodeStop>=endOffset ||
                        nodeStart<=startOffset && nodeStop>endOffset;
            }
            else {
                return false;
            }
        }
        @Override
        public void visit(CompilationUnit that) {
            if (expandsSelection(that)) {
                result = that;
            }
            super.visit(that);
        }
        @Override
        public void visit(Body that) {
            if (expandsSelection(that)) {
                result = that;
            }
            super.visit(that);
        }
        @Override
        public void visit(ArgumentList that) {
            if (expandsSelection(that)) {
                result = that;
            }
            super.visit(that);
        }
        @Override
        public void visit(ParameterList that) {
            if (expandsSelection(that)) {
                result = that;
            }
            super.visit(that);
        }
        @Override
        public void visit(ControlClause that) {
            if (expandsSelection(that)) {
                result = that;
            }
            super.visit(that);
        }
        @Override
        public void visit(ConditionList that) {
            if (expandsSelection(that)) {
                result = that;
            }
            super.visit(that);
        }
        @Override
        public void visit(Condition that) {
            if (expandsSelection(that)) {
                result = that;
            }
            super.visit(that);
        }
        @Override
        public void visit(Type that) {
            if (expandsSelection(that)) {
                result = that;
            }
            super.visit(that);
        }
        @Override
        public void visit(Identifier that) {
            if (expandsSelection(that)) {
                result = that;
            }
            super.visit(that);
        }
        @Override
        public void visit(Term that) {
            if (expandsSelection(that)) {
                result = that;
            }
            super.visit(that);
        }
        @Override
        public void visit(ImportMemberOrTypeList that) {
            if (expandsSelection(that)) {
                result = that;
            }
            super.visit(that);
        }
        @Override
        public void visit(ImportMemberOrType that) {
            if (expandsSelection(that)) {
                result = that;
            }
            super.visit(that);
        }
        @Override
        public void visit(SpecifierOrInitializerExpression that) {
            if (expandsSelection(that)) {
                result = that;
            }
            super.visit(that);
        }
        @Override
        public void visit(Expression that) {
            if (expandsSelection(that)) {
                result = that;
            }
            super.visit(that);
        }
        @Override
        public void visit(StatementOrArgument that) {
            if (expandsSelection(that)) {
                result = that;
            }
            super.visit(that);
        }
    }

    @Override
    public void run() {
        IRegion selection = editor.getSelection();
        int startOffset = selection.getOffset();
        int endOffset = startOffset + selection.getLength();
        CompilationUnit rootNode = editor.getParseController().getParsedRootNode();
        if (rootNode!=null) {
            EnclosingVisitor ev = new EnclosingVisitor(startOffset, endOffset);
            ev.visit(rootNode);
            Node result = ev.result;
            if (result!=null) {
                editor.selectAndReveal(result.getStartIndex(), 
                        result.getDistance());
            }
        }
    }
}