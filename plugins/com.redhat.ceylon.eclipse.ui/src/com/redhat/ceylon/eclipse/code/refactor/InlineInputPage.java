package com.redhat.ceylon.eclipse.code.refactor;

import org.eclipse.ltk.ui.refactoring.UserInputWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class InlineInputPage extends UserInputWizardPage {
    public InlineInputPage(String name) {
        super(name);
    }

    /**
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
     */
    public void createControl(Composite parent) {
        Composite result = new Composite(parent, SWT.NONE);
        setControl(result);
        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        result.setLayout(layout);
        Label title = new Label(result, SWT.LEFT);  
        title.setText("Inline " + getInlineRefactoring().getCount() + 
                " occurrences of declaration '" + 
                getInlineRefactoring().getDeclaration().getName() + "'.");
        GridData gd2 = new GridData(GridData.FILL_HORIZONTAL);
        gd2.horizontalSpan=2;
        new Label(result, SWT.SEPARATOR|SWT.HORIZONTAL).setLayoutData(gd2);
        GridData gd = new GridData();
        gd.horizontalSpan=2;
        title.setLayoutData(gd);
        final Button checkbox = new Button(result, SWT.CHECK);
        checkbox.setText("Delete declaration");
        checkbox.setSelection(true);
        checkbox.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                getInlineRefactoring().setDelete();
            }
            @Override
            public void widgetDefaultSelected(SelectionEvent event) {}
        });        
    }

    private InlineRefactoring getInlineRefactoring() {
        return (InlineRefactoring) getRefactoring();
    }
}
