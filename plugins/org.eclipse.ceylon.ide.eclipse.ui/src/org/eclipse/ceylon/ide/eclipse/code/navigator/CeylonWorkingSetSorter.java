/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.code.navigator;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.internal.navigator.workingsets.WorkingSetSorter;

public class CeylonWorkingSetSorter extends WorkingSetSorter {
    @Override
    public int compare(Viewer viewer, Object e1, Object e2) {
        if (e1 instanceof IProject && e2 instanceof IProject) {
            String e1String = ((IProject) e1).getName();
            String e2String = ((IProject) e2).getName();
            return e1String.compareToIgnoreCase(e2String);
        }
        return super.compare(viewer, e1, e2);
    }
}
