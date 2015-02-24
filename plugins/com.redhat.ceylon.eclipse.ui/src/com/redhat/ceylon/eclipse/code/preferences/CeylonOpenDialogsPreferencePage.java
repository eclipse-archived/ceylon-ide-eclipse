package com.redhat.ceylon.eclipse.code.preferences;


import static com.redhat.ceylon.eclipse.code.preferences.CeylonPreferenceInitializer.INACTIVE_OPEN_FILTERS;
import static com.redhat.ceylon.eclipse.code.preferences.CeylonPreferenceInitializer.OPEN_FILTERS;
import static com.redhat.ceylon.eclipse.code.preferences.CeylonPreferenceInitializer.PARAMS_IN_DIALOGS;
import static com.redhat.ceylon.eclipse.code.preferences.CeylonPreferenceInitializer.PARAM_TYPES_IN_DIALOGS;
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
