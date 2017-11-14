/********************************************************************************
 * Copyright (c) {date} Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.core.launch;

import static org.eclipse.ceylon.ide.eclipse.ui.CeylonPlugin.PLUGIN_ID;

import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;

public interface ICeylonLaunchConfigurationConstants extends
        IJavaLaunchConfigurationConstants {
    /**
     * Identifier for the Local Ceylon Application launch configuration type
     * (value <code>"org.eclipse.jdt.launching.localJavaApplication"</code>).
     */
    public static final String ID_CEYLON_APPLICATION = PLUGIN_ID + ".launching.localCeylonApplication";

    public static final String ID_CEYLON_JAVA_JAR_BASED_APPLICATION = PLUGIN_ID + ".launching.javaJarBasedCeylonApplication";

    public static final String ID_CEYLON_JAVA_MODULE = PLUGIN_ID + ".launching.ceylonJavaModule";
    public static final String ID_CEYLON_JAVASCRIPT_MODULE = PLUGIN_ID + ".launching.ceylonJavaScriptModule";
    public static final String ID_CEYLON_JAR_PACKAGED_MODULE = PLUGIN_ID + ".launching.jarPackagedCeylonApplication";
    
    public static final String ATTR_CEYLON_MODULE = PLUGIN_ID + ".launching.ceylonModule";
    public static final String ATTR_JS_DEBUG = PLUGIN_ID + ".launching.ceylonDebugJs";
    public static final String ATTR_JS_NODEPATH = PLUGIN_ID + ".launching.ceylonJsNodePath";
    
    public static final String ATTR_LAUNCH_TYPE = "CEYLON_LAUNCH_TYPE";
    public static final String ATTR_MODULE_NAME = "CEYLON_MODULE";
    public static final String ATTR_JAR_CREATION_TOOL_NAME = "CEYLON_JAR_CREATION_TOOL";
    public static final String ATTR_TOPLEVEL_NAME = "CEYLON_TOPLEVEL";
    public static final String ATTR_LAUNCH_VERBOSE = "CEYLON_LAUNCH_VERBOSE";
    
    public static final String CAN_LAUNCH_AS_CEYLON_JAVA_MODULE = "canLaunchAsCeylonJavaModule";
    public static final String CAN_LAUNCH_AS_CEYLON_SWARM_PACKAGED_JAVA_MODULE = "canLaunchAsCeylonSwarmPackagedJavaModule";
    public static final String CAN_LAUNCH_AS_CEYLON_JAVASCIPT_MODULE = "canLaunchAsCeylonJavaScriptModule";
    public static final String CEYLON_FILE_EXTENSION = "ceylon";
    
    /**
     * Marker in the launch configuration that the module should be run without --run
     * and a visual indication in the configuration name.
     * 
     * It cannot be part of a normal runnable identifier
     */
    public static final String DEFAULT_RUN_MARKER = " (default)";
}
