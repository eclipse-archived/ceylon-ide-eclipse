package com.redhat.ceylon.eclipse.ui.test.headless;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.DocumentCommand;
import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.graphics.Point;
import org.junit.Before;
import org.junit.Test;

import com.redhat.ceylon.eclipse.code.editor.CeylonAutoEditStrategy;

public class AutoEditTests {
    
	public AutoEditTests() {}
	
	private CeylonAutoEditStrategy strategy;
	
	@Before
	public void beforeTest() {
	    strategy = new CeylonAutoEditStrategy(null);
	}
		    
    @Test
    public void testCorrectIndentation() {
        checkForCorrectIndentation(
                "class Test()\n\t\textends Super(){\n\nvoid method(){\n\nfor (x in xs){}\n\n}\n\n}", 
                "class Test()\n\t\textends Super(){\n\t\n\tvoid method(){\n\t\t\n\t\tfor (x in xs){}\n\t\t\n\t}\n\t\n}");

        checkForCorrectIndentation(
                "class Test()\n\t\textends Super(){\n\nvoid method(){}\n\n}",
                "class Test()\n\t\textends Super(){\n\t\n\tvoid method(){}\n\t\n}");
        
        checkForCorrectIndentation(
                "class Test()\n\t\textends Super(){\n\n\t\tvoid method(){}\n\t\n}",
                "class Test()\n\t\textends Super(){\n\t\n\tvoid method(){}\n\t\n}");
        
        checkForCorrectIndentation(
                "class Test()\n\t\textends Super(){\nvoid method(){}\n}",
                "class Test()\n\t\textends Super(){\n\tvoid method(){}\n}");
        
        checkForCorrectIndentation(
                "class Test()\n\t\textends Super(){\nvoid method(){\nfor (x in xs){}\n}\n}",
                "class Test()\n\t\textends Super(){\n\tvoid method(){\n\t\tfor (x in xs){}\n\t}\n}");
        
        checkForCorrectIndentation(
                "class Test()\n\t\textends Super(){\n\t\tvoid method(){}\n}",
                "class Test()\n\t\textends Super(){\n\tvoid method(){}\n}");
        
        checkForCorrectIndentation(
                "class Test()\nextends Super(){\n\nvoid method(){}\n\n}",
                "class Test()\nextends Super(){\n\t\n\tvoid method(){}\n\t\n}");
        
        checkForCorrectIndentation(
                "class Test() extends Super(){\n\nvoid method(){}\n\t\n}",
                "class Test() extends Super(){\n\t\n\tvoid method(){}\n\t\n}");
        
        checkForCorrectIndentation(
                "class Test()\n\t\textends Super()\n{\n\nvoid method(){}\n\t\n}",
                "class Test()\n\t\textends Super()\n{\n\t\n\tvoid method(){}\n\t\n}");
        
        checkForCorrectIndentation(
                "\tclass Test()\n\t\textends Super(){\n\nvoid method(){\n}\n\t\n}",
                "class Test()\n\t\textends Super(){\n\t\n\tvoid method(){\n\t}\n\t\n}");
        
        checkForCorrectIndentation(
                "void x(){\n\tclass Test()\n\t\textends Super(){\n\nvoid method(){\n}\n\t\n}",
                "void x(){\n\tclass Test()\n\t\t\textends Super(){\n\t\t\n\t\tvoid method(){\n\t\t}\n\t\t\n\t}");

        checkForCorrectIndentation(
                "void x(){\n\tclass Test()\n\t\textends Super(){\nvoid method(){\n}\n}\n",
                "void x(){\n\tclass Test()\n\t\t\textends Super(){\n\t\tvoid method(){\n\t\t}\n\t}\n");
        
        checkForCorrectIndentation(
                "void x(){\n\tclass Test()\n\t\textends Super(){//foo\nvoid method(){//bar\n}//baz\n}\n",
                "void x(){\n\tclass Test()\n\t\t\textends Super(){//foo\n\t\tvoid method(){//bar\n\t\t}//baz\n\t}\n");
        
        checkForCorrectIndentation(
                "doc (\"Hello\n\t World\n\t !\")\nvoid hello(){}",
                "doc (\"Hello\n\t World\n\t !\")\nvoid hello(){}");

        checkForCorrectIndentation(
                "\"Hello\n World\n !\"\nvoid hello(){}",
                "\"Hello\n World\n !\"\nvoid hello(){}");
        
        checkForCorrectIndentation(
                "\"\"\"Hello\n   World\n   !\"\"\"\nvoid hello(){}",
                "\"\"\"Hello\n   World\n   !\"\"\"\nvoid hello(){}");
        
        checkForCorrectIndentation(
                "void x(){\n\t\"\"\"Hello\n\t   World\n\t   !\"\"\"\n\tvoid hello(){}",
                "void x(){\n\t\"\"\"Hello\n\t   World\n\t   !\"\"\"\n\tvoid hello(){}");
        
        //Note: this test fails, but that is more of a conceptual
        //      problem with how the whole concept of correct
        //      indentation works!
        checkForCorrectIndentation(
                "\t\"\"\"Hello\n\t   World\n\t   !\"\"\"\n\tvoid hello(){}",
                "\"\"\"Hello\n   World\n   !\"\"\"\nvoid hello(){}");
        
        checkForCorrectIndentation(
                "String x()\n=>\"hello\";",
                "String x()\n=>\"hello\";");

        checkForCorrectIndentation(
                "String x()\n\t=>\"hello\";",
                "String x()\n\t\t=>\"hello\";");
        
        checkForCorrectIndentation(
                "String x()\n\t\t=>\"hello\";",
                "String x()\n\t\t=>\"hello\";");

        checkForCorrectIndentation(
                "void x(){\n\tString x()\n\t\t\t=>\"hello\";",
                "void x(){\n\tString x()\n\t\t\t=>\"hello\";");
        
        checkForCorrectIndentation(
                "\tString x()\n\t\t\t=>\"hello\";",
                "String x()\n\t\t=>\"hello\";");
        
        checkForCorrectIndentation(
                "class X()\nextends Y()",
                "class X()\nextends Y()");

        checkForCorrectIndentation(
                "class X()\n\textends Y()",
                "class X()\n\t\textends Y()");
        
        checkForCorrectIndentation(
                "class X()\n\t\textends Y()",
                "class X()\n\t\textends Y()");

        checkForCorrectIndentation(
                "void x(){\n\tclass X()\n\t\t\textends Y()",
                "void x(){\n\tclass X()\n\t\t\textends Y()");
        
        checkForCorrectIndentation(
                "class X()\nextends Y()\nsatisfies Z",
                "class X()\nextends Y()\nsatisfies Z");

        checkForCorrectIndentation(
                "class X()\n\textends Y()\n\tsatisfies Z",
                "class X()\n\t\textends Y()\n\t\tsatisfies Z");
        
        checkForCorrectIndentation(
                "class X()\n\t\textends Y()\n\t\tsatisfies Z",
                "class X()\n\t\textends Y()\n\t\tsatisfies Z");

        checkForCorrectIndentation(
                "void x(){\n\tclass X()\n\t\t\textends Y()\n\t\t\tsatisfies Z",
                "void x(){\n\tclass X()\n\t\t\textends Y()\n\t\t\tsatisfies Z");
        
        checkForCorrectIndentation(
                "\tclass X()\n\t\t\textends Y()\n\t\t\tsatisfies Z",
                "class X()\n\t\textends Y()\n\t\tsatisfies Z");
        
        checkForCorrectIndentation(
                "void x(){\n\t//comment\n",
                "void x(){\n\t//comment\n");
        
        checkForCorrectIndentation(
                "void x(){\n//comment\n",
                "void x(){\n\t//comment\n");
        
        checkForCorrectIndentation(
                "void x(){\n\t\t//comment\n",
                "void x(){\n\t//comment\n");
        
        checkForCorrectIndentation(
                "\t//comment\n",
                "//comment\n");
        
        checkForCorrectIndentation(
                "\t\t//comment\n",
                "//comment\n");
        
        checkForCorrectIndentation(
                "\t/*\n\tcomment\n\t*/\n",
                "\t/*\n\tcomment\n\t*/\n");
        
        checkForCorrectIndentation(
                "\t\t/*\n\tcomment\n\t*/\n",
                "\t\t/*\n\tcomment\n\t*/\n");
    }
    
