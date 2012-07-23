package com.redhat.ceylon.eclipse.test;

import java.io.ByteArrayInputStream;

import org.antlr.runtime.ANTLRInputStream;
import org.antlr.runtime.CommonTokenStream;

import com.redhat.ceylon.compiler.typechecker.parser.CeylonLexer;
import com.redhat.ceylon.compiler.typechecker.parser.CeylonParser;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.CompilationUnit;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.Statement;
import com.redhat.ceylon.eclipse.code.parse.CeylonSourcePositionLocator;

public class TestUtil {

	public static CompilationUnit getCompilationUnit(String contents) throws Exception {
		ByteArrayInputStream inputStream = new ByteArrayInputStream(contents.getBytes());
		ANTLRInputStream input;
		input = new ANTLRInputStream(inputStream);
	    CeylonLexer lexer = new CeylonLexer(input);
	    CommonTokenStream tokenStream = new CommonTokenStream(lexer);
	    tokenStream.fill();
	    CeylonParser parser = new CeylonParser(tokenStream);
	    CompilationUnit cu = parser.compilationUnit();
		return cu;
	}

	public static Statement findStatementAtOffset(CompilationUnit cu, int offset) {
		Node node = CeylonSourcePositionLocator.findNode(cu, offset);
	    Statement statement = CeylonSourcePositionLocator.findStatement(cu, node);
		return statement;
	}

}
