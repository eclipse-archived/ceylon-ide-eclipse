package com.redhat.ceylon.eclipse.code.outline;

import org.eclipse.debug.core.ILaunchManager;

import com.redhat.ceylon.eclipse.core.launch.CeylonJavaModuleShortcut;

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
