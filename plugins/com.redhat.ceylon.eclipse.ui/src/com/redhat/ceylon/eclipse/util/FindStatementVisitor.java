package com.redhat.ceylon.eclipse.util;

import com.redhat.ceylon.compiler.typechecker.tree.NaturalVisitor;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;

public class FindStatementVisitor extends Visitor 
		implements NaturalVisitor {
	Tree.Term term;
	Tree.Statement statement;
	Tree.Statement currentStatement;
	public Tree.Statement getStatement() {
		return statement;
	}
	public FindStatementVisitor(Tree.Term term) {
		this.term=term;
	}
	@Override
	public void visit(Tree.Term that) {
		if (that==term) {
			statement=currentStatement;
		}
		super.visit(that);
	}
	@Override
	public void visit(Tree.Statement that) {
	    if (!(that instanceof Tree.Variable || 
	            that instanceof Tree.Parameter)) {
	        currentStatement = that;
	    }
		super.visit(that);
	}
	public void visitAny(Node node) {
		if (statement==null) {
			super.visitAny(node);
		}
	}
}