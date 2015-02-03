package com.redhat.ceylon.eclipse.code.refactor;

import static com.redhat.ceylon.eclipse.code.select.SourceFileSelectionDialog.selectSourceFile;
import static com.redhat.ceylon.eclipse.code.wizard.WizardUtil.getSelectedJavaElement;
import static org.eclipse.core.resources.ResourcesPlugin.getWorkspace;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.IStructuredSelection;
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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class MoveToUnitWizardPage extends UserInputWizardPage {
    
    private String unitName = "";
    private IFile unit;
    
    private IStructuredSelection selection;
    private Text unitNameText;
    private IFile file;
    
    MoveToUnitWizardPage(String title, IFile file) {
        super(title);
        this.file = file;
    }
    
    @Override
    public void createControl(Composite parent) {
        initializeDialogUnits(parent);
        
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
        createUnitField(composite);
    }

    void createUnitField(Composite composite) {
        
        Label unitLabel = new Label(composite, SWT.LEFT | SWT.WRAP);
        unitLabel.setText(getUnitLabel());
        GridData plgd= new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        plgd.horizontalSpan = 1;
        unitLabel.setLayoutData(plgd);

        final Text unit = new Text(composite, SWT.SINGLE | SWT.BORDER);
        GridData pgd= new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        pgd.horizontalSpan = 2;
        pgd.grabExcessHorizontalSpace = true;
        unit.setLayoutData(pgd);
        unit.setText(getUnitName());
        unit.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                setUnitName(unit.getText());
                if (!unitNameIsLegal()) {
                    setErrorMessage(getIllegalUnitNameMessage());
                }
                else {
                    try {
                        IFile file = getWorkspace().getRoot()
                                .getFile(Path.fromPortableString(unit.getText()));
                        if (file==null || !file.exists()) {
                            setErrorMessage(getUnitNotExistMessage());
                        }
                        else if (file.equals(MoveToUnitWizardPage.this.file)) {
                            setErrorMessage(getSameUnitMessage());
                        }
                        else {
                            setUnit(file);
                            setErrorMessage(null);
                        }
                    }
                    catch (IllegalArgumentException iae) {
                        setErrorMessage(iae.getMessage());
                    }
                }
                setPageComplete(isComplete());
            }
        });
        
        Button selectUnit = new Button(composite, SWT.PUSH);
        selectUnit.setText("Browse...");
        GridData spgd= new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        spgd.horizontalSpan = 1;
        selectUnit.setLayoutData(spgd);
        selectUnit.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                IFile file = selectSourceFile(getShell(), 
                        getSelectedJavaElement(selection));
                if (file!=null) {
                    setUnitName(file.getFullPath()
                            .toPortableString());
                    unit.setText(getUnitName());
                    setUnit(file);
                    setPageComplete(isComplete());
                    if (!file.exists()) {
                        setErrorMessage(getUnitNotExistMessage());
                    }
                    else if (file.equals(MoveToUnitWizardPage.this.file)) {
                        setErrorMessage(getSameUnitMessage());
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

    private void createTitle(Composite composite) {
        Label title = new Label(composite, SWT.LEFT);  
        String name = getMoveToUnitRefactoring().getNode().getDeclarationModel().getName();
        title.setText("Move '" + name + "' to another source file.");
        GridData gd = new GridData();
        gd.horizontalSpan=4;
        title.setLayoutData(gd);
        GridData gd2 = new GridData(GridData.FILL_HORIZONTAL);
        gd2.horizontalSpan=4;
        new Label(composite, SWT.SEPARATOR|SWT.HORIZONTAL).setLayoutData(gd2);
    }
    
    String getUnitLabel() {
        return "Source file: ";
    }
    
    public void init(IStructuredSelection selection) {
        this.selection = selection;
    }

    boolean isComplete() {
        return unitNameIsLegal() && 
                getUnit()!=null &&
                getUnit().exists() &&
                !getUnit().equals(file);
    }

    private boolean unitNameIsLegal() {
        return getUnitName()!=null && 
                unitIsNameLegal(getUnitName());
    }

    boolean unitIsNameLegal(String unitName) {
        return unitName.matches("^((\\w|-)*/)*(\\w|-)+\\.ceylon$");
    }
    
    private String getIllegalUnitNameMessage() {
        return "Please enter a legal compilation unit name.";
    }
    
    private String getUnitNotExistMessage() {
        return "Source file does not exist.";
    }
    
    private String getSameUnitMessage() {
        return "Selected source file is the original source file.";
    }
    
    Text getUnitNameText() {
        return unitNameText;
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
    
    MoveToUnitRefactoring getMoveToUnitRefactoring() {
        return (MoveToUnitRefactoring) getRefactoring();
    }

}
