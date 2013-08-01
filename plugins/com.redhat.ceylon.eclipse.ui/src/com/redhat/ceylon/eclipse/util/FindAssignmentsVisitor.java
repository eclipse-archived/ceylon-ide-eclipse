package com.redhat.ceylon.eclipse.util;

import java.util.HashSet;
import java.util.Set;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.Parameter;
import com.redhat.ceylon.compiler.typechecker.model.TypedDeclaration;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.AssignmentOp;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.AttributeDeclaration;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.InitializerParameter;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.MemberOrTypeExpression;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.PostfixOperatorExpression;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.PrefixOperatorExpression;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.SpecifierStatement;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.Term;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.Variable;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;

//TODO: fix all the copy/paste from FindReferenceVisitor
public class FindAssignmentsVisitor extends Visitor {
	
	private Declaration declaration;
	private final Set<Node> nodes = new HashSet<Node>();
	
	public FindAssignmentsVisitor(Declaration declaration) {
	    if (declaration instanceof TypedDeclaration) {
	        Declaration od = declaration;
	        while (od!=null) {
	            declaration = od;
	            od = ((TypedDeclaration) od).getOriginalDeclaration();
	        }
	    }
		this.declaration = declaration;
	}
	
	public Declaration getDeclaration() {
        return declaration;
    }
	
	public Set<Node> getNodes() {
		return nodes;
	}
	
    protected boolean isReference(Parameter p) {
        return p!=null && isReference(p.getModel());
    }
    
	protected boolean isReference(Declaration ref) {
	    return ref!=null && declaration.refines(ref);
	}
	
    private boolean isReference(Term lhs) {
        return lhs instanceof MemberOrTypeExpression && 
                isReference(((MemberOrTypeExpression)lhs).getDeclaration());
    }
        
    @Override
    public void visit(SpecifierStatement that) {
        super.visit(that);
        Term lhs = that.getBaseMemberExpression();
        if (isReference(lhs)) {
            nodes.add(that.getSpecifierExpression());
        }
    }

    public void visit(InitializerParameter that) {
        super.visit(that);
        if (that.getSpecifierExpression()!=null) {
            if (isReference(that.getParameterModel())) {
                nodes.add(that.getSpecifierExpression());
            }
        }
    }

    @Override
    public void visit(AssignmentOp that) {
        super.visit(that);
        Term lhs = that.getLeftTerm();
        if (isReference(lhs)) {
            nodes.add(that.getRightTerm());
        }
    }
        
    @Override
    public void visit(PostfixOperatorExpression that) {
        super.visit(that);
        Term lhs = that.getTerm();
        if (isReference(lhs)) {
            nodes.add(that.getTerm());
        }
    }
        
    @Override
    public void visit(PrefixOperatorExpression that) {
        super.visit(that);
        Term lhs = that.getTerm();
        if (isReference(lhs)) {
            nodes.add(that.getTerm());
        }
    }
        
    @Override
    public void visit(AttributeDeclaration that) {
        super.visit(that);
        if (that.getSpecifierOrInitializerExpression()!=null && 
                isReference(that.getDeclarationModel())) {
            nodes.add(that.getSpecifierOrInitializerExpression());
        }
    }
        
    @Override
    public void visit(Variable that) {
        super.visit(that);
        if (that.getSpecifierExpression()!=null && 
                isReference(that.getDeclarationModel())) {
            nodes.add(that.getSpecifierExpression());
        }
    }
    
    @Override
    public void visit(Tree.NamedArgument that) {
        if (isReference(that.getParameter())) {
            if (that instanceof Tree.SpecifiedArgument) {
                nodes.add(((Tree.SpecifiedArgument) that).getSpecifierExpression());
            }
            else {
                nodes.add(that);
            }
        }
        super.visit(that);
    }
        
    @Override
    public void visit(Tree.PositionalArgument that) {
        if (isReference(that.getParameter())) {
            nodes.add(that);
        }
        super.visit(that);
    }
        
}