    @Test
    public void testNewLine() {
        checkForNewLine(
                "x=1;",
                "x=1;\n");

        checkForNewLine(
                "x=1; ",
                "x=1; \n");

        checkForNewLine(
                "void x(){\n\tx=1;",
                "void x(){\n\tx=1;\n\t");
        
        checkForNewLine(
                "void x(){\n\tx=1; //foo",
                "void x(){\n\tx=1; //foo\n\t");
        
        checkForNewLine(
                "void x(){\nvoid y(){\n\t\tx=1; //foo",
                "void x(){\nvoid y(){\n\t\tx=1; //foo\n\t\t");
        
        checkForNewLine(
                "\tx=1; //foo",
                "\tx=1; //foo\n\t");
        
        checkForNewLine(
                "\t\tx=1; //foo",
                "\t\tx=1; //foo\n\t\t");
        
        checkForNewLine(
                "Integer x {",
                "Integer x {\n\t\n}");

        checkForNewLine(
                "Integer x { ",
                "Integer x { \n\t\n}");

        checkForNewLine(
                "\tInteger x {",
                "\tInteger x {\n\t\t\n\t}");

        checkForNewLine(
                "\tInteger x { //foo",
                "\tInteger x { //foo\n\t\t\n\t}");

        checkForNewLine(
                "\t\tInteger x { //foo",
                "\t\tInteger x { //foo\n\t\t\t\n\t\t}");

        checkForNewLine(
                "//hello",
                "//hello\n");

        checkForNewLine(
                "//hello ",
                "//hello \n");

        checkForNewLine(
                "\t//hello",
                "\t//hello\n\t");
        
        checkForNewLine(
                "\t//hello ",
                "\t//hello \n\t");
        
        checkForNewLine(
                "\t\t//hello",
                "\t\t//hello\n\t\t");
        
        checkForNewLine(
                "//hello\n",
                "//hello\n\n");

        checkForNewLine(
                "\t//hello\n",
                "\t//hello\n\n\t");

        checkForNewLine(
                "\t//hello\n\t",
                "\t//hello\n\t\n\t");

        checkForNewLine(
                "//hello \n ",
                "//hello \n \n");

        checkForNewLine(
                "//hello \n\t",
                "//hello \n\t\n");

        checkForNewLine(
                "\t//hello \n\t",
                "\t//hello \n\t\n\t");

        checkForNewLine(
                "\t//hello\n\t",
                "\t//hello\n\t\n\t");
        
        checkForNewLine(
                "\t//hello \n\t",
                "\t//hello \n\t\n\t");
        
        checkForNewLine(
                "/*hello",
                "/*hello\n\n*/");
        
        checkForNewLine(
                "\t/*hello",
                "\t/*hello\n\t\n\t*/");
        
        checkForNewLine(
                "\t/*\n\thello",
                "\t/*\n\thello\n\t");
        
        checkForNewLine(
                "void x() {}",
                "void x() {}\n");

        checkForNewLine(
                "void x() {} ",
                "void x() {} \n");

        checkForNewLine(
                "\tvoid x() {}",
                "\tvoid x() {}\n\t");
        
        checkForNewLine(
                "\tvoid x() {} //foo",
                "\tvoid x() {} //foo\n\t");
        
        checkForNewLine(
                "x=1;\n",
                "x=1;\n\n");

        checkForNewLine(
                "x=1; \n ",
                "x=1; \n \n "); //do we really want the space at the end here?

        checkForNewLine(
                "\tx=1;\n\t",
                "\tx=1;\n\t\n\t");
        
        checkForNewLine(
                "\tx=1; //foo\n\t",
                "\tx=1; //foo\n\t\n\t");
        
        checkForNewLine(
                "\t\tx=1; //foo\n\t\t",
                "\t\tx=1; //foo\n\t\t\n\t\t");
        
        checkForNewLine(
                "Integer x {\n\t",
                "Integer x {\n\t\n\t");

        checkForNewLine(
                "Integer x { \n\t",
                "Integer x { \n\t\n\t");

        checkForNewLine(
                "\tInteger x {\n\t\t",
                "\tInteger x {\n\t\t\n\t\t");

        checkForNewLine(
                "\tInteger x { //foo\n\t\t",
                "\tInteger x { //foo\n\t\t\n\t\t");

        checkForNewLine(
                "\t\tInteger x { //foo\n\t\t\t",
                "\t\tInteger x { //foo\n\t\t\t\n\t\t\t");

        checkForNewLine(
                "void x() {}\n",
                "void x() {}\n\n");

        checkForNewLine(
                "void x() {} \n ",
                "void x() {} \n \n "); //do we really want the space

        checkForNewLine(
                "\tvoid x() {}\n\t",
                "\tvoid x() {}\n\t\n\t");
        
        checkForNewLine(
                "\tvoid x() {} //foo\n\t",
                "\tvoid x() {} //foo\n\t\n\t");
        
        checkForNewLine(
                "String greeting = \"hello",
                "String greeting = \"hello\n                   ");
        
        checkForNewLine(
                "\tString greeting = \"hello",
                "\tString greeting = \"hello\n\t                   ");
        
        checkForNewLine(
                "String greeting = \"hello\n                   world",
                "String greeting = \"hello\n                   world\n                   ");
        
        checkForNewLine(
                "\tString greeting = \"hello\n\t                   world",
                "\tString greeting = \"hello\n\t                   world\n\t                   ");
        
        checkForNewLine(
                "String greeting = \"\"\"hello",
                "String greeting = \"\"\"hello\n                     ");
        
        checkForNewLine(
                "\tString greeting = \"\"\"hello",
                "\tString greeting = \"\"\"hello\n\t                     ");
        
        checkForNewLine(
                "String greeting = \"\"\"hello\n                     world",
                "String greeting = \"\"\"hello\n                     world\n                     ");
        
        checkForNewLine(
                "\tString greeting = \"\"\"hello\n\t                     world",
                "\tString greeting = \"\"\"hello\n\t                     world\n\t                     ");
        
        checkForNewLine(
                "String x()\n=>\"hello\" +",
                "String x()\n=>\"hello\" +\n");

        checkForNewLine(
                "String x()\n\t=>\"hello\" +",
                "String x()\n\t=>\"hello\" +\n\t");
        
        checkForNewLine(
                "String x()\n\t\t=>\"hello\" +",
                "String x()\n\t\t=>\"hello\" +\n\t\t");

        checkForNewLine(
                "\tString x()\n\t\t\t=>\"hello\" +",
                "\tString x()\n\t\t\t=>\"hello\" +\n\t\t\t");

        checkForNewLine(
                "String x()\n=>\"hello\";",
                "String x()\n=>\"hello\";\n");

        checkForNewLine(
                "String x()\n\t=>\"hello\";",
                "String x()\n\t=>\"hello\";\n");
        
        checkForNewLine(
                "String x()\n\t\t=>\"hello\";",
                "String x()\n\t\t=>\"hello\";\n");

        checkForNewLine(
                "\tString x()\n\t\t\t=>\"hello\";",
                "\tString x()\n\t\t\t=>\"hello\";\n\t");
    }
    
