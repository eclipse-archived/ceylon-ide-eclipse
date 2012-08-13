package com.redhat.ceylon.eclipse.code.quickfix;

import static com.redhat.ceylon.eclipse.test.TestUtil.findStatementAtOffset;
import static com.redhat.ceylon.eclipse.test.TestUtil.getCompilationUnit;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.fail;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.ltk.core.refactoring.TextChange;
import org.junit.Test;

import com.redhat.ceylon.compiler.typechecker.tree.Tree.CompilationUnit;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.Statement;

public class TestConvertIfElseToThenElse {
	
	

	private static final String FOOTER = "\n}";
	private static final String HEADER = "void run() {\n\t";
	
	@Test
	public void testReturnThenElse() throws Exception {
        testConversion("if (test) {\n" + 
		"		return x;\n" + 
		"	}\n" + 
		"	else {\n" + 
		"		return y;\n" + 
		"	}", 
        "return test then x else y;"); 
	}
	
	@Test
	public void testReturnThen() throws Exception {
        testConversion("if (test) {\n" + 
		"		return x;\n" + 
		"	}\n" + 
		"	else {\n" + 
		"		return null;\n" + 
		"	}", 
		"return test then x;"); 
	}
	
	@Test
	public void testReturnElse() throws Exception {
        testConversion("if (exists obj) {\n" + 
		"		return obj;\n" + 
		"	}\n" + 
		"	else {\n" + 
		"		return default;\n" + 
		"	}", 
		"return obj else default;"); 
	}
	
	@Test
	public void testReturnElseProcessOnceMethod() throws Exception {
        testConversion("if (exists first = process.first()) {\n" + 
		"		return first;\n" + 
		"	}\n" + 
		"	else {\n" + 
		"		return default;\n" + 
		"	}", 
		"return process.first() else default;"); 
	}

	@Test
	public void testReturnElseProcessOnceAttribute() throws Exception {
        testConversion("if (exists first = process.first) {\n" + 
		"		return first;\n" + 
		"	}\n" + 
		"	else {\n" + 
		"		return default;\n" + 
		"	}", 
		"return process.first else default;"); 
	}

	@Test
	public void testReturnNestedThenElse() throws Exception {
        testConversion("if (test) {\n" + 
		"		return test2 then x else y;\n" + 
		"	}\n" + 
		"	else {\n" + 
		"		return test2 then z;\n" + 
		"	}", 
		"return test then (test2 then x else y) else (test2 then z);"); 
	}

	@Test
	public void testReturnThenElseAssign() throws Exception {
        testConversion("if (test) {\n" + 
		"		return x;\n" + 
		"	}\n" + 
		"	else {\n" + 
		"		return v1 := \"\";\n" + 
		"	}", 
        "return test then x else (v1 := \"\");"); 
	}

	@Test
	public void testDeclareThenElse() throws Exception {
        String declaration = "Object o;\n\t";
		testConversion(declaration + 
		"if (test) {\n" + 
		"		o = x;\n" + 
		"	}\n" + 
		"	else {\n" + 
		"		o = y;\n" + 
		"	}", 
        "Object o = test then x else y;",
        declaration.length()); 
	}
	
	@Test
	public void testDeclareThen() throws Exception {
        String declaration = "Object o;\n\t";
		testConversion(declaration + 
		"if (test) {\n" + 
		"		o = default;\n" + 
		"	}\n" + 
		"	else {\n" + 
		"		o = null;\n" + 
		"	}", 
        "Object o = test then default;",
        declaration.length()); 
	}
	
	@Test
	public void testDeclareElse() throws Exception {
        String decl = "Object o;\n\t";
		testConversion(decl + 
		"if (exists obj) {\n" + 
		"		o = obj;\n" + 
		"	}\n" + 
		"	else {\n" + 
		"		o = default;\n" + 
		"	}", 
        "Object o = obj else default;",
        decl.length()); 
	}
	
