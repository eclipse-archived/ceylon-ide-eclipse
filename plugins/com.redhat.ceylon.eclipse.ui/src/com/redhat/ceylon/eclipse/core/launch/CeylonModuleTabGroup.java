package com.redhat.ceylon.eclipse.core.launch;

import org.eclipse.debug.ui.AbstractLaunchConfigurationTabGroup;
import org.eclipse.debug.ui.CommonTab;
import org.eclipse.debug.ui.EnvironmentTab;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTab;

public class CeylonModuleTabGroup extends AbstractLaunchConfigurationTabGroup {

    public void createTabs(ILaunchConfigurationDialog dialog, String mode) {
        ILaunchConfigurationTab[] tabs = new ILaunchConfigurationTab[] {
            new CeylonMainTab(),
            new EnvironmentTab(),
            new CommonTab()
        };
        setTabs(tabs);
    }
}
