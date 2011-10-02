package com.redhat.ceylon.eclipse.imp.wizard;

import static com.redhat.ceylon.eclipse.ui.ICeylonResources.CEYLON_NEW_FILE;

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

import com.redhat.ceylon.compiler.typechecker.model.Package;
import com.redhat.ceylon.eclipse.ui.CeylonPlugin;

public class NewUnitWizardPage extends WizardPage implements IWizardPage {

    private String packageName="";
    private String unitName="";
    
    protected NewUnitWizardPage() {
        super("New Ceylon Unit", "New Ceylon Unit", CeylonPlugin.getInstance()
                .getImageRegistry().getDescriptor(CEYLON_NEW_FILE));
        setDescription("Create a new Ceylon compilation unit that will contain Ceylon source.");
    }

    @Override
    public void createControl(Composite parent) {
        initializeDialogUnits(parent);

        Composite composite= new Composite(parent, SWT.NONE);
        composite.setFont(parent.getFont());

        GridLayout layout = new GridLayout();
        layout.numColumns = 4;
        composite.setLayout(layout);
        
        Label nameLabel = new Label(composite, SWT.LEFT | SWT.WRAP);
        nameLabel.setText("Compilation unit name: ");
        GridData lgd= new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        lgd.horizontalSpan = 1;
        nameLabel.setLayoutData(lgd);

        final Text name = new Text(composite, SWT.SINGLE | SWT.BORDER);
        GridData ngd= new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        ngd.horizontalSpan = 3;
        ngd.grabExcessHorizontalSpace = true;
        name.setLayoutData(ngd);
        name.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                NewUnitWizardPage.this.unitName = name.getText();
                setPageComplete(isComplete());
            }
        });
        
        Label packageLabel = new Label(composite, SWT.LEFT | SWT.WRAP);
        packageLabel.setText("Package: ");
        GridData plgd= new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        plgd.horizontalSpan = 1;
        nameLabel.setLayoutData(plgd);

        final Text pkg = new Text(composite, SWT.SINGLE | SWT.BORDER);
        GridData pgd= new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        pgd.horizontalSpan = 2;
        pgd.grabExcessHorizontalSpace = true;
        pkg.setLayoutData(pgd);
        pkg.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                NewUnitWizardPage.this.packageName = pkg.getText();
                setPageComplete(isComplete());
            }
        });
        
        Button selectPackage = new Button(composite, SWT.PUSH);
        selectPackage.setText("Browse...");
        GridData spgd= new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        spgd.horizontalSpan = 1;
        selectPackage.setLayoutData(spgd);
        selectPackage.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                PackageSelectionDialog dialog = new PackageSelectionDialog(getShell());
                dialog.setMultipleSelection(false);
                dialog.setTitle("Package Selection");
                dialog.setMessage("Select a package:");
                dialog.open();
                Object result = dialog.getFirstResult();
                String pn = ((Package) result).getQualifiedNameString();
                pkg.setText(pn);
                NewUnitWizardPage.this.packageName = pn;
                setPageComplete(isComplete());
            }
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {}
        });
        
        setControl(composite);

        Dialog.applyDialogFont(composite);
    }
    
    public String getPackageName() {
        return packageName;
    }
    
    public String getUnitName() {
        return unitName;
    }

    public void init(IStructuredSelection selection) {
        // TODO
    }
    
    @Override
    public boolean canFlipToNextPage() {
        // TODO Auto-generated method stub
        return super.canFlipToNextPage();
    }
    
    private boolean isComplete() {
        return !packageName.equals("")
                && !unitName.equals("");
    }
    
}
