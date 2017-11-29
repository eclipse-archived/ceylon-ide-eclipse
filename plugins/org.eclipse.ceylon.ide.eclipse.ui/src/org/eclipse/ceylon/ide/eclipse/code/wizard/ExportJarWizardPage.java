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

import static org.eclipse.ceylon.ide.eclipse.core.builder.CeylonBuilder.getCeylonRepositories;
import static org.eclipse.ceylon.ide.eclipse.ui.CeylonResources.CEYLON_EXPORT_JAR;

import java.io.File;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import org.eclipse.ceylon.ide.eclipse.ui.CeylonPlugin;

public class ExportJarWizardPage extends WizardPage implements IWizardPage {

    private static final String DEFAULT_VERSION = "1.0.0";
    
    //private IStructuredSelection selection;
    private String moduleName;
    private String version = "";
    private String jarPath;
    private String repositoryPath;
    private IJavaProject project;
//    private IJavaElement selection;
    private Text versionField;
    private Text nameField;
    
    ExportJarWizardPage(String defaultRepositoryPath, 
            IJavaProject project, IJavaElement selection) {
        super("Export Java Archive", 
                "Export Java Archive", 
                CeylonPlugin.imageRegistry()
                    .getDescriptor(CEYLON_EXPORT_JAR));
        setDescription("Export a Java archive to a module repository.");
        repositoryPath = defaultRepositoryPath;
        this.project = project;
//        this.selection = selection;
    }

    /*public void init(IStructuredSelection selection) {
        this.selection = selection;
    }*/
    
    @Override
    public void createControl(Composite parent) {
        initializeDialogUnits(parent);
        
        Composite composite= new Composite(parent, SWT.NONE);
        composite.setFont(parent.getFont());

        GridLayout layout = new GridLayout();
        layout.numColumns = 4;
        composite.setLayout(layout);
        
        addSelectArchive(composite);
        addSpecifyModule(composite);
        addSelectRepo(composite);
        
        setControl(composite);

        Dialog.applyDialogFont(composite);

        setPageComplete(isComplete());
    }

    boolean moduleNameIsLegal(String packageName) {
        return moduleName.isEmpty() || 
                moduleName.matches("^[a-z_](\\w|-)*(\\.[a-z_](\\w|-)*)*$");
    }
    
    private boolean packageNameIsLegal() {
        return moduleName!=null &&
                moduleNameIsLegal(moduleName);
    }

    private void updateModuleInfoFromJar() {
        if(jarPath == null)
            return;
        File jar = new File(jarPath);
        if(!jar.exists())
            return;
        String lastPart = jar.getName();
        if(lastPart == null || lastPart.isEmpty())
            return;
        int suffix = lastPart.lastIndexOf('.');
        if(suffix == -1)
            return;
        String nameVersion = lastPart.substring(0, suffix);
        if(nameVersion.isEmpty())
            return;
        int dash = nameVersion.indexOf('-');
        String name;
        String version;
        if (dash != -1){
            version = nameVersion.substring(dash+1);
            name = nameVersion.substring(0,dash);
        }
        else {
            name = nameVersion;
            version = DEFAULT_VERSION;
        }
        if (nameField.getText().isEmpty()) {
            nameField.setText(name);
        }
        if (versionField.getText().isEmpty()) {
            versionField.setText(version);
        }
    }

    private void updateMessage() {
//        if (project==null) {
//            setErrorMessage("Please select a project");
//        }
        if (!isValidJar()) {
            setErrorMessage("Please select an existing Java archive");
        }
        else if (!packageNameIsLegal()) {
            setErrorMessage("Please enter a legal module name");
        }
        else if (version.isEmpty()) {
            setErrorMessage("Please enter a version");
        }
        else if (!isValidRepo()) {
            setErrorMessage("Please select an existing local repository");
        }
//        else if (modules.getSelection().length==0) {
//            setErrorMessage("Please select a module to export");
//        }
        else {
            setErrorMessage(null);
        }
    }

