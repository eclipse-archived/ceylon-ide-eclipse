package com.redhat.ceylon.eclipse.imp.wizard;

import static com.redhat.ceylon.eclipse.ui.ICeylonResources.CEYLON_NEW_FILE;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.redhat.ceylon.eclipse.ui.CeylonPlugin;

public class NewSubtypeWizardPage extends WizardPage implements IWizardPage {

    private String className;
    
    public NewSubtypeWizardPage(String title, String className) {
        super(title, title, CeylonPlugin.getInstance()
                .getImageRegistry().getDescriptor(CEYLON_NEW_FILE));
        this.className = className;
        setDescription("Enter a name for the new class.");
    }
    
    public String getClassName() {
        return className;
    }

    @Override
    public void createControl(Composite parent) {
        initializeDialogUnits(parent);
        
        Composite composite = new Composite(parent, SWT.NONE);
        composite.setFont(parent.getFont());

        GridLayout layout = new GridLayout();
        layout.numColumns = 4;
        composite.setLayout(layout);
        
        Label nameLabel = new Label(composite, SWT.LEFT | SWT.WRAP);
        nameLabel.setText("Class name: ");
        GridData lgd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        lgd.horizontalSpan = 1;
        nameLabel.setLayoutData(lgd);

        final Text name = new Text(composite, SWT.SINGLE | SWT.BORDER);
        GridData ngd= new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        ngd.horizontalSpan = 2;
        ngd.grabExcessHorizontalSpace = true;
        name.setLayoutData(ngd);
        name.setText(className);
        name.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                className = name.getText();
                getNextPage().setUnitName(className);
                getNextPage().getUnitNameText().setText(className);
                boolean legal = classNameIsLegal(className);
                if (legal) {
                    setErrorMessage(null);
                }
                else {
                    setErrorMessage("Please enter a legal type name.");
                }
                setPageComplete(legal);
            }

        });
        
        new Label(composite, SWT.NONE);
        //new Label(composite, SWT.NONE); 
        
        /*Label createLabel = new Label(composite, SWT.LEFT | SWT.WRAP);
        createLabel.setText("Create: ");
        GridData clgd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        clgd.horizontalSpan = 1;
        createLabel.setLayoutData(clgd);

        Button cbut = new Button(composite, SWT.RADIO);
        cbut.setText("Class");
        cbut.setSelection(true);
        
        Button cbut = new Button(composite, SWT.RADIO);
        cbut.setText("Object");
        cbut.setSelection(true);
        
        Button ibut = new Button(composite, SWT.RADIO);
        ibut.setText("Interface");*/
        
        setControl(composite);

        Dialog.applyDialogFont(composite);
    }

    public NewUnitWizardPage getNextPage() {
        return (NewUnitWizardPage) super.getNextPage();
    }
    
    boolean classNameIsLegal(String className) {
        return className.matches("^[A-Z]\\w*$");
    }

}
