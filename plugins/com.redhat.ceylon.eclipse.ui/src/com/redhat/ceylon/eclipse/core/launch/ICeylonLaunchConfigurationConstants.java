package com.redhat.ceylon.eclipse.core.launch;

import static com.redhat.ceylon.eclipse.ui.CeylonPlugin.PLUGIN_ID;

import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;

public interface ICeylonLaunchConfigurationConstants extends
        IJavaLaunchConfigurationConstants {
    /**
     * Identifier for the Local Ceylon Application launch configuration type
     * (value <code>"org.eclipse.jdt.launching.localJavaApplication"</code>).
     */
    public static final String ID_CEYLON_APPLICATION = PLUGIN_ID + ".launching.localCeylonApplication";
    public static final String ID_JS_APPLICATION = PLUGIN_ID + ".launching.localCeylonJsApplication";

    public static final String ATTR_CEYLON_MODULE = PLUGIN_ID + ".launching.ceylonModule";
    public static final String ATTR_JS_DEBUG = PLUGIN_ID + ".launching.ceylonDebugJs";
    public static final String ATTR_JS_NODEPATH = PLUGIN_ID + ".launching.ceylonJsNodePath";
    
    public static final String ATTR_MODULE_NAME = "CEYLON_MODULE";
    public static final String ATTR_TOPLEVEL_NAME = "CEYLON_TOPLEVEL";
    
    /**
     * Marker in the launch configuration that the module should be run without --run
     * and a visual indication in the configuration name.
     * 
     * It cannot be part of a normal runnable identifier
     */
    public static final String DEFAULT_RUN_MARKER = " - default";
}
