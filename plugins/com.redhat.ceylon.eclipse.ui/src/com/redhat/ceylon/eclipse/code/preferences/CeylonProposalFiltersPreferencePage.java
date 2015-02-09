package com.redhat.ceylon.eclipse.code.preferences;


import static com.redhat.ceylon.eclipse.code.preferences.CeylonPreferenceInitializer.COMPLETION_FILTERS;
import static com.redhat.ceylon.eclipse.code.preferences.CeylonPreferenceInitializer.INACTIVE_COMPLETION_FILTERS;

import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * The preference page for Ceylon proposal filtering
 */
public class CeylonProposalFiltersPreferencePage 
        extends FiltersPreferencePage 
        implements IWorkbenchPreferencePage {
    
    public static final String ID = "com.redhat.ceylon.eclipse.ui.preferences.completion.filters";
    
    public CeylonProposalFiltersPreferencePage() {
        setDescription("Filtered packages and types will be excluded from Ceylon completion proposal lists."); 
    }
    
    @Override
    protected String getInactiveFiltersPreference() {
        return INACTIVE_COMPLETION_FILTERS;
    }

    @Override
    protected String getActiveFiltersPreference() {
        return COMPLETION_FILTERS;
    }
}
