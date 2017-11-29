/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.ui.test.headless;


import static org.eclipse.ceylon.ide.eclipse.code.preferences.CeylonPreferenceInitializer.CLOSE_ANGLES;
import static org.eclipse.ceylon.ide.eclipse.code.preferences.CeylonPreferenceInitializer.CLOSE_BACKTICKS;
import static org.eclipse.ceylon.ide.eclipse.code.preferences.CeylonPreferenceInitializer.CLOSE_BRACKETS;
import static org.eclipse.ceylon.ide.eclipse.code.preferences.CeylonPreferenceInitializer.CLOSE_PARENS;
import static org.eclipse.ceylon.ide.eclipse.code.preferences.CeylonPreferenceInitializer.CLOSE_QUOTES;
import static org.eclipse.ceylon.ide.eclipse.code.preferences.CeylonPreferenceInitializer.CLOSE_BRACES;
import static org.junit.Assert.assertEquals;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.DocumentCommand;
import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.graphics.Point;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import org.eclipse.ceylon.ide.eclipse.code.editor.CeylonAutoEditStrategy;
import org.eclipse.ceylon.ide.eclipse.ui.CeylonPlugin;

public class AutoEditTests {
 
    @BeforeClass
    public static void setupPreferences() {
        String[] FENCES = {
            CLOSE_BRACES,
            CLOSE_QUOTES,
            CLOSE_BACKTICKS,
            CLOSE_ANGLES,
            CLOSE_PARENS,
            CLOSE_BRACKETS};
        IPreferenceStore store = CeylonPlugin.getPreferences();
        for (String closeSetting : FENCES) {
            store.setValue(closeSetting, true);
            store.setDefault(closeSetting, true);
        }
    }
    
    @Test
    public void testCorrectIndentation1() {
        checkForCorrectIndentation(
            "class Test()\n\t\textends Super(){\n\nvoid method(){\n\nfor (x in xs){}\n\n}\n\n}",
            "class Test()\n\t\textends Super(){\n\t\n\tvoid method(){\n\t\t\n\t\tfor (x in xs){}\n\t\t\n\t}\n\t\n}");
    }
    
    @Test
    public void testCorrectIndentation2() {
        checkForCorrectIndentation(
            "class Test()\n\t\textends Super(){\n\nvoid method(){}\n\n}",
            "class Test()\n\t\textends Super(){\n\t\n\tvoid method(){}\n\t\n}");
    }
    
    @Test
    public void testCorrectIndentation3() {
        checkForCorrectIndentation(
            "class Test()\n\t\textends Super(){\n\n\t\tvoid method(){}\n\t\n}",
            "class Test()\n\t\textends Super(){\n\t\n\tvoid method(){}\n\t\n}");
    }
    
    @Test
    public void testCorrectIndentation4() {
        checkForCorrectIndentation(
            "class Test()\n\t\textends Super(){\nvoid method(){}\n}",
            "class Test()\n\t\textends Super(){\n\tvoid method(){}\n}");
    }
    
    @Test
    public void testCorrectIndentation5() {
        checkForCorrectIndentation(
            "class Test()\n\t\textends Super(){\nvoid method(){\nfor (x in xs){}\n}\n}",
            "class Test()\n\t\textends Super(){\n\tvoid method(){\n\t\tfor (x in xs){}\n\t}\n}");
    }
    
    @Test
    public void testCorrectIndentation6() {
        checkForCorrectIndentation(
            "class Test()\n\t\textends Super(){\n\t\tvoid method(){}\n}",
            "class Test()\n\t\textends Super(){\n\tvoid method(){}\n}");
    }
    
    @Test
    public void testCorrectIndentation7() {
        checkForCorrectIndentation(
            "class Test()\nextends Super(){\n\nvoid method(){}\n\n}",
            "class Test()\n\t\textends Super(){\n\t\n\tvoid method(){}\n\t\n}");
    }
    
    @Test
    public void testCorrectIndentation8() {
        checkForCorrectIndentation(
            "class Test() extends Super(){\n\nvoid method(){}\n\t\n}",
            "class Test() extends Super(){\n\t\n\tvoid method(){}\n\t\n}");
    }
    
