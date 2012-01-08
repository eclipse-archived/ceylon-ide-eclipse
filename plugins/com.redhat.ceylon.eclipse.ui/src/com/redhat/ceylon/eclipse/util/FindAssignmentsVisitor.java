package com.redhat.ceylon.eclipse.util;

import java.util.HashSet;
import java.util.Set;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.TypedDeclaration;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.AssignmentOp;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.AttributeDeclaration;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.MemberOrTypeExpression;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.PostfixOperatorExpression;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.PrefixOperatorExpression;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.SpecifierStatement;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.Term;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;

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
        
}
