/********************************************************************************
 * Copyright (c) {date} Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.code.wizard;

import static org.eclipse.ceylon.ide.eclipse.ui.CeylonPlugin.getInstance;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class EditDialog extends Dialog {
    
    private String text;
    
    public String getText() {
        return text;
    }
    
    public void setText(String text) {
        this.text = text;
    }
    
    public EditDialog(Shell parent) {
        super(parent);
        setShellStyle(getShellStyle() | SWT.RESIZE | SWT.MAX);
    }
    
    @Override
    protected Control createDialogArea(Composite parent) {
        Composite composite = (Composite) super.createDialogArea(parent);
        final Text text = new Text(composite, SWT.MULTI | SWT.BORDER| SWT.WRAP | SWT.V_SCROLL);
        FontData fd = PreferenceConverter.getFontData(getInstance().getPreferenceStore(),
                "sourceFont");
        text.setFont(new Font(getShell().getDisplay(), fd));
        text.setText(this.text);
        GridData gd = new GridData(GridData.FILL_BOTH);
        text.setLayoutData(gd);
        text.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                EditDialog.this.text = text.getText();
            }
        });
        return composite;
    }
    
    @Override
    protected Point getInitialSize() {
        Point size = super.getInitialSize();
        if (size.x<600) size.x=600;
        if (size.y<200) size.y=200;
        return size;
    }
    
    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText("Edit Project Source Header");
    }
    
}