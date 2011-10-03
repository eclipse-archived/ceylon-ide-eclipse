package com.redhat.ceylon.eclipse.imp.wizard;

import static com.redhat.ceylon.eclipse.ui.ICeylonResources.CEYLON_NEW_FILE;
import static org.eclipse.jdt.core.IJavaElement.PACKAGE_FRAGMENT_ROOT;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.refactoring.nls.SourceContainerDialog;
import org.eclipse.jface.dialogs.Dialog;
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
import org.eclipse.swt.widgets.Text;

import com.redhat.ceylon.eclipse.ui.CeylonPlugin;

public class NewUnitWizardPage extends WizardPage implements IWizardPage {

    private String unitName="";
    private IPackageFragmentRoot sourceDir;
    private IPackageFragment packageFragment;
    private boolean includePreamble = true;
    
    IStructuredSelection selection;
    
    NewUnitWizardPage() {
        super("New Ceylon Unit", "New Ceylon Unit", CeylonPlugin.getInstance()
                .getImageRegistry().getDescriptor(CEYLON_NEW_FILE));
        setDescription("Create a new Ceylon compilation unit that will contain Ceylon source.");
    }

    private IJavaElement getSelectedElement() {
        if (selection!=null && selection.size()==1) {
            IJavaElement je = (IJavaElement) ((IAdaptable) selection.getFirstElement())
                    .getAdapter(IJavaElement.class);
            //TODO: handle the case of an IFile
            return je;
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
        folder.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                String folderName = folder.getText();
                try {
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
                setPageComplete(isComplete());
            }
        });
        
        if (sourceDir!=null) {
            String folderName = sourceDir.getPath().toPortableString();
            folder.setText(folderName);
        }
        
        Button selectFolder = new Button(composite, SWT.PUSH);
        selectFolder.setText("Browse...");
        GridData sfgd= new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        sfgd.horizontalSpan = 1;
        selectFolder.setLayoutData(sfgd);
        selectFolder.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                IPackageFragmentRoot pfr = SourceContainerDialog.getSourceContainer(getShell(), 
                        ResourcesPlugin.getWorkspace().getRoot(), sourceDir);
                if (pfr!=null) {
                    sourceDir = pfr;
                    String folderName = sourceDir.getPath().toPortableString();
                    folder.setText(folderName);
                    setPageComplete(isComplete());
                }
            }
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {}
        });
        
        Label packageLabel = new Label(composite, SWT.LEFT | SWT.WRAP);
        packageLabel.setText("Package: ");
        GridData plgd= new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        plgd.horizontalSpan = 1;
        packageLabel.setLayoutData(plgd);

        final Text pkg = new Text(composite, SWT.SINGLE | SWT.BORDER);
        GridData pgd= new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        pgd.horizontalSpan = 2;
        pgd.grabExcessHorizontalSpace = true;
        pkg.setLayoutData(pgd);
        pkg.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                String packageName = pkg.getText();
                packageFragment = sourceDir.getPackageFragment(packageName);
                setPageComplete(isComplete());
            }
        });
        
        if (packageFragment!=null) {
            String pkgName = packageFragment.getElementName();
            pkg.setText(pkgName);
        }
        
        Button selectPackage = new Button(composite, SWT.PUSH);
        selectPackage.setText("Browse...");
        GridData spgd= new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        spgd.horizontalSpan = 1;
        selectPackage.setLayoutData(spgd);
        selectPackage.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (sourceDir!=null) {
                    PackageSelectionDialog dialog = new PackageSelectionDialog(getShell(), sourceDir);
                    dialog.setMultipleSelection(false);
                    dialog.setTitle("Package Selection");
                    dialog.setMessage("Select a package:");
                    dialog.open();
                    Object result = dialog.getFirstResult();
                    if (result!=null) {
                        String packageName = ((IPackageFragment) result).getElementName();
                        pkg.setText(packageName);
                        setPageComplete(isComplete());
                    }
                }
            }
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {}
        });
        
        Label sep = new Label(composite, SWT.SEPARATOR | SWT.HORIZONTAL);
        GridData sgd= new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        sgd.horizontalSpan = 4;
        sep.setLayoutData(sgd);
        
        Label nameLabel = new Label(composite, SWT.LEFT | SWT.WRAP);
        nameLabel.setText("Compilation unit name: ");
        GridData lgd= new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        lgd.horizontalSpan = 1;
        nameLabel.setLayoutData(lgd);

        final Text name = new Text(composite, SWT.SINGLE | SWT.BORDER);
        GridData ngd= new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        ngd.horizontalSpan = 2;
        ngd.grabExcessHorizontalSpace = true;
        name.setLayoutData(ngd);
        name.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                unitName = name.getText();
                setPageComplete(isComplete());
            }
        });
        
        new Label(composite, SWT.NONE);        
        new Label(composite, SWT.NONE);
        
        Button includeHeader = new Button(composite, SWT.CHECK);
        includeHeader.setText("Include preamble in 'header.ceylon' in project root directory");
        includeHeader.setSelection(includePreamble);
        includeHeader.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                includePreamble = !includePreamble;
            }
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {}
        });
        
        setControl(composite);

        Dialog.applyDialogFont(composite);
    }

    public void initFromSelection() {
        IJavaElement je = getSelectedElement();
        if (je instanceof IPackageFragmentRoot) {
            sourceDir = (IPackageFragmentRoot) je;
        }
        else if (je instanceof IPackageFragment) {
            packageFragment = (IPackageFragment) je;
            sourceDir = (IPackageFragmentRoot) packageFragment.getAncestor(PACKAGE_FRAGMENT_ROOT);
        }
    }
    
    public void init(IStructuredSelection selection) {
        this.selection = selection;
    }
    
    private boolean isComplete() {
        return packageFragment!=null &&
                sourceDir!=null &&
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
    
}
