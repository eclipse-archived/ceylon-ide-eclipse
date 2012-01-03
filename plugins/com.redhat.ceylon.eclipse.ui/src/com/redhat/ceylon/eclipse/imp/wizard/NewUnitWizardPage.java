package com.redhat.ceylon.eclipse.imp.wizard;

import static org.eclipse.jdt.core.IJavaElement.PACKAGE_FRAGMENT_ROOT;
import static org.eclipse.jdt.internal.ui.refactoring.nls.SourceContainerDialog.getSourceContainer;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
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
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Text;

import com.redhat.ceylon.eclipse.ui.CeylonPlugin;

public class NewUnitWizardPage extends WizardPage implements IWizardPage {

    private String unitName;
    private IPackageFragmentRoot sourceDir;
    private IPackageFragment packageFragment;
    private String packageName = "";
    private boolean includePreamble = true;
    private boolean shared = true;
    private boolean declaration;
    private final boolean declarationButtonDisabled;
    
    private IStructuredSelection selection;
    
    NewUnitWizardPage(String title, String description, 
            String defaultUnitName, String icon,
            boolean declarationButtonDisabled) {
        super(title, title, CeylonPlugin.getInstance()
                .getImageRegistry().getDescriptor(icon));
        setDescription(description);
        unitName = defaultUnitName;
        this.declarationButtonDisabled = declarationButtonDisabled;
        declaration = declarationButtonDisabled;
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
        
        Composite composite= new Composite(parent, SWT.NONE);
        composite.setFont(parent.getFont());

        GridLayout layout = new GridLayout();
        layout.numColumns = 4;
        composite.setLayout(layout);
        
        createControls(composite);
        
        setControl(composite);

        Dialog.applyDialogFont(composite);
    }

