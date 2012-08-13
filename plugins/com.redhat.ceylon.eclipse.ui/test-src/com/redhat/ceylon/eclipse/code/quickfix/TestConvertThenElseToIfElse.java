package com.redhat.ceylon.eclipse.code.quickfix;

import static com.redhat.ceylon.eclipse.test.TestUtil.findStatementAtOffset;
import static com.redhat.ceylon.eclipse.test.TestUtil.getCompilationUnit;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.ltk.core.refactoring.TextChange;
import org.junit.Ignore;
import org.junit.Test;

import com.redhat.ceylon.compiler.typechecker.tree.Tree.CompilationUnit;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.Statement;

public class TestConvertThenElseToIfElse {
	
	

	private static final String FOOTER = "\n}";
	private static final String HEADER = "void run() {\n\t";
	
	@Test
	public void testReturnThenElse() throws Exception {
        testConversion("return test then x else y;", 
        		"if (test) {\n" + 
        		"		return x;\n" + 
        		"	}\n" + 
        		"	else {\n" + 
        		"		return y;\n" + 
        		"	}"); 
	}
	
	@Test
	public void testReturnThen() throws Exception {
        testConversion("return test then x;", 
        		"if (test) {\n" + 
        		"		return x;\n" + 
        		"	}\n" + 
        		"	else {\n" + 
        		"		return null;\n" + 
        		"	}"); 
	}
	
	@Test
	public void testReturnElse() throws Exception {
        testConversion("return obj else default;", 
        		"if (exists obj) {\n" + 
        		"		return obj;\n" + 
        		"	}\n" + 
        		"	else {\n" + 
        		"		return default;\n" + 
        		"	}"); 
	}
	
	@Test
	public void testReturnElseProcessOnceMethod() throws Exception {
        testConversion("return process.first() else default;", 
        		"if (exists first = process.first()) {\n" + 
        		"		return first;\n" + 
        		"	}\n" + 
        		"	else {\n" + 
        		"		return default;\n" + 
        		"	}"); 
	}

	@Test
	public void testReturnElseProcessOnceAttribute() throws Exception {
        testConversion("return process.first else default;", 
        		"if (exists first = process.first) {\n" + 
        		"		return first;\n" + 
        		"	}\n" + 
        		"	else {\n" + 
        		"		return default;\n" + 
        		"	}"); 
	}

	@Test
	public void testReturnNestedThenElse() throws Exception {
        testConversion("return test then (test2 then x else y) else (test2 then z);", 
        		"if (test) {\n" + 
        		"		return test2 then x else y;\n" + 
        		"	}\n" + 
        		"	else {\n" + 
        		"		return test2 then z;\n" + 
        		"	}"); 
	}

	@Test
	public void testReturnThenElseAssign() throws Exception {
        testConversion("return test then x else (v1 := \"\");", 
        		"if (test) {\n" + 
        		"		return x;\n" + 
        		"	}\n" + 
        		"	else {\n" + 
        		"		return v1 := \"\";\n" + 
        		"	}"); 
	}

	@Test
	public void testDeclareThenElse() throws Exception {
        testConversion("Object o = test then x else y;", 
        		"Object o;\n" + 
        		"	if (test) {\n" + 
        		"		o = x;\n" + 
        		"	}\n" + 
        		"	else {\n" + 
        		"		o = y;\n" + 
        		"	}"); 
	}
	
	@Test
	public void testDeclareThen() throws Exception {
        testConversion("Object o = test then default;", 
        		"Object o;\n" + 
        		"	if (test) {\n" + 
        		"		o = default;\n" + 
        		"	}\n" + 
        		"	else {\n" + 
        		"		o = null;\n" + 
        		"	}"); 
	}
	
	@Test
	public void testDeclareElse() throws Exception {
        testConversion("Object o = obj else default;", 
        		"Object o;\n" + 
        		"	if (exists obj) {\n" + 
        		"		o = obj;\n" + 
        		"	}\n" + 
        		"	else {\n" + 
        		"		o = default;\n" + 
        		"	}"); 
	}
	
