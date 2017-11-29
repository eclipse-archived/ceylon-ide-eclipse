/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.platform;

import org.eclipse.ltk.core.refactoring.Change;

import org.eclipse.ceylon.ide.eclipse.java2ceylon.PlatformJ2C;
import org.eclipse.ceylon.ide.common.platform.PlatformServices;
import org.eclipse.ceylon.ide.common.platform.TextChange;

public class platformJ2C implements PlatformJ2C {

    @Override
    public PlatformServices platformServices() {
        return eclipsePlatformServices_.get_();
    }

    @Override
    public Change getNativeChange(Object commonChange) {
        if (commonChange instanceof EclipseCompositeChange) {
            return ((EclipseCompositeChange) commonChange).getNativeChange();
        } else if (commonChange instanceof EclipseTextChange) {
            return ((EclipseTextChange) commonChange).getNativeChange();
        }
        return null;
    }



    @Override
    public TextChange newChange(String desc, Object doc) {
        return new EclipseTextChange(desc, doc);
    }
}
