package com.redhat.ceylon.eclipse.util;

import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;

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

}
