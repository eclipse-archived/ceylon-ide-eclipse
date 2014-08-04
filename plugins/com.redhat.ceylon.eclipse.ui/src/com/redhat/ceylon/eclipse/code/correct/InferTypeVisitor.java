package com.redhat.ceylon.eclipse.code.correct;

import static com.redhat.ceylon.compiler.typechecker.model.Util.intersectionType;
import static com.redhat.ceylon.compiler.typechecker.model.Util.isTypeUnknown;
import static com.redhat.ceylon.compiler.typechecker.model.Util.unionType;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.NothingType;
import com.redhat.ceylon.compiler.typechecker.model.Parameter;
import com.redhat.ceylon.compiler.typechecker.model.ProducedReference;
import com.redhat.ceylon.compiler.typechecker.model.ProducedType;
import com.redhat.ceylon.compiler.typechecker.model.TypedDeclaration;
import com.redhat.ceylon.compiler.typechecker.model.Unit;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.Term;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;

class InferTypeVisitor extends Visitor {
    
    Unit unit;
    Declaration dec;
    ProducedType inferredType;
    
    void intersect(ProducedType pt) {
        if (!isTypeUnknown(pt)) {
            if (inferredType==null) {
                inferredType = pt;
            }
            else {
                ProducedType it = intersectionType(inferredType, pt, unit);
                if (!(it.getDeclaration() instanceof NothingType)) {
                    inferredType = it;
                }
            }
        }
    }
    
    void union(ProducedType pt) {
        if (!isTypeUnknown(pt)) {
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
            Declaration d = ((Tree.BaseMemberExpression) term).getDeclaration();
            if (d!=null && d.equals(dec)) {
                intersect(that.getType().getTypeModel());
            }
        }
    }
    
    @Override public void visit(Tree.MethodDeclaration that) {
        super.visit(that);
        Term term = that.getSpecifierExpression()==null ? 
                null : that.getSpecifierExpression().getExpression().getTerm();
        if (term instanceof Tree.BaseMemberExpression) {
            Declaration d = ((Tree.BaseMemberExpression) term).getDeclaration();
            if (d!=null && d.equals(dec)) {
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
            Declaration d = ((Tree.BaseMemberExpression) bme).getDeclaration();
            if (d!=null && d.equals(dec)) {
                if (term!=null) union(term.getTypeModel());
            }
        } 
        if (term instanceof Tree.BaseMemberExpression) {
            Declaration d = ((Tree.BaseMemberExpression) term).getDeclaration();
            if (d!=null && d.equals(dec)) {
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
    
    private ProducedReference pr;
    
    @Override public void visit(Tree.InvocationExpression that) {
        ProducedReference opr=null;
        Tree.Primary primary = that.getPrimary();
        if (primary!=null) {
            if (primary instanceof Tree.MemberOrTypeExpression) {
                pr = ((Tree.MemberOrTypeExpression) primary).getTarget();
            }
        }
        super.visit(that);
        pr = opr;
    }
    
    @Override public void visit(Tree.ListedArgument that) {
        super.visit(that);
        Tree.Term t = that.getExpression().getTerm();
        if (t instanceof Tree.BaseMemberExpression) {
            Tree.BaseMemberExpression bme = (Tree.BaseMemberExpression) t;
            Declaration d = bme.getDeclaration();
            if (d!=null && d.equals(dec)) {
                Parameter p = that.getParameter();
                if (p!=null && pr!=null) {
                    ProducedType ft = pr.getTypedParameter(p)
                            .getFullType();
                    if (p.isSequenced()) {
                        ft = unit.getIteratedType(ft);
                    }
                    intersect(ft);
                }
            }
        }
    }
    
    @Override public void visit(Tree.SpreadArgument that) {
        super.visit(that);
        Tree.Term t = that.getExpression().getTerm();
        if (t instanceof Tree.BaseMemberExpression) {
            Tree.BaseMemberExpression bme = (Tree.BaseMemberExpression) t;
            Declaration d = bme.getDeclaration();
            if (d!=null && d.equals(dec)) {
                Parameter p = that.getParameter();
                if (p!=null && pr!=null) {
                    //TODO: is this correct?
                    ProducedType ft = pr.getTypedParameter(p)
                            .getFullType();
                    intersect(unit.getIterableType(unit.getIteratedType(ft)));
                }
            }
        }
    }
    
    @Override public void visit(Tree.SpecifiedArgument that) {
        super.visit(that);
        Tree.Term t = that.getSpecifierExpression().getExpression().getTerm();
        if (t instanceof Tree.BaseMemberExpression) {
            Tree.BaseMemberExpression bme = (Tree.BaseMemberExpression) t;
            Declaration d = bme.getDeclaration();
            if (d!=null && d.equals(dec)) {
                Parameter p = that.getParameter();
                if (p!=null && pr!=null) {
                    ProducedType ft = pr.getTypedParameter(p)
                            .getFullType();
                    intersect(ft);
                }
            }
        }
    }
    
    @Override public void visit(Tree.Return that) {
        super.visit(that);
        Tree.Term bme = that.getExpression().getTerm();
        if (bme instanceof Tree.BaseMemberExpression) {
            Declaration bmed = ((Tree.BaseMemberExpression) bme).getDeclaration();
            if (bmed!=null && bmed.equals(dec)) {
                Declaration d = that.getDeclaration();
                if (d instanceof TypedDeclaration) {
                    intersect(((TypedDeclaration) d).getType());
                }
            }
        }
    }
    //TODO: MethodDeclarations
}