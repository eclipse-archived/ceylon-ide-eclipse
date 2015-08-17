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

import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.ide.common.util.Escaping;
import com.redhat.ceylon.model.typechecker.model.Declaration;

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
        Declaration dm = ((Tree.Declaration) getMoveOutRefactoring().node)
                .getDeclarationModel();
        Label desc = new Label(result, SWT.RIGHT);
        GridData gd = new GridData();
        gd.horizontalSpan=2;
        desc.setLayoutData(gd);
        desc.setText("Move '" + 
                dm.getName() + 
                "()' out of the type '" + 
                ((Declaration) dm.getContainer()).getName() + 
               "', and add a parameter.");
        GridData gd2 = new GridData(GridData.FILL_HORIZONTAL);
        gd2.horizontalSpan=2;
        new Label(result, SWT.SEPARATOR|SWT.HORIZONTAL).setLayoutData(gd2);
        Label label = new Label(result, SWT.RIGHT);  
        label.setText("Parameter name: ");
        final Text text = new Text(result, SWT.SINGLE|SWT.BORDER);
        text.setText(getMoveOutRefactoring().getNewName());
        text.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
        text.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent event) {
                String name = text.getText();
                validateIdentifier(name);
                getMoveOutRefactoring().setNewName(name);
            }
        });
        final Button checkbox = new Button(result, SWT.CHECK);
        checkbox.setText("Make referenced locals shared");
        checkbox.setSelection(true);
        checkbox.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                getMoveOutRefactoring().setMakeShared();
            }
            @Override
            public void widgetDefaultSelected(SelectionEvent event) {}
        });
        final Button delegate = new Button(result, SWT.CHECK);
        delegate.setText("Leave original as delegate");
        delegate.setSelection(false);
        delegate.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                getMoveOutRefactoring().setLeaveDelegate();
            }
            @Override
            public void widgetDefaultSelected(SelectionEvent event) {}
        });
        delegate.setEnabled(getMoveOutRefactoring().isMethod());
        text.addKeyListener(new SubwordIterator(text));
        text.selectAll();
        text.setFocus();
    }

    private MoveOutRefactoring getMoveOutRefactoring() {
        return (MoveOutRefactoring) getRefactoring();
    }

    void validateIdentifier(String name) {
        if (!name.matches("^[a-z_]\\w*$")) {
            setErrorMessage("Not a legal Ceylon identifier");
            setPageComplete(false);
        }
        else if (Escaping.KEYWORDS.contains(name)) {
            setErrorMessage("'" + name + "' is a Ceylon keyword");
            setPageComplete(false);
        }
        else {
            setErrorMessage(null);
            setPageComplete(true);
        }
    }
    
}
