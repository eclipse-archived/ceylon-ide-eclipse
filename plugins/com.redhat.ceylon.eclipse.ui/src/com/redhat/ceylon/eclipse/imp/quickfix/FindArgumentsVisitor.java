package com.redhat.ceylon.eclipse.imp.quickfix;

import com.redhat.ceylon.compiler.typechecker.model.ProducedType;
import com.redhat.ceylon.compiler.typechecker.model.TypedDeclaration;
import com.redhat.ceylon.compiler.typechecker.tree.NaturalVisitor;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;

class FindArgumentsVisitor extends Visitor 
        implements NaturalVisitor {
    Tree.StaticMemberOrTypeExpression smte;
    Tree.NamedArgumentList namedArgs;
    Tree.PositionalArgumentList positionalArgs;
    ProducedType currentType;
    ProducedType expectedType;
    boolean found = false;
    FindArgumentsVisitor(Tree.StaticMemberOrTypeExpression smte) {
        this.smte = smte;
    }
    
    @Override
    public void visit(Tree.StaticMemberOrTypeExpression that) {
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
        currentType = that.getParameter().getType();
        super.visit(that);
        currentType = null;
    }
    @Override
    public void visit(Tree.PositionalArgument that) {
        currentType = that.getParameter().getType();
        super.visit(that);
        currentType = null;
    }
    @Override
    public void visit(Tree.AttributeDeclaration that) {
        currentType = that.getType().getTypeModel();
        super.visit(that);
        currentType = null;
    }
    @Override
    public void visit(Tree.Variable that) {
        currentType = that.getType().getTypeModel();
        super.visit(that);
        currentType = null;
    }
    @Override
    public void visit(Tree.ValueIterator that) {
        currentType = that.getVariable().getType().getTypeModel();
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
        currentType = that.getLeftTerm().getTypeModel();
        super.visit(that);
        currentType = null;
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