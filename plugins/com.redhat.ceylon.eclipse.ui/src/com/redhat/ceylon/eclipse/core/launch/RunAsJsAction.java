package com.redhat.ceylon.eclipse.core.launch;

import org.eclipse.debug.core.ILaunchManager;

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