	@Test
	public void testDeclareElseProcessOnceMethod() throws Exception {
        String decl = "Object o;\n\t";
		testConversion(decl + 
		"if (exists first = process.first()) {\n" + 
		"		o = first;\n" + 
		"	}\n" + 
		"	else {\n" + 
		"		o = default;\n" + 
		"	}", 
        "Object o = process.first() else default;", 
        decl.length()); 
	}

	@Test
	public void testDeclareElseProcessOnceAttribute() throws Exception {
        String decl = "Object o;\n\t";
		testConversion(decl + 
		"if (exists firstAttr = process.firstAttr) {\n" + 
		"		o = firstAttr;\n" + 
		"	}\n" + 
		"	else {\n" + 
		"		o = default;\n" + 
		"	}", 
        "Object o = process.firstAttr else default;", 
        decl.length()); 
	}

	@Test
	public void testSpeciyElseNoDeclaration() throws Exception {
		testConversion(
		"if (exists obj) {\n" + 
		"		o = obj;\n" + 
		"	}\n" + 
		"	else {\n" + 
		"		o = default;\n" + 
		"	}", 
	    "o = obj else default;"); 
	}

	@Test
	public void testSpeciyElseDeclarationToFarAway() throws Exception {
		String decl = "Object o;\n" +
				"	x++;\n\t";
		testConversion(decl +
		"if (exists obj) {\n" + 
		"		o = obj;\n" + 
		"	}\n" + 
		"	else {\n" + 
		"		o = default;\n" + 
		"	}", 
	    decl + "o = obj else default;",
	    decl.length()); 
	}
	
	@Test
	public void testDeclareElseWithWhitspace() throws Exception {
        String decl = "Object o;\n\n\t";
		testConversion(decl + 
		"if (exists obj) {\n" + 
		"		o = obj;\n" + 
		"	}\n" + 
		"	else {\n" + 
		"		o = default;\n" + 
		"	}", 
        "Object o = obj else default;",
        decl.length()); 
	}

	
	@Test
	public void testSpeciyElseDeclarationOtherIdentifier() throws Exception {
		String decl = "Object o;\n" +
				"	Object x;\n\t";
		testConversion(decl +
		"if (exists obj) {\n" + 
		"		o = obj;\n" + 
		"	}\n" + 
		"	else {\n" + 
		"		o = default;\n" + 
		"	}", 
	    decl + "o = obj else default;",
	    decl.length()); 
	}
	
	@Test
	public void testDeclareVariableThenElse() throws Exception {
        String decl = "variable Object v;\n\t";
		testConversion(decl + 
		"if (test) {\n" + 
		"		v := x;\n" + 
		"	}\n" + 
		"	else {\n" + 
		"		v := y;\n" + 
		"	}", 
        "variable Object v := test then x else y;",
        decl.length()); 
	}

	@Test
	public void testDeclareVariableThen() throws Exception {
        String decl = "variable String? v;\n\t";
		testConversion(decl + 
		"if (test) {\n" + 
		"		v := default;\n" + 
		"	}\n" + 
		"	else {\n" + 
		"		v := null;\n" + 
		"	}", 
        "variable String? v := test then default;",
        decl.length()); 
	}

	@Test
	public void testDeclareVariableElse() throws Exception {
        String decl = "variable Object v;\n\t";
		testConversion(decl + 
		"if (exists obj) {\n" + 
		"		v := obj;\n" + 
		"	}\n" + 
		"	else {\n" + 
		"		v := default;\n" + 
		"	}", 
        "variable Object v := obj else default;",
        decl.length()); 
	}

	@Test
	public void testDeclareVariableElseProcessMethodOnce() throws Exception {
        String decl = "variable Object v;\n\t";
		testConversion(decl + 
		"if (exists first = process.first()) {\n" + 
		"		v := first;\n" + 
		"	}\n" + 
		"	else {\n" + 
		"		v := default;\n" + 
		"	}", 
        "variable Object v := process.first() else default;",
        decl.length()); 
	}

