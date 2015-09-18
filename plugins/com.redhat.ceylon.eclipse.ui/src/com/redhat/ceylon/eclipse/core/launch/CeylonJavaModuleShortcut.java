package com.redhat.ceylon.eclipse.core.launch;

import static com.redhat.ceylon.eclipse.core.launch.ICeylonLaunchConfigurationConstants.ID_CEYLON_JAVA_MODULE;

import org.eclipse.debug.core.ILaunchConfigurationType;

public class CeylonJavaModuleShortcut extends CeylonModuleLaunchShortcut {

    @Override
    protected ILaunchConfigurationType getConfigurationType() {
        return getLaunchManager()
                .getLaunchConfigurationType(ID_CEYLON_JAVA_MODULE);
    }
    
    @Override
    String launchType() {
        return "Java";
    }
}
