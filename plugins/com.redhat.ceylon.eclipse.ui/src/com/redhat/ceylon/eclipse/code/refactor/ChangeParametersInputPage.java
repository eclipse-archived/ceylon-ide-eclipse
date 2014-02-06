package com.redhat.ceylon.eclipse.code.refactor;


import static com.redhat.ceylon.eclipse.code.complete.CodeCompletions.getDescriptionFor;
import static org.eclipse.swt.layout.GridData.HORIZONTAL_ALIGN_FILL;
import static org.eclipse.swt.layout.GridData.VERTICAL_ALIGN_BEGINNING;

import java.util.List;

import org.eclipse.ltk.ui.refactoring.UserInputWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.Functional;
import com.redhat.ceylon.compiler.typechecker.model.Parameter;
import com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider;

public class ChangeParametersInputPage extends UserInputWizardPage {
    
	public ChangeParametersInputPage(String name) {
		super(name);
	}
	
	public void createControl(Composite parent) {
		Composite result = new Composite(parent, SWT.NONE);
		setControl(result);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		result.setLayout(layout);
		Label title = new Label(result, SWT.LEFT);  
		Declaration dec = getRenameRefactoring().getDeclaration();
        title.setText("Change parameters of " + getRenameRefactoring().getCount() + 
				" occurrences of '" + 
				dec.getName() + "'");
		GridData gd = new GridData();
		gd.horizontalSpan=2;
		title.setLayoutData(gd);

        Composite composite = new Composite(result, SWT.NONE);
        GridData cgd = new GridData(GridData.FILL_HORIZONTAL|GridData.FILL_VERTICAL);
        cgd.grabExcessHorizontalSpace = true;
        cgd.grabExcessVerticalSpace = true;
        composite.setLayoutData(cgd);
        GridLayout tableLayout = new GridLayout(3, true);
        tableLayout.marginWidth=0;
        composite.setLayout(tableLayout);
        
        final Table parameters = new Table(composite, SWT.SINGLE | SWT.V_SCROLL | SWT.BORDER);
        GridData tgd = new GridData(GridData.FILL_HORIZONTAL|GridData.FILL_VERTICAL);
        tgd.horizontalSpan=2;
        tgd.verticalSpan=4;
        tgd.grabExcessHorizontalSpace = true;
//        gd.grabExcessVerticalSpace = true;
        tgd.heightHint = 100;
        tgd.widthHint = 250;
        parameters.setLayoutData(tgd);
        
		Functional fun = (Functional) dec;
		for (Parameter p: fun.getParameterLists().get(0).getParameters()) {
		    TableItem item = new TableItem(parameters, SWT.NONE);
		    item.setImage(CeylonLabelProvider.getImageForDeclaration(p.getModel()));
		    item.setText(getDescriptionFor(p.getModel()));
		}

        Button upButton = new Button(composite, SWT.PUSH);
        upButton.setText("Up");
        GridData bgd = new GridData(VERTICAL_ALIGN_BEGINNING|HORIZONTAL_ALIGN_FILL);
        bgd.grabExcessHorizontalSpace=false;
        bgd.widthHint = 50;
        upButton.setLayoutData(bgd);
        upButton.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                int[] indices = parameters.getSelectionIndices();
                if (indices.length>0 && indices[0]>0) {
                    int index = indices[0];
                    TableItem selection = parameters.getItem(index);
                    Image image = selection.getImage();
                    String text = selection.getText();
                    parameters.remove(indices);
                    TableItem item = new TableItem(parameters, SWT.NONE, index-1);
                    item.setImage(image);
                    item.setText(text);
                    parameters.select(index-1);
                    List<Integer> order = getRenameRefactoring().getOrder();
                    order.add(index-1, order.remove(index));
                }
            }
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {}
        });
        
        Button downButton = new Button(composite, SWT.PUSH);
        downButton.setText("Down");
        downButton.setLayoutData(bgd);
        downButton.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                int[] indices = parameters.getSelectionIndices();
                int lastIndex = parameters.getItemCount()-1;
                if (indices.length>0 && indices[0]<lastIndex) {
                    int index = indices[0];
                    TableItem selection = parameters.getItem(index);
                    Image image = selection.getImage();
                    String text = selection.getText();
                    parameters.remove(indices);
                    TableItem item = new TableItem(parameters, SWT.NONE, index+1);
                    item.setImage(image);
                    item.setText(text);
                    parameters.select(index+1);
                    List<Integer> order = getRenameRefactoring().getOrder();
                    order.add(index+1, order.remove(index));
                }
            }
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {}
        });

	}
	
	private ChangeParametersRefactoring getRenameRefactoring() {
		return (ChangeParametersRefactoring) getRefactoring();
	}
	
}
