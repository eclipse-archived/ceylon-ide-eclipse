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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;

public class SelectNewUnitWizard extends Wizard implements INewWizard {
    
    private IStructuredSelection selection;
    private IWorkbench workbench;

    private NewUnitWizardPage page;
    private String title;
    private String description;
    private String suggestedUnitName;
    
    public SelectNewUnitWizard(String title, String description, 
            String suggestedUnitName) {
        this.title = title;
        this.description = description;
        this.suggestedUnitName = suggestedUnitName;
    }
    
    public IProject getProject() {
        return getPackageFragment().getResource().getProject();
    }

    public IPackageFragment getPackageFragment() {
        return page.getPackageFragment();
    }
    
    public IFile getFile() {
        return page.getFile();
    }
    
    public boolean includePreamble() {
        return page.isIncludePreamble();
    }
    
    @Override
    public void init(IWorkbench workbench, IStructuredSelection selection) {
        this.selection = selection;
        this.workbench = workbench;
    }
    
    @Override
    public void addPages() {
        super.addPages();
        page = new NewUnitWizardPage(title, description, CEYLON_NEW_FILE);
        page.init(workbench, selection);
        page.setUnitName(suggestedUnitName);
        addPage(page);
    }
    
    @Override
    public boolean performFinish() {
        return true;
    }
    
    public boolean open(IFile file) {
        init(PlatformUI.getWorkbench(), new StructuredSelection(file));
        Shell shell = Display.getCurrent().getActiveShell();
        WizardDialog wd = new WizardDialog(shell, this);
        wd.setTitle(title);
        return wd.open()!=Window.CANCEL;
    }
    
}