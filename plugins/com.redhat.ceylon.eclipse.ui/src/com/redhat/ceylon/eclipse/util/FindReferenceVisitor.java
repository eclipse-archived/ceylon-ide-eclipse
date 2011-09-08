package com.redhat.ceylon.eclipse.util;

import java.util.HashSet;
import java.util.Set;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;

public class FindReferenceVisitor extends Visitor {
	
	private final Declaration declaration;
	private final Set<Node> nodes = new HashSet<Node>();
	
	public FindReferenceVisitor(Declaration declaration) {
		this.declaration = declaration;
	}
	
	public Set<Node> getNodes() {
		return nodes;
	}
	
	protected boolean equals(Declaration x, Declaration y) {
	    return x!=null && y!=null && x.equals(y);
	}
	
    @Override
    public void visit(Tree.ExtendedTypeExpression that) {}
    
	@Override
	public void visit(Tree.MemberOrTypeExpression that) {
		//TODO: handle refinement!
		if (equals(that.getDeclaration(), declaration)) {
			nodes.add(that);
		}
		super.visit(that);
	}
		
	@Override
	public void visit(Tree.NamedArgument that) {
		if (equals(that.getParameter(), declaration)) {
			nodes.add(that);
		}
		super.visit(that);
	}
		
	@Override
	public void visit(Tree.SimpleType that) {
		if (equals(that.getTypeModel().getDeclaration(), declaration)) {
			nodes.add(that);
		}
		super.visit(that);
	}
	
	/*@Override
	public void visit(Tree.SyntheticVariable that) {}*/

	@Override
	public void visit(Tree.ImportMemberOrType that) {
		if (equals(that.getDeclarationModel(), declaration)) {
			nodes.add(that);
		}
		super.visit(that);
	}
		
}
