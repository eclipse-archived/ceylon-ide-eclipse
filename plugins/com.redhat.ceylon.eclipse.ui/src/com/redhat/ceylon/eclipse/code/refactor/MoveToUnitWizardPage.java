package com.redhat.ceylon.eclipse.code.refactor;

import static com.redhat.ceylon.eclipse.ui.CeylonPlugin.PLUGIN_ID;
import static org.eclipse.core.resources.ResourcesPlugin.getWorkspace;
import static org.eclipse.jdt.core.IJavaElement.PACKAGE_FRAGMENT_ROOT;
import static org.eclipse.jdt.internal.ui.refactoring.nls.SourceContainerDialog.getSourceContainer;
import static org.eclipse.ui.PlatformUI.getWorkbench;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.dialogs.Dialog;
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
import org.eclipse.ui.wizards.IWizardDescriptor;

import com.redhat.ceylon.eclipse.code.select.PackageSelectionDialog;
import com.redhat.ceylon.eclipse.code.select.UnitSelectionDialog;
import com.redhat.ceylon.eclipse.code.wizard.NewUnitWizard;
import com.redhat.ceylon.eclipse.util.Escaping;

public class MoveToUnitWizardPage extends UserInputWizardPage {

    private IPackageFragmentRoot sourceDir;
    private String unitName = "";
    private IFile unit;
    private IPackageFragment packageFragment;
    private String packageName = "";
    
    private IStructuredSelection selection;
    private Text unitNameText;
    private String name;
    
    MoveToUnitWizardPage(String title, String name) {
        super(title);
        this.name = name;
    }

    //TODO: fix copy/paste to ExportModuleWizard
    private IJavaElement getSelectedElement() {
        if (selection!=null && selection.size()==1) {
            Object element = selection.getFirstElement();
            if (element instanceof IFile) {
                return JavaCore.create(((IFile) element).getParent());
            }
            else {
                return (IJavaElement) ((IAdaptable) element)
                        .getAdapter(IJavaElement.class);
            }
        }
        else {
            return null;
        }
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
        createFolderField(composite);
        createPackageField(composite);
        createUnitField(composite);
    }

