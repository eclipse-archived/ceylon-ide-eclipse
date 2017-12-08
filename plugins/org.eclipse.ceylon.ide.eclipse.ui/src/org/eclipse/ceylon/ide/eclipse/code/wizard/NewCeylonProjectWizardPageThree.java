/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 1.0 which is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.code.wizard;

import static org.eclipse.swt.layout.GridData.HORIZONTAL_ALIGN_FILL;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import org.eclipse.ceylon.ide.eclipse.code.preferences.CeylonRepoConfigBlock;

public class NewCeylonProjectWizardPageThree extends WizardPage {

    private static final String PAGE_NAME= "NewJavaProjectWizardPageThree"; //$NON-NLS-1$
    
    private NewCeylonProjectWizardPageTwo pageTwo;
    private CeylonRepoConfigBlock block;
    private IProject provisonalProject;
    
    public NewCeylonProjectWizardPageThree(NewCeylonProjectWizardPageTwo pageTwo) {
        super(PAGE_NAME);
        this.pageTwo = pageTwo;
    }
    
    @Override
    public void createControl(Composite parent) {
        initializeDialogUnits(parent);

        final Composite composite = new Composite(parent, SWT.NULL);
        composite.setFont(parent.getFont());
        composite.setLayout(initGridLayout(new GridLayout(1, false), true));
        composite.setLayoutData(new GridData(HORIZONTAL_ALIGN_FILL));

        block = new CeylonRepoConfigBlock(new CeylonRepoConfigBlock.ValidationCallback() {
            @Override
            public void validationResultChange(boolean isValid, String message) {
                setPageComplete(isValid);
                setErrorMessage(message);
            }
        });
        block.initContents(composite);

        setControl(composite);
    }
    
    @Override
    public void setVisible(boolean visible) {
        if (visible == true && provisonalProject != pageTwo.getProvisonalProject()) {
            provisonalProject = pageTwo.getProvisonalProject();
            block.initState(provisonalProject, true);
        }
        super.setVisible(visible);
    }

    @Override
    protected void setControl(Control newControl) {
        Dialog.applyDialogFont(newControl);
        
        /*PlatformUI.getWorkbench()
            .getHelpSystem()
            .setHelp(newControl, 
                    IJavaHelpContextIds.NEW_JAVAPROJECT_WIZARD_PAGE);*/
        
        super.setControl(newControl);
    }

    private GridLayout initGridLayout(GridLayout layout, boolean margins) {
        layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
        layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
        if (margins) {
            layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
            layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
        } else {
            layout.marginWidth = 0;
            layout.marginHeight = 0;
        }
        return layout;
    }

    public CeylonRepoConfigBlock getBlock() {
        return block;
    }

}