package com.redhat.ceylon.eclipse.code.style;

import static com.redhat.ceylon.eclipse.code.style.CeylonFormatterConstants.*;

import java.util.Observable;
import java.util.Observer;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

public class FormatterTabSpace extends FormatterTabPage {

    private final String PREVIEW =
            "import ceylon.file {\n"
            + "    pp=parsePath,\n"
            + "    File,\n"
            + "    Reader\n"
            + "}\n"
            + "import ceylon.collection {\n"
            + "    LL=LinkedList\n"
            + "}\n"
            + "\n"
            + "by (\"Someone\")\n"
            + "throws (`class Exception`, \"If anything goes wrong\")\n"
            + "shared class WithLineNumbersReader(Reader r)\n"
            + "        satisfies Reader & Iterable<String,Null> {\n"
            + "    \n"
            + "    LL<String> lines = LL<String>();\n"
            + "    variable Integer lineNum = 0;\n"
            + "    \n"
            + "    close() => r.close();\n"
            + "    shared actual Byte[] readBytes(Integer max) {\n"
            + "        throw AssertionError(\"Can't read bytes from line-oriented reader\");\n"
            + "    }\n"
            + "    shared actual Iterator<String> iterator() => lines.iterator();\n"
            + "    shared actual String? readLine() {\n"
            + "        if (exists line = r.readLine()) {\n"
            + "            value ret = lineNum.string + \"t\" + line;\n"
            + "            lines.add(ret);\n"
            + "            lineNum++;\n"
            + "            return ret;\n"
            + "        } else {\n"
            + "            return null;\n"
            + "        }\n"
            + "    }\n"
            + "}\n"
            + "\n"
            + "void run() {\n"
            + "    assert (is File f = pp(process.arguments.first else nothing).resource);\n"
            + "    try (r = WithLineNumbersReader(f.Reader())) {\n"
            + "        variable String? line = \"\";\n"
            + "        while (line exists) {\n"
            + "            line = r.readLine();\n"
            + "        }\n"
            + "        for (l in r) {\n"
            + "            print(l);\n"
            + "        }\n"
            + "    } catch (Exception e) {\n"
            + "        e.printStackTrace();\n"
            + "    }\n"
            + "    print({ \"Here\", \"have\", \"an\", \"iterable\", \"enumeration\" });\n"
            + "}\n";

    private CeylonPreview ceylonPreview;

    public FormatterTabSpace(FormatterModifyProfileDialog modifyDialog,
            FormatterPreferences workingValues) {
        super(modifyDialog, workingValues);
    }

    @Override
    protected void doCreatePreferences(Composite composite, int numColumns) {

        final Group generalGroup = createGroup(numColumns, composite,
                "Spacing Options");
        createCheckboxPref(generalGroup, numColumns, "Space before positional argument list",
                FORMATTER_space_BeforeMethodOrClassPositionalArgumentList, FALSE_TRUE);
        createCheckboxPref(generalGroup, numColumns, "Space before resource list",
                FORMATTER_space_BeforeResourceList, FALSE_TRUE);
        createCheckboxPref(generalGroup, numColumns, "Space before catch variable",
                FORMATTER_space_BeforeCatchVariable, FALSE_TRUE);
        createCheckboxPref(generalGroup, numColumns, "Space around satisfied and case types",
                FORMATTER_space_AroundSatisfiesOf, FALSE_TRUE);
        createCheckboxPref(generalGroup, numColumns, "Space around '=' for import alias",
                FORMATTER_space_AroundImportAliasEqualsSign, FALSE_TRUE);
        createCheckboxPref(generalGroup, numColumns, "Space after type argument or parameter list comma",
                FORMATTER_space_AfterTypeArgOrParamListComma, FALSE_TRUE);

        createLabel(2, generalGroup, "Parameter List Opening");
        createCheckboxPref(generalGroup, 1, "Before (",
                FORMATTER_space_BeforeParamListOpeningParen, FALSE_TRUE);
        createCheckboxPref(generalGroup, 1, "After (",
                FORMATTER_space_AfterParamListOpeningParen, FALSE_TRUE);
            
        createLabel(2, generalGroup, "Parameter List Closing");
        createCheckboxPref(generalGroup, 1, "Before )",
                FORMATTER_space_BeforeParamListClosingParen, FALSE_TRUE);  
        final CheckboxPreference saplcp = createCheckboxPref(generalGroup, 1, "After )",
                FORMATTER_space_AfterParamListClosingParen, FALSE_TRUE);

        final NumberPreference saplcpn = createNumberPref(generalGroup, numColumns, 
                "Detailed, " + MINIMUM_SPACE_AFTER_SHIFT + " to " + MAXIMUM_SPACE_AFTER_SHIFT,
                FORMATTER_space_AfterParamListClosingParen_Number, MINIMUM_SPACE_AFTER_SHIFT, MAXIMUM_SPACE_AFTER_SHIFT);
        
        Observer o = new Observer() {
            public void update(Observable o, Object arg) {
                saplcp.updateWidget();
                saplcpn.updateWidget();
            }
        };
        saplcp.addObserver(o);
        saplcpn.addObserver(o);
        
        createLabel(2, generalGroup, "Value Iterator");
        createCheckboxPref(generalGroup, 1, "After (",
                FORMATTER_space_AfterValueIteratorOpeningParenthesis, FALSE_TRUE);  
        createCheckboxPref(generalGroup, 1, "Before )",
                FORMATTER_space_BeforeValueIteratorClosingParenthesis, FALSE_TRUE);
        
        createLabel(2, generalGroup, "Iterable Enumeration");
        createCheckboxPref(generalGroup, 1, "After {",
                FORMATTER_space_AfterSequenceEnumerationOpeningBrace, FALSE_TRUE);         
        createCheckboxPref(generalGroup, 1, "Before }",
                FORMATTER_space_BeforeSequenceEnumerationClosingBrace, FALSE_TRUE);
 
        createCheckboxPref(generalGroup, numColumns, "Space before 'if' opening (",
                FORMATTER_space_BeforeIfOpeningParenthesis, FALSE_TRUE);
        createCheckboxPref(generalGroup, numColumns, "Space before 'for' opening (",
                FORMATTER_space_BeforeForOpeningParenthesis, FALSE_TRUE);  
        createCheckboxPref(generalGroup, numColumns, "Space before 'while' opening (",
                FORMATTER_space_BeforeWhileOpeningParenthesis, FALSE_TRUE);  
        createCheckboxPref(generalGroup, numColumns, "Space before annotation positional argument list",
                FORMATTER_space_BeforeAnnotationPositionalArgumentList, FALSE_TRUE);
  
    }

    @Override
    public void initializePage() {
        ceylonPreview.setPreviewText(PREVIEW);
    }

    @Override
    protected CeylonPreview doCreateCeylonPreview(Composite parent) {
        ceylonPreview = new CeylonPreview(this.workingValues, parent);
        return ceylonPreview;
    }

    @Override
    protected void doUpdatePreview() {
        super.doUpdatePreview();
        ceylonPreview.update();
    }
}
