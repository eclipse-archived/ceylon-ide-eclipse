/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 1.0 which is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.util;

import org.eclipse.core.runtime.IPath;

import org.eclipse.ceylon.ide.common.util.Path;

public class PathUtils {
    public static Path toCommonPath(IPath path) {
        return new Path(path.toString());
    }

    public static IPath toIPath(Path path) {
        return new org.eclipse.core.runtime.Path(path.toString());
    }

}
