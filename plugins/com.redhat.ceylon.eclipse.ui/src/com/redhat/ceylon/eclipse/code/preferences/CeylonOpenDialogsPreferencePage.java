package com.redhat.ceylon.eclipse.code.preferences;


import static com.redhat.ceylon.eclipse.code.preferences.CeylonPreferenceInitializer.INACTIVE_OPEN_FILTERS;
import static com.redhat.ceylon.eclipse.code.preferences.CeylonPreferenceInitializer.OPEN_FILTERS;
import static com.redhat.ceylon.eclipse.code.preferences.CeylonPreferenceInitializer.PARAMS_IN_DIALOGS;
import static com.redhat.ceylon.eclipse.code.preferences.CeylonPreferenceInitializer.TYPE_PARAMS_IN_DIALOGS;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * The preference page for Ceylon open dialog filtering
 */
public class CeylonOpenDialogsPreferencePage 
        extends FiltersPreferencePage 
        implements IWorkbenchPreferencePage {
    
    public static final String ID = "com.redhat.ceylon.eclipse.ui.preferences.open.filters";
    
    private BooleanFieldEditor typeParams;
    private BooleanFieldEditor params;
    
    public CeylonOpenDialogsPreferencePage() {
        setDescription("Preferences applying to the 'Open Ceylon Declaration' and 'Open in Type Hierarchy View' dialogs."); 
    }
    
    @Override
    public boolean performOk() {
        params.store();
        typeParams.store();
        return super.performOk();
    }
    
    
    @Override
    protected void performDefaults() {
        params.loadDefault();
        typeParams.loadDefault();
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
//        group.setLayout(GridLayoutFactory.swtDefaults().equalWidth(true).numColumns(2).create());
//        group.setLayoutData(GridDataFactory.fillDefaults().create());
//        group.setText("Labels in Open dialogs");
//        Button showTypeParameters = new Button(group, SWT.CHECK);
//        showTypeParameters.setText("Show type parameters");
//        showTypeParameters.setLayoutData(GridDataFactory.fillDefaults().create());
//        Button showParameters = new Button(group, SWT.CHECK);
//        showParameters.setText("Show parameters with types");
//        showParameters.setLayoutData(GridDataFactory.fillDefaults().create());
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
