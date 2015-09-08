package com.redhat.ceylon.eclipse.code.wizard;

import static com.redhat.ceylon.eclipse.ui.CeylonResources.CEYLON_NEW_MODULE;
import static org.eclipse.swt.layout.GridData.HORIZONTAL_ALIGN_FILL;
import static org.eclipse.swt.layout.GridData.VERTICAL_ALIGN_BEGINNING;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import com.redhat.ceylon.common.Versions;
import com.redhat.ceylon.model.typechecker.model.Module;
import com.redhat.ceylon.eclipse.ui.CeylonPlugin;
import com.redhat.ceylon.eclipse.ui.CeylonResources;

public abstract class ImportModulesWizardPage extends WizardPage {
    
    private Table moduleImportsTable;
    
    ImportModulesWizardPage() {
        super("Add Module Imports", 
                "Add Module Imports", 
                CeylonPlugin.imageRegistry()
                        .getDescriptor(CEYLON_NEW_MODULE));
        setDescription("Add module imports to new module.");
    }

    @Override
    public void createControl(Composite parent) {
        initializeDialogUnits(parent);
        
        Composite composite = new Composite(parent, SWT.NONE);
        composite.setFont(parent.getFont());
        
        GridLayout layout = new GridLayout();
        layout.numColumns = 1;
        composite.setLayout(layout);
        
        createModulesBlock(composite);

        setControl(composite);
        
        Dialog.applyDialogFont(composite);
        
        setPageComplete(true);
    }

    private void createModulesBlock(Composite parent) {
        Label label = new Label(parent, SWT.NONE);
        label.setText("Imported modules:");
        GridData lgd = new GridData(VERTICAL_ALIGN_BEGINNING);
        label.setLayoutData(lgd);
        
        Composite composite = new Composite(parent, SWT.NONE);
        GridData cgd = new GridData(GridData.FILL_HORIZONTAL);
        cgd.grabExcessHorizontalSpace = true;
        composite.setLayoutData(cgd);
        GridLayout layout = new GridLayout(3, true);
        layout.marginWidth=0;
        composite.setLayout(layout);
        
        moduleImportsTable = new Table(composite, 
                SWT.MULTI | SWT.V_SCROLL | SWT.BORDER);
        moduleImportsTable.setHeaderVisible(true);
        moduleImportsTable.setLinesVisible(true);
        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.horizontalSpan=2;
        gd.verticalSpan=4;
        gd.grabExcessHorizontalSpace = true;
//        gd.grabExcessVerticalSpace = true;
        gd.heightHint = 100;
        gd.widthHint = 300;
        moduleImportsTable.setLayoutData(gd);
        
        TableColumn column = new TableColumn(moduleImportsTable, SWT.NONE, 0);
        column.setText("Module");
        column.setWidth(180);
        TableColumn versionColumn = new TableColumn(moduleImportsTable, SWT.NONE, 1);
        versionColumn.setText("Version");
        versionColumn.setWidth(70);
        TableColumn sharedColumn = new TableColumn(moduleImportsTable, SWT.NONE, 2);
        sharedColumn.setText("Transitivity");
        sharedColumn.setWidth(70);

        final TableItem item = new TableItem(moduleImportsTable, SWT.NONE);
        item.setImage(CeylonResources.MODULE);
        item.setText(Module.LANGUAGE_MODULE_NAME);
        item.setText(1, Versions.CEYLON_VERSION_NUMBER); //TODO: is this right?
        item.setText(2, "shared");
        
        GridData bgd = new GridData(VERTICAL_ALIGN_BEGINNING|HORIZONTAL_ALIGN_FILL);
        bgd.grabExcessHorizontalSpace=false;
        bgd.widthHint = 50;
        
        Button sharedButton = new Button(composite, SWT.PUSH);
        sharedButton.setText("Toggle Shared");
        sharedButton.setLayoutData(bgd);
        sharedButton.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                for (TableItem item: moduleImportsTable.getSelection()) {
                    item.setText(2, item.getText(2).isEmpty() ? "shared": "");
                }
            }
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {}
        });
        
        Button addButton = new Button(composite, SWT.PUSH);
        addButton.setText("Add Imports...");
        addButton.setLayoutData(bgd);
        addButton.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                selectAndAddModules();
            }
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {}
        });
        
        Button removeButton = new Button(composite, SWT.PUSH);
        removeButton.setText("Remove Selected");
        removeButton.setLayoutData(bgd);
        removeButton.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                removeSelectedModules();
            }
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {}
        });

//        Label l = new Label(parent, SWT.NONE);
//        l.setText("(Checked modules are exported.)");
//        label.setLayoutData(lgd);
    }

    private void selectAndAddModules() {
        Map<String, String> added = getModules();
        for (Map.Entry<String, String> entry: added.entrySet()) {
            TableItem item = new TableItem(moduleImportsTable, SWT.NONE);
            item.setImage(CeylonResources.MODULE);
            item.setText(entry.getKey());
            item.setText(1, entry.getValue());
        }
    }

    abstract Map<String, String> getModules();

    private void removeSelectedModules() {
        int[] selection = moduleImportsTable.getSelectionIndices();
        List<String> names = new ArrayList<String>();
        List<Integer> removed = new ArrayList<Integer>();
        for (int index: selection) {
            TableItem item = moduleImportsTable.getItem(index);
            String name = item.getText();
            if (!name.equals(Module.LANGUAGE_MODULE_NAME)) {
                names.add(name);
                removed.add(index);
            }
        }
        int[] indices = new int[removed.size()];
        for (int i=0; i<removed.size(); i++) {
            indices[i] = removed.get(i);
        }
        moduleImportsTable.remove(indices);
    }
    
    Map<String,String> getImports() {
        Map<String,String> result = new HashMap<String,String>();
        for (TableItem item: moduleImportsTable.getItems()) {
            String name = item.getText();
            String version = item.getText(1);
            if (!name.equals(Module.LANGUAGE_MODULE_NAME)) {
                result.put(name, version);
            }
        }
        return result;
    }
    
    Set<String> getSharedImports() {
        Set<String> result = new HashSet<String>();
        for (TableItem item: moduleImportsTable.getItems()) {
            String name = item.getText();
            boolean shared = !item.getText(2).isEmpty();
            if (shared && !name.equals(Module.LANGUAGE_MODULE_NAME)) {
                result.add(name);
            }
        }
        return result;
    }
    
}
