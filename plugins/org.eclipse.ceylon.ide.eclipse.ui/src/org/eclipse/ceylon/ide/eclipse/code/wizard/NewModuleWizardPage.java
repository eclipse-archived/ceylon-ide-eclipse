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

import static org.eclipse.ceylon.ide.eclipse.ui.CeylonResources.CEYLON_NEW_MODULE;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

final class NewModuleWizardPage extends NewUnitWizardPage {
    
    private String version = "1.0.0";

    NewModuleWizardPage() {
        super("New Ceylon Module", 
                "Create a runnable Ceylon module with module and package descriptors.", 
                CEYLON_NEW_MODULE);
        setUnitName("run");
    }
    
    String getVersion() {
        return version;
    }

    @Override
    String getCompilationUnitLabel() {
        return "Runnable compilation unit: ";
    }

    @Override
    String getPackageLabel() {
        return "Module name: ";
    }

    @Override
    String getSharedPackageLabel() {
        return "Create module with shared root package"; // (visible to other modules)
    }

    @Override
    void createControls(Composite composite) {
        Text name = createPackageField(composite);
        createVersionField(composite);
        createSharedField(composite);
        createNameField(composite);
        createSeparator(composite);
        createFolderField(composite);
        name.forceFocus();
    }

    @Override
    boolean isComplete() {
        return super.isComplete() && 
                !getPackageFragment().isDefaultPackage();
    }

    @Override
    boolean packageNameIsLegal(String packageName) {
        return !packageName.isEmpty() && 
                super.packageNameIsLegal(packageName);
    }

    @Override
    String getIllegalPackageNameMessageStart() {
        return "Please enter a legal module name.";
    }

    @Override
    String[] getFileNames() {
        return new String[] { "module", "package", getUnitName() };
    }

    void createVersionField(Composite composite) {
        Label versionLabel = 
                new Label(composite, SWT.LEFT | SWT.WRAP);
        versionLabel.setText("Module version:");
        GridData lgd = 
                new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        lgd.horizontalSpan = 1;
        versionLabel.setLayoutData(lgd);

        final Text versionName = 
                new Text(composite, SWT.SINGLE | SWT.BORDER);
        GridData ngd = 
                new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        ngd.horizontalSpan = 2;
        ngd.grabExcessHorizontalSpace = true;
        versionName.setLayoutData(ngd);
        versionName.setText(version);
        versionName.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                version = versionName.getText();
                setPageComplete(isComplete());
            }
        });
        new Label(composite, SWT.NONE);
    }
}