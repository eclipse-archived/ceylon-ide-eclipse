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

public class TestInvertIfElse {
	
	

	private static final String FOOTER = "\n}";
	private static final String HEADER = "void run() {\n\t";
	
	@Test
	public void testBooleanVariable() throws Exception {
        testThereAndBackAgain("if (test) {\n" + 
		"		return x;\n" + 
		"	}\n" + 
		"	else {\n" + 
		"		return y;\n" + 
		"	}", 
		
		"if (!test) {\n" + 
		"		return y;\n" + 
		"	}\n" + 
		"	else {\n" + 
		"		return x;\n" + 
			"	}"); 
	}

	@Test
	public void testTrue() throws Exception {
        testThereAndBackAgain("if (true) {\n" + 
		"		return x;\n" + 
		"	}\n" + 
		"	else {\n" + 
		"		return y;\n" + 
		"	}", 
		
		"if (false) {\n" + 
		"		return y;\n" + 
		"	}\n" + 
		"	else {\n" + 
		"		return x;\n" + 
			"	}"); 
	}
	

	@Test
	public void testEquals() throws Exception {
        testThereAndBackAgain("if (x == y) {\n" + 
		"		return x;\n" + 
		"	}\n" + 
		"	else {\n" + 
		"		return y;\n" + 
		"	}", 
		
		"if (x != y) {\n" + 
		"		return y;\n" + 
		"	}\n" + 
		"	else {\n" + 
		"		return x;\n" + 
			"	}"); 
	}

	@Test
	public void testGreaterThen() throws Exception {
        testThereAndBackAgain("if (x > y) {\n" + 
		"		return x;\n" + 
		"	}\n" + 
		"	else {\n" + 
		"		return y;\n" + 
		"	}", 
		
		"if (x <= y) {\n" + 
		"		return y;\n" + 
		"	}\n" + 
		"	else {\n" + 
		"		return x;\n" + 
			"	}"); 
	}

	@Test
	public void testLessThen() throws Exception {
        testThereAndBackAgain("if (x < y) {\n" + 
		"		return x;\n" + 
		"	}\n" + 
		"	else {\n" + 
		"		return y;\n" + 
		"	}", 
		
		"if (x >= y) {\n" + 
		"		return y;\n" + 
		"	}\n" + 
		"	else {\n" + 
		"		return x;\n" + 
			"	}"); 
	}

	@Test
	public void testBooleanExpression() throws Exception {
        testThereAndBackAgain("if (x || y) {\n" + 
		"		return x;\n" + 
		"	}\n" + 
		"	else {\n" + 
		"		return y;\n" + 
		"	}", 
		
		"if (!(x || y)) {\n" + 
		"		return y;\n" + 
		"	}\n" + 
		"	else {\n" + 
		"		return x;\n" + 
			"	}"); 
	}

	@Test
	public void testAttribute() throws Exception {
        testThereAndBackAgain("if (x.attr) {\n" + 
		"		return x;\n" + 
		"	}\n" + 
		"	else {\n" + 
		"		return y;\n" + 
		"	}", 
		
		"if (!x.attr) {\n" + 
		"		return y;\n" + 
		"	}\n" + 
		"	else {\n" + 
		"		return x;\n" + 
			"	}"); 
	}

	@Test
	public void testMethod() throws Exception {
        testThereAndBackAgain("if (x.method()) {\n" + 
		"		return x;\n" + 
		"	}\n" + 
		"	else {\n" + 
		"		return y;\n" + 
		"	}", 
		
		"if (!x.method()) {\n" + 
		"		return y;\n" + 
		"	}\n" + 
		"	else {\n" + 
		"		return x;\n" + 
			"	}"); 
	}

	@Test
	public void testAssignVariable() throws Exception {
        testThereAndBackAgain("if (x := y) {\n" + 
		"		return x;\n" + 
		"	}\n" + 
		"	else {\n" + 
		"		return y;\n" + 
		"	}", 
		
		"if (!(x := y)) {\n" + 
		"		return y;\n" + 
		"	}\n" + 
		"	else {\n" + 
		"		return x;\n" + 
			"	}"); 
	}

	@Test
	public void testOperandParens() throws Exception {
        testThereAndBackAgain("if ((x) == (y)) {\n" + 
		"		return x;\n" + 
		"	}\n" + 
		"	else {\n" + 
		"		return y;\n" + 
		"	}", 
		
		"if ((x) != (y)) {\n" + 
		"		return y;\n" + 
		"	}\n" + 
		"	else {\n" + 
		"		return x;\n" + 
			"	}"); 
	}

