/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 1.0 which is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.code.refactor;


import org.eclipse.ltk.ui.refactoring.UserInputWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import org.eclipse.ceylon.ide.common.util.escaping_;
import org.eclipse.ceylon.model.typechecker.model.Declaration;
import org.eclipse.ceylon.model.typechecker.model.TypeDeclaration;
import org.eclipse.ceylon.model.typechecker.model.TypedDeclaration;

public class RenameInputPage extends UserInputWizardPage {
    public RenameInputPage(String name) {
        super(name);
    }

    public void createControl(Composite parent) {
        Composite result = new Composite(parent, SWT.NONE);
        setControl(result);
        GridLayout layout = new GridLayout();
        layout.numColumns = 3;
        result.setLayout(layout);
        Label title = new Label(result, SWT.LEFT);  
        Declaration declaration = 
                getRenameRefactoring().getDeclaration();
        String name = declaration.getName();
        title.setText("Rename " + getRenameRefactoring().getCount() + 
                " occurrences of declaration '" + 
                name + "'.");
        GridData gd = new GridData();
        gd.horizontalSpan=3;
        title.setLayoutData(gd);
        GridData gd2 = new GridData(GridData.FILL_HORIZONTAL);
        gd2.horizontalSpan=3;
        new Label(result, SWT.SEPARATOR|SWT.HORIZONTAL).setLayoutData(gd2);
        
        Label label = new Label(result, SWT.RIGHT);  
        label.setText("Rename to: ");
        final Text text = new Text(result, SWT.SINGLE|SWT.BORDER);
        GridData gd3 = new GridData();
        gd3.horizontalSpan=2;
        text.setLayoutData(gd3);
        text.setText(getRenameRefactoring().getNewName());
        text.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
        text.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent event) {
                String name = text.getText();
                validateIdentifier(name);
                getRenameRefactoring().setNewName(name);
            }
        });
        text.addKeyListener(new SubwordIterator(text));
        text.selectAll();
        text.setFocus();
        
        final Button renameSourceFile = 
                new Button(result, SWT.CHECK);
        GridData gd4 = new GridData(GridData.FILL_HORIZONTAL);
        gd4.horizontalSpan=3;
        gd4.grabExcessHorizontalSpace=true;
        renameSourceFile.setLayoutData(gd4);
        String filename = declaration.getUnit().getFilename();
        renameSourceFile.setText("Also rename source file '" + filename + "'");
        renameSourceFile.setSelection(getRenameRefactoring().isRenameFile());
        renameSourceFile.setEnabled(filename.endsWith(".ceylon"));
        renameSourceFile.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                getRenameRefactoring().setRenameFile(renameSourceFile.getSelection());
            }
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {}
        });

        final Button renameLocals = 
                new Button(result, SWT.CHECK);
        renameLocals.setLayoutData(gd4);
        boolean typeDec = declaration instanceof TypeDeclaration;
        String txt = "Rename similarly-named values and functions";
        if (typeDec) txt += " of type '" + declaration.getName() + "'";
        renameLocals.setText(txt);
        renameLocals.setSelection(getRenameRefactoring().isRenameValuesAndFunctions());
        renameLocals.setEnabled(typeDec);
        renameLocals.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                getRenameRefactoring().setRenameValuesAndFunctions(renameLocals.getSelection());
            }
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {}
        });
}
    
    private RenameRefactoring getRenameRefactoring() {
        return (RenameRefactoring) getRefactoring();
    }
    
    void validateIdentifier(String name) {
        if (!name.matches("^[a-zA-Z_]\\w*$")) {
            setErrorMessage("Not a legal Ceylon identifier");
            setPageComplete(false);
        }
        else if (escaping_.get_().isKeyword(name)) {
            setErrorMessage("'" + name + "' is a Ceylon keyword");
            setPageComplete(false);
        }
        else {
            int ch = name.codePointAt(0);
            Declaration declaration = getRenameRefactoring().getDeclaration();
            if (declaration instanceof TypedDeclaration) {
                if (!Character.isLowerCase(ch) && ch!='_') {
                    setErrorMessage("Not an initial lowercase identifier");
                    setPageComplete(false);
                }
                else {
                    setErrorMessage(null);
                    setPageComplete(true);
                }
            }
            else if (declaration instanceof TypeDeclaration) {
                if (!Character.isUpperCase(ch)) {
                    setErrorMessage("Not an initial uppercase identifier");
                    setPageComplete(false);
                }
                else {
                    setErrorMessage(null);
                    setPageComplete(true);
                }
            }
        }
    }

}
