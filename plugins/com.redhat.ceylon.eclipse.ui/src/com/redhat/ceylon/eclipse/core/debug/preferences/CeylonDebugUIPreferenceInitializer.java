package com.redhat.ceylon.eclipse.core.debug.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import com.redhat.ceylon.eclipse.ui.CeylonPlugin;

public class CeylonDebugUIPreferenceInitializer extends AbstractPreferenceInitializer {

    public CeylonDebugUIPreferenceInitializer() {
        super();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
     */
    @Override
    public void initializeDefaultPreferences() {
        IPreferenceStore store = CeylonPlugin.getInstance().getPreferenceStore();

        // CeylonStepFilterPreferencePage
        store.setDefault(CeylonDebugOptionsManager.PREF_FILTER_LANGUAGE_MODULE, true);
        store.setDefault(CeylonDebugOptionsManager.PREF_FILTER_MODULE_RUNTIME, true);
    }
}
