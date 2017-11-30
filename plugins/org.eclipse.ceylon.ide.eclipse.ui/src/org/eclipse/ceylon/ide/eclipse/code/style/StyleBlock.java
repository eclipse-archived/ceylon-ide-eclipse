/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 1.0 which is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.code.style;

import org.eclipse.core.resources.IProject;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

public abstract class StyleBlock {

    protected Shell shell;
    protected Composite block;
    protected IProject project;
    protected boolean projectSettings;

    protected void setShell(Shell shell) {
        this.shell = shell;
    }

    public void disableProjectSettings() {
        projectSettings = false;
    }

    public void enableProjectSettings() {
        projectSettings = true;
    }

    protected abstract boolean performApply();

    protected abstract void performDefaults();

    protected abstract Control createContents(Composite composite);

    protected Composite createComposite(Composite parent, int numColumns) {
        final Composite composite = new Composite(parent, SWT.NONE);
        composite.setFont(parent.getFont());
        final GridLayout layout = new GridLayout(numColumns, false);
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        composite.setLayout(layout);
        return composite;
    }

    public abstract void initialize();

    public boolean performOk() {
        return performApply();
    }

    public void dispose() {
        if (block != null) {
            block.dispose();
        }
    }
}
