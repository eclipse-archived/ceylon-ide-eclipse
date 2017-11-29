/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.code.style;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import org.eclipse.ceylon.ide.eclipse.ui.CeylonPlugin;

/*
 * The page to configure the Ceylon code formatter profiles.
 */
public class FormatterPreferencePage extends StylePage {

    public static final String ID = CeylonPlugin.PLUGIN_ID + ".preferences.style.formatter";

    public FormatterPreferencePage() {
        setTitle("Code Formatter");
    }

    @Override
    protected Control createContents(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        composite.setLayout(layout);
        composite.setFont(parent.getFont());
        GridData data = new GridData(GridData.FILL, GridData.FILL, true, true);
        styleBlockControl = createPreferenceContent(composite);
        styleBlockControl.setLayoutData(data);

        Dialog.applyDialogFont(composite);
        return composite;
    }

    @Override
    public void performApply() {
        if (styleBlock != null) {
            styleBlock.performApply();
        }
    }

    @Override
    public boolean performOk() {
        performApply();
        return true;
    }

    @Override
    public void performDefaults() {
        if (styleBlock != null) {
            styleBlock.performDefaults();
        }
    }
    
    private Control createPreferenceContent(Composite composite) {
        if (styleBlock == null) {
            styleBlock = new FormatterConfigurationBlock(project);
        }
        return styleBlock.createContents(composite);
    }
}
