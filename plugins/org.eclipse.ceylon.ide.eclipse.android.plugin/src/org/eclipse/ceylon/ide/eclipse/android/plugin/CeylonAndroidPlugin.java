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