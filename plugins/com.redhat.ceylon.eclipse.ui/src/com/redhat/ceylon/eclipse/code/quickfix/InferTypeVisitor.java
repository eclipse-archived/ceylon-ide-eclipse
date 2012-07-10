package com.redhat.ceylon.eclipse.code.quickfix;

import static com.redhat.ceylon.compiler.typechecker.model.Util.intersectionType;
import static com.redhat.ceylon.compiler.typechecker.model.Util.unionType;

import com.redhat.ceylon.compiler.typechecker.model.BottomType;
import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.ProducedType;
import com.redhat.ceylon.compiler.typechecker.model.TypedDeclaration;
import com.redhat.ceylon.compiler.typechecker.model.Unit;
import com.redhat.ceylon.compiler.typechecker.model.UnknownType;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.Term;

class InferTypeVisitor extends Visitor {
    Unit unit;
    Declaration dec;
    ProducedType inferredType;
    
    void intersect(ProducedType pt) {
        if (pt!=null && !(pt.getDeclaration() instanceof UnknownType)) {
            if (inferredType==null) {
                inferredType = pt;
            }
            else {
                ProducedType it = intersectionType(inferredType, pt, unit);
                if (!(it.getDeclaration() instanceof BottomType)) {
                    inferredType = it;
                }
            }
        }
    }
    
    void union(ProducedType pt) {
        if (pt!=null && !(pt.getDeclaration() instanceof UnknownType)) {
            if (inferredType==null) {
                inferredType = pt;
            }
            else {
                inferredType = unionType(inferredType, unit.denotableType(pt), unit);
            }
        }
    }
    
    @Override public void visit(Tree.AttributeDeclaration that) {
        super.visit(that);
        Term term = that.getSpecifierOrInitializerExpression()==null ? 
                null : that.getSpecifierOrInitializerExpression().getExpression().getTerm();
        if (term instanceof Tree.BaseMemberExpression) {
            if (((Tree.BaseMemberExpression) term).getDeclaration().equals(dec)) {
                intersect(that.getType().getTypeModel());
            }
        }
    }
    
    @Override public void visit(Tree.SpecifierStatement that) {
        super.visit(that);
        Tree.Term bme = that.getBaseMemberExpression();
        Term term = that.getSpecifierExpression()==null ? 
                null : that.getSpecifierExpression().getExpression().getTerm();
        if (bme instanceof Tree.BaseMemberExpression) {
            if (((Tree.BaseMemberExpression) bme).getDeclaration().equals(dec)) {
                if (term!=null) union(term.getTypeModel());
            }
        } 
        if (term instanceof Tree.BaseMemberExpression) {
            if (((Tree.BaseMemberExpression) term).getDeclaration().equals(dec)) {
                if (bme!=null) intersect(bme.getTypeModel());
            }
        }
    }
    
    @Override public void visit(Tree.AssignmentOp that) {
        super.visit(that);
        Term rt = that.getRightTerm();
        Term lt = that.getLeftTerm();
        if (lt instanceof Tree.BaseMemberExpression) {
            if (((Tree.BaseMemberExpression) lt).getDeclaration().equals(dec)) {
                if (rt!=null) union(rt.getTypeModel());
            }
        }
        if (rt instanceof Tree.BaseMemberExpression) {
            if (((Tree.BaseMemberExpression) rt).getDeclaration().equals(dec)) {
                if (lt!=null) intersect(lt.getTypeModel());
            }
        }
    }
    
    @Override public void visit(Tree.PositionalArgument that) {
        super.visit(that);
        Tree.Term bme = that.getExpression().getTerm();
        if (bme instanceof Tree.BaseMemberExpression) {
            if (((Tree.BaseMemberExpression) bme).getDeclaration().equals(dec)) {
                intersect(that.getParameter().getType());
            }
        }
    }
    
    @Override public void visit(Tree.SpecifiedArgument that) {
        super.visit(that);
        Tree.Term bme = that.getSpecifierExpression().getExpression().getTerm();
        if (bme instanceof Tree.BaseMemberExpression) {
            if (((Tree.BaseMemberExpression) bme).getDeclaration().equals(dec)) {
                intersect(that.getParameter().getType());
            }
        }
    }
    
    @Override public void visit(Tree.Return that) {
        super.visit(that);
        Tree.Term bme = that.getExpression().getTerm();
        if (bme instanceof Tree.BaseMemberExpression) {
            if (((Tree.BaseMemberExpression) bme).getDeclaration().equals(dec)) {
                Declaration d = that.getDeclaration();
                if (d instanceof TypedDeclaration) {
                    intersect(((TypedDeclaration) d).getType());
                }
            }
        }
    }
    //TODO: MethodDeclarations
}