package com.redhat.ceylon.eclipse.code.editor;

import static com.redhat.ceylon.eclipse.code.editor.EditorActionIds.SELECT_ENCLOSING;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.text.IRegion;
import org.eclipse.ui.texteditor.ITextEditor;

import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.ArgumentList;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.Body;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.CompilationUnit;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.Condition;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.ConditionList;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.ControlClause;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.Expression;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.Identifier;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.ImportMemberOrType;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.ImportMemberOrTypeList;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.ParameterList;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.SpecifierOrInitializerExpression;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.StatementOrArgument;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.Term;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.Type;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;

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
            Integer nodeStop = that.getStopIndex();
            if (nodeStart!=null && nodeStop!=null) {
                return nodeStart<startOffset && nodeStop+1>=endOffset ||
                        nodeStart<=startOffset && nodeStop+1>endOffset;
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
        CompilationUnit rootNode = editor.getParseController().getRootNode();
        if (rootNode!=null) {
            EnclosingVisitor ev = new EnclosingVisitor(startOffset, endOffset);
            ev.visit(rootNode);
            Node result = ev.result;
            if (result!=null) {
                editor.selectAndReveal(result.getStartIndex(), 
                        result.getStopIndex()-result.getStartIndex()+1);
            }
        }
    }
}