package com.redhat.ceylon.eclipse.code.correct;

import static com.redhat.ceylon.compiler.typechecker.model.Util.intersectionType;
import static com.redhat.ceylon.compiler.typechecker.model.Util.isTypeUnknown;
import static com.redhat.ceylon.compiler.typechecker.model.Util.unionType;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.Interface;
import com.redhat.ceylon.compiler.typechecker.model.NothingType;
import com.redhat.ceylon.compiler.typechecker.model.Parameter;
import com.redhat.ceylon.compiler.typechecker.model.ProducedReference;
import com.redhat.ceylon.compiler.typechecker.model.ProducedType;
import com.redhat.ceylon.compiler.typechecker.model.TypeDeclaration;
import com.redhat.ceylon.compiler.typechecker.model.TypedDeclaration;
import com.redhat.ceylon.compiler.typechecker.model.Unit;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.Term;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;

class InferredType {
    
    private Unit unit;

    InferredType(Unit unit) {
        this.unit = unit;
        
    }
    
    ProducedType inferredType;
    ProducedType generalizedType;
    
    void intersect(ProducedType pt) {
        if (!isTypeUnknown(pt)) {
            if (generalizedType==null) {
                generalizedType = pt;
            }
            else {
                ProducedType it = intersectionType(generalizedType, pt, unit);
                if (!(it.getDeclaration() instanceof NothingType)) {
                    generalizedType = it;
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
    
}

class InferTypeVisitor extends Visitor {
    
    Unit unit;
    Declaration dec;
    InferredType result;
    
    InferTypeVisitor(Unit unit) {
        this.unit = unit;
        result = new InferredType(unit);
    }
    
    @Override public void visit(Tree.AttributeDeclaration that) {
        super.visit(that);
        //TODO: an assignment to something with an inferred
        //      type doesn't _directly_ constrain the type
        //      ... but _indirectly_ it can!
//        if (!(that.getType() instanceof Tree.LocalModifier)) {
            Term term = that.getSpecifierOrInitializerExpression()==null ? 
                    null : that.getSpecifierOrInitializerExpression().getExpression().getTerm();
            if (term instanceof Tree.BaseMemberExpression) {
                Declaration d = ((Tree.BaseMemberExpression) term).getDeclaration();
                if (d!=null && d.equals(dec)) {
                    result.intersect(that.getType().getTypeModel());
                }
            }
            else if (term!=null) {
                if (that.getDeclarationModel().equals(dec)) {
                    result.union(term.getTypeModel());
                }
            }
//        }
    }
    
    @Override public void visit(Tree.MethodDeclaration that) {
        super.visit(that);
        //TODO: an assignment to something with an inferred
        //      type doesn't _directly_ constrain the type
        //      ... but _indirectly_ it can!
//        if (!(that.getType() instanceof Tree.LocalModifier)) {
            Term term = that.getSpecifierExpression()==null ? 
                    null : that.getSpecifierExpression().getExpression().getTerm();
            if (term instanceof Tree.BaseMemberExpression) {
                Declaration d = ((Tree.BaseMemberExpression) term).getDeclaration();
                if (d!=null && d.equals(dec)) {
                    result.intersect(that.getType().getTypeModel());
                }
            }
            else if (term!=null) {
                if (that.getDeclarationModel().equals(dec)) {
                    result.union(term.getTypeModel());
                }
            }
//        }
    }
    
    @Override public void visit(Tree.SpecifierStatement that) {
        super.visit(that);
        Tree.Term bme = that.getBaseMemberExpression();
        Term term = that.getSpecifierExpression()==null ? 
                null : that.getSpecifierExpression().getExpression().getTerm();
        if (bme instanceof Tree.BaseMemberExpression) {
            Declaration d = ((Tree.BaseMemberExpression) bme).getDeclaration();
            if (d!=null && d.equals(dec)) {
                if (term!=null)
                    result.union(term.getTypeModel());
            }
        } 
        if (term instanceof Tree.BaseMemberExpression) {
            Declaration d = ((Tree.BaseMemberExpression) term).getDeclaration();
            if (d!=null && d.equals(dec)) {
                if (bme!=null)
                    result.intersect(bme.getTypeModel());
            }
        }
    }
    
    @Override public void visit(Tree.AssignmentOp that) {
        super.visit(that);
        Term rt = that.getRightTerm();
        Term lt = that.getLeftTerm();
        if (lt instanceof Tree.BaseMemberExpression) {
            if (((Tree.BaseMemberExpression) lt).getDeclaration().equals(dec)) {
                if (rt!=null)
                    result.union(rt.getTypeModel());
            }
        }
        if (rt instanceof Tree.BaseMemberExpression) {
            if (((Tree.BaseMemberExpression) rt).getDeclaration().equals(dec)) {
                if (lt!=null)
                    result.intersect(lt.getTypeModel());
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
                    result.intersect(ft);
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
                    result.intersect(unit.getIterableType(unit.getIteratedType(ft)));
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
                    result.intersect(ft);
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
                    result.intersect(((TypedDeclaration) d).getType());
                }
            }
        }
        else if (bme!=null) {
            if (that.getDeclaration().equals(dec)) {
                result.union(bme.getTypeModel());
            }
        }
    }
    
    @Override
    public void visit(Tree.QualifiedMemberOrTypeExpression that) {
        super.visit(that);
        Tree.Primary primary = that.getPrimary();
        if (primary instanceof Tree.BaseMemberExpression) {
            Declaration bmed = ((Tree.BaseMemberExpression) primary).getDeclaration();
            if (bmed!=null && bmed.equals(dec)) {
                TypeDeclaration td = (TypeDeclaration) that.getDeclaration().getRefinedDeclaration().getContainer();
                result.intersect(that.getTarget().getQualifyingType().getSupertype(td));
            }
        }
    }
    
    @Override
    public void visit(Tree.KeyValueIterator that) {
        super.visit(that);
        Tree.Term primary = that.getSpecifierExpression().getExpression().getTerm();
        if (primary instanceof Tree.BaseMemberExpression) {
            Declaration bmed = ((Tree.BaseMemberExpression) primary).getDeclaration();
            if (bmed!=null && bmed.equals(dec)) {
                ProducedType kt = that.getKeyVariable().getType().getTypeModel();
                ProducedType vt = that.getValueVariable().getType().getTypeModel();
                result.intersect(that.getUnit().getIterableType(that.getUnit().getEntryType(kt, vt)));
            }
        }
    }
    
    @Override
    public void visit(Tree.ValueIterator that) {
        super.visit(that);
        Tree.Term primary = that.getSpecifierExpression().getExpression().getTerm();
        if (primary instanceof Tree.BaseMemberExpression) {
            Declaration bmed = ((Tree.BaseMemberExpression) primary).getDeclaration();
            if (bmed!=null && bmed.equals(dec)) {
                ProducedType vt = that.getVariable().getType().getTypeModel();
                result.intersect(that.getUnit().getIterableType(vt));
            }
        }
    }
    
    @Override
    public void visit(Tree.BooleanCondition that) {
        super.visit(that);
        Tree.Term primary = that.getExpression().getTerm();
        if (primary instanceof Tree.BaseMemberExpression) {
            Declaration bmed = ((Tree.BaseMemberExpression) primary).getDeclaration();
            if (bmed!=null && bmed.equals(dec)) {
                result.intersect(that.getUnit().getBooleanDeclaration().getType());
            }
        }
    }

    @Override
    public void visit(Tree.NonemptyCondition that) {
        super.visit(that);
        Tree.Term primary = that.getVariable().getSpecifierExpression().getExpression().getTerm();
        if (primary instanceof Tree.BaseMemberExpression) {
            Declaration bmed = ((Tree.BaseMemberExpression) primary).getDeclaration();
            if (bmed!=null && bmed.equals(dec)) {
                ProducedType et = that.getUnit().getSequentialElementType(that.getVariable().getType().getTypeModel());
                result.intersect(that.getUnit().getSequentialType(et));
            }
        }
    }
    
    @Override
    public void visit(Tree.ArithmeticOp that) {
        super.visit(that);
        Interface sd = getArithmeticDeclaration(that);
        genericOperatorTerm(sd, that.getLeftTerm());
        genericOperatorTerm(sd, that.getRightTerm());
    }

    @Override
    public void visit(Tree.NegativeOp that) {
        super.visit(that);
        Interface sd = unit.getInvertableDeclaration();
        genericOperatorTerm(sd, that.getTerm());
    }

    @Override
    public void visit(Tree.PrefixOperatorExpression that) {
        super.visit(that);
        Interface sd = unit.getOrdinalDeclaration();
        genericOperatorTerm(sd, that.getTerm());
    }

    @Override
    public void visit(Tree.PostfixOperatorExpression that) {
        super.visit(that);
        Interface sd = unit.getOrdinalDeclaration();
        genericOperatorTerm(sd, that.getTerm());
    }

    @Override
    public void visit(Tree.BitwiseOp that) {
        super.visit(that);
        Interface sd = unit.getSetDeclaration();
        genericOperatorTerm(sd, that.getLeftTerm());
        genericOperatorTerm(sd, that.getRightTerm());
    }

    @Override
    public void visit(Tree.ComparisonOp that) {
        super.visit(that);
        Interface sd = unit.getComparableDeclaration();
        genericOperatorTerm(sd, that.getLeftTerm());
        genericOperatorTerm(sd, that.getRightTerm());
    }

    @Override
    public void visit(Tree.CompareOp that) {
        super.visit(that);
        Interface sd = unit.getComparableDeclaration();
        genericOperatorTerm(sd, that.getLeftTerm());
        genericOperatorTerm(sd, that.getRightTerm());
    }

    @Override
    public void visit(Tree.LogicalOp that) {
        super.visit(that);
        TypeDeclaration sd = unit.getBooleanDeclaration();
        operatorTerm(sd, that.getLeftTerm());
        operatorTerm(sd, that.getRightTerm());
    }

    @Override
    public void visit(Tree.NotOp that) {
        super.visit(that);
        TypeDeclaration sd = unit.getBooleanDeclaration();
        operatorTerm(sd, that.getTerm());
    }

    @Override
    public void visit(Tree.EntryOp that) {
        super.visit(that);
        TypeDeclaration sd = unit.getObjectDeclaration();
        operatorTerm(sd, that.getLeftTerm());
        operatorTerm(sd, that.getRightTerm());
    }

    private Interface getArithmeticDeclaration(Tree.ArithmeticOp that) {
        if (that instanceof Tree.PowerOp) {
            return unit.getExponentiableDeclaration();
        }
        else if (that instanceof Tree.SumOp) {
            return unit.getSummableDeclaration();
        }
        else if (that instanceof Tree.DifferenceOp) {
            return unit.getInvertableDeclaration();
        }
        else if (that instanceof Tree.RemainderOp) {
            return unit.getIntegralDeclaration();
        }
        else {
            return unit.getNumericDeclaration();
        }
    }

    public void operatorTerm(TypeDeclaration sd, Tree.Term lhs) {
        if (lhs instanceof Tree.BaseMemberExpression) {
            Declaration bmed = ((Tree.BaseMemberExpression) lhs).getDeclaration();
            if (bmed!=null && bmed.equals(dec)) {
                result.intersect(sd.getType());
            }
        }
    }
    
    public void genericOperatorTerm(TypeDeclaration sd, Tree.Term lhs) {
        if (lhs instanceof Tree.BaseMemberExpression) {
            Declaration bmed = ((Tree.BaseMemberExpression) lhs).getDeclaration();
            if (bmed!=null && bmed.equals(dec)) {
                result.intersect(lhs.getTypeModel().getSupertype(sd).getTypeArguments().get(0));
            }
        }
    }
    
    //TODO: more operator expressions!
}