	@Test
	public void testDeclareVariableElseProcessAttributeOnce() throws Exception {
        String decl = "variable Object v;\n\t";
		testConversion(decl + 
		"if (exists first = process.first) {\n" + 
		"		v := first;\n" + 
		"	}\n" + 
		"	else {\n" + 
		"		v := default;\n" + 
		"	}", 
        "variable Object v := process.first else default;", 
        decl.length()); 
	}

	@Test
	public void testAssignVariableThenElse() throws Exception {
        testConversion("if (test) {\n" + 
		"		v := x;\n" + 
		"	}\n" + 
		"	else {\n" + 
		"		v := y;\n" + 
		"	}", 
		"v := test then x else y;"); 
	}

	@Test
	public void testAssignVariableThen() throws Exception {
        testConversion("if (test) {\n" + 
		"		v := default;\n" + 
		"	}\n" + 
		"	else {\n" + 
		"		v := null;\n" + 
		"	}", 
		"v := test then default;"); 
	}

	@Test
	public void testAssignVariableElse() throws Exception {
        testConversion("if (exists obj) {\n" + 
		"		v := obj;\n" + 
		"	}\n" + 
		"	else {\n" + 
		"		v := default;\n" + 
		"	}", 
		"v := obj else default;"); 
	}

	@Test
	public void testAssignVariableElseProcessMethodOnce() throws Exception {
        testConversion("if (exists first = process.first()) {\n" + 
		"		v := first;\n" + 
		"	}\n" + 
		"	else {\n" + 
		"		v := default;\n" + 
		"	}", 
		"v := process.first() else default;"); 
	}
	
	@Test
	public void testAssignVariableElseProcessAttributeOnce() throws Exception {
        testConversion("if (exists first = process.first) {\n" + 
		"		v := first;\n" + 
		"	}\n" + 
		"	else {\n" + 
		"		v := default;\n" + 
		"	}", 
		"v := process.first else default;"); 
	}
	

	@Test
	public void testNotIfStatement() throws  Exception {
		testNoConversion("return x;");
	}

	@Test
	public void testNoElse() throws  Exception {
		testNoConversion("if (test) { return x; }");
	}

	@Test
	public void testMultistatmentIfBlock() throws  Exception {
		testNoConversion("if (test) {\n" +
				"	String x = \"\";\n" +
				"	return x;\n" +
				"} else {\n" +
				"	return null;\n" +
				"}");
	}

	@Test
	public void testMultistatmentElseBlock() throws  Exception {
		testNoConversion("if (test) {\n" +
				"	return null;\n" +
				"} else {\n" +
				"	String x = \"\";\n" +
				"	return x;\n" +
				"}");
	}

	@Test
	public void testIfReturnNotMatchingElse() throws  Exception {
		testNoConversion("if (test) {\n" +
				"	return null;\n" +
				"} else {\n" +
				"	x = \"\";\n" +
				"}");
	}

	@Test
	public void testIfSpecifyNotMatchingElse() throws  Exception {
		testNoConversion("if (test) {\n" +
				"	x = \"\";\n" +
				"} else {\n" +
				"	return null;\n" +
				"}");
	}

	@Test
	public void testIfSpecifyingDifferentAttributes() throws  Exception {
		testNoConversion("if (test) {\n" +
				"	x = \"\";\n" +
				"} else {\n" +
				"	y = null;\n" +
				"}");
	}

	@Test
	public void testIfAssignmentNotMatchingElse() throws  Exception {
		testNoConversion("if (test) {\n" +
				"	x := \"\";\n" +
				"} else {\n" +
				"	return null;\n" +
				"}");
	}
	@Test
	public void testIfAssignmentNotMatchingElse2() throws  Exception {
		testNoConversion("if (test) {\n" +
				"	x := \"\";\n" +
				"} else {\n" +
				"	i++;\n" +
				"}");
	}

	@Test
	public void testIfAssigningDifferentVariables() throws  Exception {
		testNoConversion("if (test) {\n" +
				"	x := \"\";\n" +
				"} else {\n" +
				"	y := null;\n" +
				"}");
	}

