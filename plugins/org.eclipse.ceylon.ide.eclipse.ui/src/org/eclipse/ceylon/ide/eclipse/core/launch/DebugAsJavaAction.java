package org.eclipse.ceylon.ide.eclipse.core.launch;

import org.eclipse.debug.core.ILaunchManager;

public class DebugAsJavaAction extends RunAction {
    
    @Override
    protected String getLaunchMode() {
        return ILaunchManager.DEBUG_MODE;
    }
    
    @Override
    protected CeylonJavaModuleShortcut getShortcut() {
        return new CeylonJavaModuleShortcut();
    }
    
}
