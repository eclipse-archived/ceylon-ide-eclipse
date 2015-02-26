package com.redhat.ceylon.eclipse.core.launch;

import org.eclipse.debug.core.ILaunchManager;

public class DebugAsJsAction extends RunAction {
    
    @Override
    protected String getLaunchMode() {
        return ILaunchManager.DEBUG_MODE;
    }
    
    @Override
    protected CeylonJsModuleShortcut getShortcut() {
        return new CeylonJsModuleShortcut();
    }
    
}
