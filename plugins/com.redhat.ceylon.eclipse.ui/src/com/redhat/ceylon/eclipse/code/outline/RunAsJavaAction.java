package com.redhat.ceylon.eclipse.code.outline;

import org.eclipse.debug.core.ILaunchManager;

import com.redhat.ceylon.eclipse.core.launch.CeylonJavaModuleShortcut;

public class RunAsJavaAction extends RunAction {
    
    @Override
    protected String getLaunchMode() {
        return ILaunchManager.RUN_MODE;
    }
    
    @Override
    protected CeylonJavaModuleShortcut getShortcut() {
        return new CeylonJavaModuleShortcut();
    }
    
}
