package org.eclipse.ceylon.ide.eclipse.code.preferences;


import static org.eclipse.ceylon.ide.eclipse.code.preferences.CeylonPreferenceInitializer.ENABLE_OPEN_FILTERS;
import static org.eclipse.ceylon.ide.eclipse.code.preferences.CeylonPreferenceInitializer.INACTIVE_OPEN_FILTERS;
import static org.eclipse.ceylon.ide.eclipse.code.preferences.CeylonPreferenceInitializer.OPEN_FILTERS;
import static org.eclipse.ceylon.ide.eclipse.code.preferences.CeylonPreferenceInitializer.PARAMS_IN_DIALOGS;
import static org.eclipse.ceylon.ide.eclipse.code.preferences.CeylonPreferenceInitializer.PARAM_TYPES_IN_DIALOGS;
import static org.eclipse.ceylon.ide.eclipse.code.preferences.CeylonPreferenceInitializer.RETURN_TYPES_IN_DIALOGS;
import static org.eclipse.ceylon.ide.eclipse.code.preferences.CeylonPreferenceInitializer.TYPE_PARAMS_IN_DIALOGS;
import static org.eclipse.ceylon.ide.eclipse.ui.CeylonPlugin.COLORS_AND_FONTS_PAGE_ID;
import static org.eclipse.ceylon.ide.eclipse.ui.CeylonPlugin.OPEN_FONT_PREFERENCE;
import static org.eclipse.ui.dialogs.PreferencesUtil.createPreferenceDialogOn;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Link;
import org.eclipse.ui.IWorkbenchPreferencePage;

import org.eclipse.ceylon.ide.eclipse.ui.CeylonPlugin;

/**
 * The preference page for Ceylon open dialog filtering
 */
public class CeylonOpenDialogsPreferencePage 
        extends FiltersPreferencePage 
        implements IWorkbenchPreferencePage {
    
    public static final String ID = CeylonPlugin.PLUGIN_ID + ".preferences.open";
    
    private BooleanFieldEditor typeParams;
    private BooleanFieldEditor paramTypes;
    private BooleanFieldEditor params;
    private BooleanFieldEditor types;
    
    public CeylonOpenDialogsPreferencePage() {
        setDescription("Preferences applying to the 'Open Declaration' and 'Open in Type Hierarchy View' dialogs."); 
    }
    
    @Override
    public boolean performOk() {
        paramTypes.store();
        params.store();
        typeParams.store();
        types.store();
        return super.performOk();
    }
    
    
    @Override
    protected void performDefaults() {
        paramTypes.loadDefault();
        params.loadDefault();
        typeParams.loadDefault();
        types.loadDefault();
        super.performDefaults();
    }
    
    @Override
    protected void createFieldEditors() {
        Group group = createGroup(2, "Labels");
        types = new BooleanFieldEditor(RETURN_TYPES_IN_DIALOGS, 
                "Display return types", 
                getFieldEditorParent(group));
        types.load();
        addField(types);
        typeParams = new BooleanFieldEditor(TYPE_PARAMS_IN_DIALOGS, 
                "Display type parameters", 
                getFieldEditorParent(group));
        typeParams.load();
        addField(typeParams);
        paramTypes = new BooleanFieldEditor(PARAM_TYPES_IN_DIALOGS, 
                "Display parameter types", 
                getFieldEditorParent(group));
        paramTypes.load();
        addField(paramTypes);
        params = new BooleanFieldEditor(PARAMS_IN_DIALOGS, 
                "Display parameter names", 
                getFieldEditorParent(group));
        params.load();
        addField(params);

        super.createFieldEditors();
    }
    
    @Override
    protected String getLabelText() {
        return "Filtered packages and declarations are excluded from 'Open' dialogs.";
    }
    
    @Override
    protected String getInactiveFiltersPreference() {
        return INACTIVE_OPEN_FILTERS;
    }

    @Override
    protected String getActiveFiltersPreference() {
        return OPEN_FILTERS;
    }
    
    @Override
    protected String getEnabledPreference() {
        return ENABLE_OPEN_FILTERS;
    }
    
    @Override
    protected Control createContents(Composite parent) {
        Link colorsAndFontsLink = new Link(parent, 0);
        colorsAndFontsLink.setLayoutData(
                GridDataFactory.swtDefaults()
                    .align(SWT.FILL, SWT.CENTER)
                    .create());
        colorsAndFontsLink.setText("See '<a>Colors and Fonts</a>' to customize font and label colors.");
        colorsAndFontsLink.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                createPreferenceDialogOn(getShell(), 
                        COLORS_AND_FONTS_PAGE_ID, null, 
                        "selectFont:" 
                                + OPEN_FONT_PREFERENCE);
            }
        });
        
        Link filtersLink = new Link(parent, 0);
        filtersLink.setLayoutData(
                GridDataFactory.swtDefaults()
                    .align(SWT.FILL, SWT.CENTER)
                    .create());
        filtersLink.setText("See '<a>Filtering</a>' to set up global filters and match highlighting.");
        filtersLink.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                createPreferenceDialogOn(getShell(), 
                        CeylonFiltersPreferencePage.ID, 
                        null, null);
            }
        });
        
        return super.createContents(parent);
    }

}