    @Test
    public void testCorrectIndentation9() {
        checkForCorrectIndentation(
            "class Test()\n\t\textends Super()\n{\n\nvoid method(){}\n\t\n}",
            "class Test()\n\t\textends Super()\n{\n\t\n\tvoid method(){}\n\t\n}");
    }
    
    @Test
    public void testCorrectIndentation10() {
        checkForCorrectIndentation(
            "\tclass Test()\n\t\textends Super(){\n\nvoid method(){\n}\n\t\n}",
            "class Test()\n\t\textends Super(){\n\t\n\tvoid method(){\n\t}\n\t\n}");
    }
    
    @Test
    public void testCorrectIndentation11() {
        checkForCorrectIndentation(
            "void x(){\n\tclass Test()\n\t\textends Super(){\n\nvoid method(){\n}\n\t\n}",
            "void x(){\n\tclass Test()\n\t\t\textends Super(){\n\t\t\n\t\tvoid method(){\n\t\t}\n\t\t\n\t}");
    }
    
    @Test
    public void testCorrectIndentation12() {
        checkForCorrectIndentation(
            "void x(){\n\tclass Test()\n\t\textends Super(){\nvoid method(){\n}\n}\n",
            "void x(){\n\tclass Test()\n\t\t\textends Super(){\n\t\tvoid method(){\n\t\t}\n\t}\n");
    }
    
    @Test
    public void testCorrectIndentation13() {
        checkForCorrectIndentation(
            "void x(){\n\tclass Test()\n\t\textends Super(){//foo\nvoid method(){//bar\n}//baz\n}\n",
            "void x(){\n\tclass Test()\n\t\t\textends Super(){//foo\n\t\tvoid method(){//bar\n\t\t}//baz\n\t}\n");
    }
    
    @Test
    public void testCorrectIndentation14() {
        checkForCorrectIndentation(
            "doc (\"Hello\n\t World\n\t !\")\nvoid hello(){}",
            "doc (\"Hello\n      World\n      !\")\nvoid hello(){}");
    }
    
    @Test
    public void testCorrectIndentation15() {
        checkForCorrectIndentation(
            "\"Hello\n World\n !\"\nvoid hello(){}",
            "\"Hello\n World\n !\"\nvoid hello(){}");
    }
    
    @Test
    public void testCorrectIndentation16() {
        checkForCorrectIndentation(
            "\"\"\"Hello\n   World\n   !\"\"\"\nvoid hello(){}",
            "\"\"\"Hello\n   World\n   !\"\"\"\nvoid hello(){}");
    }
    
    @Test
    public void testCorrectIndentation17() {
        checkForCorrectIndentation(
            "void x(){\n\t\"\"\"Hello\n\t   World\n\t   !\"\"\"\n\tvoid hello(){}",
            "void x(){\n\t\"\"\"Hello\n\t   World\n\t   !\"\"\"\n\tvoid hello(){}");
    }
    
    @Test
    @Ignore("This test fails, but that is more of a conceptual\n" +
            "problem with how the whole concept of correct\n" +
            "indentation works")
    public void testCorrectIndentation18() {
        //Note: this test fails, but that is more of a conceptual
        //      problem with how the whole concept of correct
        //      indentation works!
        checkForCorrectIndentation(
            "\t\"\"\"Hello\n\t   World\n\t   !\"\"\"\n\tvoid hello(){}",
            "\"\"\"Hello\n   World\n   !\"\"\"\nvoid hello(){}");
    }
    
    @Test
    public void testCorrectIndentation19() {
        checkForCorrectIndentation(
            "String x()\n=>\"hello\";",
            "String x()\n\t\t=>\"hello\";");
    }
    
    @Test
    public void testCorrectIndentation20() {
        checkForCorrectIndentation(
            "String x()\n\t=>\"hello\";",
            "String x()\n\t\t=>\"hello\";");
    }
    
