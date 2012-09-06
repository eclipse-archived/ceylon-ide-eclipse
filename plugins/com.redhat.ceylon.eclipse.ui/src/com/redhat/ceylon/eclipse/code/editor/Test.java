package com.redhat.ceylon.eclipse.code.editor;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.DocumentCommand;
import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.graphics.Point;

class Test extends CeylonAutoEditStrategy {
    
	Test() { super(null); }
	
    public static void main(String[] args) {
        Test instance = new Test();
        
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
        assertResult(doc, "class Test()\nextends Super(){\n\t\n\tvoid method(){}\n\t\n}");
        
        doc = new Document("class Test() extends Super(){\n\nvoid method(){}\n\t\n}");
        instance.doCorrectIndentation(doc);
        assertResult(doc, "class Test() extends Super(){\n\t\n\tvoid method(){}\n\t\n}");
        
        doc = new Document("class Test()\n\t\textends Super()\n{\n\nvoid method(){}\n\t\n}");
        instance.doCorrectIndentation(doc);
        assertResult(doc, "class Test()\n\t\textends Super()\n{\n\t\n\tvoid method(){}\n\t\n}");
        
        //failing:
        doc = new Document("\tclass Test()\n\t\textends Super(){\n\nvoid method(){\n}\n\t\n}");
        instance.doCorrectIndentation(doc);
        assertResult(doc, "\tclass Test()\n\t\t\textends Super(){\n\t\t\n\t\tvoid method(){\n\t\t}\n\t\t\n\t}");

        doc = new Document("\tclass Test()\n\t\textends Super(){\nvoid method(){\n}\n}\n");
        instance.doCorrectIndentation(doc);
        assertResult(doc, "\tclass Test()\n\t\t\textends Super(){\n\t\tvoid method(){\n\t\t}\n\t}\n");
        
        doc = new Document("\tclass Test()\n\t\textends Super(){//foo\nvoid method(){//bar\n}//baz\n}\n");
        instance.doCorrectIndentation(doc);
        assertResult(doc, "\tclass Test()\n\t\t\textends Super(){//foo\n\t\tvoid method(){//bar\n\t\t}//baz\n\t}\n");
        
        /*doc = new Document("doc \"Hello\n     World\n     !\"\n void hello(){}");
        instance.doCorrectIndentation(doc);
        assertResult(doc, "doc \"Hello\n     World\n     !\"\n void hello(){}");*/

        doc = new Document("x=1;");
        instance.doNewline(doc);
        assertResult(doc, "x=1;\n");

        doc = new Document("x=1; ");
        instance.doNewline(doc);
        assertResult(doc, "x=1; \n");

        doc = new Document("\tx=1;");
        instance.doNewline(doc);
        assertResult(doc, "\tx=1;\n\t");
        
        doc = new Document("\tx=1; //foo");
        instance.doNewline(doc);
        assertResult(doc, "\tx=1; //foo\n\t");
        
        doc = new Document("\t\tx=1; //foo");
        instance.doNewline(doc);
        assertResult(doc, "\t\tx=1; //foo\n\t\t");
        
        doc = new Document("Integer x {");
        instance.doNewline(doc);
        assertResult(doc, "Integer x {\n\t");

        doc = new Document("Integer x { ");
        instance.doNewline(doc);
        assertResult(doc, "Integer x { \n\t");

        doc = new Document("\tInteger x {");
        instance.doNewline(doc);
        assertResult(doc, "\tInteger x {\n\t\t");

        doc = new Document("\tInteger x { //foo");
        instance.doNewline(doc);
        assertResult(doc, "\tInteger x { //foo\n\t\t");

        doc = new Document("\t\tInteger x { //foo");
        instance.doNewline(doc);
        assertResult(doc, "\t\tInteger x { //foo\n\t\t\t");

        doc = new Document("//hello");
        instance.doNewline(doc);
        assertResult(doc, "//hello\n");

        doc = new Document("//hello ");
        instance.doNewline(doc);
        assertResult(doc, "//hello \n");

        doc = new Document("\t//hello");
        instance.doNewline(doc);
        assertResult(doc, "\t//hello\n\t");
        
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

        doc = new Document("//hello\n");
        instance.doNewline(doc);
        assertResult(doc, "//hello\n\n");

        doc = new Document("//hello \n ");
        instance.doNewline(doc);
        assertResult(doc, "//hello \n \n "); //do we really want the space

        doc = new Document("\t//hello\n\t");
        instance.doNewline(doc);
        assertResult(doc, "\t//hello\n\t\n\t");
        
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

    private static void assertResult(Document doc, String result) {
        if (!doc.get().equals(result)) {
            System.out.println(doc.get());
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