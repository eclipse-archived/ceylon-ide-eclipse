package com.redhat.ceylon.eclipse.code.correct;

import static com.redhat.ceylon.model.typechecker.model.Util.isTypeUnknown;
import static com.redhat.ceylon.model.typechecker.model.Util.producedType;
import static com.redhat.ceylon.model.typechecker.model.Util.unionType;

import com.redhat.ceylon.model.typechecker.model.Type;
import com.redhat.ceylon.model.typechecker.model.TypedDeclaration;
import com.redhat.ceylon.model.typechecker.model.Unit;
import com.redhat.ceylon.compiler.typechecker.tree.NaturalVisitor;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;

class FindArgumentsVisitor extends Visitor 
        implements NaturalVisitor {
    
    Tree.MemberOrTypeExpression smte;
    Tree.NamedArgumentList namedArgs;
    Tree.PositionalArgumentList positionalArgs;
    Type currentType;
    Type expectedType;
    boolean found = false;
    boolean inEnumeration = false;
    
    FindArgumentsVisitor(Tree.MemberOrTypeExpression smte) {
        this.smte = smte;
    }
    
    @Override
    public void visit(Tree.MemberOrTypeExpression that) {
        super.visit(that);
        if (that==smte) {
            expectedType = currentType;
            found = true;
        }
    }
    
    @Override
    public void visit(Tree.InvocationExpression that) {
        super.visit(that);
        if (that.getPrimary()==smte) {
            namedArgs = that.getNamedArgumentList();
            positionalArgs = that.getPositionalArgumentList();
        }
    }
    @Override
    public void visit(Tree.NamedArgument that) {
        Type ct = currentType;
        currentType = that.getParameter()==null ? 
                null : that.getParameter().getType();
        super.visit(that);
        currentType = ct;
    }
    @Override
    public void visit(Tree.PositionalArgument that) {
        if (inEnumeration) {
            inEnumeration = false;
            super.visit(that);
            inEnumeration = true;
        }
        else {
            Type ct = currentType;
            currentType = that.getParameter()==null ? 
                    null : that.getParameter().getType();
            super.visit(that);
            currentType = ct;
        }
    }
    @Override
    public void visit(Tree.AttributeDeclaration that) {
        currentType = that.getType().getTypeModel();
        super.visit(that);
        currentType = null;
    }
    /*@Override
    public void visit(Tree.Variable that) {
        currentType = that.getType().getTypeModel();
        super.visit(that);
        currentType = null;
    }*/
    @Override
    public void visit(Tree.Resource that) {
        Unit unit = that.getUnit();
        currentType = unionType(unit.getDestroyableDeclaration().getType(), 
                unit.getObtainableDeclaration().getType(), unit);
        super.visit(that);
        currentType = null;
    }
    @Override
    public void visit(Tree.BooleanCondition that) {
        currentType = that.getUnit().getBooleanDeclaration().getType();
        super.visit(that);
        currentType = null;
    }
    @Override
    public void visit(Tree.ExistsCondition that) {
        Tree.Statement st = that.getVariable();
        if (st instanceof Tree.Variable) {
            Type varType = 
                    ((Tree.Variable) st).getType().getTypeModel();
            currentType = that.getUnit().getOptionalType(varType);
        }
        super.visit(that);
        currentType = null;
    }
    @Override
    public void visit(Tree.NonemptyCondition that) {
        Tree.Statement st = that.getVariable();
        if (st instanceof Tree.Variable) {
            Type varType = 
                    ((Tree.Variable) st).getType().getTypeModel();
            currentType = that.getUnit().getEmptyType(varType);
        }
        super.visit(that);
        currentType = null;
    }
    @Override
    public void visit(Tree.Exists that) {
        Type oit = currentType;
        currentType = that.getUnit().getAnythingDeclaration().getType();
        super.visit(that);
        currentType = oit;
    }
    @Override
    public void visit(Tree.Nonempty that) {
        Unit unit = that.getUnit();
        Type oit = currentType;
        currentType = unit.getSequentialType(unit.getAnythingDeclaration().getType());
        super.visit(that);
        currentType = oit;
    }
    @Override
    public void visit(Tree.SatisfiesCondition that) {
        Type objectType = that.getUnit().getObjectDeclaration().getType();
        currentType = objectType;
        super.visit(that);
        currentType = null;
    }
    @Override
    public void visit(Tree.ValueIterator that) {
        Type varType = that.getVariable().getType().getTypeModel();
        currentType = that.getUnit().getIterableType(varType);
        super.visit(that);
        currentType = null;
    }
    /*@Override
    public void visit(Tree.PatternIterator that) {
        Type keyType = that.getKeyVariable().getType().getTypeModel();
        Type valueType = that.getValueVariable().getType().getTypeModel();
        currentType = that.getUnit().getIterableType(that.getUnit()
                .getEntryType(keyType, valueType));
        super.visit(that);
        currentType = null;
    }*/
    @Override
    public void visit(Tree.BinaryOperatorExpression that) {
        if (currentType==null) {
            //fallback strategy for binary operators
            //TODO: not quite appropriate for a
            //      couple of them!
            Tree.Term rightTerm = that.getRightTerm();
            Tree.Term leftTerm = that.getLeftTerm();
            if (rightTerm!=null &&
                    !isTypeUnknown(rightTerm.getTypeModel())) {
                currentType = rightTerm.getTypeModel();
            }
            if (leftTerm!=null) leftTerm.visit(this);
            if (leftTerm!=null &&
                    !isTypeUnknown(leftTerm.getTypeModel())) {
                currentType = leftTerm.getTypeModel();
            }
            if (rightTerm!=null) rightTerm.visit(this);
            currentType = null;
        }
        else {
            super.visit(that);
        }
    }
    @Override public void visit(Tree.EntryOp that) {
        Unit unit = that.getUnit();
        if (currentType!=null &&
                currentType.getDeclaration().equals(unit.getEntryDeclaration())) {
            Type oit = currentType;
            currentType = unit.getKeyType(oit);
            if (that.getLeftTerm()!=null) {
                that.getLeftTerm().visit(this);
            }
            currentType = unit.getValueType(oit);
            if (that.getRightTerm()!=null) {
                that.getRightTerm().visit(this);
            }
            currentType = oit;
        }
        else {
            Type oit = currentType;
            currentType = that.getUnit().getObjectDeclaration().getType();
            super.visit(that);
            currentType = oit;
        }
    }
    @Override public void visit(Tree.RangeOp that) {
        Unit unit = that.getUnit();
        if (currentType!=null &&
                unit.isIterableType(currentType)) {
            Type oit = currentType;
            currentType = unit.getIteratedType(oit);
            super.visit(that);
            currentType = oit;
        }
        else {
            Type oit = currentType;
            currentType = that.getUnit().getObjectDeclaration().getType();
            super.visit(that);
            currentType = oit;
        }
    }
    @Override public void visit(Tree.SegmentOp that) {
        Unit unit = that.getUnit();
        if (currentType!=null &&
                unit.isIterableType(currentType)) {
            Type oit = currentType;
            currentType = unit.getIteratedType(oit);
            super.visit(that);
            currentType = oit;
        }
        else {
            Type oit = currentType;
            currentType = that.getUnit().getObjectDeclaration().getType();
            super.visit(that);
            currentType = oit;
        }
    }
    @Override public void visit(Tree.IndexExpression that) {
        Unit unit = that.getUnit();
        Tree.ElementOrRange eor = that.getElementOrRange();
        Tree.Primary primary = that.getPrimary();
        Type oit = currentType;
        Type indexType = unit.getObjectDeclaration().getType();
        if (eor instanceof Tree.Element) {
            Tree.Expression e = ((Tree.Element) eor).getExpression();
            if (e!=null && !isTypeUnknown(e.getTypeModel())) {
                indexType = e.getTypeModel();
            }
            
        }
        if (eor instanceof Tree.ElementRange) {
            Tree.Expression l = ((Tree.ElementRange) eor).getLowerBound();
            Tree.Expression u = ((Tree.ElementRange) eor).getUpperBound();
            if (l!=null && !isTypeUnknown(l.getTypeModel())) {
                indexType = l.getTypeModel();
            }
            else if (u!=null && !isTypeUnknown(u.getTypeModel())) {
                indexType = u.getTypeModel();
            }
        }
        currentType = producedType(unit.getCorrespondenceDeclaration(), 
                indexType, unit.getDefiniteType(currentType));
        if (primary!=null) {
            primary.visit(this);
        }
        currentType = unit.getObjectDeclaration().getType();
        if (primary!=null && !isTypeUnknown(primary.getTypeModel())) {
            //TODO: move this to Unit!
            Type supertype = primary.getTypeModel()
                    .getSupertype(unit.getCorrespondenceDeclaration());
            if (supertype!=null && !supertype.getTypeArgumentList().isEmpty()) {
                currentType = supertype.getTypeArgumentList().get(0);
            }
        }
        if (eor!=null) {
            eor.visit(this);
        }
        currentType = oit;
    }
    @Override public void visit(Tree.LogicalOp that) {
        Unit unit = that.getUnit();
        Type oit = currentType;
        currentType = unit.getBooleanDeclaration().getType();
        super.visit(that);
        currentType = oit;
    }
    @Override public void visit(Tree.BitwiseOp that) {
        Unit unit = that.getUnit();
        Type oit = currentType;
        currentType = unit.getSetType(unit.getObjectDeclaration().getType()).getType();
        super.visit(that);
        currentType = oit;
    }
    @Override public void visit(Tree.NotOp that) {
        Unit unit = that.getUnit();
        Type oit = currentType;
        currentType = unit.getBooleanDeclaration().getType();
        super.visit(that);
        currentType = oit;
    }
    @Override public void visit(Tree.InOp that) {
        Unit unit = that.getUnit();
        Type oit = currentType;
        currentType = unit.getObjectDeclaration().getType();
        if (that.getLeftTerm()!=null) {
            that.getLeftTerm().visit(this);
        }
        currentType = unit.getCategoryDeclaration().getType();
        if (that.getRightTerm()!=null) {
            that.getRightTerm().visit(this);
        }
        currentType = oit;
    }
    @Override public void visit(Tree.SequenceEnumeration that) {
        Unit unit = that.getUnit();
        if (currentType!=null &&
                unit.isIterableType(currentType)) {
            Type oit = currentType;
            boolean oie = inEnumeration;
            inEnumeration = true;
            currentType = unit.getIteratedType(oit);
            super.visit(that);
            currentType = oit;
            inEnumeration = oie;
        }
        else {
            Type oit = currentType;
            currentType = that.getUnit().getAnythingDeclaration().getType();
            super.visit(that);
            currentType = oit;
        }
    }
    @Override public void visit(Tree.Tuple that) {
        Unit unit = that.getUnit();
        if (currentType!=null &&
                unit.isIterableType(currentType)) {
            Type oit = currentType;
            boolean oie = inEnumeration;
            inEnumeration = true;
            currentType = unit.getIteratedType(oit);
            super.visit(that);
            currentType = oit;
            inEnumeration = oie;
        }
        else {
            Type oit = currentType;
            currentType = that.getUnit().getAnythingDeclaration().getType();
            super.visit(that);
            currentType = oit;
        }
    }
    @Override
    public void visit(Tree.SpecifierStatement that) {
        currentType = that.getBaseMemberExpression().getTypeModel();
        Tree.SpecifierExpression se = that.getSpecifierExpression();
        if (se!=null) {
            if (isTypeUnknown(currentType) &&
                    se.getExpression()!=null) {
                currentType = se.getExpression().getTypeModel();
            }
        }
        else {
            currentType = null;
        }
        super.visit(that);
        currentType = null;
    }
    @Override
    public void visit(Tree.AssignmentOp that) {
        Type ct = currentType;
        Tree.Term leftTerm = that.getLeftTerm();
        Tree.Term rightTerm = that.getRightTerm();
        if (leftTerm!=null && rightTerm!=null) {
            currentType = leftTerm.getTypeModel();
            if (isTypeUnknown(currentType) ) {
                currentType = rightTerm.getTypeModel();
            }
        }
        else {
            currentType = null;
        }
        super.visit(that);
        currentType = ct;
    }
    @Override
    public void visit(Tree.Return that) {
        if (that.getDeclaration() instanceof TypedDeclaration) {
            currentType = ((TypedDeclaration) that.getDeclaration()).getType();
        }
        super.visit(that);
        currentType = null;
    }
    @Override
    public void visit(Tree.Throw that) {
        super.visit(that);
        //set expected type to Exception
    }
    @Override
    public void visitAny(Node that) {
        if (!found) super.visitAny(that);
    }
    @Override
    public void visit(Tree.FunctionArgument that) {
        Type ct = currentType;
        currentType = null;
        super.visit(that);
        currentType = ct;
    }
}