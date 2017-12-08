/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 1.0 which is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.test.eclipse.plugin.launch;

import static org.eclipse.ceylon.test.eclipse.plugin.CeylonTestPlugin.LAUNCH_CONFIG_TYPE_JS;

public class CeylonTestJsLaunchShortcut extends CeylonTestLaunchShortcut {

    public CeylonTestJsLaunchShortcut() {
        super(LAUNCH_CONFIG_TYPE_JS);
    }
    
    @Override
    String launchType() {
        return "JavaScript";
    }

}