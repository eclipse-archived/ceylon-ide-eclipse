package com.redhat.ceylon.eclipse.code.quickfix;

import com.redhat.ceylon.compiler.typechecker.model.ProducedType;
import com.redhat.ceylon.compiler.typechecker.model.TypedDeclaration;
import com.redhat.ceylon.compiler.typechecker.tree.NaturalVisitor;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;

class FindArgumentsVisitor extends Visitor 
        implements NaturalVisitor {
    Tree.MemberOrTypeExpression smte;
    Tree.NamedArgumentList namedArgs;
    Tree.PositionalArgumentList positionalArgs;
    ProducedType currentType;
    ProducedType expectedType;
    boolean found = false;
    
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
        ProducedType ct = currentType;
        currentType = that.getParameter()==null ? 
                null : that.getParameter().getType();
        super.visit(that);
        currentType = ct;
    }
    @Override
    public void visit(Tree.PositionalArgument that) {
        ProducedType ct = currentType;
        currentType = that.getParameter()==null ? 
                null : that.getParameter().getType();
        super.visit(that);
        currentType = ct;
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
        currentType = that.getUnit().getCloseableDeclaration().getType();
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
        ProducedType varType = that.getVariable().getType().getTypeModel();
        currentType = that.getUnit().getOptionalType(varType);
        super.visit(that);
        currentType = null;
    }
    @Override
    public void visit(Tree.NonemptyCondition that) {
        ProducedType varType = that.getVariable().getType().getTypeModel();
        currentType = that.getUnit().getEmptyType(varType);
        super.visit(that);
        currentType = null;
    }
    @Override
    public void visit(Tree.SatisfiesCondition that) {
        ProducedType objectType = that.getUnit().getValueDeclaration().getType();
        currentType = objectType;
        super.visit(that);
        currentType = null;
    }
    @Override
    public void visit(Tree.ValueIterator that) {
        ProducedType varType = that.getVariable().getType().getTypeModel();
        currentType = that.getUnit().getIterableType(varType);
        super.visit(that);
        currentType = null;
    }
    @Override
    public void visit(Tree.KeyValueIterator that) {
        ProducedType keyType = that.getKeyVariable().getType().getTypeModel();
        ProducedType valueType = that.getValueVariable().getType().getTypeModel();
        currentType = that.getUnit().getIterableType(that.getUnit()
                .getEntryType(keyType, valueType));
        super.visit(that);
        currentType = null;
    }
    @Override
    public void visit(Tree.SpecifierStatement that) {
        currentType = that.getBaseMemberExpression().getTypeModel();
        super.visit(that);
        currentType = null;
    }
    @Override
    public void visit(Tree.AssignmentOp that) {
        ProducedType ct = currentType;
        currentType = that.getLeftTerm().getTypeModel();
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
}