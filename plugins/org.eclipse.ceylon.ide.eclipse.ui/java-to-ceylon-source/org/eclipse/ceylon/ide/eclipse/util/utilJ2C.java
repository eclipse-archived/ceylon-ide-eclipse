/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.util;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;

import org.eclipse.ceylon.ide.eclipse.java2ceylon.UtilJ2C;
import org.eclipse.ceylon.ide.common.util.Path;
import org.eclipse.ceylon.ide.common.util.ProgressMonitor;

public class utilJ2C implements UtilJ2C {
    
    @Override
    public Indents indents() {
        return Indents.INSTANCE;
    }
    
    @Override
    public ProgressMonitor<IProgressMonitor> wrapProgressMonitor(IProgressMonitor monitor) {
        return wrapProgressMonitor_.wrapProgressMonitor(monitor);
    }
    
    @Override
    public IPath toEclipsePath(Path commonPath) {
        return toEclipsePath_.toEclipsePath(commonPath);
    }

    @Override
    public Path fromEclipsePath(IPath eclipsePath) {
        return fromEclipsePath_.fromEclipsePath(eclipsePath);
    }
}
