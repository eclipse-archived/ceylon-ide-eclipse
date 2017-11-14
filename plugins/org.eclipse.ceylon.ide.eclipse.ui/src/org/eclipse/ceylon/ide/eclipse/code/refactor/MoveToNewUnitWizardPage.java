/********************************************************************************
 * Copyright (c) {date} Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.code.refactor;

import static org.eclipse.ceylon.ide.eclipse.code.wizard.WizardUtil.getSelectedJavaElement;
import static org.eclipse.ceylon.ide.eclipse.ui.CeylonPlugin.PLUGIN_ID;
import static org.eclipse.core.resources.ResourcesPlugin.getWorkspace;
import static org.eclipse.jdt.core.IJavaElement.PACKAGE_FRAGMENT_ROOT;
import static org.eclipse.jdt.internal.ui.refactoring.nls.SourceContainerDialog.getSourceContainer;
import static org.eclipse.ui.PlatformUI.getWorkbench;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.wizards.NewSourceFolderCreationWizard;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
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
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.wizards.IWizardDescriptor;

import org.eclipse.ceylon.ide.eclipse.code.select.PackageSelectionDialog;
import org.eclipse.ceylon.ide.eclipse.code.wizard.EditDialog;
import org.eclipse.ceylon.ide.eclipse.code.wizard.NewPackageWizard;

/*
 * TODO: this class is a huge copy/paste job of NewUnitWizardPage
 */
public class MoveToNewUnitWizardPage extends UserInputWizardPage {

    private String unitName = "";
    private IPackageFragmentRoot sourceDir;
    private IPackageFragment packageFragment;
    private String packageName = "";
    private boolean includePreamble = true;
    boolean shared = true;
    
    private IStructuredSelection selection;
    private IWorkbench workbench;
    private Text unitNameText;
    
    public MoveToNewUnitWizardPage(String title) {
        super(title);
    }
    
    @Override
    public void createControl(Composite parent) {
        initializeDialogUnits(parent);
        
        initFromSelection();
        
        Composite composite = new Composite(parent, SWT.NONE);
        composite.setFont(parent.getFont());
        
        GridLayout layout = new GridLayout();
        layout.numColumns = 4;
        composite.setLayout(layout);
        
        createControls(composite);
        
        setControl(composite);
        
        Dialog.applyDialogFont(composite);
        
        setPageComplete(isComplete());
    }

    void createControls(Composite composite) {
        createTitle(composite);
        Text name = createNameField(composite);
        createDeclarationField(composite);
        createSeparator(composite);
        Text folder = createFolderField(composite);
        createPackageField(composite, folder);
        name.forceFocus();
    }

    private void createTitle(Composite composite) {
        Label title = new Label(composite, SWT.LEFT);  
        MoveToNewUnitRefactoring refactoring = 
                (MoveToNewUnitRefactoring)
                    getRefactoring();
        String name = 
                refactoring.getNode()
                    .getDeclarationModel()
                    .getName();
        title.setText("Move '" + name + "' to a new source file.");
        GridData gd = new GridData();
        gd.horizontalSpan=4;
        title.setLayoutData(gd);
        GridData gd2 = new GridData(GridData.FILL_HORIZONTAL);
        gd2.horizontalSpan=4;
        new Label(composite, SWT.SEPARATOR|SWT.HORIZONTAL)
            .setLayoutData(gd2);
    }
    
    void createSeparator(Composite composite) {
        Label sep = new Label(composite, SWT.SEPARATOR | SWT.HORIZONTAL);
        GridData sgd= new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        sgd.horizontalSpan = 4;
        sep.setLayoutData(sgd);
    }

