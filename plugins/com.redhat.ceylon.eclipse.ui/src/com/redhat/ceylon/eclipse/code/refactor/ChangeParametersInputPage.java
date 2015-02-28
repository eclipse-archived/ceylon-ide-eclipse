package com.redhat.ceylon.eclipse.code.refactor;


import static com.redhat.ceylon.eclipse.code.complete.CodeCompletions.getStyledDescriptionFor;
import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.getImageForDeclaration;
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
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.window.Window;
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
import com.redhat.ceylon.compiler.typechecker.model.ProducedType;
import com.redhat.ceylon.compiler.typechecker.model.Scope;
import com.redhat.ceylon.compiler.typechecker.model.Value;

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
        GridLayout tableLayout = new GridLayout(4, true);
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
        tgd.horizontalSpan = 3;
        tgd.verticalSpan = 6;
        tgd.grabExcessHorizontalSpace = true;
//        gd.grabExcessVerticalSpace = true;
        tgd.heightHint = 100;
        tgd.widthHint = 425;
        viewer.getTable().setLayoutData(tgd);
        TableViewerColumn orderCol = new TableViewerColumn(viewer, SWT.LEFT);
        orderCol.getColumn().setText("");
        orderCol.getColumn().setWidth(10);
        orderCol.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                final int originalIndex = refactoring.getParameters().indexOf(element);
                final int currentIndex = parameterModels.indexOf(element);
                if (originalIndex==currentIndex) return "";
                return originalIndex>currentIndex ? "\u2191" : "\u2193"; 
            }
            @Override
            public Image getImage(Object element) {
                return null;
            }
        });
        TableViewerColumn sigCol = new TableViewerColumn(viewer, SWT.LEFT);
        sigCol.getColumn().setText("Parameter");
        sigCol.getColumn().setWidth(240);
        sigCol.setLabelProvider(new StyledCellLabelProvider() {
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
        TableViewerColumn optCol = new TableViewerColumn(viewer, SWT.LEFT);
        optCol.getColumn().setText("Optionality");
        optCol.getColumn().setWidth(70);
        optCol.setLabelProvider(new ColumnLabelProvider() {
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
        optCol.setEditingSupport(new EditingSupport(viewer) {
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
        TableViewerColumn diffCol = new TableViewerColumn(viewer, SWT.LEFT);
        diffCol.getColumn().setText("");
        diffCol.getColumn().setWidth(15);
        diffCol.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                Parameter p = (Parameter) element;
                boolean def = isDefaulted(parameterModels, p);
                if (!def && p.isDefaulted()) {
                    return "\u2296";
                }
                if (def && !p.isDefaulted()) {
                    return "\u2295";
                }
                if (def && 
                        refactoring.defaultHasChanged(p)) {
                    return ">";
                }
                return "";
            }
            @Override
            public Image getImage(Object element) {
                /*Parameter p = (Parameter) element;
                boolean def = isDefaulted(parameterModels, p);
                if (p.isDefaulted()) {
                    return  def ? null : REMOVE_CORR;
                }
                else {
                    return def ? ADD_CORR : null;
                }*/
                return null;
            }
        });
        TableViewerColumn argCol = new TableViewerColumn(viewer, SWT.LEFT);
        argCol.getColumn().setText("Default Argument");
        argCol.getColumn().setWidth(100);
        argCol.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                MethodOrValue model = ((Parameter) element).getModel();
                if (isDefaulted(parameterModels, (Parameter) element)) {
                    return refactoring.getDefaultArgs().get(model);
                }
                else {
                    return null;
                }
            }
        });
        argCol.setEditingSupport(new EditingSupport(viewer) {
            @Override
            protected CellEditor getCellEditor(Object element) {
                return new TextCellEditor(viewer.getTable(), SWT.FLAT);
            }
            @Override
            protected boolean canEdit(Object element) {
                return isDefaulted(parameterModels, (Parameter) element);
            }
            @Override
            protected Object getValue(Object element) {
                MethodOrValue model = ((Parameter) element).getModel();
                if (isDefaulted(parameterModels, (Parameter) element)) {
                    String arg = refactoring.getDefaultArgs().get(model);
                    return arg==null ? "" : arg;
                }
                else {
                    return "";
                }
            }
            @Override
            protected void setValue(Object element, Object value) {
                MethodOrValue model = ((Parameter) element).getModel();
                refactoring.getDefaultArgs().put(model, 
                        (String) value);
                viewer.update(element, null);
            } 
        });
        
        viewer.setInput(parameterModels);
        
        final Button upButton = new Button(composite, SWT.PUSH);
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
        
        final Button downButton = new Button(composite, SWT.PUSH);
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
        
        final Button addButton = new Button(composite, SWT.PUSH);
        addButton.setText("Add...");
        addButton.setLayoutData(bgd);
        
        final Button removeButton = new Button(composite, SWT.PUSH);
        removeButton.setText("Remove");
        removeButton.setLayoutData(bgd);
        
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
        
        removeButton.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                int[] indices = viewer.getTable().getSelectionIndices();
                int lastIndex = viewer.getTable().getItemCount()-1;
                if (indices.length>0 && indices[0]<=lastIndex) {
                    int index = indices[0];
                    Parameter p = parameterModels.remove(index);
                    refactoring.getDefaulted().remove(index);
                    refactoring.getOrder().remove(index);
                    viewer.remove(p);
                    viewer.refresh();
                    if (parameterModels.isEmpty()) {
                        toggleButton.setEnabled(false);
                        removeButton.setEnabled(false);
                    }
                    if (parameterModels.size()<2) {
                        upButton.setEnabled(false);
                        downButton.setEnabled(false);
                    }
                }
            }
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {}
        });
        
        addButton.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                AddParameterDialog dialog = new AddParameterDialog(getShell(), 
                        refactoring.node, refactoring.project);
                if (dialog.open()==Window.OK) {
                    String name = dialog.getName();
                    ProducedType type = dialog.getType();
                    String arg = dialog.getArgument();
                    Value model = new Value();
                    model.setType(type);
                    model.setName(name);
                    Scope scope = refactoring.node.getScope();
                    model.setContainer(scope);
                    model.setScope(scope);
                    Parameter p = new Parameter();
                    p.setModel(model);
                    p.setName(name);
                    p.setDefaulted(false);
                    p.setDeclaration((Declaration) scope);
                    model.setInitializerParameter(p);
                    int index = parameterModels.size();
                    int order = refactoring.getParameters().size();
                    parameterModels.add(p);
                    refactoring.getParameters().add(p);
                    refactoring.getDefaulted().add(false);
                    refactoring.getArguments().put(p.getModel(), arg);
                    refactoring.getDefaultArgs().put(model, arg);
                    refactoring.getOrder().add(index, order);
                    viewer.add(p);
                    viewer.refresh();
                    viewer.getTable().select(index);
                    if (index==1) {
                        upButton.setEnabled(true);
                        downButton.setEnabled(true);
                    }
                    if (index==0) {
                        toggleButton.setEnabled(true);
                        removeButton.setEnabled(true);
                    }
                }
            }
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {}
        });
        
        if (parameterModels.isEmpty()) {
            toggleButton.setEnabled(false);
            removeButton.setEnabled(false);
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
