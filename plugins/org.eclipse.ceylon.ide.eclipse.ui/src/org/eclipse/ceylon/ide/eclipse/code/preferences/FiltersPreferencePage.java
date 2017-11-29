/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.code.preferences;


import static org.eclipse.ceylon.ide.eclipse.core.debug.preferences.CreateFilterDialog.showCreateFilterDialog;
import static org.eclipse.debug.internal.ui.SWTFactory.createPushButton;
import static org.eclipse.jdt.internal.debug.ui.JDIDebugUIPlugin.createAllPackagesDialog;
import static org.eclipse.jdt.internal.debug.ui.JavaDebugOptionsManager.parseList;
import static org.eclipse.jdt.internal.debug.ui.JavaDebugOptionsManager.serializeList;

import java.util.ArrayList;

import org.eclipse.debug.internal.ui.SWTFactory;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.debug.ui.Filter;
import org.eclipse.jdt.internal.debug.ui.FilterViewerComparator;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;

import org.eclipse.ceylon.ide.eclipse.code.open.OpenDeclarationDialog;
import org.eclipse.ceylon.ide.eclipse.ui.CeylonPlugin;
import org.eclipse.ceylon.model.typechecker.model.Class;
import org.eclipse.ceylon.model.typechecker.model.Declaration;
import org.eclipse.ceylon.model.typechecker.model.Function;
import org.eclipse.ceylon.model.typechecker.model.Interface;
import org.eclipse.ceylon.model.typechecker.model.Value;

public abstract class FiltersPreferencePage 
        extends FieldEditorPreferencePage 
        implements IWorkbenchPreferencePage {
    
    class FilterContentProvider 
            implements IStructuredContentProvider {
        public FilterContentProvider() {
            initTableState(false);
        }
        
        public Object[] getElements(Object inputElement) {
            return getAllFiltersFromTable();
        }

        public void inputChanged(Viewer viewer, 
                Object oldInput, Object newInput) {}

        public void dispose() {}        
    }
    
    private CheckboxTableViewer tableViewer;
//    private Button fUseStepFiltersButton;
    private Button addPackageButton;
    private Button addTypeButton;
    private Button removeFilterButton;
    private Button addFilterButton;
    private BoolFieldEditor enable;
    private Composite container; 
//    private Button fSelectAllButton;
//    private Button fDeselectAllButton;
    
    public FiltersPreferencePage() {
        super(GRID);
        setPreferenceStore(CeylonPlugin.getPreferences());
    }
    
    protected Group createGroup(int cols, String text) {
        Composite parent = getFieldEditorParent();
        Group group = new Group(parent, SWT.NONE);
        group.setText(text);
        group.setLayout(GridLayoutFactory.swtDefaults().equalWidth(true).numColumns(cols).create());
        group.setLayoutData(GridDataFactory.fillDefaults().span(3, 1).grab(true, false).create());
        return group;
    }
    
    protected Composite getFieldEditorParent(Composite group) {
        Composite parent = new Composite(group, SWT.NULL);
        parent.setLayoutData(GridDataFactory.fillDefaults().create());
        return parent;
    }

    @Override
    protected Control createContents(Composite parent) {
//        PlatformUI.getWorkbench().getHelpSystem().setHelp(getControl(), 
//            IJavaDebugHelpContextIds.JAVA_STEP_FILTER_PREFERENCE_PAGE);
        
        Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());
        composite.setLayout(new GridLayout(1, true));
        
        Control contents = super.createContents(composite);
        
        createFilterPreferences(composite);

        return contents;   
    }
    
    public void init(IWorkbench workbench) {}
    
    @Override
    protected void createFieldEditors() {
        enable = new BoolFieldEditor(getEnabledPreference(), 
                "Enable filters (in addition to global filters)", 
                getFieldEditorParent());
        enable.setListener(new BoolFieldEditor.Listener() {
            @Override
            public void valueChanged(boolean oldValue, boolean newValue) {
                container.setVisible(newValue);
                ((GridData)container.getLayoutData()).exclude = !newValue;
                container.getParent().getParent().getParent().getParent().layout(true,true);
            }
        });
        enable.load();
        addField(enable);
    }
    
    private void createFilterPreferences(Composite parent) {
        
        container = SWTFactory.createGroup(parent, 
                "Filtering", 2, 1, GridData.FILL_BOTH);
        
//        fUseStepFiltersButton = SWTFactory.createCheckButton(container, 
//                DebugUIMessages.JavaStepFilterPreferencePage__Use_step_filters, 
//                null, getPreferenceStore().getBoolean(StepFilterManager.PREF_USE_STEP_FILTERS), 2);
//        fUseStepFiltersButton.addSelectionListener(new SelectionListener() {
//                public void widgetSelected(SelectionEvent e) {
//                    setPageEnablement(fUseStepFiltersButton.getSelection());
//                }
//                public void widgetDefaultSelected(SelectionEvent e) {}
//            }
//        );
        
        SWTFactory.createLabel(container, getLabelText(), 2);
        tableViewer = CheckboxTableViewer.newCheckList(container, 
                SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER | SWT.MULTI | SWT.FULL_SELECTION);
        tableViewer.getTable().setFont(container.getFont());
        tableViewer.setLabelProvider(new FilterLabelProvider());
        tableViewer.setComparator(new FilterViewerComparator());
        tableViewer.setContentProvider(new FilterContentProvider());
        tableViewer.setInput(getAllStoredFilters(false));
        tableViewer.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
        tableViewer.addCheckStateListener(new ICheckStateListener() {
            public void checkStateChanged(CheckStateChangedEvent event) {
                ((Filter) event.getElement()).setChecked(event.getChecked());
            }
        });
        tableViewer.addSelectionChangedListener(new ISelectionChangedListener() {
            public void selectionChanged(SelectionChangedEvent event) {
                removeFilterButton.setEnabled(!event.getSelection().isEmpty());
            }
        }); 
        tableViewer.getControl().addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent event) {
                if (event.character == SWT.DEL && event.stateMask == 0) {
                    removeFilters();
                }
            }
        }); 
        
        createStepFilterButtons(container);

        SWTFactory.createLabel(container, "Unchecked filters are disabled.", 2);
