package com.redhat.ceylon.eclipse.code.style;

import static com.redhat.ceylon.eclipse.code.style.CeylonFormatterConstants.*;

import java.util.Observable;
import java.util.Observer;

import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

public class FormatterTabMisc extends FormatterTabPage {

    private final String PREVIEW = 
            "import ceylon.collection { HashSet, LinkedList }\n"
            + "\n"
            + "\"Tests the Collatz conjecture for all numbers up to the first argument\n"
            + " (defaulting to 100).\n"
            + " \n"
            + " The Collatz conjecture states that if you start at any number,\n"
            + " and repeatedly\n"
            + " \n"
            + " - divide it by 2 if it’s even, or\n"
            + " - multiply it by 3 and add 1 if it’s odd,\n"
            + " \n"
            + " then you’ll always arrive at the cycle 1 4 2.\"\n"
            + "throws (`class AssertionError`, \"If another cycle is found\")\n"
            + "by (\"Lothar Collatz\")\n"
            + "deprecated (\"Very inefficient\")\n"
            + "shared void run() {\n"
            + "    value max = parseInteger(process.arguments.first else \"100\") else 100;\n"
            + "    HashSet<Integer[]> cycles = HashSet<Integer[]>();\n"
            + "    for (start in 1..max) {\n"
            + "        variable Integer i = start;\n"
            + "        HashSet<Integer> current = HashSet<Integer>();\n"
            + "        while (!i in current) {\n"
            + "            current.add(i);\n"
            + "            if (i % 2 == 0) {\n"
            + "                i /= 2;\n"
            + "            } else {\n"
            + "                i = 3 * i + 1;\n"
            + "            }\n"
            + "        }\n"
            + "        LinkedList<Integer> cycle = LinkedList<Integer>();\n"
            + "        while (!i in cycle) {\n"
            + "            cycle.add(i);\n"
            + "            if (i % 2 == 0) {\n"
            + "                i /= 2;\n"
            + "            } else {\n"
            + "                i = 3 * i + 1;\n"
            + "            }\n"
            + "        }\n"
            + "        assert (exists smallestFromCycle = cycle.sort(byIncreasing(identity<Integer>)).first);\n"
            + "        i = smallestFromCycle;\n"
            + "        LinkedList<Integer> orderedCycle = LinkedList<Integer>();\n"
            + "        while (!i in orderedCycle) {\n"
            + "            orderedCycle.add(i);\n"
            + "            if (i % 2 == 0) {\n"
            + "                i /= 2;\n"
            + "            } else {\n"
            + "                i = 3 * i + 1;\n"
            + "            }\n"
            + "        }\n"
            + "        cycles.add(orderedCycle.sequence());\n"
            + "    }\n"
            + "    \"Conjecture\"\n"
            + "    assert (cycles.size == 1);\n"
            + "}\n";

    private CeylonPreview ceylonPreview;

    public FormatterTabMisc(FormatterModifyProfileDialog modifyDialog,
            FormatterPreferences workingValues) {
        super(modifyDialog, workingValues);
    }

    @Override
    protected void doCreatePreferences(Composite composite, int numColumns) {

        final Group generalGroup = createGroup(numColumns, composite,
                "Miscellaneous Options");

        createComboPref(generalGroup, numColumns, "Line break strategy",
                FORMATTER_lineBreak, new String[] { "os", "lf", "crlf" },
                new String[] { "Operating System", "LF (Unix)",
                        "CR + LF (Windows)" });

        createComboPref(generalGroup, numColumns, "Import Style",
                FORMATTER_importStyle,
                new String[] { "singleLine", "multiLine" }, new String[] {
                        "Single Line", "Multi-line" });

        createCheckboxPref(generalGroup, numColumns, "'else' on its own line",
                FORMATTER_elseOnOwnLine, FALSE_TRUE);
        createCheckboxPref(generalGroup, numColumns,
                "Brace '{' on its own line", FORMATTER_braceOnOwnLine,
                FALSE_TRUE);
        createCheckboxPref(generalGroup, numColumns, "Fail Fast",
                FORMATTER_failFast, FALSE_TRUE);

        final CheckboxPreference allAnnotations = createCheckboxPref(
                generalGroup, numColumns, "All annotations inline",
                FORMATTER_inlineAnnotations, FALSE_TRUE);

        final StringPreference annotationsList = createStringPref(generalGroup,
                numColumns, "Only these:", FORMATTER_inlineAnnotations_List,
                new IInputValidator() {
                    @Override
                    public String isValid(String l) {
                        String[] tokens = l.split(",");
                        for (String token : tokens) {
                            for (int i = 0; i < token.length(); i += Character.isBmpCodePoint(i) ? 1 : 2) {
                                int cp = token.codePointAt(i);
                                if (!Character.isLetterOrDigit(cp) && cp != '_'
                                        || Character.isUpperCase(cp)
                                        || i == 0 && Character.isDigit(cp)) {
                                    StringBuilder ret = new StringBuilder();
                                    ret.append("Invalid character in annotation name: \'");
                                    ret.appendCodePoint(cp);
                                    ret.append('\'');
                                    return ret.toString();
                                }
                            }
                        }
                        return null;
                    }
                },
                /* enabled = */ !allAnnotations.getChecked());
        
        allAnnotations.addObserver(new Observer() {
            public void update(Observable o, Object arg) {
                updatePreferences((String) arg, annotationsList,
                        (CheckboxPreference) o);
            }
        });
    }

    private void updatePreferences(String v, StringPreference np,
            CheckboxPreference cbp) {
        if (!cbp.getChecked()) {
            np.setEnabled(true);
        } else {
            np.setEnabled(false);
        }
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
