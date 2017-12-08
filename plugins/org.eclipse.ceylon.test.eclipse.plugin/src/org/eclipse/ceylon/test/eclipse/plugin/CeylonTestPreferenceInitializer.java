/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 1.0 which is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.test.eclipse.plugin;

import static org.eclipse.ceylon.test.eclipse.plugin.CeylonTestPlugin.PREF_SHOW_TESTS_ELAPSED_TIME;
import static org.eclipse.ceylon.test.eclipse.plugin.CeylonTestPlugin.PREF_SHOW_TESTS_IN_HIERARCHY;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

public class CeylonTestPreferenceInitializer extends AbstractPreferenceInitializer {

    @Override
    public void initializeDefaultPreferences() {
        IPreferenceStore preferenceStore = CeylonTestPlugin.getDefault().getPreferenceStore();

        if (!preferenceStore.contains(PREF_SHOW_TESTS_ELAPSED_TIME)) {
            preferenceStore.setValue(PREF_SHOW_TESTS_ELAPSED_TIME, true);
        }
        if (!preferenceStore.contains(PREF_SHOW_TESTS_IN_HIERARCHY)) {
            preferenceStore.setValue(PREF_SHOW_TESTS_IN_HIERARCHY, true);
        }
    }

}