package com.redhat.ceylon.eclipse.code.style;

import static com.redhat.ceylon.eclipse.code.style.CeylonFormatterConstants.*;

import java.util.Observable;
import java.util.Observer;

import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

public class FormatterTabIndent extends FormatterTabPage {

    private final String PREVIEW = 
            "import ceylon.collection { MutableList }\n\n\n"
            + "shared class Example() {"
            + "value test => 0;"
            + "}";

    private CeylonPreview ceylonPreview;

    private String oldTabChar;

    public FormatterTabIndent(FormatterModifyProfileDialog modifyDialog,
            FormatterPreferences workingValues) {
        super(modifyDialog, workingValues);
    }

    @Override
    protected void doCreatePreferences(Composite composite, int numColumns) {

        final Group generalGroup = createGroup(numColumns, composite,
                "Indent Mode");

        final String[] indentModeValues = new String[] {
                FORMATTER_indentMode_Spaces.toLowerCase(),
                FORMATTER_indentMode_Tabs.toLowerCase(),
                FORMATTER_indentMode_Mixed.toLowerCase() };
        final String[] indentModeLabels = new String[] {
                FORMATTER_indentMode_Spaces, FORMATTER_indentMode_Tabs,
                FORMATTER_indentMode_Mixed };
        final ComboPreference indentMode = createComboPref(generalGroup,
                numColumns, "Indent Mode", FORMATTER_indentMode,
                indentModeValues, indentModeLabels);

        final NumberPreference indentSpacesSize = createNumberPref(
                generalGroup, numColumns, "Indent Mode Spaces Size",
                FORMATTER_indentMode_Spaces_Size, 0, 32);

        final NumberPreference indentTabsSize = createNumberPref(generalGroup,
                numColumns, "Indent Mode Tabs Size",
                FORMATTER_indentMode_Tabs_Size, 0, 32);

        updateTabPreferences(this.workingValues.get(FORMATTER_indentMode),
                indentSpacesSize, indentTabsSize);

        indentMode.addObserver(new Observer() {
            public void update(Observable o, Object arg) {
                updateTabPreferences((String) arg, indentSpacesSize,
                        indentTabsSize);
            }
        });

        // group ended
        final Group otherGroup = createGroup(numColumns, composite,
                "Other Indent Options");
        createCheckboxPref(otherGroup, numColumns, "Indent Blank Lines",
                FORMATTER_indent_Blank_Lines, FALSE_TRUE);
        createNumberPref(otherGroup, 3, "Indent before type info",
                FORMATTER_indent_Before_Type_Info, 0, 32);
        createComboPref(otherGroup, 3, "Indent after specifier expression start", 
                FORMATTER_indent_After_Specifier_Expression_Start,
                new String[] {"stack", "addindentbefore"}, // not camel case
                new String[] {"Stack", "Add Indent Before"});
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

    private void updateTabPreferences(String tabPolicy,
            NumberPreference indentSpacesSize, NumberPreference indentTabsSize) {

        if (FORMATTER_indentMode_Spaces.equalsIgnoreCase(tabPolicy)) {
            indentSpacesSize.setEnabled(true);
            indentTabsSize.setEnabled(false);
        } else if (FORMATTER_indentMode_Tabs.equalsIgnoreCase(tabPolicy)) {
            indentSpacesSize.setEnabled(false);
            indentTabsSize.setEnabled(true);
        } else if (FORMATTER_indentMode_Mixed.equalsIgnoreCase(tabPolicy)) {
            indentSpacesSize.setEnabled(true);
            indentTabsSize.setEnabled(true);
        } else {
            Assert.isTrue(false);
        }
        oldTabChar = tabPolicy;
    }
}