    void addSelectRepo(Composite composite) {
        Label folderLabel = new Label(composite, SWT.LEFT | SWT.WRAP);
        folderLabel.setText("Target module repository: ");
        GridData flgd= new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        flgd.horizontalSpan = 1;
        folderLabel.setLayoutData(flgd);

        final Combo folder = new Combo(composite, SWT.SINGLE | SWT.BORDER);
        GridData fgd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        fgd.horizontalSpan = 2;
        fgd.grabExcessHorizontalSpace = true;
        fgd.widthHint = 300;
        folder.setLayoutData(fgd);
        folder.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                repositoryPath = folder.getText();
                updateMessage();
                setPageComplete(isComplete());
            }
        });
        
        folder.setText(repositoryPath);
        if (project!=null) {
            folder.add(repositoryPath);
            for (String path: getCeylonRepositories(project.getProject())) {
                if (!path.startsWith("http://") && !path.equals(repositoryPath)) {
                    folder.add(path);
                }
            }
        }
        
        Button selectFolder = new Button(composite, SWT.PUSH);
        selectFolder.setText("Browse...");
        GridData sfgd= new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        sfgd.horizontalSpan = 1;
        selectFolder.setLayoutData(sfgd);
        selectFolder.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                String dir = new DirectoryDialog(getShell(), SWT.SHEET).open();
                if (dir!=null) {
                    repositoryPath = dir;
                    folder.setText(repositoryPath);
                }
                updateMessage();
                setPageComplete(isComplete());
            }
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {}
        });
        
//        modules.addSelectionListener(new SelectionListener() {
//            @Override
//            public void widgetSelected(SelectionEvent e) {
//                updateMessage();
//                setPageComplete(isComplete());
//            }
//            @Override
//            public void widgetDefaultSelected(SelectionEvent e) {}
//        });
    
    }
    
//    Table modules;
    
    void addSpecifyModule(Composite composite) {
        
        Label nameLabel = new Label(composite, SWT.LEFT | SWT.WRAP);
        nameLabel.setText("Module name and version: ");
        GridData jlgd= new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        jlgd.horizontalSpan = 1;
        nameLabel.setLayoutData(jlgd);

        nameField = new Text(composite, SWT.SINGLE | SWT.BORDER);
        GridData ngd= new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        ngd.horizontalSpan = 1;
        ngd.grabExcessHorizontalSpace = true;
        nameField.setLayoutData(ngd);
        nameField.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                moduleName = nameField.getText();
                updateMessage();
                setPageComplete(isComplete());
            }
        });
        
        versionField = new Text(composite, SWT.SINGLE | SWT.BORDER);
        versionField.setText(version);
        GridData vgd= new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        vgd.horizontalSpan = 1;
        vgd.grabExcessHorizontalSpace = true;
        versionField.setLayoutData(vgd);
        versionField.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                version = versionField.getText();
                updateMessage();
                setPageComplete(isComplete());
            }
        });
        
        new Label(composite, SWT.NONE).setLayoutData(jlgd);
    }
    
    void addSelectArchive(Composite composite) {
        
        Label jarLabel = new Label(composite, SWT.LEFT | SWT.WRAP);
        jarLabel.setText("Select Java archive: ");
        GridData jlgd= new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        jlgd.horizontalSpan = 1;
        jarLabel.setLayoutData(jlgd);

        final Text jarField = new Text(composite, SWT.SINGLE | SWT.BORDER);
        GridData pgd= new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        pgd.horizontalSpan = 2;
        pgd.grabExcessHorizontalSpace = true;
        jarField.setLayoutData(pgd);
        jarField.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                jarPath = jarField.getText();
                updateMessage();
                setPageComplete(isComplete());
            }
        });

        Button selectJar = new Button(composite, SWT.PUSH);
        selectJar.setText("Browse...");
        GridData sfgd= new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        sfgd.horizontalSpan = 1;
        selectJar.setLayoutData(sfgd);
        selectJar.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                FileDialog fd = new FileDialog(getShell(), SWT.SHEET);
                fd.setFilterExtensions(new String[]{"*.jar"});
                fd.setText("Select Java Archive");
                String dir = fd.open();
                if (dir != null) {
                    jarField.setText(dir);
                    jarPath = dir;
                    updateModuleInfoFromJar();
                    updateMessage();
                    setPageComplete(isComplete());
                }
            }
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {}
        });

