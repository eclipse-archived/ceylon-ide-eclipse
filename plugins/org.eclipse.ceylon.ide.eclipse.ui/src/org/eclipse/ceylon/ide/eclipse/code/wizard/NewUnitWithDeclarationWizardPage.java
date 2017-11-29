/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.code.wizard;

import static org.eclipse.ceylon.ide.eclipse.ui.CeylonResources.CEYLON_NEW_FILE;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

class NewUnitWithDeclarationWizardPage extends NewUnitWizardPage {
    
    private boolean declaration = true;
    
    public boolean isDeclaration() {
        return declaration;
    }
    
    protected NewUnitWithDeclarationWizardPage() {
        super("New Ceylon Source File", 
                "Create a new Ceylon source file.", 
                CEYLON_NEW_FILE);
    }

    @Override
    void createDeclarationField(Composite composite) {
        new Label(composite, SWT.NONE);
        Button dec = new Button(composite, SWT.CHECK);
        dec.setText("Create toplevel class or function declaration");
        dec.setSelection(declaration);
        dec.setEnabled(true);
        GridData igd = 
                new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        igd.horizontalSpan = 3;
        igd.grabExcessHorizontalSpace = true;
        dec.setLayoutData(igd);
        dec.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                declaration = !declaration;
            }
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {}
        });
    }
}