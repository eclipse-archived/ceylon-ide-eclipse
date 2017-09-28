package org.eclipse.ceylon.ide.eclipse.code.editor;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.DocumentCommand;
import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.graphics.Point;

class AutoEditTest extends CeylonAutoEditStrategy {
    
    AutoEditTest() { super(); }
    
    public static void main(String[] args) {
        AutoEditTest instance = new AutoEditTest();
        
        Document doc = new Document("class Test()\n\t\textends Super(){\n\nvoid method(){\n\nfor (x in xs){}\n\n}\n\n}");
        instance.doCorrectIndentation(doc);
        assertResult(doc, "class Test()\n\t\textends Super(){\n\t\n\tvoid method(){\n\t\t\n\t\tfor (x in xs){}\n\t\t\n\t}\n\t\n}");
        
        doc = new Document("class Test()\n\t\textends Super(){\n\nvoid method(){}\n\n}");
        instance.doCorrectIndentation(doc);
        assertResult(doc, "class Test()\n\t\textends Super(){\n\t\n\tvoid method(){}\n\t\n}");
        
        doc = new Document("class Test()\n\t\textends Super(){\n\n\t\tvoid method(){}\n\t\n}");
        instance.doCorrectIndentation(doc);
        assertResult(doc, "class Test()\n\t\textends Super(){\n\t\n\tvoid method(){}\n\t\n}");
        
        doc = new Document("class Test()\n\t\textends Super(){\nvoid method(){}\n}");
        instance.doCorrectIndentation(doc);
        assertResult(doc, "class Test()\n\t\textends Super(){\n\tvoid method(){}\n}");
        
        doc = new Document("class Test()\n\t\textends Super(){\nvoid method(){\nfor (x in xs){}\n}\n}");
        instance.doCorrectIndentation(doc);
        assertResult(doc, "class Test()\n\t\textends Super(){\n\tvoid method(){\n\t\tfor (x in xs){}\n\t}\n}");
        
        doc = new Document("class Test()\n\t\textends Super(){\n\t\tvoid method(){}\n}");
        instance.doCorrectIndentation(doc);
        assertResult(doc, "class Test()\n\t\textends Super(){\n\tvoid method(){}\n}");
        
        doc = new Document("class Test()\nextends Super(){\n\nvoid method(){}\n\n}");
        instance.doCorrectIndentation(doc);
        assertResult(doc, "class Test()\n\t\textends Super(){\n\t\n\tvoid method(){}\n\t\n}");
        
        doc = new Document("class Test() extends Super(){\n\nvoid method(){}\n\t\n}");
        instance.doCorrectIndentation(doc);
        assertResult(doc, "class Test() extends Super(){\n\t\n\tvoid method(){}\n\t\n}");
        
        doc = new Document("class Test()\n\t\textends Super()\n{\n\nvoid method(){}\n\t\n}");
        instance.doCorrectIndentation(doc);
        assertResult(doc, "class Test()\n\t\textends Super()\n{\n\t\n\tvoid method(){}\n\t\n}");
        
        doc = new Document("\tclass Test()\n\t\textends Super(){\n\nvoid method(){\n}\n\t\n}");
        instance.doCorrectIndentation(doc);
        assertResult(doc, "class Test()\n\t\textends Super(){\n\t\n\tvoid method(){\n\t}\n\t\n}");
        
        doc = new Document("void x(){\n\tclass Test()\n\t\textends Super(){\n\nvoid method(){\n}\n\t\n}");
        instance.doCorrectIndentation(doc);
        assertResult(doc, "void x(){\n\tclass Test()\n\t\t\textends Super(){\n\t\t\n\t\tvoid method(){\n\t\t}\n\t\t\n\t}");

        doc = new Document("void x(){\n\tclass Test()\n\t\textends Super(){\nvoid method(){\n}\n}\n");
        instance.doCorrectIndentation(doc);
        assertResult(doc, "void x(){\n\tclass Test()\n\t\t\textends Super(){\n\t\tvoid method(){\n\t\t}\n\t}\n");
        
        doc = new Document("void x(){\n\tclass Test()\n\t\textends Super(){//foo\nvoid method(){//bar\n}//baz\n}\n");
        instance.doCorrectIndentation(doc);
        assertResult(doc, "void x(){\n\tclass Test()\n\t\t\textends Super(){//foo\n\t\tvoid method(){//bar\n\t\t}//baz\n\t}\n");
        
        doc = new Document("doc (\"Hello\n\t World\n\t !\")\nvoid hello(){}");
        instance.doCorrectIndentation(doc);
//        assertResult(doc, "doc (\"Hello\n\t World\n\t !\")\nvoid hello(){}");
        assertResult(doc, "doc (\"Hello\n      World\n      !\")\nvoid hello(){}");

        doc = new Document("\"Hello\n World\n !\"\nvoid hello(){}");
        instance.doCorrectIndentation(doc);
        assertResult(doc, "\"Hello\n World\n !\"\nvoid hello(){}");
        
        doc = new Document("\"\"\"Hello\n   World\n   !\"\"\"\nvoid hello(){}");
        instance.doCorrectIndentation(doc);
        assertResult(doc, "\"\"\"Hello\n   World\n   !\"\"\"\nvoid hello(){}");
        
        doc = new Document("void x(){\n\t\"\"\"Hello\n\t   World\n\t   !\"\"\"\n\tvoid hello(){}");
        instance.doCorrectIndentation(doc);
        assertResult(doc, "void x(){\n\t\"\"\"Hello\n\t   World\n\t   !\"\"\"\n\tvoid hello(){}");
        
        //Note: this test fails, but that is more of a conceptual
        //      problem with how the whole concept of correct
        //      indentation works!
        doc = new Document("\t\"\"\"Hello\n\t   World\n\t   !\"\"\"\n\tvoid hello(){}");
        instance.doCorrectIndentation(doc);
        assertResult(doc, "\"\"\"Hello\n   World\n   !\"\"\"\nvoid hello(){}");
        
        doc = new Document("String x()\n=>\"hello\";");
        instance.doCorrectIndentation(doc);
        assertResult(doc, "String x()\n\t\t=>\"hello\";");

        doc = new Document("String x()\n\t=>\"hello\";");
        instance.doCorrectIndentation(doc);
        assertResult(doc, "String x()\n\t\t=>\"hello\";");
        
        doc = new Document("String x()\n\t\t=>\"hello\";");
        instance.doCorrectIndentation(doc);
        assertResult(doc, "String x()\n\t\t=>\"hello\";");

        doc = new Document("void x(){\n\tString x()\n\t\t\t=>\"hello\";");
        instance.doCorrectIndentation(doc);
        assertResult(doc, "void x(){\n\tString x()\n\t\t\t=>\"hello\";");
        
        doc = new Document("\tString x()\n\t\t\t=>\"hello\";");
        instance.doCorrectIndentation(doc);
        assertResult(doc, "String x()\n\t\t=>\"hello\";");
        
        doc = new Document("class X()\nextends Y()");
        instance.doCorrectIndentation(doc);
        assertResult(doc, "class X()\n\t\textends Y()");

        doc = new Document("class X()\n\textends Y()");
        instance.doCorrectIndentation(doc);
        assertResult(doc, "class X()\n\t\textends Y()");
        
        doc = new Document("class X()\n\t\textends Y()");
        instance.doCorrectIndentation(doc);
        assertResult(doc, "class X()\n\t\textends Y()");

        doc = new Document("void x(){\n\tclass X()\n\t\t\textends Y()");
        instance.doCorrectIndentation(doc);
        assertResult(doc, "void x(){\n\tclass X()\n\t\t\textends Y()");
        
        doc = new Document("class X()\nextends Y()\nsatisfies Z");
        instance.doCorrectIndentation(doc);
        assertResult(doc, "class X()\n\t\textends Y()\n\t\tsatisfies Z");

        doc = new Document("class X()\n\textends Y()\n\tsatisfies Z");
        instance.doCorrectIndentation(doc);
        assertResult(doc, "class X()\n\t\textends Y()\n\t\tsatisfies Z");
        
        doc = new Document("class X()\n\t\textends Y()\n\t\tsatisfies Z");
        instance.doCorrectIndentation(doc);
        assertResult(doc, "class X()\n\t\textends Y()\n\t\tsatisfies Z");

        doc = new Document("void x(){\n\tclass X()\n\t\t\textends Y()\n\t\t\tsatisfies Z");
        instance.doCorrectIndentation(doc);
        assertResult(doc, "void x(){\n\tclass X()\n\t\t\textends Y()\n\t\t\tsatisfies Z");
        
        doc = new Document("\tclass X()\n\t\t\textends Y()\n\t\t\tsatisfies Z");
        instance.doCorrectIndentation(doc);
        assertResult(doc, "class X()\n\t\textends Y()\n\t\tsatisfies Z");
        
        doc = new Document("void x(){\n\t//comment\n");
        instance.doCorrectIndentation(doc);
        assertResult(doc, "void x(){\n\t//comment\n");
        
        doc = new Document("void x(){\n//comment\n");
        instance.doCorrectIndentation(doc);
        assertResult(doc, "void x(){\n\t//comment\n");
        
        doc = new Document("void x(){\n\t\t//comment\n");
        instance.doCorrectIndentation(doc);
        assertResult(doc, "void x(){\n\t//comment\n");
        
        doc = new Document("\t//comment\n");
        instance.doCorrectIndentation(doc);
        assertResult(doc, "//comment\n");
        
        doc = new Document("\t\t//comment\n");
        instance.doCorrectIndentation(doc);
        assertResult(doc, "//comment\n");
        
        doc = new Document("\t/*\n\tcomment\n\t*/\n");
        instance.doCorrectIndentation(doc);
        assertResult(doc, "/*\n comment\n */\n");
        
        doc = new Document("\t\t/*\n\tcomment\n\t*/\n");
        instance.doCorrectIndentation(doc);
        assertResult(doc, "/*\n comment\n */\n");
        
        doc = new Document("/*\nhello\n  world\n*/\n");
        instance.doCorrectIndentation(doc);
        assertResult(doc, "/*\n hello\n  world\n */\n");
        
        doc = new Document("x=1;");
        instance.doNewline(doc);
        assertResult(doc, "x=1;\n");

        doc = new Document("x=1; ");
        instance.doNewline(doc);
        assertResult(doc, "x=1; \n");

        doc = new Document("void x(){\n\tx=1;");
        instance.doNewline(doc);
        assertResult(doc, "void x(){\n\tx=1;\n\t");
        
        doc = new Document("void x(){\n\tx=1; //foo");
        instance.doNewline(doc);
        assertResult(doc, "void x(){\n\tx=1; //foo\n\t");
        
        doc = new Document("void x(){\nvoid y(){\n\t\tx=1; //foo");
        instance.doNewline(doc);
        assertResult(doc, "void x(){\nvoid y(){\n\t\tx=1; //foo\n\t\t");
        
        doc = new Document("\tx=1; //foo");
        instance.doNewline(doc);
        assertResult(doc, "\tx=1; //foo\n\t");
        
        doc = new Document("\t\tx=1; //foo");
        instance.doNewline(doc);
        assertResult(doc, "\t\tx=1; //foo\n\t\t");
        
        doc = new Document("Integer x {");
        instance.doNewline(doc);
        assertResult(doc, "Integer x {\n\t\n}");

        doc = new Document("Integer x { ");
        instance.doNewline(doc);
        assertResult(doc, "Integer x { \n\t\n}");

        doc = new Document("\tInteger x {");
        instance.doNewline(doc);
        assertResult(doc, "\tInteger x {\n\t\t\n\t}");

        doc = new Document("\tInteger x { //foo");
        instance.doNewline(doc);
        assertResult(doc, "\tInteger x { //foo\n\t\t\n\t}");

        doc = new Document("\t\tInteger x { //foo");
        instance.doNewline(doc);
        assertResult(doc, "\t\tInteger x { //foo\n\t\t\t\n\t\t}");

        doc = new Document("//hello");
        instance.doNewline(doc);
        assertResult(doc, "//hello\n");

        doc = new Document("//hello ");
        instance.doNewline(doc);
        assertResult(doc, "//hello \n");

        doc = new Document("\t//hello");
        instance.doNewline(doc);
        assertResult(doc, "\t//hello\n\t");
        
        doc = new Document("\t//hello ");
        instance.doNewline(doc);
        assertResult(doc, "\t//hello \n\t");
        
        doc = new Document("\t\t//hello");
        instance.doNewline(doc);
        assertResult(doc, "\t\t//hello\n\t\t");
        
        doc = new Document("//hello\n");
        instance.doNewline(doc);
        assertResult(doc, "//hello\n\n");

        doc = new Document("\t//hello\n");
        instance.doNewline(doc);
        assertResult(doc, "\t//hello\n\n\t");

        doc = new Document("\t//hello\n\t");
        instance.doNewline(doc);
        assertResult(doc, "\t//hello\n\t\n\t");

        doc = new Document("//hello \n ");
        instance.doNewline(doc);
        assertResult(doc, "//hello \n \n");

        doc = new Document("//hello \n\t");
        instance.doNewline(doc);
        assertResult(doc, "//hello \n\t\n");

        doc = new Document("\t//hello \n\t");
        instance.doNewline(doc);
        assertResult(doc, "\t//hello \n\t\n\t");

        doc = new Document("\t//hello\n\t");
        instance.doNewline(doc);
        assertResult(doc, "\t//hello\n\t\n\t");
        
        doc = new Document("\t//hello \n\t");
        instance.doNewline(doc);
        assertResult(doc, "\t//hello \n\t\n\t");
        
        doc = new Document("/*hello");
        instance.doNewline(doc);
        assertResult(doc, "/*hello\n \n */");
        
        doc = new Document("\t/*hello");
        instance.doNewline(doc);
        assertResult(doc, "\t/*hello\n\t \n\t */");
        
        doc = new Document("\t/*\n\thello");
        instance.doNewline(doc);
        assertResult(doc, "\t/*\n\thello\n\t ");
        
        doc = new Document("void x() {}");
        instance.doNewline(doc);
        assertResult(doc, "void x() {}\n");

        doc = new Document("void x() {} ");
        instance.doNewline(doc);
        assertResult(doc, "void x() {} \n");

        doc = new Document("\tvoid x() {}");
        instance.doNewline(doc);
        assertResult(doc, "\tvoid x() {}\n\t");
        
        doc = new Document("\tvoid x() {} //foo");
        instance.doNewline(doc);
        assertResult(doc, "\tvoid x() {} //foo\n\t");
        
        doc = new Document("x=1;\n");
        instance.doNewline(doc);
        assertResult(doc, "x=1;\n\n");

        doc = new Document("x=1; \n ");
        instance.doNewline(doc);
        assertResult(doc, "x=1; \n \n "); //do we really want the space at the end here?

        doc = new Document("\tx=1;\n\t");
        instance.doNewline(doc);
        assertResult(doc, "\tx=1;\n\t\n\t");
        
        doc = new Document("\tx=1; //foo\n\t");
        instance.doNewline(doc);
        assertResult(doc, "\tx=1; //foo\n\t\n\t");
        
        doc = new Document("\t\tx=1; //foo\n\t\t");
        instance.doNewline(doc);
        assertResult(doc, "\t\tx=1; //foo\n\t\t\n\t\t");
        
        doc = new Document("Integer x {\n\t");
        instance.doNewline(doc);
        assertResult(doc, "Integer x {\n\t\n\t");

        doc = new Document("Integer x { \n\t");
        instance.doNewline(doc);
        assertResult(doc, "Integer x { \n\t\n\t");

        doc = new Document("\tInteger x {\n\t\t");
        instance.doNewline(doc);
        assertResult(doc, "\tInteger x {\n\t\t\n\t\t");

        doc = new Document("\tInteger x { //foo\n\t\t");
        instance.doNewline(doc);
        assertResult(doc, "\tInteger x { //foo\n\t\t\n\t\t");

        doc = new Document("\t\tInteger x { //foo\n\t\t\t");
        instance.doNewline(doc);
        assertResult(doc, "\t\tInteger x { //foo\n\t\t\t\n\t\t\t");

        doc = new Document("void x() {}\n");
        instance.doNewline(doc);
        assertResult(doc, "void x() {}\n\n");

        doc = new Document("void x() {} \n ");
        instance.doNewline(doc);
        assertResult(doc, "void x() {} \n \n "); //do we really want the space

        doc = new Document("\tvoid x() {}\n\t");
        instance.doNewline(doc);
        assertResult(doc, "\tvoid x() {}\n\t\n\t");
        
        doc = new Document("\tvoid x() {} //foo\n\t");
        instance.doNewline(doc);
        assertResult(doc, "\tvoid x() {} //foo\n\t\n\t");
        
        doc = new Document("String greeting = \"hello");
        instance.doNewline(doc);
        assertResult(doc, "String greeting = \"hello\n                   ");
        
        doc = new Document("\tString greeting = \"hello");
        instance.doNewline(doc);
        assertResult(doc, "\tString greeting = \"hello\n\t                   ");
        
        doc = new Document("String greeting = \"hello\n                   world");
        instance.doNewline(doc);
        assertResult(doc, "String greeting = \"hello\n                   world\n                   ");
        
        doc = new Document("\tString greeting = \"hello\n\t                   world");
        instance.doNewline(doc);
        assertResult(doc, "\tString greeting = \"hello\n\t                   world\n\t                   ");
        
        doc = new Document("String greeting = \"\"\"hello");
        instance.doNewline(doc);
        assertResult(doc, "String greeting = \"\"\"hello\n                     ");
        
        doc = new Document("\tString greeting = \"\"\"hello");
        instance.doNewline(doc);
        assertResult(doc, "\tString greeting = \"\"\"hello\n\t                     ");
        
        doc = new Document("String greeting = \"\"\"hello\n                     world");
        instance.doNewline(doc);
        assertResult(doc, "String greeting = \"\"\"hello\n                     world\n                     ");
        
        doc = new Document("\tString greeting = \"\"\"hello\n\t                     world");
        instance.doNewline(doc);
        assertResult(doc, "\tString greeting = \"\"\"hello\n\t                     world\n\t                     ");
        
        doc = new Document("String x()\n=>\"hello\" +");
        instance.doNewline(doc);
        assertResult(doc, "String x()\n=>\"hello\" +\n\t\t");

        //What should this one really do? Not well-defined
        doc = new Document("String x()\n\t=>\"hello\" +");
        instance.doNewline(doc);
        assertResult(doc, "String x()\n\t=>\"hello\" +\n\t\t");
        
        doc = new Document("String x()\n\t\t=>\"hello\" +");
        instance.doNewline(doc);
        assertResult(doc, "String x()\n\t\t=>\"hello\" +\n\t\t");

        doc = new Document("\tString x()\n\t\t\t=>\"hello\" +");
        instance.doNewline(doc);
        assertResult(doc, "\tString x()\n\t\t\t=>\"hello\" +\n\t\t\t");

        doc = new Document("String x()\n=>\"hello\";");
        instance.doNewline(doc);
        assertResult(doc, "String x()\n=>\"hello\";\n");

        doc = new Document("String x()\n\t=>\"hello\";");
        instance.doNewline(doc);
        assertResult(doc, "String x()\n\t=>\"hello\";\n");
        
        doc = new Document("String x()\n\t\t=>\"hello\";");
        instance.doNewline(doc);
        assertResult(doc, "String x()\n\t\t=>\"hello\";\n");

        doc = new Document("\tString x()\n\t\t\t=>\"hello\";");
        instance.doNewline(doc);
        assertResult(doc, "\tString x()\n\t\t\t=>\"hello\";\n\t");
        
        doc = new Document("void x() {\n\t");
        instance.doClosingBrace(doc);
        assertResult(doc, "void x() {\n}");

        doc = new Document("\tvoid x() {\n\t\t");
        instance.doClosingBrace(doc);
        assertResult(doc, "\tvoid x() {\n\t}");

        doc = new Document("void x() {\n\tprint(\"hello\");\n\t");
        instance.doClosingBrace(doc);
        assertResult(doc, "void x() {\n\tprint(\"hello\");\n}");

        doc = new Document("void x() {\n\tprint(\"hello\");\n\t//bye\n\t");
        instance.doClosingBrace(doc);
        assertResult(doc, "void x() {\n\tprint(\"hello\");\n\t//bye\n}");
    }
    
    static int count=0;
    
    private static void assertResult(Document doc, String expected) {
        count++;
        String actual = doc.get();
        if (!actual.equals(expected)) {
            System.out.println("assertion failed: " + count);
            System.out.println(actual);
            System.out.println("expected:");
            System.out.println(expected);
        }
    }
    
    private void doNewline(IDocument doc) {
        try {
            DocumentCommand cmd= new DocumentCommand() { };
            cmd.offset= doc.getLength();
            cmd.length= 0;
            cmd.text= Character.toString('\n');
            cmd.doit= true;
            cmd.shiftsCaret= true;
            customizeDocumentCommand(doc, cmd);
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
            customizeDocumentCommand(doc, cmd);
            doc.replace(cmd.offset, cmd.length, cmd.text);
        } catch (BadLocationException e) {
            System.err.println("Correct Indentation command failed " + e.getMessage());
        }
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
                customizeDocumentCommand(doc, cmd);
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