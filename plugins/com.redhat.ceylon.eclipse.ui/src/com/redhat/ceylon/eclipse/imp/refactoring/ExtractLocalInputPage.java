package com.redhat.ceylon.eclipse.imp.refactoring;

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

public class ExtractLocalInputPage extends UserInputWizardPage {
	public ExtractLocalInputPage(String name) {
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
		Label label = new Label(result, SWT.RIGHT);  
		label.setText("Local name: ");
		final Text text = new Text(result, SWT.SINGLE|SWT.BORDER);
		text.setText(getExtractLocalRefactoring().getNewName());
		text.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		text.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent event) {
				getExtractLocalRefactoring().setNewName(text.getText());
			}
		});
		final Button checkbox = new Button(result, SWT.CHECK);
		checkbox.setText("Use explicit type declaration");
		checkbox.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				getExtractLocalRefactoring().setExplicitType();
			}
			@Override
			public void widgetDefaultSelected(SelectionEvent event) {}
		});		
	}

	private ExtractLocalRefactoring getExtractLocalRefactoring() {
		return (ExtractLocalRefactoring) getRefactoring();
	}
}
