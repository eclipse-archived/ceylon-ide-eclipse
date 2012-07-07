package com.redhat.ceylon.eclipse.code.outline;

import org.antlr.runtime.Token;

import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;

public class PackageNode extends Node {
	
	private String packageName;
	
	public String getPackageName() {
		return packageName;
	}
	
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	
	public PackageNode(Token token) {
		super(token);
	}

	@Override
	public void visit(Visitor visitor) {}

	@Override
	public void visitChildren(Visitor visitor) {}

}
