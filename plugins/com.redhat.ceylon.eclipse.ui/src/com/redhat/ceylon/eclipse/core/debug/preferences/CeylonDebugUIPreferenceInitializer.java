package com.redhat.ceylon.eclipse.core.debug.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jdt.internal.debug.ui.IJDIPreferencesConstants;
import org.eclipse.jface.preference.IPreferenceStore;

import com.redhat.ceylon.eclipse.ui.CeylonPlugin;

public class CeylonDebugUIPreferenceInitializer extends AbstractPreferenceInitializer {

    public CeylonDebugUIPreferenceInitializer() {
        super();
    }

    @Override
    public void initializeDefaultPreferences() {
        IPreferenceStore store = CeylonPlugin.getInstance().getPreferenceStore();
        store.setDefault(IJDIPreferencesConstants.PREF_ACTIVE_FILTERS_LIST,
                "org.jboss.modules.*,ceylon.modules.*");
        store.setDefault(IJDIPreferencesConstants.PREF_INACTIVE_FILTERS_LIST,
                "ceylon.language.*,com.redhat.ceylon.*,java.lang.*");
    }
}