//        Label projectLabel = new Label(composite, SWT.LEFT | SWT.WRAP);
//        projectLabel.setText("Project containing modules: ");
//        GridData plgd= new GridData(GridData.HORIZONTAL_ALIGN_FILL);
//        plgd.horizontalSpan = 1;
//        projectLabel.setLayoutData(plgd);
//
//        final Text projectField = new Text(composite, SWT.SINGLE | SWT.BORDER);
//        GridData pgd= new GridData(GridData.HORIZONTAL_ALIGN_FILL);
//        pgd.horizontalSpan = 2;
//        pgd.grabExcessHorizontalSpace = true;
//        projectField.setLayoutData(pgd);
//        
//        Button selectProject = new Button(composite, SWT.PUSH);
//        selectProject.setText("Browse...");
//        GridData spgd= new GridData(GridData.HORIZONTAL_ALIGN_FILL);
//        spgd.horizontalSpan = 1;
//        selectProject.setLayoutData(spgd);
//
//        Label modulesLabel = new Label(composite, SWT.LEFT | SWT.WRAP);
//        modulesLabel.setText("Modules to export: ");
//        GridData mlgd= new GridData(GridData.HORIZONTAL_ALIGN_FILL|GridData.VERTICAL_ALIGN_BEGINNING);
//        mlgd.horizontalSpan = 1;
//        modulesLabel.setLayoutData(mlgd);
//
//        modules = new Table(composite, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL);
//        //modules.setEnabled(false);
//        GridData mgd= new GridData(GridData.HORIZONTAL_ALIGN_FILL);
//        mgd.horizontalSpan = 2;
//        mgd.grabExcessHorizontalSpace = true;
//        mgd.heightHint = 50;
//        modules.setLayoutData(mgd);
//        if (project!=null) {
//            projectField.setText(project.getElementName());
//            updateModuleList();
//        }
//        if (selection!=null) {
//            String selectionName = selection.getElementName();
//            TableItem[] items = modules.getItems();
//            for (int i=0; i<items.length; i++) {
//                String itemText = items[i].getText();
//                int j = itemText.indexOf('/');
//                if (itemText.substring(0,j).equals(selectionName)) {
//                    modules.deselectAll();
//                    modules.select(i);
//                }
//            }
//        }
//        
//        new Label(composite, SWT.NONE);
//        
//        selectProject.addSelectionListener(new SelectionListener() {
//            @Override
//            public void widgetSelected(SelectionEvent e) {
//                ProjectSelectionDialog dialog = new ProjectSelectionDialog(getShell());
//                dialog.setMultipleSelection(false);
//                dialog.setTitle("Project Selection");
//                dialog.setMessage("Select a project:");
//                dialog.open();
//                Object result = dialog.getFirstResult();
//                if (result!=null) {
//                    project = (IJavaProject) result;
//                    projectField.setText(project.getElementName());
//                    updateModuleList();
//                }
//                updateMessage();
//                setPageComplete(isComplete());
//            }
//            @Override
//            public void widgetDefaultSelected(SelectionEvent e) {}
//        });
//        
//        projectField.addModifyListener(new ModifyListener() {
//            @Override
//            public void modifyText(ModifyEvent e) {
//                String projectName = projectField.getText();
//                if (project==null ||
//                        !project.getElementName().equals(projectName)) {
//                    setProject(projectName);
//                    updateModuleList();
//                }
//                updateMessage();
//                setPageComplete(isComplete());
//            }
//            private void setProject(String projectName) {
//                try {
//                    project = null;
//                    for (IJavaProject jp: JavaCore.create(ResourcesPlugin.getWorkspace().getRoot())
//                            .getJavaProjects()) {
//                        if (jp.getElementName().equals(projectName)) {
//                            project = jp;
//                            return;
//                        }
//                    }
//                }
//                catch (JavaModelException jme) {
//                    jme.printStackTrace();
//                }
//            }
//        });
    }

//    private void updateModuleList() {
//        if (project!=null) {
//            modules.removeAll();
//            for (Module m: getProjectModules(project.getProject()).getListOfModules()) {
//                if (!m.isDefaultModule() && !m.isJava()) {
//                    try {
//                        for (IPackageFragment pkg: project.getPackageFragments()) {
//                            if (!pkg.isReadOnly() &&
//                                    pkg.getElementName().equals(m.getNameAsString())) {
//                                TableItem item = new TableItem(modules, SWT.NONE);
//                                item.setText(m.getNameAsString() + "/" + m.getVersion());
//                                item.setImage(CeylonLabelProvider.ARCHIVE);
//                            }
//                        }
//                    } 
//                    catch (JavaModelException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//            modules.selectAll();
//        }
//    }
//    
//    public Table getModules() {
//        return modules;
//    }

    private boolean isComplete() {
        return packageNameIsLegal() && 
                !version.isEmpty() && 
                isValidRepo() && 
                isValidJar();
    }
    
    private boolean isValidRepo() {
        return repositoryPath!=null &&
                !repositoryPath.isEmpty() &&
                !repositoryPath.startsWith("http://");
    }
    
    private boolean isValidJar() {
        return jarPath!=null &&
                !jarPath.isEmpty() &&
                new File(jarPath).exists();
    }
    
    public String getRepositoryPath() {
        return repositoryPath;
    }
    
    public String getJarPath() {
        return jarPath;
    }
    
    public String getModuleName() {
        return moduleName;
    }
    
    public String getVersion() {
        return version;
    }
    
    public IJavaProject getProject() {
        return project;
    }
    
}