    @Test
    public void testCorrectIndentation21() {
        checkForCorrectIndentation(
            "String x()\n\t\t=>\"hello\";",
            "String x()\n\t\t=>\"hello\";");
    }
    
    @Test
    public void testCorrectIndentation22() {
        checkForCorrectIndentation(
            "void x(){\n\tString x()\n\t\t\t=>\"hello\";",
            "void x(){\n\tString x()\n\t\t\t=>\"hello\";");
    }
    
    @Test
    public void testCorrectIndentation23() {
        checkForCorrectIndentation(
            "\tString x()\n\t\t\t=>\"hello\";",
            "String x()\n\t\t=>\"hello\";");
    }
    
    @Test
    public void testCorrectIndentation24() {
        checkForCorrectIndentation(
            "class X()\nextends Y()",
            "class X()\n\t\textends Y()");
    }
    
    @Test
    public void testCorrectIndentation25() {
        checkForCorrectIndentation(
            "class X()\n\textends Y()",
            "class X()\n\t\textends Y()");
    }
    
    @Test
    public void testCorrectIndentation26() {
        checkForCorrectIndentation(
            "class X()\n\t\textends Y()",
            "class X()\n\t\textends Y()");
    }
    
    @Test
    public void testCorrectIndentation27() {
        checkForCorrectIndentation(
            "void x(){\n\tclass X()\n\t\t\textends Y()",
            "void x(){\n\tclass X()\n\t\t\textends Y()");
    }
    
    @Test
    public void testCorrectIndentation28() {
        checkForCorrectIndentation(
            "class X()\nextends Y()\nsatisfies Z",
            "class X()\n\t\textends Y()\n\t\tsatisfies Z");
    }
    
    @Test
    public void testCorrectIndentation29() {
        checkForCorrectIndentation(
            "class X()\n\textends Y()\n\tsatisfies Z",
            "class X()\n\t\textends Y()\n\t\tsatisfies Z");
    }
    
    @Test
    public void testCorrectIndentation30() {
        checkForCorrectIndentation(
            "class X()\n\t\textends Y()\n\t\tsatisfies Z",
            "class X()\n\t\textends Y()\n\t\tsatisfies Z");
    }
    
    @Test
    public void testCorrectIndentation31() {
        checkForCorrectIndentation(
            "void x(){\n\tclass X()\n\t\t\textends Y()\n\t\t\tsatisfies Z",
            "void x(){\n\tclass X()\n\t\t\textends Y()\n\t\t\tsatisfies Z");
    }
    
    @Test
    public void testCorrectIndentation32() {
        checkForCorrectIndentation(
            "\tclass X()\n\t\t\textends Y()\n\t\t\tsatisfies Z",
            "class X()\n\t\textends Y()\n\t\tsatisfies Z");
    }
    
    @Test
    public void testCorrectIndentation33() {
        checkForCorrectIndentation(
            "void x(){\n\t//comment\n",
            "void x(){\n\t//comment\n");
    }
    
    @Test
    public void testCorrectIndentation34() {
        checkForCorrectIndentation(
            "void x(){\n//comment\n",
            "void x(){\n\t//comment\n");
    }
    
    @Test
    public void testCorrectIndentation35() {
        checkForCorrectIndentation(
            "void x(){\n\t\t//comment\n",
            "void x(){\n\t//comment\n");
    }
    
    @Test
    public void testCorrectIndentation36() {
        checkForCorrectIndentation(
            "\t//comment\n",
            "//comment\n");
    }
    
    @Test
    public void testCorrectIndentation37() {
        checkForCorrectIndentation(
            "\t\t//comment\n",
            "//comment\n");
    }
    
    @Test
    public void testCorrectIndentation38() {
        checkForCorrectIndentation(
            "\t/*\n\tcomment\n\t*/\n",
            "/*\n comment\n */\n");
    }
    
    @Test
    public void testCorrectIndentation39() {
        checkForCorrectIndentation(
            "\t\t/*\n\tcomment\n\t*/\n",
            "/*\n comment\n */\n");
    }
    
