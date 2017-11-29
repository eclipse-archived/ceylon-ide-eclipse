/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.android.plugin;

import java.io.IOException;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

public class CeylonAndroidPlugin extends AbstractUIPlugin {

    public static final String PLUGIN_ID = "org.eclipse.ceylon.ide.eclipse.android.plugin";
        
    private static CeylonAndroidPlugin plugin;
    
    @Override
    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        plugin = null;
        super.stop(context);
    }
    
    public static CeylonAndroidPlugin getDefault() {
        return plugin;
    }

    public static void logInfo(String msg) {
        plugin.getLog().log(new Status(IStatus.INFO, PLUGIN_ID, msg));
    }

    public static void logInfo(String msg, IOException e) {
        plugin.getLog().log(new Status(IStatus.INFO, PLUGIN_ID, msg, e));
    }

    public static void logError(String msg, Exception e) {
        plugin.getLog().log(new Status(IStatus.ERROR, PLUGIN_ID, msg, e));
    }

}