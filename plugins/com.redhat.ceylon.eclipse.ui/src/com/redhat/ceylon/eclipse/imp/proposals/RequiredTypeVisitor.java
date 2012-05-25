package com.redhat.ceylon.eclipse.imp.proposals;

import java.util.List;

import com.redhat.ceylon.compiler.typechecker.model.Functional;
import com.redhat.ceylon.compiler.typechecker.model.Parameter;
import com.redhat.ceylon.compiler.typechecker.model.ProducedReference;
import com.redhat.ceylon.compiler.typechecker.model.ProducedType;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.NamedArgumentList;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.PositionalArgumentList;
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
        PositionalArgumentList pal = that.getPositionalArgumentList();
        if (pal==node) {
            int pos = pal.getPositionalArguments().size();
            ProducedReference pr = getTarget(that);
            if (pr!=null) {
                Parameter param = getParameters(pr).get(pos);
                requiredType = pr.getTypedParameter(param).getType();
                if (param.isSequenced()) {
                    requiredType = that.getUnit().getElementType(requiredType);
                }
            }
        }
        NamedArgumentList nal = that.getNamedArgumentList();
        if (nal==node || (nal!=null &&
            nal.getSequencedArgument().getExpressionList()==node)) {
            ProducedReference pr = getTarget(that);
            if (pr!=null) {
                List<Parameter> params = getParameters(pr);
                Parameter param = params.get(params.size()-1);
                if (param.isSequenced()) {
                    requiredType = pr.getTypedParameter(param).getType();
                    requiredType = that.getUnit().getElementType(requiredType);
                }
            }
        }
    }

    private static ProducedReference getTarget(Tree.InvocationExpression that) {
        Tree.Primary p = that.getPrimary();
        if (p instanceof Tree.MemberOrTypeExpression) {
            return ((Tree.MemberOrTypeExpression) p).getTarget();
        }
        else {
            return null;
        }
    }
    
    private static List<Parameter> getParameters(ProducedReference pr) {
        return ((Functional) pr.getDeclaration()).getParameterLists()
                .get(0).getParameters();
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