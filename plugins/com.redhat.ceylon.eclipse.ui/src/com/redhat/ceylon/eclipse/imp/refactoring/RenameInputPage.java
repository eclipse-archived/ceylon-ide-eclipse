package com.redhat.ceylon.eclipse.imp.refactoring;

import org.eclipse.ltk.ui.refactoring.UserInputWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;

public class RenameInputPage extends UserInputWizardPage {
	public RenameInputPage(String name) {
		super(name);
	}

	/**
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite parent) {
		Declaration dec = getRenameRefactoring().getDeclaration();
		Composite result = new Composite(parent, SWT.NONE);
		setControl(result);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		result.setLayout(layout);
		Label title = new Label(result, SWT.LEFT);  
		title.setText("Rename " + getRenameRefactoring().getCount() + 
				" occurrences of declaration '" + 
				getRenameRefactoring().getDeclaration().getName() + "'");
		GridData gd = new GridData();
		gd.horizontalSpan=2;
		title.setLayoutData(gd);
		Label label = new Label(result, SWT.RIGHT);  
		label.setText("Rename to: ");
		final Text text = new Text(result, SWT.SINGLE|SWT.BORDER);
		text.setText(dec.getName());
		text.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		text.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent event) {
			    String name = text.getText();
                validateIdentifier(name);
				getRenameRefactoring().setNewName(name);
			}
		});
	}
	
	private RenameRefactoring getRenameRefactoring() {
		return (RenameRefactoring) getRefactoring();
	}
	
    void validateIdentifier(String name) {
        if (!name.matches("^[a-zA-Z_]\\w*$")) {
            setErrorMessage("Not a legal Ceylon identifier");
            setPageComplete(false);
        }
        else {
            setErrorMessage(null);
            setPageComplete(true);
        }
    }

}
