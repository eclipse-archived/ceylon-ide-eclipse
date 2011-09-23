package com.redhat.ceylon.eclipse.imp.parser;

import static com.redhat.ceylon.eclipse.imp.parser.CeylonSourcePositionLocator.OccurrenceLocation;

import com.redhat.ceylon.compiler.typechecker.tree.NaturalVisitor;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.QualifiedMemberOrTypeExpression;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.Term;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;

class FindNodeVisitor extends Visitor
        implements NaturalVisitor {
    
    FindNodeVisitor(int fStartOffset, int fEndOffset) {
        this.startOffset = fStartOffset;
        this.endOffset = fEndOffset;
    }
    
    private Node node;
    private int startOffset;
    private int endOffset;
    private OccurrenceLocation occurrence;
    
    public Node getNode() {
        return node;
    }
    
    OccurrenceLocation getOccurrenceLocation() {
        return occurrence;
    }
    
    public void visit(Tree.ExtendedType that) {
        if (inBounds(that)) {
            occurrence = OccurrenceLocation.EXTENDS;
        }
        super.visit(that);
    }
    
    public void visit(Tree.SatisfiedTypes that) {
        if (inBounds(that)) {
            occurrence = OccurrenceLocation.SATISFIES;
        }
        super.visit(that);
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
        Term right = that.getRightTerm();
        if (right==null) {
            right = that;
        }
        if (node==null && inBounds(that.getLeftTerm(), right)) {
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
        if (right==null) right=left;
        Integer tokenStartIndex = left.getStartIndex();
        Integer tokenStopIndex = right.getStopIndex();
        /*Token endToken = right.getEndToken();
        if (endToken!=null && (endToken.getType()==CeylonLexer.SEMICOLON ||
                endToken.getType()==CeylonLexer.RBRACE)) {
            tokenStopIndex--;
        }*/
        return tokenStartIndex!=null && tokenStopIndex!=null &&
                tokenStartIndex <= startOffset && 
                tokenStopIndex+1 >= endOffset;
    }
    
}