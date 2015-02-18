package com.redhat.ceylon.eclipse.code.preferences;


import static com.redhat.ceylon.eclipse.code.preferences.CeylonPreferenceInitializer.INACTIVE_OPEN_FILTERS;
import static com.redhat.ceylon.eclipse.code.preferences.CeylonPreferenceInitializer.OPEN_FILTERS;
import static com.redhat.ceylon.eclipse.code.preferences.CeylonPreferenceInitializer.PARAMS_IN_DIALOGS;
import static com.redhat.ceylon.eclipse.code.preferences.CeylonPreferenceInitializer.RETURN_TYPES_IN_DIALOGS;
import static com.redhat.ceylon.eclipse.code.preferences.CeylonPreferenceInitializer.TYPE_PARAMS_IN_DIALOGS;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.redhat.ceylon.eclipse.ui.CeylonPlugin;

/**
 * The preference page for Ceylon open dialog filtering
 */
public class CeylonOpenDialogsPreferencePage 
        extends FiltersPreferencePage 
        implements IWorkbenchPreferencePage {
    
    public static final String ID = CeylonPlugin.PLUGIN_ID + ".preferences.open.filters";
    
    private BooleanFieldEditor typeParams;
    private BooleanFieldEditor params;
    private BooleanFieldEditor types;
    
    public CeylonOpenDialogsPreferencePage() {
        setDescription("Preferences applying to the 'Open Declaration' and 'Open in Type Hierarchy View' dialogs."); 
    }
    
    @Override
    public boolean performOk() {
        params.store();
        typeParams.store();
        types.store();
        return super.performOk();
    }
    
    
    @Override
    protected void performDefaults() {
        params.loadDefault();
        typeParams.loadDefault();
        types.loadDefault();
        super.performDefaults();
    }
    
    @Override
    protected void createFieldEditors() {
        Group group = createGroup(2, "Labels");
        typeParams = new BooleanFieldEditor(TYPE_PARAMS_IN_DIALOGS, 
                "Display type parameters", 
                getFieldEditorParent(group));
        typeParams.load();
        addField(typeParams);
        params = new BooleanFieldEditor(PARAMS_IN_DIALOGS, 
                "Display parameters with types", 
                getFieldEditorParent(group));
        params.load();
        addField(params);
        types = new BooleanFieldEditor(RETURN_TYPES_IN_DIALOGS, 
                "Display return types", 
                getFieldEditorParent(group));
        types.load();
        addField(types);
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

}
