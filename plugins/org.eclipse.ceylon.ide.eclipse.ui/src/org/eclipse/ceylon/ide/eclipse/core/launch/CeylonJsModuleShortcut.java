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

import static org.eclipse.ceylon.ide.eclipse.core.launch.ICeylonLaunchConfigurationConstants.ID_CEYLON_JAVASCRIPT_MODULE;

import org.eclipse.debug.core.ILaunchConfigurationType;

public class CeylonJsModuleShortcut extends CeylonModuleLaunchShortcut {

    @Override
    protected ILaunchConfigurationType getConfigurationType() {
         return getLaunchManager()
                 .getLaunchConfigurationType(ID_CEYLON_JAVASCRIPT_MODULE);
    }
    
    @Override
    String launchType() {
        return "JavaScript";
    }
}