    Text createNameField(Composite composite) {
        Label nameLabel = new Label(composite, SWT.LEFT | SWT.WRAP);
        nameLabel.setText(getCompilationUnitLabel());
        GridData lgd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        lgd.horizontalSpan = 1;
        nameLabel.setLayoutData(lgd);

        final Text name = new Text(composite, SWT.SINGLE | SWT.BORDER);
        GridData ngd= new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        ngd.horizontalSpan = 2;
        ngd.grabExcessHorizontalSpace = true;
        name.setLayoutData(ngd);
        name.setText(unitName);
        name.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                unitName = name.getText();
                if (!unitNameIsLegal()) {
                    setErrorMessage(getIllegalUnitNameMessage());
                }
                else if (sourceDir==null) {
                    setErrorMessage(getSelectSourceFolderMessage());
                }
                else if (!packageNameIsLegal()) {
                    setErrorMessage(getIllegalPackageNameMessage());
                }
                else {
                    setErrorMessage(null); 
                }
                setPageComplete(isComplete());
            }
        });
        
        unitNameText = name;
        
        new Label(composite, SWT.NONE);        
        new Label(composite, SWT.NONE);
        
        Button includeHeader = new Button(composite, SWT.CHECK);
        includeHeader.setText("Include preamble in 'header.ceylon' in project root");
        includeHeader.setSelection(includePreamble);
        GridData igd= new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        igd.horizontalSpan = 3;
        igd.grabExcessHorizontalSpace = true;
        includeHeader.setLayoutData(igd);
        includeHeader.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                includePreamble = !includePreamble;
            }
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {}
        });
        
        new Label(composite, SWT.NONE);

        Link link = new Link(composite, SWT.NONE);
        link.setText("<a>Edit 'header.ceylon'...</a>");
        GridData kgd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        kgd.horizontalSpan = 2;
        kgd.grabExcessHorizontalSpace = true;
        link.setLayoutData(kgd);
        link.addSelectionListener(new SelectionListener() {            
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (sourceDir==null) {
                    MessageDialog.openWarning(getShell(), 
                            "No Source Folder", 
                            getSelectSourceFolderMessage());
                }
                else {
                    EditDialog d = new EditDialog(getShell());
                    d.setText(readHeader());
                    if (d.open()==Status.OK) {
                        saveHeader(d.getText());
                    }
                }
            }
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {}
        });

        new Label(composite, SWT.NONE);

        return name;
    }

    String getCompilationUnitLabel() {
        return "Compilation unit name: ";
    }

    Text createFolderField(Composite composite) {
        Label folderLabel = new Label(composite, SWT.LEFT | SWT.WRAP);
        folderLabel.setText("Source folder: ");
        GridData flgd= new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        flgd.horizontalSpan = 1;
        folderLabel.setLayoutData(flgd);

        final Text folder = new Text(composite, SWT.SINGLE | SWT.BORDER);
        GridData fgd= new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        fgd.horizontalSpan = 2;
        fgd.grabExcessHorizontalSpace = true;
        folder.setLayoutData(fgd);
        if (sourceDir!=null) {
            String folderName = 
                    sourceDir.getPath()
                        .toPortableString();
            folder.setText(folderName);
        }        
        folder.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                setSourceDir(folder.getText());
                if (sourceDir!=null && packageNameIsLegal()) {
                    packageFragment = 
                            sourceDir.getPackageFragment(packageName);
                }
                if (sourceDir==null) {
                    setErrorMessage(getSelectSourceFolderMessage());
                }
                else if (!packageNameIsLegal()) {
                    setErrorMessage(getIllegalPackageNameMessage());
                }
                else if (!unitNameIsLegal()) {
                    setErrorMessage(getIllegalUnitNameMessage());
                }
                else {
                    setErrorMessage(null);
                }
                setPageComplete(isComplete());
            }
            private void setSourceDir(String folderName) {
                try {
                    sourceDir = null;
                    IJavaProject[] projects = 
                            JavaCore.create(getWorkspace().getRoot())
                                .getJavaProjects();
                    for (IJavaProject jp: projects) {
                        for (IPackageFragmentRoot pfr: 
                                jp.getPackageFragmentRoots()) {
                            if (pfr.getPath()
                                    .toPortableString()
                                    .equals(folderName)) {
                                sourceDir = pfr;
                                return;
                            }
                        }
                    }
                }
                catch (JavaModelException jme) {
                    jme.printStackTrace();
                }
            }
        });
        
        Button selectFolder = new Button(composite, SWT.PUSH);
        selectFolder.setText("Browse...");
        GridData sfgd= new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        sfgd.horizontalSpan = 1;
        selectFolder.setLayoutData(sfgd);
        selectFolder.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                IPackageFragmentRoot pfr = 
                        getSourceContainer(getShell(), 
                                getWorkspace().getRoot(), 
                                sourceDir);
                if (pfr!=null) {
                    sourceDir = pfr;
                    String folderName = 
                            sourceDir.getPath()
                                .toPortableString();
                    folder.setText(folderName);
                    packageFragment = 
                            sourceDir.getPackageFragment(packageName);
                    setPageComplete(isComplete());
                }
                if (sourceDir==null) {
                    setErrorMessage(getSelectSourceFolderMessage());
                }
                else if (!packageNameIsLegal()) {
                    setErrorMessage(getIllegalPackageNameMessage());
                }
                else if (!unitNameIsLegal()) {
                    setErrorMessage(getIllegalUnitNameMessage());
                }
                else {
                    setErrorMessage(null);
                }
            }
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {}
        });
        
        new Label(composite, SWT.NONE);

        Link link = new Link(composite, SWT.NONE);
        link.setText("<a>Create new source folder...</a>");
        GridData kgd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        kgd.horizontalSpan = 3;
        kgd.grabExcessHorizontalSpace = true;
        link.setLayoutData(kgd);
        link.addSelectionListener(new SelectionListener() {            
            @Override
            public void widgetSelected(SelectionEvent e) {
                IPackageFragmentRoot pfr = 
                        (IPackageFragmentRoot) 
                            openSourceFolderWizard();
                if (pfr!=null) {
                    sourceDir = pfr;
                    String folderName = 
                            sourceDir.getPath()
                                .toPortableString();
                    folder.setText(folderName);
                    packageFragment = 
                            sourceDir.getPackageFragment(packageName);
                    setPageComplete(isComplete());
                }
                if (sourceDir==null) {
                    setErrorMessage(getSelectSourceFolderMessage());
                }
                else if (!packageNameIsLegal()) {
                    setErrorMessage(getIllegalPackageNameMessage());
                }
                else if (!unitNameIsLegal()) {
                    setErrorMessage(getIllegalUnitNameMessage());
                }
                else {
                    setErrorMessage(null);
                }
            }
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {}
        });

        return folder;
    }
    
    Text createPackageField(Composite composite, final Text folder) {
        
        final Text pkg = createPackageField(composite);
        
        new Label(composite, SWT.NONE);
        
        Link link = new Link(composite, SWT.NONE);
        link.setText("<a>Create new Ceylon package with descriptor...</a>");
        GridData kgd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        kgd.horizontalSpan = 3;
        kgd.grabExcessHorizontalSpace = true;
        link.setLayoutData(kgd);
        link.addSelectionListener(new SelectionListener() {            
            @Override
            public void widgetSelected(SelectionEvent e) {
                NewPackageWizard wiz = openPackageWizard();
                IPackageFragment pfr = wiz.getPackageFragment();
                if (pfr!=null) {
                    sourceDir = wiz.getSourceFolder();
                    String folderName = 
                            sourceDir.getPath()
                                .toPortableString();
                    folder.setText(folderName);
                    pkg.setText(pfr.getElementName());
                    packageFragment = pfr;
                    setPageComplete(isComplete());
                }
                if (!packageNameIsLegal()) {
                    setErrorMessage(getIllegalPackageNameMessage());
                }
                else if (sourceDir==null) {
                    setErrorMessage(getSelectSourceFolderMessage());
                }
                else if (!unitNameIsLegal()) {
                    setErrorMessage(getIllegalUnitNameMessage());
                }
                else {
                    setErrorMessage(null);
                }
            }
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {}
        });
        
        return pkg;

    }
        
    Text createPackageField(Composite composite) {
        
        Label packageLabel = new Label(composite, SWT.LEFT | SWT.WRAP);
        packageLabel.setText(getPackageLabel());
        GridData plgd= new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        plgd.horizontalSpan = 1;
        packageLabel.setLayoutData(plgd);

        final Text pkg = new Text(composite, SWT.SINGLE | SWT.BORDER);
        GridData pgd= new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        pgd.horizontalSpan = 2;
        pgd.grabExcessHorizontalSpace = true;
        pkg.setLayoutData(pgd);
        pkg.setText(packageName);
        pkg.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                packageName = pkg.getText();
                if (sourceDir!=null && packageNameIsLegal()) {
                    packageFragment = 
                            sourceDir.getPackageFragment(packageName);
                }
                if (!packageNameIsLegal()) {
                    setErrorMessage(getIllegalPackageNameMessage());
                }
                else if (sourceDir==null) {
                    setErrorMessage(getSelectSourceFolderMessage());
                }
                else if (!unitNameIsLegal()) {
                    setErrorMessage(getIllegalUnitNameMessage());
                }
                else {
                    setErrorMessage(null);
                }
                setPageComplete(isComplete());
            }
        });
        
        /*if (packageFragment!=null) {
            String pkgName = packageFragment.getElementName();
            pkg.setText(pkgName);
        }*/
        
        Button selectPackage = new Button(composite, SWT.PUSH);
        selectPackage.setText("Browse...");
        GridData spgd= new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        spgd.horizontalSpan = 1;
        selectPackage.setLayoutData(spgd);
        selectPackage.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (sourceDir==null) {
                    MessageDialog.openWarning(getShell(), 
                            "No Source Folder", 
                            getSelectSourceFolderMessage());
                }
                else {
                    IPackageFragment result = 
                            PackageSelectionDialog.selectPackage(
                                    getShell(), sourceDir);
                    if (result!=null) {
                        packageName = result.getElementName();
                        pkg.setText(packageName);
                        if (sourceDir!=null) {
                            packageFragment = 
                                    sourceDir.getPackageFragment(packageName);
                        }
                        setPageComplete(isComplete());
                    }
                    if (!packageNameIsLegal()) {
                        setErrorMessage(getIllegalPackageNameMessage());
                    }
                    else if (sourceDir==null) {
                        setErrorMessage(getSelectSourceFolderMessage());
                    }
                    else if (!unitNameIsLegal()) {
                        setErrorMessage(getIllegalUnitNameMessage());
                    }
                    else {
                        setErrorMessage(null);
                    }
                }
            }
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {}
        });
        
        return pkg;
    }

    private NewPackageWizard openPackageWizard() {
        IWizardDescriptor descriptor = 
                getWorkbench()
                    .getNewWizardRegistry()
                    .findWizard(PLUGIN_ID 
                            + ".newPackageWizard");
        if (descriptor!=null) {
            try {
                NewPackageWizard wizard = 
                        (NewPackageWizard) 
                            descriptor.createWizard();
                wizard.init(workbench, selection);
                WizardDialog wd = 
                        new WizardDialog(
                                Display.getCurrent()
                                    .getActiveShell(), 
                                wizard);
                wd.setTitle(wizard.getWindowTitle());
                wd.open();
                return wizard;
            }
            catch (CoreException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
    
    private IJavaElement openSourceFolderWizard() {
        IWizardDescriptor descriptor = 
                getWorkbench()
                    .getNewWizardRegistry()
                    .findWizard("org.eclipse.jdt.ui.wizards.NewSourceFolderCreationWizard");
        if (descriptor!=null) {
            try {
                NewSourceFolderCreationWizard wizard = 
                        (NewSourceFolderCreationWizard) 
                            descriptor.createWizard();
                wizard.init(workbench, selection);
                WizardDialog wd = 
                        new WizardDialog(
                                Display.getCurrent()
                                    .getActiveShell(), 
                                wizard);
                wd.setTitle(wizard.getWindowTitle());
                wd.open();
                return wizard.getCreatedElement();
            }
            catch (CoreException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
    
    void createSharedField(Composite composite) {        
        new Label(composite, SWT.NONE);
        Button sharedPackage = new Button(composite, SWT.CHECK);
        sharedPackage.setText(getSharedPackageLabel());
        sharedPackage.setSelection(shared);
        GridData igd= new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        igd.horizontalSpan = 3;
        igd.grabExcessHorizontalSpace = true;
        sharedPackage.setLayoutData(igd);
        sharedPackage.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                shared = !shared;
            }
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {}
        });
    }

    void createDeclarationField(Composite composite) {}

    String getSharedPackageLabel() {
        return "Create shared package (visible to other modules)";
    }
    
    String getPackageLabel() {
        return "Package: ";
    }

    public void initFromSelection() {
        IJavaElement je = getSelectedJavaElement(selection);
        if (je instanceof IJavaProject) {
            IJavaProject jp = (IJavaProject) je;
            if (jp.isOpen()) {
                //default to the first source dir 
                //we find in the selected project
                try {
                    for (IPackageFragmentRoot pfr: 
                            jp.getAllPackageFragmentRoots()) {
                        if (!pfr.isExternal() && !pfr.isArchive()) {
                            je = pfr;
                            break;
                        }
                    }
                } 
                catch (JavaModelException e) {}
            }
        }
        if (je instanceof IPackageFragmentRoot) {
            sourceDir = (IPackageFragmentRoot) je;
            packageFragment = sourceDir.getPackageFragment("");
            packageName = packageFragment.getElementName();
        }
        else if (je instanceof IPackageFragment) {
            packageFragment = (IPackageFragment) je;
            packageName = packageFragment.getElementName();
            sourceDir = (IPackageFragmentRoot) 
                    packageFragment.getAncestor(PACKAGE_FRAGMENT_ROOT);
        }
    }
    
    public void init(IWorkbench workbench, IStructuredSelection selection) {
        this.selection = selection;
        this.workbench = workbench;
    }
    
    boolean isComplete() {
        return packageNameIsLegal() && unitNameIsLegal() &&
                sourceDir!=null &&
                sourceDir.getPackageFragment(
                        packageFragment.getElementName())
                            .equals(packageFragment);
    }
    
    public IFile getFile() {
        String fileName = unitName;
        if (!fileName.endsWith(".ceylon")) {
            fileName += ".ceylon";
        }
        IPath path = 
                packageFragment.getPath()
                    .append(fileName);
        IProject project = 
                sourceDir.getJavaProject()
                    .getProject();
        return project.getFile(path.makeRelativeTo(project.getFullPath()));
    }
    
    public IPackageFragment getPackageFragment() {
        return packageFragment;
    }
    
    public IPackageFragmentRoot getSourceDir() {
        return sourceDir;
    }
    
    String getUnitName() {
        return unitName;
    }
    
    Text getUnitNameText() {
        return unitNameText;
    }
    
    public boolean isIncludePreamble() {
        return includePreamble;
    }
    
    public boolean isShared() {
        return shared;
    }
    
    private String readHeader() {
        //TODO: use IRunnableWithProgress
        StringBuilder sb = new StringBuilder();
        IFile file = getHeaderFile();
        if (file.exists() && file.isAccessible()) {
            InputStream stream = null;
            try {
                stream = file.getContents();
                BufferedReader reader = 
                        new BufferedReader(
                                new InputStreamReader(stream));
                String line;
                while ((line = reader.readLine())!=null) {
                    sb.append(line)
                        .append(System.lineSeparator());
                }
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
            finally {
                try {
                    if (stream!=null) {
                        stream.close();
                    }
                }
                catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }
        }
        return sb.toString();
    }
    
    private void saveHeader(String header) {
        //TODO: use IRunnableWithProgress
        IFile file = getHeaderFile();
        ByteArrayInputStream stream = null;
        try {
            if (file.exists()) {
                file.delete(true, null);
            }
            stream = new ByteArrayInputStream(header.getBytes()); //TODO: encoding
            file.create(stream, true, null);
        }
        catch (CoreException e) {
            e.printStackTrace();
        }
        finally {
            try {
                if (stream!=null) stream.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private IFile getHeaderFile() {
        return sourceDir.getJavaProject()
                .getProject()
                .getFile("header.ceylon");
    }

    private boolean unitNameIsLegal() {
        return unitName!=null && 
                unitIsNameLegal(unitName);
    }

    boolean unitIsNameLegal(String unitName) {
        return unitName.matches("(\\w|-)+(\\.ceylon)?");
    }
    
    private String getIllegalUnitNameMessage() {
        return "Please enter a legal compilation unit name.";
    }
    
    private String getSelectSourceFolderMessage() {
        return "Please select a source folder.";
    }
    
    boolean packageNameIsLegal(String packageName) {
        return packageName.isEmpty() || 
                packageName.matches("^[a-z_]\\w*(\\.[a-z_]\\w*)*$") /*&&
                !packageName.matches(KEYWORD_PATTERN)*/;
    }
    
    private boolean packageNameIsDiscouraged() {
        return packageName.matches("^(ceylon|java|javax)\\b.*");
    }
    
    private boolean packageNameIsLegal() {
        return packageName!=null &&
                packageNameIsLegal(packageName);
    }
    
    String getIllegalPackageNameMessage() {
        return "Please enter a legal package name (a period-separated list of all-lowercase identifiers).";
    }
    
    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    @Override
    public void setPageComplete(boolean complete) {
        if (packageNameIsDiscouraged()) {
            setMessage(getDiscouragedNamespaceMessage(), WARNING);
        }
        else {
            setMessage(null);
        }
        if (complete) {
            if (!packageFragment.exists()) {
                setMessage("Package does not exist: " + 
                        packageFragment.getElementName(), 
                        IMessageProvider.ERROR);
                super.setPageComplete(false);
                return;
            }
            for (String file: getFileNames()) {
                IPath path = 
                        packageFragment.getPath()
                            .append(file)
                            .addFileExtension("ceylon");
                if (getWorkspace()
                        .getRoot()
                        .getFile(path)
                        .exists()) {
                    setMessage("Existing unit will not be overwritten: " + 
                            path.toPortableString(), 
                            IMessageProvider.ERROR);
                    super.setPageComplete(false);
                    return;
                }
            }
            MoveToNewUnitRefactoring refactoring = 
                    (MoveToNewUnitRefactoring) 
                        getRefactoring();
            refactoring.setTargetProject(
                    getPackageFragment()
                        .getResource()
                        .getProject());
            refactoring.setTargetFile(getFile());
            refactoring.setTargetPackage(getPackageFragment());
            refactoring.setIncludePreamble(isIncludePreamble());
        }
        super.setPageComplete(complete);
    }

    String getDiscouragedNamespaceMessage() {
        return "Discouraged namespace: " 
                + packageName.split("\\.")[0];
    }

    String[] getFileNames() {
        return new String[] { unitName };
    }
    
}
