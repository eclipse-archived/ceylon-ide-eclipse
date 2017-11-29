/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.code.preferences;

import static org.eclipse.ceylon.ide.eclipse.code.preferences.CeylonPreferenceInitializer.ENABLE_HIERARCHY_FILTERS;
import static org.eclipse.ceylon.ide.eclipse.code.preferences.CeylonPreferenceInitializer.HIERARCHY_FILTERS;
import static org.eclipse.ceylon.ide.eclipse.code.preferences.CeylonPreferenceInitializer.INACTIVE_HIERARCHY_FILTERS;
import static org.eclipse.ceylon.ide.eclipse.code.preferences.CeylonPreferenceInitializer.PARAMS_IN_OUTLINES;
import static org.eclipse.ceylon.ide.eclipse.code.preferences.CeylonPreferenceInitializer.PARAM_TYPES_IN_OUTLINES;
import static org.eclipse.ceylon.ide.eclipse.code.preferences.CeylonPreferenceInitializer.RETURN_TYPES_IN_OUTLINES;
import static org.eclipse.ceylon.ide.eclipse.code.preferences.CeylonPreferenceInitializer.TYPE_PARAMS_IN_OUTLINES;
import static org.eclipse.ceylon.ide.eclipse.ui.CeylonPlugin.COLORS_AND_FONTS_PAGE_ID;
import static org.eclipse.ceylon.ide.eclipse.ui.CeylonPlugin.OUTLINE_FONT_PREFERENCE;
import static org.eclipse.ui.dialogs.PreferencesUtil.createPreferenceDialogOn;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Link;
import org.eclipse.ui.IWorkbenchPreferencePage;

import org.eclipse.ceylon.ide.eclipse.ui.CeylonPlugin;

public class CeylonOutlinesPreferencePage 
        extends FiltersPreferencePage 
        implements IWorkbenchPreferencePage {

    private BooleanFieldEditor displayOutlineTypes;
    private BooleanFieldEditor displayOutlineParameters;
    private BooleanFieldEditor displayOutlineParameterTypes;
    private BooleanFieldEditor displayOutlineTypeParameters;
    
    public static final String ID = 
            CeylonPlugin.PLUGIN_ID + ".preferences.outlines";
    
    public CeylonOutlinesPreferencePage() {
        setDescription("Preferences applying to outlines, type hierarchies, and the search results view.");
    }
    
    @Override
    protected Control createContents(Composite parent) {
        
        Link colorsAndFontsLink = new Link(parent, 0);
        colorsAndFontsLink.setLayoutData(GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).indent(0, 0).create());
        colorsAndFontsLink.setText("See '<a>Colors and Fonts</a>' to customize fonts and label colors.");
        colorsAndFontsLink.addSelectionListener(
                new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                createPreferenceDialogOn(getShell(), 
                        COLORS_AND_FONTS_PAGE_ID, null, 
                        "selectFont:" 
                                + OUTLINE_FONT_PREFERENCE);
            }
        });
                
        Link filtersLink = new Link(parent, 0);
        filtersLink.setLayoutData(
                GridDataFactory.swtDefaults()
                    .align(SWT.FILL, SWT.CENTER)
                    .create());
        filtersLink.setText("See '<a>Filtering</a>' to set up global filters and match highlighting.");
        filtersLink.addSelectionListener(
                new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                createPreferenceDialogOn(getShell(), 
                        CeylonFiltersPreferencePage.ID, null, null);
            }
        });
        
//        Composite composite = new Composite(parent, SWT.NONE);
//        composite.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());
//        composite.setLayout(new GridLayout(1, true));
        
        return super.createContents(parent);
    }
    
    @Override
    protected void createFieldEditors() {
        final Composite outlines = createGroup(2, "Labels");
        displayOutlineTypes = 
                new BooleanFieldEditor(RETURN_TYPES_IN_OUTLINES, 
                    "Display return types", 
                    getFieldEditorParent(outlines));
        displayOutlineTypes.load();
        addField(displayOutlineTypes);
        displayOutlineTypeParameters = 
                new BooleanFieldEditor(TYPE_PARAMS_IN_OUTLINES, 
                    "Display type parameters", 
                    getFieldEditorParent(outlines));
        displayOutlineTypeParameters.load();
        addField(displayOutlineTypeParameters);
        displayOutlineParameterTypes = 
                new BooleanFieldEditor(PARAM_TYPES_IN_OUTLINES, 
                    "Display parameter types", 
                    getFieldEditorParent(outlines));
        displayOutlineParameterTypes.load();
        addField(displayOutlineParameterTypes);
        displayOutlineParameters = 
                new BooleanFieldEditor(PARAMS_IN_OUTLINES, 
                    "Display parameter names ", 
                    getFieldEditorParent(outlines));
        displayOutlineParameters.load();
        addField(displayOutlineParameters);
        
        super.createFieldEditors();
    }
    
    @Override
    protected void performDefaults() {
        displayOutlineTypes.loadDefault();
        displayOutlineParameters.loadDefault();
        displayOutlineTypeParameters.loadDefault();
        displayOutlineParameterTypes.loadDefault();
        super.performDefaults();
    }
    
    @Override
    public boolean performOk() {
        displayOutlineTypes.store();
        displayOutlineParameters.store();
        displayOutlineTypeParameters.store();
        displayOutlineParameterTypes.store();
        return super.performOk();
    }

    @Override
    protected String getInactiveFiltersPreference() {
        return INACTIVE_HIERARCHY_FILTERS;
    }

    @Override
    protected String getActiveFiltersPreference() {
        return HIERARCHY_FILTERS;
    }
    
    @Override
    protected String getEnabledPreference() {
        return ENABLE_HIERARCHY_FILTERS;
    }
    
    @Override
    protected String getLabelText() {
        return "Filtered packages and declarations are excluded from type hierarchies\nand the search results view.";
    }

}