    @Test
    public void testNewLine1() {
        checkForNewLine(
            "x=1;",
            "x=1;\n");
    }
    
    @Test
    public void testNewLine2() {
        checkForNewLine(
            "x=1; ",
            "x=1; \n");
    }
    
    @Test
    public void testNewLine3() {
        checkForNewLine(
            "void x(){\n\tx=1;",
            "void x(){\n\tx=1;\n\t");
    }
    
    @Test
    public void testNewLine4() {
        checkForNewLine(
            "void x(){\n\tx=1; //foo",
            "void x(){\n\tx=1; //foo\n\t");
    }
    
    @Test
    public void testNewLine5() {
        checkForNewLine(
            "void x(){\nvoid y(){\n\t\tx=1; //foo",
            "void x(){\nvoid y(){\n\t\tx=1; //foo\n\t\t");
    }
    
    @Test
    public void testNewLine6() {
        checkForNewLine(
            "\tx=1; //foo",
            "\tx=1; //foo\n\t");
    }
    
    @Test
    public void testNewLine7() {
        checkForNewLine(
            "\t\tx=1; //foo",
            "\t\tx=1; //foo\n\t\t");
    }
    
    @Test
    public void testNewLine8() {
        checkForNewLine(
            "Integer x {",
            "Integer x {\n\t\n}");
    }
    
    @Test
    public void testNewLine9() {
        checkForNewLine(
            "Integer x { ",
            "Integer x { \n\t\n}");
    }
    
    @Test
    public void testNewLine10() {
        checkForNewLine(
            "\tInteger x {",
            "\tInteger x {\n\t\t\n\t}");
    }
    
    @Test
    public void testNewLine11() {
        checkForNewLine(
            "\tInteger x { //foo",
            "\tInteger x { //foo\n\t\t\n\t}");
    }
    
    @Test
    public void testNewLine12() {
        checkForNewLine(
            "\t\tInteger x { //foo",
            "\t\tInteger x { //foo\n\t\t\t\n\t\t}");
    }
    
    @Test
    public void testNewLine13() {
        checkForNewLine(
            "//hello",
            "//hello\n");
    }
    
    @Test
    public void testNewLine14() {
        checkForNewLine(
            "//hello ",
            "//hello \n");
    }
    
    @Test
    public void testNewLine15() {
        checkForNewLine(
            "\t//hello",
            "\t//hello\n\t");
    }
    
    @Test
    public void testNewLine16() {
        checkForNewLine(
            "\t//hello ",
            "\t//hello \n\t");
    }
    
    @Test
    public void testNewLine17() {
        checkForNewLine(
            "\t\t//hello",
            "\t\t//hello\n\t\t");
    }
    
    @Test
    public void testNewLine18() {
        checkForNewLine(
            "//hello\n",
            "//hello\n\n");
    }
    
    @Test
    public void testNewLine19() {
        checkForNewLine(
            "\t//hello\n",
            "\t//hello\n\n\t");
    }
    
    @Test
    public void testNewLine20() {
        checkForNewLine(
            "\t//hello\n\t",
            "\t//hello\n\t\n\t");
    }
    
    @Test
    public void testNewLine21() {
        checkForNewLine(
            "//hello \n ",
            "//hello \n \n");
    }
    
    @Test
    public void testNewLine22() {
        checkForNewLine(
            "//hello \n\t",
            "//hello \n\t\n");
    }
    
    @Test
    public void testNewLine23() {
        checkForNewLine(
            "\t//hello \n\t",
            "\t//hello \n\t\n\t");
    }
    
    @Test
    public void testNewLine24() {
        checkForNewLine(
            "\t//hello\n\t",
            "\t//hello\n\t\n\t");
    }
    
    @Test
    public void testNewLine25() {
        checkForNewLine(
            "\t//hello \n\t",
            "\t//hello \n\t\n\t");
    }
    
    @Test
    public void testNewLine26() {
        checkForNewLine(
            "/*hello",
            "/*hello\n \n */");
    }
    
