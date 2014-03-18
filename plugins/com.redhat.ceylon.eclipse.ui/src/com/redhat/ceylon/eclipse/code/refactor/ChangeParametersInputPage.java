package com.redhat.ceylon.eclipse.code.refactor;


import static com.redhat.ceylon.eclipse.code.complete.CodeCompletions.getDescriptionFor;
import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.getImageForDeclaration;
import static com.redhat.ceylon.eclipse.ui.CeylonResources.MINOR_CHANGE;
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
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.Functional;
import com.redhat.ceylon.compiler.typechecker.model.Parameter;

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
        Declaration dec = getChangeParametersRefactoring().getDeclaration();
        title.setText("Change parameters of " + getChangeParametersRefactoring().getCount() + 
                " occurrences of '" + dec.getName() + "'.");
        GridData gd = new GridData();
        gd.horizontalSpan=2;
        title.setLayoutData(gd);
        GridData gd2 = new GridData(GridData.FILL_HORIZONTAL);
        gd2.horizontalSpan=2;
        new Label(result, SWT.SEPARATOR|SWT.HORIZONTAL).setLayoutData(gd2);

        Composite composite = new Composite(result, SWT.NONE);
        GridData cgd = new GridData(GridData.FILL_HORIZONTAL|GridData.FILL_VERTICAL);
        cgd.grabExcessHorizontalSpace = true;
        cgd.grabExcessVerticalSpace = true;
        composite.setLayoutData(cgd);
        GridLayout tableLayout = new GridLayout(3, true);
        tableLayout.marginWidth=0;
        composite.setLayout(tableLayout);
        
        final Table parameters = new Table(composite, SWT.SINGLE | SWT.V_SCROLL | SWT.BORDER);
        parameters.setHeaderVisible(true);
        parameters.setLinesVisible(true);
        GridData tgd = new GridData(GridData.FILL_HORIZONTAL|GridData.FILL_VERTICAL);
        tgd.horizontalSpan=2;
        tgd.verticalSpan=4;
        tgd.grabExcessHorizontalSpace = true;
//        gd.grabExcessVerticalSpace = true;
        tgd.heightHint = 100;
        tgd.widthHint = 300;
        parameters.setLayoutData(tgd);
        TableColumn col0 = new TableColumn(parameters, SWT.LEFT);
        col0.setText("Parameter");
        col0.setWidth(220);
        TableColumn col1 = new TableColumn(parameters, SWT.LEFT);
        col1.setText("Default Value");
        col1.setWidth(100);
        
        Functional fun = (Functional) dec;
        for (Parameter p: fun.getParameterLists().get(0).getParameters()) {
            TableItem item = new TableItem(parameters, SWT.NONE);
            item.setImage(0,getImageForDeclaration(p.getModel()));
            item.setText(0,getDescriptionFor(p.getModel()));
//            item.setImage(1, CeylonPlugin.getInstance().image(p.isDefaulted()?"enabled_co.gif":"disabled_co.gif").createImage());
            item.setText(1, p.isDefaulted()?"keep":"");
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
                    Image image1 = selection.getImage(1);
                    String text1 = selection.getText(1);
                    parameters.remove(indices);
                    TableItem item = new TableItem(parameters, SWT.NONE, index-1);
                    item.setImage(image);
                    item.setText(text);
                    item.setText(1,text1);
                    item.setImage(1,image1);
                    parameters.select(index-1);
                    List<Integer> order = getChangeParametersRefactoring().getOrder();
                    order.add(index-1, order.remove(index));
                    List<Boolean> defaulted = getChangeParametersRefactoring().getDefaulted();
                    defaulted.add(index-1, defaulted.remove(index));
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
                    Image image1 = selection.getImage(1);
                    String text1 = selection.getText(1);
                    parameters.remove(indices);
                    TableItem item = new TableItem(parameters, SWT.NONE, index+1);
                    item.setImage(image);
                    item.setText(text);
                    item.setText(1,text1);
                    item.setImage(1,image1);
                    parameters.select(index+1);
                    List<Integer> order = getChangeParametersRefactoring().getOrder();
                    order.add(index+1, order.remove(index));
                    List<Boolean> defaulted = getChangeParametersRefactoring().getDefaulted();
                    defaulted.add(index+1, defaulted.remove(index));
                }
            }
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {}
        });

        final Button toggleButton = new Button(composite, SWT.PUSH);
        toggleButton.setText("Toggle Inline");
        toggleButton.setLayoutData(bgd);
        toggleButton.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                int[] indices = parameters.getSelectionIndices();
                List<Boolean> defaultedList = getChangeParametersRefactoring().getDefaulted();
                int index=indices[0];
                Boolean inlining = defaultedList.get(index);
                defaultedList.set(index,!inlining);
                TableItem selection = parameters.getItem(index);
                String text = selection.getText(1);
                if (text==null || text.isEmpty()) {
                    selection.setText(1, "add");
                    selection.setImage(1, MINOR_CHANGE);
                }
                else if (text.equals("add")) {
                    selection.setText(1, "");
                    selection.setImage(1, null);
                }
                else if (text.equals("keep")) {
                    selection.setText(1, "inline");
                    selection.setImage(1, MINOR_CHANGE);
                }
                else if (text.equals("inline"))  {
                    selection.setText(1, "keep");
                    selection.setImage(1, null);
                }
            }
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {}
        });
        
        parameters.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                TableItem tableItem = (TableItem) e.item;
                String text = tableItem.getText(1);
                if (text==null || text.isEmpty() || text.equals("add")) {
                    toggleButton.setText("Toggle Add");
                }
                else if (text.equals("keep") || text.equals("inline")) {
                    toggleButton.setText("Toggle Inline");
                }
            }
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {}
        });
        
        if (parameters.getItemCount()==0) {
            toggleButton.setEnabled(false);
        }
        else {
            boolean required = parameters.getItem(0).getText(1).isEmpty();
            toggleButton.setText(required?"Toggle Add":"Toggle Inline");
            parameters.setSelection(0);
        }
        if (parameters.getItemCount()<2) {
            upButton.setEnabled(false);
            downButton.setEnabled(false);
        }
    }
    
    private ChangeParametersRefactoring getChangeParametersRefactoring() {
        return (ChangeParametersRefactoring) getRefactoring();
    }
    
}
