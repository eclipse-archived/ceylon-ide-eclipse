package org.eclipse.ceylon.ide.eclipse.core.launch;

import static org.eclipse.ceylon.ide.eclipse.core.launch.ICeylonLaunchConfigurationConstants.ID_CEYLON_JAVASCRIPT_MODULE;

import org.eclipse.debug.core.ILaunchConfigurationType;

public class CeylonJsModuleShortcut extends CeylonModuleLaunchShortcut {

    @Override
    protected ILaunchConfigurationType getConfigurationType() {
         return getLaunchManager()
                 .getLaunchConfigurationType(ID_CEYLON_JAVASCRIPT_MODULE);
    }
    
    @Override
    String launchType() {
        return "JavaScript";
    }
}
