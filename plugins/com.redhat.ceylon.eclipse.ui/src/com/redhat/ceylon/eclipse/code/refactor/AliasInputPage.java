package com.redhat.ceylon.eclipse.code.refactor;


import org.eclipse.ltk.ui.refactoring.UserInputWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.redhat.ceylon.ide.common.util.Escaping;
import com.redhat.ceylon.model.typechecker.model.Type;

public class AliasInputPage extends UserInputWizardPage {
    public AliasInputPage(String name) {
        super(name);
    }

    public void createControl(Composite parent) {
        Composite result = new Composite(parent, SWT.NONE);
        setControl(result);
        GridLayout layout = new GridLayout();
        layout.numColumns = 3;
        result.setLayout(layout);
        Label title = new Label(result, SWT.LEFT);  
        Type type = 
                getAliasRefactoring().getType();
        String name = type.asString();
        title.setText("Create alias for " + getAliasRefactoring().getCount() + 
                " occurrences of type '" + 
                name + "'.");
        GridData gd = new GridData();
        gd.horizontalSpan=3;
        title.setLayoutData(gd);
        GridData gd2 = new GridData(GridData.FILL_HORIZONTAL);
        gd2.horizontalSpan=3;
        new Label(result, SWT.SEPARATOR|SWT.HORIZONTAL).setLayoutData(gd2);
        
        Label label = new Label(result, SWT.RIGHT);  
        label.setText("Alias to: ");
        final Text text = new Text(result, SWT.SINGLE|SWT.BORDER);
        GridData gd3 = new GridData();
        gd3.horizontalSpan=2;
        text.setLayoutData(gd3);
        text.setText(getAliasRefactoring().getInitialName());
        text.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
        text.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent event) {
                String name = text.getText();
                validateIdentifier(name);
                getAliasRefactoring().setNewName(name);
            }
        });
        text.addKeyListener(new SubwordIterator(text));
        text.selectAll();
        text.setFocus();
        
        /*GridData gd4 = new GridData(GridData.FILL_HORIZONTAL);
        gd4.horizontalSpan=3;
        gd4.grabExcessHorizontalSpace=true;

        final Button renameLocals = 
                new Button(result, SWT.CHECK);
        renameLocals.setLayoutData(gd4);
        boolean typeDec = declaration instanceof TypeDeclaration;
        String txt = "Rename similarly-named values and functions";
        if (typeDec) txt += " of type '" + declaration.getName() + "'";
        renameLocals.setText(txt);
        renameLocals.setSelection(getAliasRefactoring().isRenameValuesAndFunctions());
        renameLocals.setEnabled(typeDec);
        renameLocals.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                getAliasRefactoring().setRenameValuesAndFunctions(renameLocals.getSelection());
            }
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {}
        });*/
    }
    
    private AliasRefactoring getAliasRefactoring() {
        return (AliasRefactoring) getRefactoring();
    }
    
    void validateIdentifier(String name) {
        if (!name.matches("^[a-zA-Z_]\\w*$")) {
            setErrorMessage("Not a legal Ceylon identifier");
            setPageComplete(false);
        }
        else if (Escaping.KEYWORDS.contains(name)) {
            setErrorMessage("'" + name + "' is a Ceylon keyword");
            setPageComplete(false);
        }
        else {
            int ch = name.codePointAt(0);
            if (!Character.isUpperCase(ch)) {
                setErrorMessage("Not an initial uppercase identifier");
                setPageComplete(false);
            }
            else {
                setErrorMessage(null);
                setPageComplete(true);
            }
        }
    }

}
