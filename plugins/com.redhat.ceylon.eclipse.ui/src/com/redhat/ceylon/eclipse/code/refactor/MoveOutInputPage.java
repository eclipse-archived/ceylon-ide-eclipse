package com.redhat.ceylon.eclipse.code.refactor;

import org.eclipse.ltk.ui.refactoring.UserInputWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

public class MoveOutInputPage extends UserInputWizardPage {
    public MoveOutInputPage(String name) {
        super(name);
    }

    public void createControl(Composite parent) {
        Composite result = new Composite(parent, SWT.NONE);
        setControl(result);
        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        result.setLayout(layout);
        final Button checkbox = new Button(result, SWT.CHECK);
        checkbox.setText("Make referenced locals shared");
        checkbox.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                getMoveOutRefactoring().setMakeShared();
            }
            @Override
            public void widgetDefaultSelected(SelectionEvent event) {}
        });
    }

    private MoveOutRefactoring getMoveOutRefactoring() {
        return (MoveOutRefactoring) getRefactoring();
    }


}
