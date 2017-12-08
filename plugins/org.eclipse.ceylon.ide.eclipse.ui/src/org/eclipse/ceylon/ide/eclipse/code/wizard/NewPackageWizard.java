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

import static org.eclipse.ceylon.ide.eclipse.code.editor.Navigation.gotoLocation;
import static org.eclipse.ceylon.ide.eclipse.code.refactor.MoveUtil.escapePackageName;
import static org.eclipse.ceylon.ide.eclipse.code.wizard.WizardUtil.runOperation;

import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.wizards.newresource.BasicNewResourceWizard;

import org.eclipse.ceylon.ide.eclipse.ui.CeylonPlugin;

public class NewPackageWizard extends Wizard implements INewWizard {

    private IStructuredSelection selection;
    private NewUnitWizardPage page;
    private IWorkbench workbench;
    private boolean created = false;
    
    public NewPackageWizard() {
        setDialogSettings(CeylonPlugin.getInstance()
                .getDialogSettings());
        setWindowTitle("New Ceylon Package");
    }
    
    public IPackageFragment getPackageFragment() {
        return page.getPackageFragment();
    }
    
    public IPackageFragmentRoot getSourceFolder() {
        return page.getSourceDir();
    }
    
    public boolean isCreated() {
        return created;
    }
    
    public boolean isShared() {
        return page.isShared();
    }
    
    @Override
    public void init(IWorkbench workbench, 
            IStructuredSelection selection) {
        this.selection = selection;
        this.workbench=workbench;
    }
    
    @Override
    public boolean performFinish() {
        CeylonPlugin.getInstance()
                .getDialogSettings()
                .put("sharedPackage", page.isShared());
        IPackageFragment pf = page.getPackageFragment();
        CreateSourceFileOperation op = packageDescriptorOp(pf);
        created = runOperation(op, getContainer());
        if (created) {
            BasicNewResourceWizard.selectAndReveal(op.getFile(), 
                    workbench.getActiveWorkbenchWindow());
            gotoLocation(op.getFile().getFullPath(), 0);
            return true;
        }
        else {
            return false;
        }
    }

    private CreateSourceFileOperation packageDescriptorOp(
            IPackageFragment pf) {
        StringBuilder packageDescriptor = 
                new StringBuilder();
        if (page.isShared()) {
            packageDescriptor.append("shared ");
        }
        String name = 
                escapePackageName(pf.getElementName());
        packageDescriptor
                .append("package ") 
                .append(name) 
                .append(";")
                .append(System.lineSeparator());
        return new CreateSourceFileOperation(
                page.getSourceDir(), 
                pf, "package", 
                page.isIncludePreamble(), 
                packageDescriptor.toString());
    }

    @Override
    public void addPages() {
        super.addPages();
        if (page == null) {
            boolean shared = 
                    getDialogSettings()
                        .getBoolean("sharedPackage");
            page = new NewPackageWizardPage(shared);
            page.init(workbench, selection);
        }
        addPage(page);
    }
}
