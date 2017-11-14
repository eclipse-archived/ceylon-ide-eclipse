/********************************************************************************
 * Copyright (c) {date} Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.core.launch;

import org.eclipse.debug.core.ILaunchManager;

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
