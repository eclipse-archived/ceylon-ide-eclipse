package com.redhat.ceylon.test.eclipse.plugin.launch;

import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestPlugin.LAUNCH_CONFIG_TYPE_JS;

public class CeylonTestJsLaunchShortcut extends CeylonTestLaunchShortcut {

    public CeylonTestJsLaunchShortcut() {
        super(LAUNCH_CONFIG_TYPE_JS);
    }
    
    @Override
    String launchType() {
        return "JavaScript";
    }

}