	@Test
	public void testPrintStatmements() throws  Exception {
		testNoConversion("if (test) {\n" +
				"	print(x);\n" +
				"} else {\n" +
				"	print(y);\n" +
				"}");
	}
	
	@Test
	public void testExistsWithVariableAssignemt() throws  Exception {
		//Wont't work because of compiler limitation
		testNoConversion("if (exists f = process.first()) {\n" + 
				"	return \"hello\";\n" + 
				"} else {\n" + 
				"	return \"hi\";\n" + 
				"}");
	}
	
	
	@Test
	public void testTypeNarrowingIsCondition() throws  Exception {
		//Wont't work because of compiler limitation
		 testNoConversion("if (obj is String) {\n" + 
			"	return obj;\n" + 
			"} else {\n" + 
			"	return \"hi\";\n" + 
			"}");
			//, "return obj is String then obj else \"hi\";"); 
	}

	@Test
	public void testTypeNarrowingEmptyCondition() throws  Exception {
		//Wont't work because of compiler limitation
		 testNoConversion("if (nonempty arr) {\n" + 
			"	return arr.first;\n" + 
			"} else {\n" + 
			"	return \"\";\n" + 
			"}");
			//, "return nonempty arr then arr.first else \"\";"); 
	}

	@Test
	public void testTypeNarrowingExistsCondition() throws  Exception {
		//Wont't work because of compiler limitation
		 testNoConversion("if (exists obj) {\n" + 
			"	return obj.attr;\n" + 
			"} else {\n" + 
			"	return \"\";\n" + 
			"}");
			//, "return exists obj then obj.attr else \"\";"); 
	}

	@Test
	public void testIfExistsReturnOtherObject() throws Exception {
		//Has been disabled even if it would work because other variations that currently won't work 
        testNoConversion("if (exists obj) {\n" + 
		"		return x;\n" + 
		"	}\n" + 
		"	else {\n" + 
		"		return default;\n" + 
		"	}");
		//, "return exists obj then x else default;"); 
	}	
	
	@Test
	public void testIsCondition() throws  Exception {
		//Has been disabled even if it would work because other variations that currently won't work 
		 testNoConversion("if (obj is String) {\n" + 
			"	return \"hello\";\n" + 
			"} else {\n" + 
			"	return \"hi\";\n" + 
			"}");
			//, "return obj is String then \"hello\" else \"hi\";"); 
	}
	
	@Test
	public void testNonemptyCondition() throws  Exception {
		//Has been disabled even if it would work because other variations that currently won't work 
		 testNoConversion("if (nonempty list) {\n" + 
			"	return \"hello\";\n" + 
			"} else {\n" + 
			"	return \"hi\";\n" + 
			"}");
			//, "return nonempty list then \"hello\" else \"hi\";"); 
	}
	
	private void testNoConversion(String testStr) throws Exception {
		String input = HEADER + testStr + FOOTER;
		Document doc = new Document(input);
		CompilationUnit cu = getCompilationUnit(input);
		Statement statement = findStatementAtOffset(cu, HEADER.length() + 0);
		TextChange change = ConvertIfElseToThenElse.createTextChange(cu, doc, statement);
		if (change != null) {
			change.getEdit().apply(doc, 0);
			fail("No proposal was supposed to be created, but this was suggested:\n" + doc.get());
		}
	}
	

	private void testConversion(String testStr, String expectedResult)
			throws Exception, BadLocationException {
		testConversion(testStr, expectedResult, 0);
	}
	
	private void testConversion(String testStr, String expectedResult, int statementOffset)
			throws Exception, BadLocationException {
		String input = HEADER + testStr + FOOTER;
		Document doc = new Document(input);
		CompilationUnit cu = getCompilationUnit(input);
		Statement statement = findStatementAtOffset(cu, HEADER.length() + statementOffset);
        TextChange change = ConvertIfElseToThenElse.createTextChange(cu, doc, statement);
        assertNotNull("No proposal created", change);
		change.getEdit().apply(doc, 0);
		assertEquals(HEADER + expectedResult + FOOTER, doc.get());
	}

}