    @Test
    public void testClosingBrace() {
        checkForClosingBrace(
                "void x() {\n\t",
                "void x() {\n}");

        checkForClosingBrace(
                "\tvoid x() {\n\t\t",
                "\tvoid x() {\n\t}");

        checkForClosingBrace(
                "void x() {\n\tprint(\"hello\");\n\t",
                "void x() {\n\tprint(\"hello\");\n}");

        checkForClosingBrace(
                "void x() {\n\tprint(\"hello\");\n\t//bye\n\t",
                "void x() {\n\tprint(\"hello\");\n\t//bye\n}");
    }
    
    private void doNewline(IDocument doc) {
        try {
            DocumentCommand cmd= new DocumentCommand() { };
            cmd.offset= doc.getLength();
            cmd.length= 0;
            cmd.text= Character.toString('\n');
            cmd.doit= true;
            cmd.shiftsCaret= true;
            strategy.customizeDocumentCommand(doc, cmd);
            doc.replace(cmd.offset, cmd.length, cmd.text);
        } catch (BadLocationException e) {
            System.err.println("Correct Indentation command failed " + e.getMessage());
        }
    }
    
    private void doClosingBrace(IDocument doc) {
        try {
            DocumentCommand cmd= new DocumentCommand() { };
            cmd.offset= doc.getLength();
            cmd.length= 0;
            cmd.text= Character.toString('}');
            cmd.doit= true;
            cmd.shiftsCaret= true;
            strategy.customizeDocumentCommand(doc, cmd);
            doc.replace(cmd.offset, cmd.length, cmd.text);
        } catch (BadLocationException e) {
            System.err.println("Correct Indentation command failed " + e.getMessage());
        }
    }
    
