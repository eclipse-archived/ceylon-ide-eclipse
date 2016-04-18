package com.redhat.ceylon.eclipse.code.refactor;

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

import com.redhat.ceylon.ide.common.util.escaping_;

public class ExtractFunctionInputPage extends UserInputWizardPage {
    public ExtractFunctionInputPage(String name) {
        super(name);
    }

    public void createControl(Composite parent) {
        Composite result = new Composite(parent, SWT.NONE);
        setControl(result);
        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        result.setLayout(layout);
        Label label = new Label(result, SWT.RIGHT);  
        label.setText("Function name: ");
        final Text text = new Text(result, SWT.SINGLE|SWT.BORDER);
        text.setText(getExtractFunctionRefactoring().getNewName());
        text.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
        text.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent event) {
                String name = text.getText();
                validateIdentifier(name);
                getExtractFunctionRefactoring().setNewName(name);
            }
        });
        final Button checkbox = new Button(result, SWT.CHECK);
        checkbox.setText("Use explicit type declaration");
        checkbox.setSelection(getExtractFunctionRefactoring().getExplicitType());
        checkbox.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                getExtractFunctionRefactoring().setExplicitType();
            }
            @Override
            public void widgetDefaultSelected(SelectionEvent event) {}
        });
        text.addKeyListener(new SubwordIterator(text));
        text.selectAll();
        text.setFocus();
    }

    private EclipseExtractFunctionRefactoring getExtractFunctionRefactoring() {
        return (EclipseExtractFunctionRefactoring) getRefactoring();
    }

    void validateIdentifier(String name) {
        if (!name.matches("^[a-z_]\\w*$")) {
            setErrorMessage("Not a legal Ceylon identifier");
            setPageComplete(false);
        }
        else if (escaping_.get_().isKeyword(name)) {
            setErrorMessage("'" + name + "' is a Ceylon keyword");
            setPageComplete(false);
        }
        else {
            setErrorMessage(null);
            setPageComplete(true);
        }
    }

}
