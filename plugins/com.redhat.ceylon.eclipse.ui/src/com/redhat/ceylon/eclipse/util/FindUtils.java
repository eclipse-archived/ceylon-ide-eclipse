package com.redhat.ceylon.eclipse.util;

import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.Statement;

public class FindUtils {

	public static Tree.Declaration findDeclaration(Tree.CompilationUnit cu, Node node) {
		FindDeclarationVisitor fcv = new FindDeclarationVisitor(node);
		fcv.visit(cu);
		return fcv.getDeclarationNode();
	}

	public static Tree.TypedArgument findArgument(Tree.CompilationUnit cu, Node node) {
		FindArgumentVisitor fcv = new FindArgumentVisitor(node);
		fcv.visit(cu);
		return fcv.getArgumentNode();
	}

	public static Statement findStatement(Tree.CompilationUnit cu, Node node) {
	    FindStatementVisitor visitor = new FindStatementVisitor(node, false);
	    cu.visit(visitor);
	    return visitor.getStatement();
	}

	public static Statement findToplevelStatement(Tree.CompilationUnit cu, Node node) {
	    FindStatementVisitor visitor = new FindStatementVisitor(node, true);
	    cu.visit(visitor);
	    return visitor.getStatement();
	}

}
