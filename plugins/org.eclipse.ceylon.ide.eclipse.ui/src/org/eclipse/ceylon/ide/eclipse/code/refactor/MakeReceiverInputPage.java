/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.code.refactor;

import org.eclipse.ltk.ui.refactoring.UserInputWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import org.eclipse.ceylon.compiler.typechecker.tree.Tree;
import org.eclipse.ceylon.model.typechecker.model.Value;

public class MakeReceiverInputPage extends UserInputWizardPage {
    public MakeReceiverInputPage(String name) {
        super(name);
    }

    public void createControl(Composite parent) {
        Composite result = new Composite(parent, SWT.NONE);
        setControl(result);
        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        result.setLayout(layout);
        Value dm = ((Tree.AttributeDeclaration) getMakeReceiverRefactoring().node)
                 .getDeclarationModel();
        new Label(result, SWT.RIGHT).setText("Make '" + 
                 dm.getInitializerParameter().getDeclaration().getName() + 
                 "()' a member of the type '" + 
                 dm.getType().getDeclaration().getName() + 
                "', and remove parameter '" + dm.getName() + "'.");
        GridData gd2 = new GridData(GridData.FILL_HORIZONTAL);
        gd2.horizontalSpan=2;
        new Label(result, SWT.SEPARATOR|SWT.HORIZONTAL).setLayoutData(gd2);
        final Button delegate = new Button(result, SWT.CHECK);
        delegate.setText("Leave original as delegate");
        delegate.setSelection(false);
        delegate.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                getMakeReceiverRefactoring().setLeaveDelegate();
            }
            @Override
            public void widgetDefaultSelected(SelectionEvent event) {}
        });
        delegate.setEnabled(getMakeReceiverRefactoring().isMethod());
    }

    private MakeReceiverRefactoring getMakeReceiverRefactoring() {
        return (MakeReceiverRefactoring) getRefactoring();
    }
    
}