    private void checkForCorrectIndentation(String before, String expectedIndentation) {
        Document doc = new Document(before);
        doCorrectIndentation(doc);
        assertEquals(doc.get(), expectedIndentation);
    }

    private void checkForNewLine(String before, String expectedIndentation) {
        Document doc = new Document(before);
        doNewline(doc);
        assertEquals(doc.get(), expectedIndentation);
    }

    private void checkForClosingBrace(String before, String expectedIndentation) {
        Document doc = new Document(before);
        doClosingBrace(doc);
        assertEquals(doc.get(), expectedIndentation);
    }

    void doCorrectIndentation(IDocument doc) {
        Point p = new Point(0, doc.getLength());
        try {
            final int selStart= p.x;
            final int selLen= p.y;
            final int selEnd= selStart + selLen;
            final int startLine= doc.getLineOfOffset(selStart);
            int endLine= doc.getLineOfOffset(selEnd);

            // If the selection extends just to the beginning of the next line, don't indent that one too
            if (selLen > 0 && lookingAtLineEnd(doc, selEnd)) {
                endLine--;
            }

            // Indent each line using the AutoEditStrategy
            for(int line= startLine; line <= endLine; line++) {
                int lineStartOffset= doc.getLineOffset(line);

                // Replace the existing indentation with the desired indentation.
                // Use the language-specific AutoEditStrategy, which requires a DocumentCommand.
                DocumentCommand cmd= new DocumentCommand() { };
                cmd.offset= lineStartOffset;
                cmd.length= 0;
                cmd.text= Character.toString('\t');
                cmd.doit= true;
                cmd.shiftsCaret= false;
//              boolean saveMode= fAutoEditStrategy.setFixMode(true);
                strategy.customizeDocumentCommand(doc, cmd);
//              fAutoEditStrategy.setFixMode(saveMode);
                doc.replace(cmd.offset, cmd.length, cmd.text);
            }
        } catch (BadLocationException e) {
            System.err.println("Correct Indentation command failed " + e.getMessage());
        }
    }
    
    boolean lookingAtLineEnd(IDocument doc, int pos) {
        String[] legalLineTerms= doc.getLegalLineDelimiters();
        try {
            for(String lineTerm: legalLineTerms) {
                int len= lineTerm.length();
                if (pos > len && doc.get(pos - len, len).equals(lineTerm)) {
                    return true;
                }
            }
        } catch (BadLocationException e) {
            //System.err.println("Error examining document for line termination", e);
        }
        return false;
    }
    
}