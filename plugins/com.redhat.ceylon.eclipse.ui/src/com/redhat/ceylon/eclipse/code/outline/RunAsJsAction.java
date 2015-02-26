package com.redhat.ceylon.eclipse.code.outline;

import org.eclipse.debug.core.ILaunchManager;

import com.redhat.ceylon.eclipse.core.launch.CeylonJsModuleShortcut;

public class RunAsJsAction extends RunAction {
    
    @Override
    protected String getLaunchMode() {
        return ILaunchManager.RUN_MODE;
    }
    
    @Override
    protected CeylonJsModuleShortcut getShortcut() {
        return new CeylonJsModuleShortcut();
    }
    
}
