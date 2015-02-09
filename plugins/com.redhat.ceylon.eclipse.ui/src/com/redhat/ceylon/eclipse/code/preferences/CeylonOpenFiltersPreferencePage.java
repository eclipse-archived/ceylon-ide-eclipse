package com.redhat.ceylon.eclipse.code.preferences;


import static com.redhat.ceylon.eclipse.code.preferences.CeylonPreferenceInitializer.INACTIVE_OPEN_FILTERS;
import static com.redhat.ceylon.eclipse.code.preferences.CeylonPreferenceInitializer.OPEN_FILTERS;

import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * The preference page for Ceylon open dialog filtering
 */
public class CeylonOpenFiltersPreferencePage 
        extends FiltersPreferencePage 
        implements IWorkbenchPreferencePage {
    
    public static final String ID = "com.redhat.ceylon.eclipse.ui.preferences.open.filters";
    
    public CeylonOpenFiltersPreferencePage() {
        setDescription("Filtered packages and types will be excluded from the 'Open Ceylon Declaration' and 'Open in Type Hierarchy View' dialogs."); 
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
