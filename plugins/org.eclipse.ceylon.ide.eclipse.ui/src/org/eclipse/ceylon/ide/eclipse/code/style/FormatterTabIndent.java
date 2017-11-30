/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 1.0 which is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.code.style;

import static org.eclipse.ceylon.ide.eclipse.code.style.CeylonFormatterConstants.*;

import java.util.Observable;
import java.util.Observer;

import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

public class FormatterTabIndent extends FormatterTabPage {

    private final String PREVIEW
            = "shared class AccessCountingIterable<Element,Absent>(Iterable<Element,Absent> wrapped)\n"
            + "        extends Object()\n"
            + "        satisfies Iterable<Element,Absent>\n"
            + "        given Absent satisfies Null {\n"
            + "    \n"
            + "    variable Integer accessCounter = 0;\n"
            + "    shared Integer accessCount => accessCounter;\n"
            + "    \n"
            + "    shared actual Iterator<Element> iterator() {\n"
            + "        accessCounter++;\n"
            + "        return wrapped.iterator();\n"
            + "    }\n"
            + "    \n"
            + "    shared actual Boolean equals(Object that) {\n"
            + "        if (is Iterable<Element,Absent> that) {\n"
            + "            return wrapped == that;\n"
            + "        } else {\n"
            + "            return false;\n"
            + "        }\n"
            + "    }\n"
            + "    \n"
            + "    shared actual Integer hash =>\n"
            + "            sum {\n"
            + "        accessCounter.hash,\n"
            + "        wrapped.hash\n"
            + "    };\n"
            + "}\n";

    private CeylonPreview ceylonPreview;

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
                numColumns, "Indent mode", FORMATTER_indentMode,
                indentModeValues, indentModeLabels);
        indentMode.setEnabled(!ideMode);

        final NumberPreference indentSpacesSize = createNumberPref(
                generalGroup, numColumns, "Indent mode spaces",
                FORMATTER_indentMode_Spaces_Size, 0, 32);
        indentSpacesSize.setEnabled(!ideMode);

        final NumberPreference indentTabsSize = createNumberPref(generalGroup,
                numColumns, "Indent mode tab width",
                FORMATTER_indentMode_Tabs_Size, 0, 32);
        indentTabsSize.setEnabled(!ideMode);

        if (!ideMode) {
            updateTabPreferences(this.workingValues.get(FORMATTER_indentMode),
                    indentSpacesSize, indentTabsSize);
        } else {
            Label warning = createLabel(numColumns, generalGroup, 
                    "IDE editor options override this profile setting");
            warning.setForeground(composite.getDisplay().getSystemColor(SWT.COLOR_RED));
        }

        indentMode.addObserver(new Observer() {
            public void update(Observable o, Object arg) {
                updateTabPreferences((String) arg, indentSpacesSize,
                        indentTabsSize);
            }
        });
        
        // group ended
        final Group otherGroup = createGroup(numColumns, composite,
                "Other Indent Options");
        createCheckboxPref(otherGroup, numColumns, "Indent blank lines",
                FORMATTER_indent_Blank_Lines, FALSE_TRUE);
        createNumberPref(otherGroup, 3, "Indent before type info",
                FORMATTER_indent_Before_Type_Info, 0, 32);
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
        ceylonPreview.setPreviewText(PREVIEW);
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
    }
}