//        setPageEnablement(fUseStepFiltersButton.getSelection());
        boolean visible = enable==null || enable.getBooleanValue();
        container.setVisible(visible);
        ((GridData)container.getLayoutData()).exclude = !visible;
//        container.getParent().layout();
        
    }
    
    protected String getEnabledPreference() {
        return null;
    }
    
    protected String getLabelText() {
        return "Defined filters:";
    }
    
    private void initTableState(boolean defaults) {
        Filter[] filters = getAllStoredFilters(defaults);
        for(int i = 0; i < filters.length; i++) {
            tableViewer.add(filters[i]);
            tableViewer.setChecked(filters[i], filters[i].isChecked());
        }
    }
    
//    /**
//     * Enables or disables the widgets on the page, with the 
//     * exception of <code>fUseStepFiltersButton</code> according
//     * to the passed boolean
//     * @param enabled the new enablement status of the page's widgets
//     * @since 3.2
//     */
//    protected void setPageEnablement(boolean enabled) {
//        fAddFilterButton.setEnabled(enabled);
//        fAddPackageButton.setEnabled(enabled);
//        fAddTypeButton.setEnabled(enabled);
//        fDeselectAllButton.setEnabled(enabled);
//        fSelectAllButton.setEnabled(enabled);
//        fTableViewer.getTable().setEnabled(enabled);
//        fRemoveFilterButton.setEnabled(enabled & !fTableViewer.getSelection().isEmpty());
//    }
    
    private void createStepFilterButtons(Composite container) {
        initializeDialogUnits(container);
        // button container
        Composite buttonContainer = 
                new Composite(container, SWT.NONE);
        GridData gd = new GridData(GridData.FILL_VERTICAL);
        buttonContainer.setLayoutData(gd);
        GridLayout buttonLayout = new GridLayout();
        buttonLayout.numColumns = 1;
        buttonLayout.marginHeight = 0;
        buttonLayout.marginWidth = 0;
        buttonContainer.setLayout(buttonLayout);
    //Add filter button
        addFilterButton = createPushButton(buttonContainer, 
                "Add &Filter...", 
                "Key in the Name of a New Filter", null);
        addFilterButton.addListener(SWT.Selection, 
                new Listener() {
            public void handleEvent(Event e) {
                addFilter();
            }
        });
    //Add type button
        addTypeButton = createPushButton(buttonContainer, 
                "Add &Declarations...", 
                "Choose Declaration(s) and Add to Filters", null);
        addTypeButton.addListener(SWT.Selection, 
                new Listener() {
            public void handleEvent(Event e) {
                addDeclaration();
            }
        });
    //Add package button
        addPackageButton = createPushButton(buttonContainer, 
                "Add &Packages...", 
                "Choose Package(s) to Add to Filters", null);
        addPackageButton.addListener(SWT.Selection, 
                new Listener() {
            public void handleEvent(Event e) {
                addPackage();
            }
        });
    //Remove button
        removeFilterButton = createPushButton(buttonContainer, 
                "&Remove", 
                "Remove a Filter", 
                null);
        removeFilterButton.addListener(SWT.Selection, 
                new Listener() {
            public void handleEvent(Event e) {
                removeFilters();
            }
        });
        removeFilterButton.setEnabled(false);
        
//        Label separator= new Label(buttonContainer, SWT.NONE);
//        separator.setVisible(false);
//        gd = new GridData();
//        gd.horizontalAlignment= GridData.FILL;
//        gd.verticalAlignment= GridData.BEGINNING;
//        gd.heightHint= 4;
//        separator.setLayoutData(gd);
    //Select All button
//        fSelectAllButton = createPushButton(buttonContainer, 
//                "&Select All", 
//                "Selects all filters", null);
//        fSelectAllButton.addListener(SWT.Selection, 
//                new Listener() {
//            public void handleEvent(Event e) {
//                fTableViewer.setAllChecked(true);
//            }
//        });
    //De-Select All button
//        fDeselectAllButton = createPushButton(buttonContainer, 
//                "D&eselect All", 
//                "Deselects all filters", null);
//        fDeselectAllButton.addListener(SWT.Selection, 
//                new Listener() {
//            public void handleEvent(Event e) {
//                fTableViewer.setAllChecked(false);
//            }
//        });
        
    }
    
    private void addFilter() {
        Filter newfilter = 
                showCreateFilterDialog(getShell(), 
                        getAllFiltersFromTable());
        if (newfilter != null) {
            tableViewer.add(newfilter);
            tableViewer.setChecked(newfilter, true);
            tableViewer.refresh(newfilter);
        }
    }
    
    private void addDeclaration() {
        OpenDeclarationDialog dialog = 
                new OpenDeclarationDialog(true, false, getShell(),
                        "Add Declaration to Filters",
                        "&Type part of a name, with wildcard *, or a camel hump pattern:",
                        "&Select one or more declarations to exclude:") {
            private static final String SETTINGS_ID = 
                    CeylonPlugin.PLUGIN_ID + ".addDeclarationFilterDialog";            
            @Override
            protected String getFilterListAsString(String preference) {
                return "";
            }
            @Override
            public boolean enableDocArea() {
                return false;
            }
            @Override
            protected IDialogSettings getDialogSettings() {
                IDialogSettings settings = CeylonPlugin.getInstance().getDialogSettings();
                IDialogSettings section = settings.getSection(SETTINGS_ID);
                if (section == null) {
                    section = settings.addNewSection(SETTINGS_ID);
                }
                return section;
            }
            @Override
            protected IDialogSettings getDialogBoundsSettings() {
                IDialogSettings settings = getDialogSettings();
                IDialogSettings section = settings.getSection(DIALOG_BOUNDS_SETTINGS);
                if (section == null) {
                    section = settings.addNewSection(DIALOG_BOUNDS_SETTINGS);
                    section.put(DIALOG_HEIGHT, 500);
                    section.put(DIALOG_WIDTH, 400);
                }
                return section;
            }
            @Override
            protected void fillViewMenu(IMenuManager menuManager) {}
        };
        if (dialog.open() == IDialogConstants.OK_ID) {
            Declaration[] results = dialog.getResult();
            if (results!=null) {
                for (Declaration dec: results) {
                    String string = 
                            dec.getQualifiedNameString() +
                            declarationType(dec);
                    addFilter(string, true);
                }
            }
        }
    }
    
    private static String declarationType(Declaration d) {
        if (d instanceof Class) {
            return "(Class)";
        }
        else if (d instanceof Interface) {
            return "(Interface)";
        }
        else if (d instanceof Function) {
            return "(Function)";
        }
        else if (d instanceof Value) {
            return "(Value)";
        }
        else {
            return "";
        }
    }
    
    private void addPackage() {
        try {
            ElementListSelectionDialog dialog = 
                    createAllPackagesDialog(getShell(), null, false);
            dialog.setTitle("Add Packages to Filters"); 
            dialog.setMessage("&Select a package to exclude:"); 
            dialog.setMultipleSelection(true);
            if (dialog.open() == IDialogConstants.OK_ID) {
                Object[] packages = dialog.getResult();
                if (packages != null) {
                    IJavaElement pkg = null;
                    for (int i = 0; i < packages.length; i++) {
                        pkg = (IJavaElement) packages[i];
                        String filter = pkg.getElementName() + "::*";
                        addFilter(filter, true);
                    }
                }       
            }
            
        } 
        catch (JavaModelException jme) { 
            jme.printStackTrace();    
        }
    }
    
    protected void removeFilters() {
        IStructuredSelection structuredSelection = 
                (IStructuredSelection) tableViewer.getSelection();
        tableViewer.remove(structuredSelection.toArray());
    }
    
    @Override
    public boolean performOk() {
        if (enable!=null) {
            enable.store();
        }
        
        ArrayList<String> active = new ArrayList<String>();
        ArrayList<String> inactive = new ArrayList<String>();
        String name = "";
        Filter[] filters = getAllFiltersFromTable();
        for(int i = 0; i < filters.length; i++) {
            name = filters[i].getName();
            if(filters[i].isChecked()) {
                active.add(name);
            }
            else {
                inactive.add(name);
            }
        }
        
        IPreferenceStore store = getPreferenceStore();
        String pref = serializeList(active.toArray(new String[active.size()]));
        store.setValue(getActiveFiltersPreference(), pref);
        pref = serializeList(inactive.toArray(new String[inactive.size()]));
        store.setValue(getInactiveFiltersPreference(), pref);

        return true;
    }

    protected abstract String getInactiveFiltersPreference();

    protected abstract String getActiveFiltersPreference();

    @Override
    protected void performDefaults() {
        if (enable!=null) {
            enable.loadDefault();
        }
//        IPreferenceStore store = getPreferenceStore();
//        boolean stepenabled = store.getBoolean(StepFilterManager.PREF_USE_STEP_FILTERS);
//        fUseStepFiltersButton.setSelection(stepenabled);
//        setPageEnablement(true);
        tableViewer.getTable().removeAll();
        initTableState(true);               
        super.performDefaults();
    }
    
    protected void addFilter(String filter, boolean checked) {
        if(filter != null) {
            Filter f = new Filter(filter, checked);
            tableViewer.add(f);
            tableViewer.setChecked(f, checked);
        }
    }
    
    protected Filter[] getAllFiltersFromTable() {
        TableItem[] items = tableViewer.getTable().getItems();
        Filter[] filters = new Filter[items.length];
        for(int i = 0; i < items.length; i++) {
            filters[i] = (Filter)items[i].getData();
            filters[i].setChecked(items[i].getChecked());
        }
        return filters;
    }
    
    protected Filter[] getAllStoredFilters(boolean defaults) {
        Filter[] filters = null;
        String[] activefilters, inactivefilters;
        
        String activeString;
        String inactiveString;
        IPreferenceStore store = getPreferenceStore();
        if (defaults) {
            activeString = store.getDefaultString(getActiveFiltersPreference());
            inactiveString = store.getDefaultString(getInactiveFiltersPreference()); 
        }   
        else {
            activeString = store.getString(getActiveFiltersPreference());
            inactiveString = store.getString(getInactiveFiltersPreference());
        }
        activefilters = parseList(activeString);
        inactivefilters = parseList(inactiveString);
        filters = new Filter[activefilters.length + inactivefilters.length];
        for(int i = 0; i < activefilters.length; i++) {
            filters[i] = new Filter(activefilters[i], true);
        }
        for(int i = 0; i < inactivefilters.length; i++) {
            filters[i+activefilters.length] = 
                    new Filter(inactivefilters[i], false);
        }
        return filters;
    }
    
}