    @Test
    public void testNewLine27() {
        checkForNewLine(
            "\t/*hello",
            "\t/*hello\n\t \n\t */");
    }
    
    @Test
    public void testNewLine28() {
        checkForNewLine(
            "\t/*\n\thello",
            "\t/*\n\thello\n\t ");
    }
    
    @Test
    public void testNewLine29() {
        checkForNewLine(
            "void x() {}",
            "void x() {}\n");
    }
    
    @Test
    public void testNewLine30() {
        checkForNewLine(
            "void x() {} ",
            "void x() {} \n");
    }
    
    @Test
    public void testNewLine31() {
        checkForNewLine(
            "\tvoid x() {}",
            "\tvoid x() {}\n\t");
    }
    
    @Test
    public void testNewLine32() {
        checkForNewLine(
            "\tvoid x() {} //foo",
            "\tvoid x() {} //foo\n\t");
    }
    
    @Test
    public void testNewLine33() {
        checkForNewLine(
            "x=1;\n",
            "x=1;\n\n");
    }
    
    @Test
    public void testNewLine34() {
        checkForNewLine(
            "x=1; \n ",
            "x=1; \n \n "); //do we really want the space at the end here?
    }
    
    @Test
    public void testNewLine35() {
        checkForNewLine(
            "\tx=1;\n\t",
            "\tx=1;\n\t\n\t");
    }
    
    @Test
    public void testNewLine36() {
        checkForNewLine(
            "\tx=1; //foo\n\t",
            "\tx=1; //foo\n\t\n\t");
    }
    
    @Test
    public void testNewLine37() {
        checkForNewLine(
            "\t\tx=1; //foo\n\t\t",
            "\t\tx=1; //foo\n\t\t\n\t\t");
    }
    
    @Test
    public void testNewLine38() {
        checkForNewLine(
            "Integer x {\n\t",
            "Integer x {\n\t\n\t");
    }
    
    @Test
    public void testNewLine39() {
        checkForNewLine(
            "Integer x { \n\t",
            "Integer x { \n\t\n\t");
    }
    
    @Test
    public void testNewLine40() {
        checkForNewLine(
            "\tInteger x {\n\t\t",
            "\tInteger x {\n\t\t\n\t\t");
    }
    
    @Test
    public void testNewLine41() {
        checkForNewLine(
            "\tInteger x { //foo\n\t\t",
            "\tInteger x { //foo\n\t\t\n\t\t");
    }
    
    @Test
    public void testNewLine42() {
        checkForNewLine(
            "\t\tInteger x { //foo\n\t\t\t",
            "\t\tInteger x { //foo\n\t\t\t\n\t\t\t");
    }
    
    @Test
    public void testNewLine43() {
        checkForNewLine(
            "void x() {}\n",
            "void x() {}\n\n");
    }
    
    @Test
    public void testNewLine44() {
        checkForNewLine(
            "void x() {} \n ",
            "void x() {} \n \n "); //do we really want the space
    }
    
    @Test
    public void testNewLine45() {
        checkForNewLine(
            "\tvoid x() {}\n\t",
            "\tvoid x() {}\n\t\n\t");
    }
    
    @Test
    public void testNewLine46() {
        checkForNewLine(
            "\tvoid x() {} //foo\n\t",
            "\tvoid x() {} //foo\n\t\n\t");
    }
    
    @Test
    public void testNewLine47() {
        checkForNewLine(
            "String greeting = \"hello",
            "String greeting = \"hello\n                   ");
    }
    
    @Test
    public void testNewLine48() {
        checkForNewLine(
            "\tString greeting = \"hello",
            "\tString greeting = \"hello\n\t                   ");
    }
    
    @Test
    public void testNewLine49() {
        checkForNewLine(
            "String greeting = \"hello\n                   world",
            "String greeting = \"hello\n                   world\n                   ");
    }
    
    @Test
    public void testNewLine50() {
        checkForNewLine(
            "\tString greeting = \"hello\n\t                   world",
            "\tString greeting = \"hello\n\t                   world\n\t                   ");
    }
    
