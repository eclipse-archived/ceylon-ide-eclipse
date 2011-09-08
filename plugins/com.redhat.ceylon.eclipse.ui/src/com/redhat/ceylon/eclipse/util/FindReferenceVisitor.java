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
	
	protected boolean isReference(Declaration ref) {
	    return ref!=null && declaration.refines(ref);
	}
	
    @Override
    public void visit(Tree.ExtendedTypeExpression that) {}
    
	@Override
	public void visit(Tree.MemberOrTypeExpression that) {
		//TODO: handle refinement!
		if (isReference(that.getDeclaration())) {
			nodes.add(that);
		}
		super.visit(that);
	}
		
	@Override
	public void visit(Tree.NamedArgument that) {
		if (isReference(that.getParameter())) {
			nodes.add(that);
		}
		super.visit(that);
	}
		
	@Override
	public void visit(Tree.SimpleType that) {
		if (isReference(that.getTypeModel().getDeclaration())) {
			nodes.add(that);
		}
		super.visit(that);
	}
	
	/*@Override
	public void visit(Tree.SyntheticVariable that) {}*/

	@Override
	public void visit(Tree.ImportMemberOrType that) {
		if (isReference(that.getDeclarationModel())) {
			nodes.add(that);
		}
		super.visit(that);
	}
		
}
