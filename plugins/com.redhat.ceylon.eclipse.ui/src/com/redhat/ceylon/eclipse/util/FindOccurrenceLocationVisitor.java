package com.redhat.ceylon.eclipse.util;

import static com.redhat.ceylon.eclipse.util.OccurrenceLocation.ALIAS_REF;
import static com.redhat.ceylon.eclipse.util.OccurrenceLocation.CASE;
import static com.redhat.ceylon.eclipse.util.OccurrenceLocation.CATCH;
import static com.redhat.ceylon.eclipse.util.OccurrenceLocation.CLASS_ALIAS;
import static com.redhat.ceylon.eclipse.util.OccurrenceLocation.CLASS_REF;
import static com.redhat.ceylon.eclipse.util.OccurrenceLocation.DOCLINK;
import static com.redhat.ceylon.eclipse.util.OccurrenceLocation.EXISTS;
import static com.redhat.ceylon.eclipse.util.OccurrenceLocation.EXPRESSION;
import static com.redhat.ceylon.eclipse.util.OccurrenceLocation.EXTENDS;
import static com.redhat.ceylon.eclipse.util.OccurrenceLocation.FUNCTION_REF;
import static com.redhat.ceylon.eclipse.util.OccurrenceLocation.IMPORT;
import static com.redhat.ceylon.eclipse.util.OccurrenceLocation.INTERFACE_REF;
import static com.redhat.ceylon.eclipse.util.OccurrenceLocation.IS;
import static com.redhat.ceylon.eclipse.util.OccurrenceLocation.META;
import static com.redhat.ceylon.eclipse.util.OccurrenceLocation.MODULE_REF;
import static com.redhat.ceylon.eclipse.util.OccurrenceLocation.NONEMPTY;
import static com.redhat.ceylon.eclipse.util.OccurrenceLocation.OF;
import static com.redhat.ceylon.eclipse.util.OccurrenceLocation.PACKAGE_REF;
import static com.redhat.ceylon.eclipse.util.OccurrenceLocation.PARAMETER_LIST;
import static com.redhat.ceylon.eclipse.util.OccurrenceLocation.SATISFIES;
import static com.redhat.ceylon.eclipse.util.OccurrenceLocation.TYPE_ALIAS;
import static com.redhat.ceylon.eclipse.util.OccurrenceLocation.TYPE_ARGUMENT_LIST;
import static com.redhat.ceylon.eclipse.util.OccurrenceLocation.TYPE_PARAMETER_LIST;
import static com.redhat.ceylon.eclipse.util.OccurrenceLocation.TYPE_PARAMETER_REF;
import static com.redhat.ceylon.eclipse.util.OccurrenceLocation.UPPER_BOUND;
import static com.redhat.ceylon.eclipse.util.OccurrenceLocation.VALUE_REF;

import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.QualifiedMemberOrTypeExpression;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.Term;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;

class FindOccurrenceLocationVisitor extends Visitor {
    
    private Node node;
    private int offset;
    
    private OccurrenceLocation occurrence;
    private boolean inTypeConstraint = false;
    
    FindOccurrenceLocationVisitor(int offset, Node node) {
        this.offset = offset;
        this.node = node;
    }
    
    OccurrenceLocation getOccurrenceLocation() {
        return occurrence;
    }
    
    @Override
    public void visitAny(Node that) {
        if (inBounds(that))  {
            super.visitAny(that);
        }
        //otherwise, as a performance optimization
        //don't go any further down this branch
    }
    
    @Override
    public void visit(Tree.Condition that) {
        if (inBounds(that)) {
            occurrence = EXPRESSION;
        }
        super.visit(that);
    }
    
    @Override
    public void visit(Tree.ExistsCondition that) {
        super.visit(that);
        Tree.Statement var = that.getVariable();
        if (var instanceof Tree.Variable ? 
                inBounds(((Tree.Variable)var).getIdentifier()) :
                inBounds(that)) {
            occurrence = EXISTS;
        }
    }
    
    @Override
    public void visit(Tree.NonemptyCondition that) {
        super.visit(that);
        Tree.Statement var = that.getVariable();
        if (var instanceof Tree.Variable ? 
                inBounds(((Tree.Variable)var).getIdentifier()) :
                inBounds(that)) {
            occurrence = NONEMPTY;
        }
    }
    
    @Override
    public void visit(Tree.IsCondition that) {
        super.visit(that);
        boolean inBounds;
        if (that.getVariable()!=null) {
            inBounds = inBounds(that.getVariable().getIdentifier());
        }
        else if (that.getType()!=null) {
            inBounds = inBounds(that) && offset>that.getType().getEndIndex();
        }
        else {
            inBounds = false;
        }
        if (inBounds) {
            occurrence = IS;
        }
    }
    
    public void visit(Tree.TypeConstraint that) {
        inTypeConstraint=true;
        super.visit(that);
        inTypeConstraint=false;
    }
    