    void createControls(Composite composite) {
        Text name = createNameField(composite);
        createDeclarationField(composite);
        createSeparator(composite);
        createFolderField(composite);
        createPackageField(composite);
        name.forceFocus();
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
        GridData lgd= new GridData(GridData.HORIZONTAL_ALIGN_FILL);
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
                if (!unitName.matches("\\w+")) {
                    setErrorMessage("Please enter a legal compilation unit name.");
                }
                else if (sourceDir==null) {
                    setErrorMessage("Please select a source folder");
                }
                else {
                    setErrorMessage(null);
                    
                }
                setPageComplete(isComplete());
            }
        });
        
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
        link.setText("<a>(Edit 'header.ceylon')</a>");
        GridData kgd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        kgd.horizontalSpan = 2;
        kgd.grabExcessHorizontalSpace = true;
        link.setLayoutData(kgd);
        link.addSelectionListener(new SelectionListener() {            
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (sourceDir==null) {
                    MessageDialog.openWarning(getShell(), "No Source Folder", 
                            "Please select a source folder");
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
            String folderName = sourceDir.getPath().toPortableString();
            folder.setText(folderName);
        }        
        folder.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                String folderName = folder.getText();
                try {
                    sourceDir = null;
                    for (IJavaProject jp: JavaCore.create(ResourcesPlugin.getWorkspace().getRoot())
                            .getJavaProjects()) {
                        for (IPackageFragmentRoot pfr: jp.getPackageFragmentRoots()) {
                            if (pfr.getPath().toPortableString().equals(folderName)) {
                                sourceDir = pfr;
                            }
                        }
                    }
                }
                catch (JavaModelException jme) {
                    jme.printStackTrace();
                }
                if (sourceDir==null) {
                    setErrorMessage("Please select a source folder");
                }
                else {
                    setErrorMessage(null);
                }
                setPageComplete(isComplete());
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
                        ResourcesPlugin.getWorkspace().getRoot(), sourceDir);
                if (pfr!=null) {
                    sourceDir = pfr;
                    String folderName = sourceDir.getPath().toPortableString();
                    folder.setText(folderName);
                    packageFragment = sourceDir.getPackageFragment(packageName);
                    setPageComplete(isComplete());
                }
                else if (sourceDir==null) {
                    setErrorMessage("Please select a source folder");
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
                if (!packageNameIsLegal(packageName)) {
                    setErrorMessage(getIllegalPackageNameMessage());
                }
                else if (sourceDir==null) {
                    setErrorMessage("Please select a source folder");
                }
                else {
                    setErrorMessage(null);
                }
                if (sourceDir!=null) {
                    packageFragment = sourceDir.getPackageFragment(packageName);
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
                    MessageDialog.openWarning(getShell(), "No Source Folder", 
                            "Please select a source folder");
                }
                else {
                    PackageSelectionDialog dialog = new PackageSelectionDialog(getShell(), sourceDir);
                    dialog.setMultipleSelection(false);
                    dialog.setTitle("Package Selection");
                    dialog.setMessage("Select a package:");
                    dialog.open();
                    Object result = dialog.getFirstResult();
                    if (result!=null) {
                        packageName = ((IPackageFragment) result).getElementName();
                        pkg.setText(packageName);
                        setPageComplete(isComplete());
                    }
                }
            }
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {}
        });
        
        return pkg;
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

    void createDeclarationField(Composite composite) {        
        new Label(composite, SWT.NONE);
        
        Button dec = new Button(composite, SWT.CHECK);
        dec.setText("create toplevel class or method declaration");
        dec.setSelection(declaration);
        dec.setEnabled(!declarationButtonDisabled);
        GridData igd= new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        igd.horizontalSpan = 3;
        igd.grabExcessHorizontalSpace = true;
        dec.setLayoutData(igd);
        dec.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
            	declaration = !declaration;
            }
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {}
        });
    }

    String getSharedPackageLabel() {
        return "Create shared package (visible to other modules)";
    }
    
    String getPackageLabel() {
        return "Package: ";
    }

    public void initFromSelection() {
        IJavaElement je = getSelectedElement();
        if (je instanceof IPackageFragmentRoot) {
            sourceDir = (IPackageFragmentRoot) je;
            packageFragment = sourceDir.getPackageFragment("");
            packageName = packageFragment.getElementName();
        }
        else if (je instanceof IPackageFragment) {
            packageFragment = (IPackageFragment) je;
            packageName = packageFragment.getElementName();
            sourceDir = (IPackageFragmentRoot) packageFragment.getAncestor(PACKAGE_FRAGMENT_ROOT);
        }
    }
    
    public void init(IStructuredSelection selection) {
        this.selection = selection;
    }
    
    boolean isComplete() {
        return packageFragment!=null &&
                sourceDir!=null &&
                sourceDir.getPackageFragment(packageFragment.getElementName())
                        .equals(packageFragment) &&
                !unitName.equals("");
    }
    
    public IPackageFragment getPackageFragment() {
        return packageFragment;
    }
    
    public IPackageFragmentRoot getSourceDir() {
        return sourceDir;
    }
    
    public String getUnitName() {
        return unitName;
    }
    
    public boolean isIncludePreamble() {
        return includePreamble;
    }
    
    public boolean isShared() {
        return shared;
    }
    
    public boolean isDeclaration() {
		return declaration;
	}
    
    private String readHeader() {
        //TODO: use IRunnableWithProgress
        StringBuilder sb = new StringBuilder();
        IFile file = getHeaderFile();
        if (file.exists() && file.isAccessible()) {
            InputStream stream = null;
            try {
                stream = file.getContents();
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
                String line;
                while ((line = reader.readLine())!=null) {
                    sb.append(line).append("\n");
                }
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
            finally {
                try {
                    if (stream!=null) stream.close();
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
        return sourceDir.getJavaProject().getProject()
                .getFile("header.ceylon");
    }

    boolean packageNameIsLegal(String packageName) {
        return packageName.isEmpty() || 
            packageName.matches("^[a-z_]\\w*(\\.[a-z_]\\w*)*$");
    }
    
    String getIllegalPackageNameMessage() {
        return "Please enter a legal package name.";
    }

}