	@Test
	public void testDeclareElseProcessOnceMethod() throws Exception {
        testConversion("Object o = process.first() else default;", 
        		"Object o;\n" + 
        		"	if (exists first = process.first()) {\n" + 
        		"		o = first;\n" + 
        		"	}\n" + 
        		"	else {\n" + 
        		"		o = default;\n" + 
        		"	}"); 
	}

	@Test
	public void testDeclareElseProcessOnceAttribute() throws Exception {
        testConversion("Object o = process.firstAttr else default;", 
        		"Object o;\n" + 
        		"	if (exists firstAttr = process.firstAttr) {\n" + 
        		"		o = firstAttr;\n" + 
        		"	}\n" + 
        		"	else {\n" + 
        		"		o = default;\n" + 
        		"	}"); 
	}

	
	@Test
	public void testSpecifyThenElse() throws Exception {
        testConversion("o = test then x else y;", 
        		"if (test) {\n" + 
        		"		o = x;\n" + 
        		"	}\n" + 
        		"	else {\n" + 
        		"		o = y;\n" + 
        		"	}"); 
	}
	
	@Test
	public void testSpecifyThen() throws Exception {
        testConversion("o = test then default;", 
        		"if (test) {\n" + 
        		"		o = default;\n" + 
        		"	}\n" + 
        		"	else {\n" + 
        		"		o = null;\n" + 
        		"	}"); 
	}
	
	@Test
	public void testSpecifyElse() throws Exception {
        testConversion("o = obj else default;", 
        		"if (exists obj) {\n" + 
        		"		o = obj;\n" + 
        		"	}\n" + 
        		"	else {\n" + 
        		"		o = default;\n" + 
        		"	}"); 
	}
	
	@Test
	public void testSpecifyElseProcessOnceMethod() throws Exception {
        testConversion("o = process.first() else default;", 
        		"if (exists first = process.first()) {\n" + 
        		"		o = first;\n" + 
        		"	}\n" + 
        		"	else {\n" + 
        		"		o = default;\n" + 
        		"	}"); 
	}

	@Test
	public void testSpecifyElseProcessOnceAttribute() throws Exception {
        testConversion("o = process.firstAttr else default;", 
        		"if (exists firstAttr = process.firstAttr) {\n" + 
        		"		o = firstAttr;\n" + 
        		"	}\n" + 
        		"	else {\n" + 
        		"		o = default;\n" + 
        		"	}"); 
	}

	@Test
	@Ignore
	public void testDeclareInferedThenElse() throws Exception {
        String variableDeclaration = "Object x = \"\";\n" +
		        		"	Object y = \"\";\n" +
		        		"	Boolean test = true;\n";
		testConversion(variableDeclaration + "value o = test then x else y;", 
        		"Object o;\n" + 
        		"	if (exists firstAttr = process.firstAttr) {\n" + 
        		"		o = firstAttr;\n" + 
        		"	}\n" + 
        		"	else {\n" + 
        		"		o = default;\n" + 
        		"	}",
        		variableDeclaration.length() + 1); 
	}

	@Test
	public void testDeclareVariableThenElse() throws Exception {
        testConversion("variable Object v := test then x else y;", 
        		"variable Object v;\n" + 
        		"	if (test) {\n" + 
        		"		v := x;\n" + 
        		"	}\n" + 
        		"	else {\n" + 
        		"		v := y;\n" + 
        		"	}"); 
	}

	@Test
	public void testDeclareVariableThen() throws Exception {
        testConversion("variable String? v := test then default;", 
        		"variable String? v;\n" + 
        		"	if (test) {\n" + 
        		"		v := default;\n" + 
        		"	}\n" + 
        		"	else {\n" + 
        		"		v := null;\n" + 
        		"	}"); 
	}

