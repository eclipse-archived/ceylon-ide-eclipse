package com.redhat.ceylon.eclipse.imp.parser;

import com.redhat.ceylon.compiler.typechecker.tree.NaturalVisitor;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.QualifiedMemberOrTypeExpression;

class FindNodeVisitor extends Visitor
        implements NaturalVisitor {
    
    FindNodeVisitor(int fStartOffset, int fEndOffset) {
        this.fStartOffset = fStartOffset;
        this.fEndOffset = fEndOffset;
    }
    
    private Node node;
    private int fStartOffset;
    private int fEndOffset;
    
    public Node getNode() {
        return node;
    }
    
    @Override
    public void visitAny(Node that) {
        super.visitAny(that);
        if (node==null && inBounds(that)) {
            node=that;
        }
    }
    
    @Override
    public void visit(Tree.ImportPath that) {
        if (inBounds(that)) {
            node = that;
        }
        else {
            super.visit(that);
        }
    }
    
    @Override
    public void visit(Tree.BinaryOperatorExpression that) {
        super.visit(that);
        if (node==null && inBounds(that.getLeftTerm(), that.getRightTerm())) {
            node=that;
        }
    }
    
    @Override
    public void visit(Tree.UnaryOperatorExpression that) {
        super.visit(that);
        if (node==null && (inBounds(that, that.getTerm())
                ||inBounds(that.getTerm(),that))) {
            node=that;
        }
    }
    
    @Override
    public void visit(QualifiedMemberOrTypeExpression that) {
        if (inBounds(that.getMemberOperator(), that.getIdentifier())) {
            node=that;
        }
        else {
            super.visit(that);
        }
    }
    
    @Override
    public void visit(Tree.StaticMemberOrTypeExpression that) {
        if (inBounds(that.getIdentifier())) {
            node = that;
        }
        else {
            super.visit(that);
        }
    }
    
    @Override
    public void visit(Tree.SimpleType that) {
        if (inBounds(that.getIdentifier())) {
            node = that;
        }
        else {
            super.visit(that);
        }
    }
    
    @Override
    public void visit(Tree.ImportMemberOrType that) {
        if (inBounds(that.getIdentifier())) {
            node = that;
        }
        else {
            super.visit(that);
        }
    }
    
    @Override
    public void visit(Tree.Declaration that) {
        if (inBounds(that.getIdentifier())) {
            node = that;
        }
        else {
            super.visit(that);
        }
    }
    
    @Override
    public void visit(Tree.NamedArgument that) {
        if (inBounds(that.getIdentifier())) {
            node = that;
        }
        else {
            super.visit(that);
        }
    }
    
    private boolean inBounds(Node that) {
        return inBounds(that, that);
    }
    
    private boolean inBounds(Node left, Node right) {
        if (left==null) return false;
        if (right==null) left=right;
        Integer tokenStartIndex = left.getStartIndex();
        Integer tokenStopIndex = right.getStopIndex();
        return tokenStartIndex!=null && tokenStopIndex!=null &&
                tokenStartIndex <= fStartOffset && 
                tokenStopIndex+1 >= fEndOffset;
    }
    
}