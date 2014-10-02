package com.redhat.ceylon.eclipse.code.style;

import static com.redhat.ceylon.eclipse.code.style.CeylonFormatterConstants.*;

import java.util.Observable;
import java.util.Observer;

import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

public class FormatterTabMisc extends FormatterTabPage {

    private final String PREVIEW = 
            "import ceylon.collection { MutableList, freq=frequencies }\n\n\n"
            + "shared class Example(str) {" + "shared String str; \n"
            + "value test => 0;\n" + "}";

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
                        "CR and LF (Windwos)" });

        createComboPref(generalGroup, numColumns, "Import Style",
                FORMATTER_importStyle,
                new String[] { "singleLine", "multiLine" }, new String[] {
                        "Single Line", "Multi-line" });

        createCheckboxPref(generalGroup, numColumns, "'else' on its own line",
                FORMATTER_elseOnOwnLine, FALSE_TRUE);
        createCheckboxPref(generalGroup, numColumns,
                "Brace - { - on its own line", FORMATTER_braceOnOwnLine,
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
                            if (!acceptedInlineAnnotations.contains(token.trim())) {
                                return "Invalid Annotation: " + token;
                            }
                        }
                        return null;
                    }
                });
        
        if (allAnnotations.getChecked()) {
            annotationsList.setEnabled(false);
        }

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