    public void visit(Tree.ImportMemberOrTypeList that) {
        if (inBounds(that)) {
            occurrence = IMPORT;
        }
        super.visit(that);
    }
    
    public void visit(Tree.ExtendedType that) {
        if (inBounds(that)) {
            occurrence = EXTENDS;
        }
        super.visit(that);
    }
    
    public void visit(Tree.DelegatedConstructor that) {
        if (inBounds(that)) {
            occurrence = EXTENDS;
        }
        super.visit(that);
    }
    
    public void visit(Tree.SatisfiedTypes that) {
        if (inBounds(that)) {
            occurrence = inTypeConstraint? 
                    UPPER_BOUND : SATISFIES;
        }
        super.visit(that);
    }
        
    public void visit(Tree.CaseTypes that) {
        if (inBounds(that)) {
            occurrence = OF;
        }
        super.visit(that);
    }
    
    public void visit(Tree.CatchClause that) {
        if (inBounds(that) && 
                !inBounds(that.getBlock())) {
            occurrence = CATCH;
        }
        else {
            super.visit(that);
        }
    }
    
    public void visit(Tree.CaseClause that) {
        if (inBounds(that) && 
                !inBounds(that.getBlock()) &&
                !inBounds(that.getExpression())) {
            occurrence = CASE;
        }
        super.visit(that);
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
            occurrence = EXPRESSION;
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
            occurrence = EXPRESSION;
        }
        super.visit(that);
    }
    
    @Override
    public void visit(Tree.ParameterList that) {
        if (inBounds(that)) {
            occurrence = PARAMETER_LIST;
        }
        super.visit(that);
    }
    
    @Override
    public void visit(Tree.TypeParameterList that) {
        if (inBounds(that)) {
            occurrence = TYPE_PARAMETER_LIST;
        }
        super.visit(that);
    }
    
    @Override
    public void visit(Tree.TypeSpecifier that) {
        if (inBounds(that)) {
            occurrence = TYPE_ALIAS;
        }
        super.visit(that);
    }
    
    @Override
    public void visit(Tree.ClassSpecifier that) {
        if (inBounds(that)) {
            occurrence = CLASS_ALIAS;
        }
        super.visit(that);
    }
    
    @Override
    public void visit(Tree.SpecifierOrInitializerExpression that) {
        if (inBounds(that)) {
            occurrence = EXPRESSION;
        }
        super.visit(that);
    }
    
    @Override
    public void visit(Tree.ArgumentList that) {
        if (inBounds(that)) {
            occurrence = EXPRESSION;
        }
        super.visit(that);
    }
    
    @Override
    public void visit(Tree.TypeArgumentList that) {
        if (inBounds(that)) {
            occurrence = TYPE_ARGUMENT_LIST;
        }
        super.visit(that);
    }
    
    @Override
    public void visit(QualifiedMemberOrTypeExpression that) {
        if (inBounds(that.getMemberOperator(), that.getIdentifier())) {
            occurrence = EXPRESSION;
        }
        else {
            super.visit(that);
        }
    }
    
    @Override
    public void visit(Tree.Declaration that) {
        if (inBounds(that)) {
            if (occurrence!=PARAMETER_LIST) {
                occurrence=null;
            }
        }
        super.visit(that);
    }
    
    public void visit(Tree.MetaLiteral that) {
        super.visit(that);
        if (inBounds(that)) {
            if (occurrence!=TYPE_ARGUMENT_LIST) {
                switch (that.getNodeType()) {
                case "ModuleLiteral": 
                    occurrence=MODULE_REF; 
                    break;
                case "PackageLiteral": 
                    occurrence=PACKAGE_REF; 
                    break;
                case "ValueLiteral": 
                    occurrence=VALUE_REF; 
                    break;
                case "FunctionLiteral": 
                    occurrence=FUNCTION_REF; 
                    break;
                case "InterfaceLiteral": 
                    occurrence=INTERFACE_REF; 
                    break;
                case "ClassLiteral": 
                    occurrence=CLASS_REF; 
                    break;
                case "TypeParameterLiteral": 
                    occurrence=TYPE_PARAMETER_REF; 
                    break;
                case "AliasLiteral": 
                    occurrence=ALIAS_REF; 
                    break;
                default:
                    occurrence = META;
                }
                
            }
        }
    }
    
    public void visit(Tree.StringLiteral that) {
        if (inBounds(that)) {
            occurrence = DOCLINK;
        }
    }
   
    public void visit(Tree.DocLink that) {
        if (this.node instanceof Tree.DocLink) {
            occurrence = DOCLINK;
        }
    }
    
    private boolean inBounds(Node that) {
        return inBounds(that, that);
    }
    
    private boolean inBounds(Node left, Node right) {
        if (left==null) return false;
        if (right==null) right=left;
        Integer startIndex = left.getStartIndex();
        Integer endIndex = right.getEndIndex();
        return startIndex!=null && endIndex!=null &&
                startIndex <= node.getStartIndex() && 
                endIndex >= node.getEndIndex();
    }
    
}