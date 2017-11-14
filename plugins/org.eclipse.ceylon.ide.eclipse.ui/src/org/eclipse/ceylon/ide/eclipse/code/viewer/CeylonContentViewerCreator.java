/********************************************************************************
 * Copyright (c) {date} Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.code.viewer;

import org.eclipse.compare.CompareConfiguration;
import org.eclipse.compare.IViewerCreator;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;


/**
 * Required when creating a JavaMergeViewer from the plugin.xml file.
 */
public class CeylonContentViewerCreator implements IViewerCreator {

    public Viewer createViewer(Composite parent, CompareConfiguration mp) {
        return new CeylonMergeViewer(parent, SWT.NULL, mp);
    }
}