    @Test
    public void testNewLine51() {
        checkForNewLine(
            "String greeting = \"\"\"hello",
            "String greeting = \"\"\"hello\n                     ");
    }
    
    @Test
    public void testNewLine52() {
        checkForNewLine(
            "\tString greeting = \"\"\"hello",
            "\tString greeting = \"\"\"hello\n\t                     ");
    }
    
    @Test
    public void testNewLine53() {
        checkForNewLine(
            "String greeting = \"\"\"hello\n                     world",
            "String greeting = \"\"\"hello\n                     world\n                     ");
    }
    
    @Test
    public void testNewLine54() {
        checkForNewLine(
            "\tString greeting = \"\"\"hello\n\t                     world",
            "\tString greeting = \"\"\"hello\n\t                     world\n\t                     ");
    }
    
    @Test
    public void testNewLine55() {
        checkForNewLine(
            "String x()\n=>\"hello\" +",
            "String x()\n=>\"hello\" +\n\t\t");
    }
    
    @Test
    public void testNewLine56() {
        //What should this one really do? Not well-defined
        checkForNewLine(
            "String x()\n\t=>\"hello\" +",
            "String x()\n\t=>\"hello\" +\n\t\t");
    }
    
    @Test
    public void testNewLine57() {
        checkForNewLine(
            "String x()\n\t\t=>\"hello\" +",
            "String x()\n\t\t=>\"hello\" +\n\t\t");
    }
    
    @Test
    public void testNewLine58() {
        checkForNewLine(
            "\tString x()\n\t\t\t=>\"hello\" +",
            "\tString x()\n\t\t\t=>\"hello\" +\n\t\t\t");
    }
    
    @Test
    public void testNewLine59() {
        checkForNewLine(
            "String x()\n=>\"hello\";",
            "String x()\n=>\"hello\";\n");
    }
    
    @Test
    public void testNewLine60() {
        checkForNewLine(
            "String x()\n\t=>\"hello\";",
            "String x()\n\t=>\"hello\";\n");
    }
    
    @Test
    public void testNewLine61() {
        checkForNewLine(
            "String x()\n\t\t=>\"hello\";",
            "String x()\n\t\t=>\"hello\";\n");
    }
    
    @Test
    public void testNewLine62() {
        checkForNewLine(
            "\tString x()\n\t\t\t=>\"hello\";",
            "\tString x()\n\t\t\t=>\"hello\";\n\t");
    }
    
    @Test
    public void testClosingBrace1() {
        checkForClosingBrace(
            "void x() {\n\t",
            "void x() {\n}");
    }

    @Test
    public void testClosingBrace2() {
        checkForClosingBrace(
            "\tvoid x() {\n\t\t",
            "\tvoid x() {\n\t}");
    }

    @Test
    public void testClosingBrace3() {
        checkForClosingBrace(
            "void x() {\n\tprint(\"hello\");\n\t",
            "void x() {\n\tprint(\"hello\");\n}");
    }

    @Test
    public void testClosingBrace4() {
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
            new CeylonAutoEditStrategy().customizeDocumentCommand(doc, cmd);
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
            new CeylonAutoEditStrategy().customizeDocumentCommand(doc, cmd);
            doc.replace(cmd.offset, cmd.length, cmd.text);
        } catch (BadLocationException e) {
            System.err.println("Correct Indentation command failed " + e.getMessage());
        }
    }
    
    private void checkForCorrectIndentation(String before, String expectedIndentation) {
        Document doc = new Document(before);
        doCorrectIndentation(doc);
        assertEquals(expectedIndentation, doc.get());
    }

    private void checkForNewLine(String before, String expectedIndentation) {
        Document doc = new Document(before);
        doNewline(doc);
        assertEquals(expectedIndentation, doc.get());
    }

    private void checkForClosingBrace(String before, String expectedIndentation) {
        Document doc = new Document(before);
        doClosingBrace(doc);
        assertEquals(expectedIndentation, doc.get());
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
                new CeylonAutoEditStrategy().customizeDocumentCommand(doc, cmd);
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