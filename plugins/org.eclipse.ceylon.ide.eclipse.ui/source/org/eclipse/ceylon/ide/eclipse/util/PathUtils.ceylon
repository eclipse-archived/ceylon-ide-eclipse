/********************************************************************************
 * Copyright (c) {date} Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
import org.eclipse.core.runtime {
    IPath,
    EclipsePath = Path
}
import org.eclipse.ceylon.ide.common.util {
    Path
}

shared IPath toEclipsePath(Path commonPath) =>
        EclipsePath(commonPath.string);

shared Path fromEclipsePath(IPath eclipsePath) =>
        Path(eclipsePath.string);