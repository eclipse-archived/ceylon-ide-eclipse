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
    
}
