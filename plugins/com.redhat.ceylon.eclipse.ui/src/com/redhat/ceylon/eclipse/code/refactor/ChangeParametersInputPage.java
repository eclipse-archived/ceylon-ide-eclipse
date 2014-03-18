package com.redhat.ceylon.eclipse.code.refactor;


import static com.redhat.ceylon.eclipse.code.complete.CodeCompletions.getStyledDescriptionFor;
import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.getImageForDeclaration;
import static com.redhat.ceylon.eclipse.ui.CeylonResources.ADD_CORR;
import static com.redhat.ceylon.eclipse.ui.CeylonResources.REMOVE_CORR;
import static org.eclipse.jface.viewers.ArrayContentProvider.getInstance;
import static org.eclipse.swt.layout.GridData.HORIZONTAL_ALIGN_FILL;
import static org.eclipse.swt.layout.GridData.VERTICAL_ALIGN_BEGINNING;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerCell;
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

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.MethodOrValue;
import com.redhat.ceylon.compiler.typechecker.model.Parameter;

public class ChangeParametersInputPage extends UserInputWizardPage {
    
    public ChangeParametersInputPage(String name) {
        super(name);
    }
    
    private boolean isDefaulted(List<Parameter> parameterModels, 
            Parameter parameter) {
        return getChangeParametersRefactoring().getDefaulted()
                .get(parameterModels.indexOf(parameter));
    }
    public void createControl(Composite parent) {
        final ChangeParametersRefactoring refactoring = 
                getChangeParametersRefactoring();
        
        Composite result = new Composite(parent, SWT.NONE);
        setControl(result);
        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        result.setLayout(layout);
        Label title = new Label(result, SWT.LEFT);  
        Declaration dec = refactoring.getDeclaration();
        title.setText("Change parameters in " + 
                refactoring.getCount() + 
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
        
        final List<Parameter> parameterModels = 
                new ArrayList<Parameter>(refactoring.getParameters());
        
        final TableViewer viewer = new TableViewer(composite, 
                SWT.SINGLE | SWT.V_SCROLL | SWT.BORDER);
        viewer.setContentProvider(getInstance());
        viewer.getTable().setHeaderVisible(true);
        viewer.getTable().setLinesVisible(true);
        GridData tgd = new GridData(GridData.FILL_HORIZONTAL|GridData.FILL_VERTICAL);
        tgd.horizontalSpan=2;
        tgd.verticalSpan=4;
        tgd.grabExcessHorizontalSpace = true;
//        gd.grabExcessVerticalSpace = true;
        tgd.heightHint = 100;
        tgd.widthHint = 380;
        viewer.getTable().setLayoutData(tgd);
        TableViewerColumn col0 = new TableViewerColumn(viewer, SWT.LEFT);
        col0.getColumn().setText("Parameter");
        col0.getColumn().setWidth(220);
        col0.setLabelProvider(new StyledCellLabelProvider() {
            @Override
            public void update(ViewerCell cell) {
                Parameter p = (Parameter) cell.getElement();
                MethodOrValue model = p.getModel();
                StyledString styledText = getStyledDescriptionFor(model);
                cell.setImage(getImageForDeclaration(model));
                cell.setText(styledText.toString());
                cell.setStyleRanges(styledText.getStyleRanges());
                super.update(cell);
            }
        });
        TableViewerColumn col3 = new TableViewerColumn(viewer, SWT.LEFT);
        col3.getColumn().setText("Optionality");
        col3.getColumn().setWidth(70);
        col3.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                return isDefaulted(parameterModels, (Parameter) element) ?
                        "defaulted" : "required"; 
            }
            @Override
            public Image getImage(Object element) {
                return null;
            }
        });
        final String[] options = new String[] {"required", "defaulted"};
        col3.setEditingSupport(new EditingSupport(viewer){
            @Override
            protected CellEditor getCellEditor(Object element) {
                return new ComboBoxCellEditor(viewer.getTable(), options, SWT.FLAT);
            }
            @Override
            protected boolean canEdit(Object element) {
                return true;
            }
            @Override
            protected Object getValue(Object element) {
                return isDefaulted(parameterModels, (Parameter) element) ? 1 : 0;
            }
            @Override
            protected void setValue(Object element, Object value) {
                refactoring.getDefaulted().set(parameterModels.indexOf(element), 
                        value.equals(1));
                viewer.update(element, null);
            } 
        });
        TableViewerColumn col1 = new TableViewerColumn(viewer, SWT.LEFT);
        col1.getColumn().setText("Default Argument");
        col1.getColumn().setWidth(100);
        col1.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                Parameter p = (Parameter) element;
                boolean def = isDefaulted(parameterModels, p);
                if (p.isDefaulted()) {
                    return def ? "" : "inline default";
                }
                else {
                    return def ? "add default" : "";
                }
            }
            @Override
            public Image getImage(Object element) {
                Parameter p = (Parameter) element;
                boolean def = isDefaulted(parameterModels, p);
                if (p.isDefaulted()) {
                    return  def ? null : REMOVE_CORR;
                }
                else {
                    return def ? ADD_CORR : null;
                }
            }
        });
        
        viewer.setInput(parameterModels);
        
        Button upButton = new Button(composite, SWT.PUSH);
        upButton.setText("Up");
        GridData bgd = new GridData(VERTICAL_ALIGN_BEGINNING|HORIZONTAL_ALIGN_FILL);
        bgd.grabExcessHorizontalSpace=false;
        bgd.widthHint = 50;
        upButton.setLayoutData(bgd);
        upButton.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                int[] indices = viewer.getTable().getSelectionIndices();
                if (indices.length>0 && indices[0]>0) {
                    int index = indices[0];
                    parameterModels.add(index-1, parameterModels.remove(index));
                    List<Integer> order = refactoring.getOrder();
                    order.add(index-1, order.remove(index));
                    List<Boolean> defaulted = refactoring.getDefaulted();
                    defaulted.add(index-1, defaulted.remove(index));
                    viewer.refresh();
                    viewer.getTable().select(index-1);
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
                int[] indices = viewer.getTable().getSelectionIndices();
                int lastIndex = viewer.getTable().getItemCount()-1;
                if (indices.length>0 && indices[0]<lastIndex) {
                    int index = indices[0];
                    parameterModels.add(index+1, parameterModels.remove(index));
                    List<Integer> order = refactoring.getOrder();
                    order.add(index+1, order.remove(index));
                    List<Boolean> defaulted = refactoring.getDefaulted();
                    defaulted.add(index+1, defaulted.remove(index));
                    viewer.refresh();
                    viewer.getTable().select(index+1);
                }
            }
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {}
        });
        
        new Label(composite, SWT.NONE);

        final Button toggleButton = new Button(composite, SWT.PUSH);
        toggleButton.setText("Toggle Optionality");
        toggleButton.setLayoutData(bgd);
        toggleButton.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                int[] indices = viewer.getTable().getSelectionIndices();
                if (indices.length>0) {
                    int index = indices[0];
                    List<Boolean> defaultedList = refactoring.getDefaulted();
                    defaultedList.set(index, !defaultedList.get(index));
                    viewer.refresh();
                }
            }
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {}
        });
        
        if (parameterModels.isEmpty()) {
            toggleButton.setEnabled(false);
        }
        else {
            viewer.getTable().setSelection(0);
        }
        if (parameterModels.size()<2) {
            upButton.setEnabled(false);
            downButton.setEnabled(false);
        }
    }
    
    private ChangeParametersRefactoring getChangeParametersRefactoring() {
        return (ChangeParametersRefactoring) getRefactoring();
    }
    
}
