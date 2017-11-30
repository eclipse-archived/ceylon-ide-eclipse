/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 1.0 which is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.code.preferences;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.swt.widgets.Composite;

class BoolFieldEditor extends BooleanFieldEditor {
    interface Listener {
        void valueChanged(boolean oldValue, boolean newValue);
    }        
    private BoolFieldEditor.Listener listener;
    public BoolFieldEditor(String name, String label, Composite parent) {
        super(name, label, parent);
    }
    public BoolFieldEditor(String name, String labelText, int style,
            Composite parent) {
        super(name, labelText, style, parent);
    }
    public void setListener(BoolFieldEditor.Listener listener) {
        this.listener = listener;
    }
    @Override
    protected void valueChanged(boolean oldValue, boolean newValue) {
        super.valueChanged(oldValue, newValue);
        if (listener!=null) {
            listener.valueChanged(oldValue, newValue);
        }
    }
    @Override
    protected void doLoadDefault() {
        boolean oldValue = getBooleanValue();
        super.doLoadDefault();
        boolean newValue = getBooleanValue();
        if (listener!=null) {
            listener.valueChanged(oldValue, newValue);
        }
    }
}