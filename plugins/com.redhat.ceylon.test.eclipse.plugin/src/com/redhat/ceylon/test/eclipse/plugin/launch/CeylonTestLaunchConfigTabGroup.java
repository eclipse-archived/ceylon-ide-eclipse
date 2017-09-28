package org.eclipse.ceylon.test.eclipse.plugin.launch;

import org.eclipse.debug.ui.AbstractLaunchConfigurationTabGroup;
import org.eclipse.debug.ui.CommonTab;
import org.eclipse.debug.ui.EnvironmentTab;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTab;
import org.eclipse.jdt.debug.ui.launchConfigurations.JavaArgumentsTab;
import org.eclipse.jdt.debug.ui.launchConfigurations.JavaJRETab;

public class CeylonTestLaunchConfigTabGroup extends AbstractLaunchConfigurationTabGroup {

    @Override
    public void createTabs(ILaunchConfigurationDialog dialog, String mode) {
        ILaunchConfigurationTab[] tabs = new ILaunchConfigurationTab[] {
                new CeylonTestLaunchConfigTab(),
                new JavaArgumentsTab(),
                new JavaJRETab(),
                new EnvironmentTab(),
                new CommonTab()
        };
        setTabs(tabs);        
    }

}