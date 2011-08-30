package com.redhat.ceylon.eclipse.imp.refactoring;

import org.eclipse.ltk.ui.refactoring.UserInputWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class ConvertToNamedArgumentsInputPage extends UserInputWizardPage {
	public ConvertToNamedArgumentsInputPage(String name) {
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
		Label title = new Label(result, SWT.RIGHT);  
		title.setText("Convert positional argument list to named argument list");
		GridData gd = new GridData();
		gd.horizontalSpan=2;
		title.setLayoutData(gd);
	}

	/*private ConvertToNamedArgumentsRefactoring getConvertToNamedArgumentsRefactoring() {
		return (ConvertToNamedArgumentsRefactoring) getRefactoring();
	}*/
}