	@Test
	public void testExists() throws Exception {
        testThereAndBackAgain("if (exists x) {\n" + 
		"		return x;\n" + 
		"	}\n" + 
		"	else {\n" + 
		"		return y;\n" + 
		"	}", 
		
		"if (!exists x) {\n" + 
		"		return y;\n" + 
		"	}\n" + 
		"	else {\n" + 
		"		return x;\n" + 
			"	}"); 
	}
	
	@Test
	public void testIs() throws Exception {
        testThereAndBackAgain("if (x is String) {\n" + 
		"		return x;\n" + 
		"	}\n" + 
		"	else {\n" + 
		"		return y;\n" + 
		"	}", 
		
		"if (!x is String) {\n" + 
		"		return y;\n" + 
		"	}\n" + 
		"	else {\n" + 
		"		return x;\n" + 
			"	}"); 
	}
	
	@Test
	public void testNotempty() throws Exception {
        testThereAndBackAgain("if (nonempty x) {\n" + 
		"		return x;\n" + 
		"	}\n" + 
		"	else {\n" + 
		"		return y;\n" + 
		"	}", 
		
		"if (!nonempty x) {\n" + 
		"		return y;\n" + 
		"	}\n" + 
		"	else {\n" + 
		"		return x;\n" + 
			"	}"); 
	}

	@Test
	public void testElseOnSameLine() throws Exception {
        testThereAndBackAgain("if (test) {\n" + 
		"		return x;\n" + 
		"	} else {\n" + 
		"		return y;\n" + 
		"	}", 
		
		"if (!test) {\n" + 
		"		return y;\n" + 
		"	} else {\n" + 
		"		return x;\n" + 
			"	}"); 
	}
	
	@Test
	public void testElseIf() throws Exception {
        testConversion("if (true) {\n" + 
		"		return x;\n" + 
		"	}\n" + 
		"	else if (false) {\n" + 
		"		return y;\n" + 
		"	}", 
		
		"if (false) {\n" + 
		"		if (false) {\n" + 
		"			return y;\n" + 
		"		}\n" + 
		"	}\n" + 
		"	else {\n" + 
		"		return x;\n" + 
		"	}"); 
	}

	@Test
	public void testIfElseIfElse() throws Exception {
        testConversion("if (true) {\n" + 
		"		return x\n" + 
		"	}\n" + 
		"	else if (false) {\n" + 
		"		return y;\n" + 
		"	} else {\n" + 
		"		return z;\n" + 
		"	}", 
		
		"if (false) {\n" + 
		"		if (false) {\n" + 
		"			return y;\n" + 
		"		} else {\n" + 
		"			return z;\n" + 
		"		}\n" + 
		"	}\n" + 
		"	else {\n" + 
		"		return x\n" + 
		"	}"); 
	}

	
	@Test
	public void testNoIf() throws Exception {
		testNoConversion("return x;");
	}

	@Test
	public void testNoElse() throws Exception {
		testNoConversion("if (test) {\n" +
				"	return x;" +
				"}");
	}

	private void testNoConversion(String testStr) throws Exception {
		String input = HEADER + testStr + FOOTER;
		Document doc = new Document(input);
		CompilationUnit cu = getCompilationUnit(input);
		Statement statement = findStatementAtOffset(cu, HEADER.length() + 0);
		TextChange change = InvertIfElse.createTextChange(doc, statement);
		if (change != null) {
			change.getEdit().apply(doc, 0);
			fail("No proposal was supposed to be created, but this was suggested:\n" + doc.get());
		}
	}
	

	private void testThereAndBackAgain(String testStr, String expectedResult)
			throws Exception, BadLocationException {
		testConversion(testStr, expectedResult);
		testConversion(expectedResult, testStr);
	}
	
	private void testConversion(String testStr, String expectedResult)
			throws Exception, BadLocationException {
		String input = HEADER + testStr + FOOTER;
		Document doc = new Document(input);
		CompilationUnit cu = getCompilationUnit(input);
		Statement statement = findStatementAtOffset(cu, HEADER.length());
        TextChange change = InvertIfElse.createTextChange(doc, statement);
        assertNotNull("No proposal created", change);
		change.getEdit().apply(doc, 0);
		assertEquals(HEADER + expectedResult + FOOTER, doc.get());
	}

}
