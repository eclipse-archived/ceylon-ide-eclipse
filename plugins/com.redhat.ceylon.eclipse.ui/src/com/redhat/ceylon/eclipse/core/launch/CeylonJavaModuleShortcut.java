package com.redhat.ceylon.eclipse.core.launch;

import org.eclipse.debug.core.ILaunchConfigurationType;

public class CeylonJavaModuleShortcut extends CeylonModuleLaunchShortcut {

	@Override
	protected ILaunchConfigurationType getConfigurationType() {
		return getLaunchManager().getLaunchConfigurationType(ICeylonLaunchConfigurationConstants.ID_CEYLON_JAVA_MODULE);
	}
}
