/********************************************************************************
 * Copyright (c) {date} Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.test.eclipse.plugin.launch;

import static org.eclipse.ceylon.test.eclipse.plugin.CeylonTestPlugin.LAUNCH_CONFIG_PORT;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.ILaunchesListener2;

import org.eclipse.ceylon.test.eclipse.plugin.CeylonTestPlugin;
import org.eclipse.ceylon.test.eclipse.plugin.model.TestEventListener;
import org.eclipse.ceylon.test.eclipse.plugin.model.TestRun;
import org.eclipse.ceylon.test.eclipse.plugin.model.TestRunContainer;

public class CeylonTestLaunchesListener implements ILaunchesListener2 {
    
    private static final CeylonTestLaunchesListener instance = new CeylonTestLaunchesListener();
    
    private final Set<ILaunch> trackedLaunches = new HashSet<ILaunch>();
    
    public static void install() {
        ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();
        launchManager.addLaunchListener(instance);
    }

    public static void uninstall() {
        ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();
        launchManager.removeLaunchListener(instance);
    }

    @Override
    public void launchesAdded(ILaunch[] launches) {
        for(ILaunch launch : launches) {
            trackedLaunches.add(launch);
        }
    }

    @Override
    public void launchesRemoved(ILaunch[] launches) {
        for(ILaunch launch : launches) {
            trackedLaunches.remove(launch);
        }
    }

    @Override
    public void launchesTerminated(ILaunch[] launches) {
        for(ILaunch launch : launches) {
            launchTerminated(launch);
        }
    }
    
    private void launchTerminated(ILaunch launch) {
        trackedLaunches.remove(launch);
        
        TestRunContainer testRunContainer = CeylonTestPlugin.getDefault().getModel();
        TestRun testRun = testRunContainer.getTestRun(launch);
        if( testRun != null ) {
            testRun.processLaunchTerminatedEvent();
        }
    }

    @Override
    public void launchesChanged(ILaunch[] launches) {
        for(ILaunch launch : launches) {
            launchChanged(launch);
        }
    }

    private void launchChanged(ILaunch launch) {
        if (!trackedLaunches.contains(launch)) {
            return;
        }

        String portAttribute = launch.getAttribute(LAUNCH_CONFIG_PORT);
        if( portAttribute == null ) {
            return;
        }
        
        int port = Integer.parseInt(portAttribute);
        TestEventListener.startListenerThread(launch, port);
        
        TestRunContainer testRunContainer = CeylonTestPlugin.getDefault().getModel();
        TestRun testRun = testRunContainer.getOrCreateTestRun(launch);        
        
        trackedLaunches.remove(launch);
    }
    
}