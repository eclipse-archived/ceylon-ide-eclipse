package com.redhat.ceylon.eclipse.launch;

import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;

import com.redhat.ceylon.eclipse.ui.CeylonPlugin;

public interface ICeylonLaunchConfigurationConstants extends
        IJavaLaunchConfigurationConstants {
    /**
     * Identifier for the Local Ceylon Application launch configuration type
     * (value <code>"org.eclipse.jdt.launching.localJavaApplication"</code>).
     */
    public static final String ID_CEYLON_APPLICATION = CeylonPlugin.PLUGIN_ID + ".launching.localCeylonApplication";
    
}