    void createFolderField(Composite composite) {
        Label folderLabel = new Label(composite, SWT.LEFT | SWT.WRAP);
        folderLabel.setText("Source folder: ");
        GridData flgd= new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        flgd.horizontalSpan = 1;
        folderLabel.setLayoutData(flgd);

        folder = new Text(composite, SWT.SINGLE | SWT.BORDER);
        GridData fgd= new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        fgd.horizontalSpan = 2;
        fgd.grabExcessHorizontalSpace = true;
        folder.setLayoutData(fgd);
        if (getSourceDir()!=null) {
            String folderName = getSourceDir().getPath().toPortableString();
            folder.setText(folderName);
        }        
        folder.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                setSourceDir(folder.getText());
                if (getSourceDir()!=null && unitNameIsLegal()) {
                    try {
                        setUnit(((IFolder) getSourceDir().getPackageFragment(packageName).getCorrespondingResource())
                                .getFile(getUnitName()));
                    }
                    catch (JavaModelException e1) {
                        setUnit(null);
                    }
                }
                if (getSourceDir()==null) {
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
            private void setSourceDir(String folderName) {
                try {
                    sourceDir = null;
                    for (IJavaProject jp: JavaCore.create(getWorkspace().getRoot())
                            .getJavaProjects()) {
                        for (IPackageFragmentRoot pfr: jp.getPackageFragmentRoots()) {
                            if (pfr.getPath().toPortableString().equals(folderName)) {
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
                IPackageFragmentRoot pfr = getSourceContainer(getShell(), 
                        getWorkspace().getRoot(), getSourceDir());
                if (pfr!=null) {
                    setSourceDir(pfr);
                    String folderName = getSourceDir().getPath().toPortableString();
                    folder.setText(folderName);
                    try {
                        setUnit(((IFolder) getSourceDir().getPackageFragment(packageName).getCorrespondingResource())
                                .getFile(getUnitName()));
                    }
                    catch (JavaModelException e1) {
                        setUnit(null);
                    }
                    setPageComplete(isComplete());
                }
                if (getSourceDir()==null) {
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
        
    }
    
    void createPackageField(Composite composite) {
        
        Label packageLabel = new Label(composite, SWT.LEFT | SWT.WRAP);
        packageLabel.setText(getPackageLabel());
        GridData plgd= new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        plgd.horizontalSpan = 1;
        packageLabel.setLayoutData(plgd);

        pkg = new Text(composite, SWT.SINGLE | SWT.BORDER);
        GridData pgd= new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        pgd.horizontalSpan = 2;
        pgd.grabExcessHorizontalSpace = true;
        pkg.setLayoutData(pgd);
        pkg.setText(packageName);
        pkg.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                packageName = pkg.getText();
                if (getSourceDir()!=null && packageNameIsLegal()) {
                    setPackageFragment(getSourceDir().getPackageFragment(packageName));
                }
                if (!packageNameIsLegal()) {
                    setErrorMessage(getIllegalPackageNameMessage());
                }
                else if (getSourceDir()==null) {
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
                if (getSourceDir()==null) {
                    MessageDialog.openWarning(getShell(), "No Source Folder", 
                            getSelectSourceFolderMessage());
                }
                else {
                    PackageSelectionDialog dialog = 
                            new PackageSelectionDialog(getShell(), getSourceDir());
                    dialog.setMultipleSelection(false);
                    dialog.setTitle("Package Selection");
                    dialog.setMessage("Select a package:");
                    dialog.open();
                    Object result = dialog.getFirstResult();
                    if (result!=null) {
                        packageName = ((IPackageFragment) result).getElementName();
                        pkg.setText(packageName);
                        if (getSourceDir()!=null) {
                            setPackageFragment(getSourceDir().getPackageFragment(packageName));
                        }
                        setPageComplete(isComplete());
                    }
                    if (!packageNameIsLegal()) {
                        setErrorMessage(getIllegalPackageNameMessage());
                    }
                    else if (getSourceDir()==null) {
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
        
    }

    void createUnitField(Composite composite) {
        
        Label unitLabel = new Label(composite, SWT.LEFT | SWT.WRAP);
        unitLabel.setText(getUnitLabel());
        GridData plgd= new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        plgd.horizontalSpan = 1;
        unitLabel.setLayoutData(plgd);

        final Text source = new Text(composite, SWT.SINGLE | SWT.BORDER);
        GridData pgd= new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        pgd.horizontalSpan = 2;
        pgd.grabExcessHorizontalSpace = true;
        source.setLayoutData(pgd);
        source.setText(getUnitName());
        source.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                setUnitName(source.getText());
                if (getSourceDir()!=null && unitNameIsLegal()) {
                    try {
                        setUnit(((IFolder) getSourceDir().getPackageFragment(packageName).getCorrespondingResource())
                                .getFile(getUnitName()));
                    }
                    catch (JavaModelException e1) {
                        setUnit(null);
                    }
                }
                if (!unitNameIsLegal()) {
                    setErrorMessage(getIllegalUnitNameMessage());
                }
                else if (getSourceDir()==null) {
                    setErrorMessage(getSelectSourceFolderMessage());
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
        
        Button selectUnit = new Button(composite, SWT.PUSH);
        selectUnit.setText("Browse...");
        GridData spgd= new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        spgd.horizontalSpan = 1;
        selectUnit.setLayoutData(spgd);
        selectUnit.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (getSourceDir()==null) {
                    MessageDialog.openWarning(getShell(), 
                            "No Source Folder", 
                            getSelectSourceFolderMessage());
                }
                else {
                    UnitSelectionDialog dialog = 
                            new UnitSelectionDialog(getShell(), 
                                    getSourceDir(), 
                                    getPackageFragment());
                    dialog.setMultipleSelection(false);
                    dialog.setTitle("Source File Selection");
                    dialog.setMessage("Select a source file:");
                    dialog.open();
                    Object result = dialog.getFirstResult();
                    if (result!=null) {
                        IFile file = (IFile) result;
//                        IPath relpath = file.getFullPath()
//                                .makeRelativeTo(getSourceDir().getPath());
                        setPackageFragment((IPackageFragment) JavaCore.create(file.getParent()));
                        pkg.setText(getPackageFragment().getElementName());
                        setUnitName(file.getName());
                        source.setText(getUnitName());
                        if (getSourceDir()!=null) {
                            setUnit(file);
                        }
                        setPageComplete(isComplete());
                    }
                    if (!unitNameIsLegal()) {
                        setErrorMessage(getIllegalUnitNameMessage());
                    }
                    else if (getSourceDir()==null) {
                        setErrorMessage(getSelectSourceFolderMessage());
                    }
                    else {
                        setErrorMessage(null);
                    }
                }
            }
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {}
        });
        
        new Label(composite, SWT.NONE);
        
        Link link = new Link(composite, SWT.NONE);
        link.setText("<a>Create new Ceylon source file...</a>");
        GridData kgd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        kgd.horizontalSpan = 3;
        kgd.grabExcessHorizontalSpace = true;
        link.setLayoutData(kgd);
        link.addSelectionListener(new SelectionListener() {            
            @Override
            public void widgetSelected(SelectionEvent e) {
                NewUnitWizard wiz = openUnitWizard();
                IPackageFragment pfr = wiz.getPackageFragment();
                if (pfr!=null) {
                    setSourceDir(wiz.getSourceFolder());
                    String folderName = getSourceDir().getPath().toPortableString();
                    folder.setText(folderName);
                    pkg.setText(pfr.getElementName());
                    setPackageFragment(pfr);
                    String un = wiz.getUnitName();
                    if (un==null || un.isEmpty()) {
                        source.setText("");
                    }
                    else {
                        source.setText(un+ ".ceylon");
                    }
                    setPageComplete(isComplete());
                }
                if (!packageNameIsLegal()) {
                    setErrorMessage(getIllegalPackageNameMessage());
                }
                else if (getSourceDir()==null) {
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
        
    }
    
    private NewUnitWizard openUnitWizard() {
        IWizardDescriptor descriptor = 
                getWorkbench().getNewWizardRegistry()
                    .findWizard(PLUGIN_ID + ".newUnitWizard");
        if (descriptor!=null) {
            try {
                NewUnitWizard wizard = 
                        (NewUnitWizard) descriptor.createWizard();
                wizard.init(getWorkbench(), selection);
                wizard.setUnitName(name);
                wizard.setPerform(false);
                WizardDialog wd = 
                        new WizardDialog(Display.getCurrent().getActiveShell(), 
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
    
    String getUnitLabel() {
        return "Source file: ";
    }

    String getPackageLabel() {
        return "Package: ";
    }

    public void initFromSelection() {
        IJavaElement je = getSelectedElement();
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
            setSourceDir((IPackageFragmentRoot) je);
        }
        else if (je instanceof IPackageFragment) {
            IPackageFragment packageFragment = (IPackageFragment) je;
            packageName = packageFragment.getElementName();
            setPackageFragment(packageFragment);
            setSourceDir((IPackageFragmentRoot) 
                    packageFragment.getAncestor(PACKAGE_FRAGMENT_ROOT));
        }
    }
    
    public void init(IStructuredSelection selection) {
        this.selection = selection;
    }
    
    boolean isComplete() {
        try {
            return unitNameIsLegal() && 
                    getSourceDir()!=null && getUnit()!=null &&
//                    getUnit().exists() &&
                    ((IFolder) getSourceDir().getCorrespondingResource())
                    .getFile(getUnit().getFullPath().makeRelativeTo(getSourceDir().getPath()))
                            .equals(getUnit());
        }
        catch (JavaModelException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean unitNameIsLegal() {
        return getUnitName()!=null && 
                unitIsNameLegal(getUnitName());
    }

    boolean unitIsNameLegal(String unitName) {
        return unitName.matches("^(\\w|-)+\\.ceylon$");
    }
    
    private String getIllegalUnitNameMessage() {
        return "Please enter a legal compilation unit name.";
    }
    
    IPackageFragmentRoot getSourceDir() {
        return sourceDir;
    }
    
    Text getUnitNameText() {
        return unitNameText;
    }
    
    private String getSelectSourceFolderMessage() {
        return "Please select a source folder.";
    }

    void setSourceDir(IPackageFragmentRoot sourceDir) {
        this.sourceDir = sourceDir;
        IPath path = sourceDir==null ? null : sourceDir.getPath();
        getMoveToUnitRefactoring().setTargetSourceDirPath(path);
        IProject project = sourceDir==null ? null : sourceDir.getResource().getProject();
        getMoveToUnitRefactoring().setTargetProject(project);
    }
    
    IFile getUnit() {
        return unit;
    }

    void setUnit(IFile unit) {
        this.unit = unit;
        getMoveToUnitRefactoring().setTargetFile(unit);
    }

    String getUnitName() {
        return unitName;
    }

    void setUnitName(String unitName) {
        this.unitName = unitName;
    }
    
    private static final String KEYWORDS;
    private Text folder;
    private Text pkg;
    static {
        StringBuilder sb = new StringBuilder();
        for (String kw: Escaping.KEYWORDS) {
            sb.append(kw).append('|');
        }
        sb.setLength(sb.length()-1);
        KEYWORDS = sb.toString();
    }

    boolean packageNameIsLegal(String packageName) {
        return packageName.isEmpty() || 
            packageName.matches("^[a-z_]\\w*(\\.[a-z_]\\w*)*$") &&
            !packageName.matches(".*\\b("+KEYWORDS+")\\b.*");
    }
    
    private boolean packageNameIsLegal() {
        return packageName!=null &&
                packageNameIsLegal(packageName);
    }
    
    String getIllegalPackageNameMessage() {
        return "Please enter a legal package name (a period-separated list of all-lowercase identifiers).";
    }
    
    MoveToUnitRefactoring getMoveToUnitRefactoring() {
        return (MoveToUnitRefactoring) getRefactoring();
    }

    IPackageFragment getPackageFragment() {
        return packageFragment;
    }

    void setPackageFragment(IPackageFragment packageFragment) {
        this.packageFragment = packageFragment;
        getMoveToUnitRefactoring().setTargetPackage(packageFragment);
    }

}
