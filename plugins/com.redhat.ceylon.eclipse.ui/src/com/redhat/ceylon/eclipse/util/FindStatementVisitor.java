package com.redhat.ceylon.eclipse.util;

import com.redhat.ceylon.compiler.typechecker.tree.NaturalVisitor;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;

public class FindStatementVisitor extends Visitor 
		implements NaturalVisitor {
	
	private final Tree.Term term;
	private Tree.Statement statement;
	private Tree.Statement currentStatement;
	private final boolean toplevel;
	private boolean currentlyToplevel=true;
	
	public Tree.Statement getStatement() {
		return statement;
	}
	
	public FindStatementVisitor(Tree.Term term, boolean toplevel) {
		this.term = term;
		this.toplevel = toplevel;
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
    	if (!toplevel || currentlyToplevel) {
    		if (!(that instanceof Tree.Variable || 
    				that instanceof Tree.Parameter)) {
    			currentStatement = that;
	    	}
	    }
	    boolean octl = currentlyToplevel;
	    currentlyToplevel = false;
		super.visit(that);
		currentlyToplevel = octl;
	}
	
	public void visitAny(Node node) {
		if (statement==null) {
			super.visitAny(node);
		}
	}
	
}