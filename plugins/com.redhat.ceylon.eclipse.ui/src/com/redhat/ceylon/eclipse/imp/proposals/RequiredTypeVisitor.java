package com.redhat.ceylon.eclipse.imp.proposals;

import com.redhat.ceylon.compiler.typechecker.model.Functional;
import com.redhat.ceylon.compiler.typechecker.model.Parameter;
import com.redhat.ceylon.compiler.typechecker.model.ProducedReference;
import com.redhat.ceylon.compiler.typechecker.model.ProducedType;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;

class RequiredTypeVisitor extends Visitor {
    private Node node;
    ProducedType requiredType = null;
    RequiredTypeVisitor(Node node) {
        this.node = node;
    }
    @Override
    public void visit(Tree.InvocationExpression that) {
        super.visit(that);
        if (that.getPositionalArgumentList()==node) {
            int pos = that.getPositionalArgumentList().getPositionalArguments().size();
            Tree.Primary p = that.getPrimary();
            if (p instanceof Tree.MemberOrTypeExpression) {
                ProducedReference pr = ((Tree.MemberOrTypeExpression) p).getTarget();
                if (pr!=null) {
                    Parameter param = ((Functional) pr.getDeclaration()).getParameterLists()
                            .get(0).getParameters().get(pos);
                    requiredType = pr.getTypedParameter(param).getType();
                }
            }
        }
    }
    @Override
    public void visit(Tree.SpecifiedArgument that) {
        super.visit(that);
        if (that.getSpecifierExpression()==node) {
            //TODO: does not substitute type args!
            requiredType = that.getParameter().getType();
        }
    }
    @Override
    public void visit(Tree.SpecifierStatement that) {
        super.visit(that);
        if (that.getSpecifierExpression()==node) {
            requiredType = that.getBaseMemberExpression().getTypeModel();
        }
    }
    @Override
    public void visit(Tree.AttributeDeclaration that) {
        super.visit(that);
        if (that.getSpecifierOrInitializerExpression()==node) {
            requiredType = that.getType().getTypeModel();
        }
    }
    @Override
    public void visit(Tree.AssignOp that) {
        super.visit(that);
        if (that==node) {
            requiredType = that.getLeftTerm().getTypeModel();
        }
    }
    @Override
    public void visit(Tree.Return that) {
        super.visit(that);
        if (that==node) {
            requiredType = CeylonContentProposer.type(that.getDeclaration());
        }
    }
}