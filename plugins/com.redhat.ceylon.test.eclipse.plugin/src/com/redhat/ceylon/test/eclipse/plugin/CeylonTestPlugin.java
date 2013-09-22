package com.redhat.ceylon.test.eclipse.plugin;

import java.io.IOException;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.redhat.ceylon.test.eclipse.plugin.launch.CeylonTestLaunchesListener;
import com.redhat.ceylon.test.eclipse.plugin.model.TestRunContainer;

public class CeylonTestPlugin extends AbstractUIPlugin {

    public static final String PLUGIN_ID = "com.redhat.ceylon.test.eclipse.plugin";
    
    public static final String CEYLON_TEST_MODULE_NAME = "ceylon.test";
    public static final String CEYLON_TEST_MODULE_DEFAULT_VERSION = "0.6.1";
    
    public static final String LAUNCH_CONFIG_TYPE = PLUGIN_ID + ".ceylonTestLaunchConfigurationType";
    public static final String LAUNCH_CONFIG_ENTRIES_KEY = PLUGIN_ID + ".entries";
    public static final String LAUNCH_CONFIG_PORT = PLUGIN_ID + ".port";
    
    public static final String PREF_SHOW_COMPLATE_TREE = PLUGIN_ID + ".showComplateTree";
    public static final String PREF_SHOW_COMPLETE_DESCRIPTION = PLUGIN_ID + ".showCompleteDescription";
    public static final String PREF_SHOW_FAILURES_ONLY = PLUGIN_ID + ".showFailuresOnly";
    public static final String PREF_SHOW_TESTS_GROUPED_BY_PACKAGES = PLUGIN_ID + ".showTestsGroupedByPackages";
    public static final String PREF_SHOW_TESTS_ELAPSED_TIME = PLUGIN_ID + ".showTestsElapsedTime";
    public static final String PREF_SCROLL_LOCK = PLUGIN_ID + ".scrollLock";
    public static final String PREF_STACK_TRACE_FILTER = PLUGIN_ID + ".stackTraceFilter";

    private static CeylonTestPlugin plugin;
    
    private TestRunContainer model;

    @Override
    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;
        model = new TestRunContainer();
        CeylonTestLaunchesListener.install();
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        CeylonTestLaunchesListener.uninstall();
        model = null;
        plugin = null;
        super.stop(context);
    }
    
    @Override
    protected void initializeImageRegistry(ImageRegistry imageRegistry) {
        CeylonTestImageRegistry.init(imageRegistry);
    }

    public static CeylonTestPlugin getDefault() {
        return plugin;
    }

    public TestRunContainer getModel() {
        return model;
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