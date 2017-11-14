/********************************************************************************
 * Copyright (c) {date} Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.code.preferences;

import static org.eclipse.ceylon.cmr.maven.MavenUtils.getDefaultMavenSettings;
import static org.eclipse.jface.layout.GridDataFactory.swtDefaults;

import java.io.File;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import org.eclipse.ceylon.ide.eclipse.ui.CeylonResources;

class AetherRepositoryDialog extends Dialog {

    private Label infoLabel;
    private Text valueText;
    private Text errorText;
    private Label errorImage;
    private Button browseButton;
    private String value = "";

    public AetherRepositoryDialog(Shell shell) {
        super(shell);
    }

    @Override
    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        shell.setText("Add Maven Repository");
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
        createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        GridLayout layout = new GridLayout(2, false);
        layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
        layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
        layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
        layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);

        Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayout(layout);
        composite.setLayoutData(swtDefaults().grab(true, true).align(SWT.FILL, SWT.FILL).create());

        infoLabel = new Label(composite, SWT.LEFT | SWT.WRAP);
        infoLabel.setText("Enter path of Maven settings.xml file (leave empty for Aether defaults)");
        infoLabel.setLayoutData(swtDefaults().align(SWT.FILL, SWT.CENTER).span(2, 1).grab(true, false)
                .minSize(convertHorizontalDLUsToPixels(IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH), 0).create());

        valueText = new Text(composite, SWT.SINGLE | SWT.BORDER);
        valueText.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));
        valueText.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                value = valueText.getText();

                boolean isValid = true;
                if (!value.isEmpty()) {
                    File f = new File(value);
                    if (!f.exists() || !f.isFile()) {
                        isValid = false;
                    }
                }
                
                errorText.setText(isValid ? "" : "Invalid path to settings.xml");
                errorText.setVisible(!isValid);
                errorImage.setVisible(!isValid);
                getButton(IDialogConstants.OK_ID).setEnabled(isValid);
            }
        });

        browseButton = new Button(composite, SWT.PUSH);
        browseButton.setText("Browse...");
        browseButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                String settingsXml = getDefaultMavenSettings();
                FileDialog fileDialog = new FileDialog(getShell(), SWT.SHEET);
                fileDialog.setFileName("settings.xml");
                fileDialog.setFilterPath(settingsXml.replace("settings.xml", ""));
                fileDialog.setFilterExtensions(new String[] { "*.xml" });
                String result = fileDialog.open();
                if (result != null) {
                    valueText.setText(result);
                }
            }
        });
        setButtonLayoutData(browseButton);
        
        GridLayout gl = new GridLayout(2, false);
        Composite errorDisplay = new Composite(composite, SWT.NONE);
        errorDisplay.setLayout(gl);
        errorDisplay.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));
        
        errorImage = new Label(errorDisplay, SWT.NONE);
        errorImage.setVisible(false);
        errorImage.setImage(CeylonResources.ERROR);
        
        errorText = new Text(errorDisplay, SWT.READ_ONLY | SWT.WRAP);
        errorText.setVisible(false);
        errorText.setBackground(errorDisplay.getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
        errorText.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));
        
        applyDialogFont(composite);

        return composite;
    }
    
    public String getValue() {
        return value;
    }

}
