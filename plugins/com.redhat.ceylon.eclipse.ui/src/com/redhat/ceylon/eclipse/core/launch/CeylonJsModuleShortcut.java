package com.redhat.ceylon.eclipse.core.launch;

import org.eclipse.debug.core.ILaunchConfigurationType;

public class CeylonJsModuleShortcut extends CeylonModuleLaunchShortcut {

	@Override
	protected ILaunchConfigurationType getConfigurationType() {
		 return getLaunchManager().getLaunchConfigurationType(ICeylonLaunchConfigurationConstants.ID_CEYLON_JAVASCRIPT_MODULE);
	}
}