	@Test
	public void testDeclareVariableElse() throws Exception {
        testConversion("variable Object v := obj else default;", 
        		"variable Object v;\n" + 
        		"	if (exists obj) {\n" + 
        		"		v := obj;\n" + 
        		"	}\n" + 
        		"	else {\n" + 
        		"		v := default;\n" + 
        		"	}"); 
	}

	@Test
	public void testDeclareVariableElseProcessMethodOnce() throws Exception {
        testConversion("variable Object v := process.first() else default;", 
        		"variable Object v;\n" + 
        		"	if (exists first = process.first()) {\n" + 
        		"		v := first;\n" + 
        		"	}\n" + 
        		"	else {\n" + 
        		"		v := default;\n" + 
        		"	}"); 
	}

	@Test
	public void testDeclareVariableElseProcessAttributeOnce() throws Exception {
        testConversion("variable Object v := process.first else default;", 
        		"variable Object v;\n" + 
        		"	if (exists first = process.first) {\n" + 
        		"		v := first;\n" + 
        		"	}\n" + 
        		"	else {\n" + 
        		"		v := default;\n" + 
        		"	}"); 
	}

	@Test
	public void testAssignVariableThenElse() throws Exception {
        testConversion("v := test then x else y;", 
        		"if (test) {\n" + 
        		"		v := x;\n" + 
        		"	}\n" + 
        		"	else {\n" + 
        		"		v := y;\n" + 
        		"	}"); 
	}

	@Test
	public void testAssignVariableThen() throws Exception {
        testConversion("v := test then default;", 
        		"if (test) {\n" + 
        		"		v := default;\n" + 
        		"	}\n" + 
        		"	else {\n" + 
        		"		v := null;\n" + 
        		"	}"); 
	}

	@Test
	public void testAssignVariableElse() throws Exception {
        testConversion("v := obj else default;", 
        		"if (exists obj) {\n" + 
        		"		v := obj;\n" + 
        		"	}\n" + 
        		"	else {\n" + 
        		"		v := default;\n" + 
        		"	}"); 
	}

	@Test
	public void testAssignVariableElseProcessMethodOnce() throws Exception {
        testConversion("v := process.first() else default;", 
        		"if (exists first = process.first()) {\n" + 
        		"		v := first;\n" + 
        		"	}\n" + 
        		"	else {\n" + 
        		"		v := default;\n" + 
        		"	}"); 
	}
	
	@Test
	public void testAssignVariableElseProcessAttributeOnce() throws Exception {
        testConversion("v := process.first else default;", 
        		"if (exists first = process.first) {\n" + 
        		"		v := first;\n" + 
        		"	}\n" + 
        		"	else {\n" + 
        		"		v := default;\n" + 
        		"	}"); 
	}
	
	@Test
	public void testReturnNoExpression() throws Exception {
		testNoConversion("return;");
	}

	@Test
	public void testReturnEmptyExpression() throws Exception {
		testNoConversion("return ();");
	}

	@Test
	public void testIncrementExpression() throws Exception {
		testNoConversion("i++;");
	}

	@Test
	public void testIfExpression() throws Exception {
		testNoConversion("if (test);");
	}

	private void testNoConversion(String testStr) throws Exception {
		String input = HEADER + testStr + FOOTER;
		Document doc = new Document(input);
		CompilationUnit cu = getCompilationUnit(input);
		Statement statement = findStatementAtOffset(cu, HEADER.length() + 0);
		TextChange change = ConvertThenElseToIfElse.createTextChange(doc, statement);
		assertNull("No proposal was supposed to be created", change);
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
        TextChange change = ConvertThenElseToIfElse.createTextChange(doc, statement);
        assertNotNull("No proposal created", change);
		change.getEdit().apply(doc, 0);
		assertEquals(HEADER + expectedResult + FOOTER, doc.get());
	}

}
