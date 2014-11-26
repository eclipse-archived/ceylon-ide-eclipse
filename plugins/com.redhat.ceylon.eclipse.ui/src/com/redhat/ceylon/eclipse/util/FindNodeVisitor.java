package com.redhat.ceylon.eclipse.util;

import com.redhat.ceylon.compiler.typechecker.tree.NaturalVisitor;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.Term;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;

public class FindNodeVisitor extends Visitor
        implements NaturalVisitor {
    
    protected FindNodeVisitor(int fStartOffset, int fEndOffset) {
        this.startOffset = fStartOffset;
        this.endOffset = fEndOffset;
    }
    
    protected Node node;
    private int startOffset;
    private int endOffset;
    
    public Node getNode() {
        return node;
    }
    
    public void visit(Tree.MemberLiteral that) {
        if (inBounds(that.getIdentifier())) {
            node = that;
        }
        else {
            super.visit(that);
        }
    }
    
    public void visit(Tree.ExtendedType that) {
        if (that.getType()!=null) that.getType().visit(this);
        if (that.getInvocationExpression()!=null &&
                that.getInvocationExpression().getPositionalArgumentList()!=null) {
            that.getInvocationExpression().getPositionalArgumentList().visit(this);
        }
        if (that.getType()==null && 
            that.getInvocationExpression()==null) {
            super.visit(that);
        }
    }
    
    public void visit(Tree.ClassSpecifier that) {
        if (that.getType()!=null) that.getType().visit(this);
        if (that.getInvocationExpression()!=null &&
                that.getInvocationExpression().getPositionalArgumentList()!=null) {
            that.getInvocationExpression().getPositionalArgumentList().visit(this);
        }
        if (that.getType()==null && 
            that.getInvocationExpression()==null) {
            super.visit(that);
        }
    }
    
    @Override
    public void visitAny(Node that) {
        if (inBounds(that) && 
                !(that instanceof Tree.LetClause)) { //yick!
            node=that;
            super.visitAny(that);
        }
        //otherwise, as a performance optimization
        //don't go any further down this branch
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
        Term right = that.getRightTerm();
        if (right==null) {
            right = that;
        }
        Term left = that.getLeftTerm();
        if (left==null) {
            left = that;
        }
        if (inBounds(left, right)) {
            node=that;
        }
        super.visit(that);
    }
    
    @Override
    public void visit(Tree.UnaryOperatorExpression that) {
        Term term = that.getTerm();
        if (term==null) {
            term = that;
        }
        if (inBounds(that, term) || inBounds(term, that)) {
            node=that;
        }
        super.visit(that);
    }
    
    @Override
    public void visit(Tree.ParameterList that) {
        if (inBounds(that)) {
            node=that;
        }
        super.visit(that);
    }
    
    @Override
    public void visit(Tree.TypeParameterList that) {
        if (inBounds(that)) {
            node=that;
        }
        super.visit(that);
    }
    
    @Override
    public void visit(Tree.ArgumentList that) {
        if (inBounds(that)) {
            node=that;
        }
        super.visit(that);
    }
    
    @Override
    public void visit(Tree.TypeArgumentList that) {
        if (inBounds(that)) {
            node=that;
        }
        super.visit(that);
    }
    
    @Override
    public void visit(Tree.QualifiedMemberOrTypeExpression that) {
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
            //Note: we can't be sure that this is "really"
            //      an EXPRESSION!
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
        if (inBounds(that.getIdentifier())||
                that.getAlias()!=null &&
                inBounds(that.getAlias())) {
            node = that;
        }
        else {
            super.visit(that);
        }
    }
    
    //TODO: do we need to do this and the same thing for 
    //      Conditions with synthetic variables?
    /*@Override
    public void visit(Tree.IsCase that) {
        if (node!=that && inBounds(that)) {
            node=that;
        }
        if (that.getType()!=null) {
            that.getType().visit(this);
        }
        super.visit(that);
    }*/
    
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
    public void visit(Tree.InitializerParameter that) {
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
    
    @Override
    public void visit(Tree.DocLink that) {
        if (inBounds(that)) {
            node = that;
        }
        else {
            super.visit(that);
        }
    }
    
    protected boolean inBounds(Node that) {
        if (that==null || that.getToken()==null) {
            return false;
        }
        return inBounds(that, that);
    }
    
    protected boolean inBounds(Node left, Node right) {
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