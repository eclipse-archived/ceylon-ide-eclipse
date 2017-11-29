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

import static org.eclipse.ceylon.ide.eclipse.ui.CeylonResources.CEYLON_NEW_PACKAGE;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

final class NewPackageWizardPage extends NewUnitWizardPage {
    
    NewPackageWizardPage(boolean shared) {
        super("New Ceylon Package", 
                "Create a Ceylon package with a package descriptor.", 
                CEYLON_NEW_PACKAGE);
        this.shared = shared;
    }
    
    @Override
    String getPackageLabel() {
        return "Package name: ";
    }

    @Override
    void createControls(Composite composite) {
        Text name = createPackageField(composite);
        createSharedField(composite);
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
    boolean unitIsNameLegal(String unitName) {
        return true;
    }

    @Override
    String[] getFileNames() {
        return new String[] { "package" };
    }
}