package com.redhat.ceylon.eclipse.core.debug.preferences;


import static com.redhat.ceylon.eclipse.core.debug.preferences.CeylonDebugPreferenceInitializer.ACTIVE_FILTERS_LIST;
import static com.redhat.ceylon.eclipse.core.debug.preferences.CeylonDebugPreferenceInitializer.INACTIVE_FILTERS_LIST;
import static com.redhat.ceylon.eclipse.core.debug.preferences.CeylonDebugPreferenceInitializer.USE_STEP_FILTERS;
import static com.redhat.ceylon.eclipse.core.debug.preferences.CeylonDebugPreferenceInitializer.FILTER_DEFAULT_ARGUMENTS_CODE;
import static com.redhat.ceylon.eclipse.core.debug.preferences.CeylonDebugPreferenceInitializer.DEBUG_AS_JAVACODE;
import static com.redhat.ceylon.eclipse.core.debug.preferences.CreateFilterDialog.showCreateFilterDialog;
import static org.eclipse.debug.internal.ui.SWTFactory.createPushButton;
import static org.eclipse.jdt.internal.debug.ui.JDIDebugUIPlugin.createAllPackagesDialog;
import static org.eclipse.jdt.internal.debug.ui.JavaDebugOptionsManager.parseList;
import static org.eclipse.jdt.internal.debug.ui.JavaDebugOptionsManager.serializeList;
import static org.eclipse.jdt.ui.JavaUI.createTypeDialog;
import static org.eclipse.ui.dialogs.PreferencesUtil.createPreferenceDialogOn;

import java.util.ArrayList;

import org.eclipse.debug.internal.ui.SWTFactory;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.internal.debug.ui.DebugUIMessages;
import org.eclipse.jdt.internal.debug.ui.ExceptionHandler;
import org.eclipse.jdt.internal.debug.ui.Filter;
import org.eclipse.jdt.internal.debug.ui.FilterLabelProvider;
import org.eclipse.jdt.internal.debug.ui.FilterViewerComparator;
import org.eclipse.jdt.ui.IJavaElementSearchConstants;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.eclipse.ui.dialogs.SelectionDialog;

import com.redhat.ceylon.eclipse.ui.CeylonPlugin;

/**
 * The preference page for Ceylon step filtering
 * 
 * @since 3.0
 */
public class CeylonStepFilterPreferencePage 
        extends PreferencePage implements IWorkbenchPreferencePage {
    
    public static final String ID = CeylonPlugin.PLUGIN_ID + ".preferences.debug.filters";
    
    /**
     * Content provider for the table.  Content consists of instances of StepFilter.
     * @since 3.2
     */ 
    class StepFilterContentProvider implements IStructuredContentProvider {
        public StepFilterContentProvider() {
            initTableState(false);
        }
        
        public Object[] getElements(Object inputElement) {return getAllFiltersFromTable();}

        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}

        public void dispose() {}        
    }
    
    //widgets
    private CheckboxTableViewer fTableViewer;
    private Button fDebugAsJavaCode;
    private Button fUseStepFiltersButton;
    private Button fAddPackageButton;
    private Button fAddTypeButton;
    private Button fRemoveFilterButton;
    private Button fAddFilterButton;
    private Button fSelectAllButton;
    private Button fDeselectAllButton;
    private Button fFilterDefaultArgumentMethodsButton;
    
    /**
     * Constructor
     */
    public CeylonStepFilterPreferencePage() {
        super();
        setPreferenceStore(CeylonPlugin.getPreferences());
        setDescription("Preferences relating to debugging Ceylon programs."); 
    }

    @Override
    protected Control createContents(Composite parent) {
        Link debugLink = new Link(parent, 0);
        debugLink.setLayoutData(GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).indent(0, 0).create());
        debugLink.setText("See Java '<a>Debug</a>' preferences for more settings.");
        debugLink.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                createPreferenceDialogOn(getShell(), 
                        "org.eclipse.jdt.debug.ui.JavaDebugPreferencePage", 
                        null, null);
            }
        });
        
        return createStepFilterPreferences(parent);
        
    }

    public void init(IWorkbench workbench) {}
    
    /**
     * handles the filter button being clicked
     * @param event the clicked event
     */
    private void handleFilterViewerKeyPress(KeyEvent event) {
        if (event.character == SWT.DEL && event.stateMask == 0) {
            removeFilters();
        }
    }
    
    /**
     * Create a group to contain the step filter related widgetry
     * @return 
     */
    private Control createStepFilterPreferences(Composite parent) {
        Composite container = SWTFactory.createComposite(parent, 1, 1, GridData.FILL_HORIZONTAL);
        Composite stepFilterGroup = SWTFactory.createGroup(container, 
                "Step filtering", 2, 1, GridData.FILL_HORIZONTAL);
        
        fUseStepFiltersButton = SWTFactory.createCheckButton(stepFilterGroup, 
                "&Enable step filtering", 
                null, getPreferenceStore().getBoolean(USE_STEP_FILTERS), 2);
        fUseStepFiltersButton.addSelectionListener(new SelectionListener() {
                public void widgetSelected(SelectionEvent e) {
                    setPageEnablement(fUseStepFiltersButton.getSelection());
                }
                public void widgetDefaultSelected(SelectionEvent e) {}
            }
        );
        
        SWTFactory.createLabel(stepFilterGroup, "Code in filtered packages and classes will be skipped by the debugger.", 2);
        
        fTableViewer = CheckboxTableViewer.newCheckList(stepFilterGroup, 
                SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER | SWT.MULTI | SWT.FULL_SELECTION);
        fTableViewer.getTable().setFont(stepFilterGroup.getFont());
        fTableViewer.setLabelProvider(new FilterLabelProvider());
        fTableViewer.setComparator(new FilterViewerComparator());
        fTableViewer.setContentProvider(new StepFilterContentProvider());
        fTableViewer.setInput(getAllStoredFilters(false));
        fTableViewer.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
        fTableViewer.addCheckStateListener(new ICheckStateListener() {
            public void checkStateChanged(CheckStateChangedEvent event) {
                ((Filter)event.getElement()).setChecked(event.getChecked());
            }
        });
        fTableViewer.addSelectionChangedListener(new ISelectionChangedListener() {
            public void selectionChanged(SelectionChangedEvent event) {
                ISelection selection = event.getSelection();
                if (selection.isEmpty()) {
                    fRemoveFilterButton.setEnabled(false);
                } else {
                    fRemoveFilterButton.setEnabled(true);                   
                }
            }
        }); 
        fTableViewer.getControl().addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent event) {
                handleFilterViewerKeyPress(event);
            }
        }); 
        
        createStepFilterButtons(stepFilterGroup);
        createStepFilterCheckboxes(stepFilterGroup);

        SWTFactory.createLabel(stepFilterGroup, "Unchecked filters are disabled.", 2);
        
        fFilterDefaultArgumentMethodsButton = SWTFactory.createCheckButton(stepFilterGroup, 
                "Filter and step through &default argument code", 
                null, getPreferenceStore().getBoolean(FILTER_DEFAULT_ARGUMENTS_CODE), 2);

        setPageEnablement(fUseStepFiltersButton.getSelection());

        fDebugAsJavaCode = SWTFactory.createCheckButton(container, 
                "Debug as &Java code", 
                null, getPreferenceStore().getBoolean(DEBUG_AS_JAVACODE), 1);
        fDebugAsJavaCode.setToolTipText("Disable the Ceylon-specific presentation, to show raw generated bytecode");
        
        return container;
    }
    
    /**
     * initializes the checked state of the filters when the dialog opens
     * @since 3.2
     */
    private void initTableState(boolean defaults) {
        Filter[] filters = getAllStoredFilters(defaults);
        for(int i = 0; i < filters.length; i++) {
            fTableViewer.add(filters[i]);
            fTableViewer.setChecked(filters[i], filters[i].isChecked());
        }
    }
    
    /**
     * Enables or disables the widgets on the page, with the 
     * exception of <code>fUseStepFiltersButton</code> according
     * to the passed boolean
     * @param enabled the new enablement status of the page's widgets
     * @since 3.2
     */
    protected void setPageEnablement(boolean enabled) {
        fAddFilterButton.setEnabled(enabled);
        fAddPackageButton.setEnabled(enabled);
        fAddTypeButton.setEnabled(enabled);
        fDeselectAllButton.setEnabled(enabled);
        fSelectAllButton.setEnabled(enabled);
        fFilterDefaultArgumentMethodsButton.setEnabled(enabled);
        fTableViewer.getTable().setEnabled(enabled);
        fRemoveFilterButton.setEnabled(enabled & 
                !fTableViewer.getSelection().isEmpty());
    }

    /**
     * create the checked preferences for the page
     * @param container the parent container
     */
    private void createStepFilterCheckboxes(Composite container) {
    }
    
    /**
     * Creates the button for the step filter options
     * @param container the parent container
     */
    private void createStepFilterButtons(Composite container) {
        initializeDialogUnits(container);
        // button container
        Composite buttonContainer = new Composite(container, SWT.NONE);
        GridData gd1 = new GridData(GridData.FILL_VERTICAL);
        buttonContainer.setLayoutData(gd1);
        GridLayout buttonLayout = new GridLayout();
        buttonLayout.numColumns = 1;
        buttonLayout.marginHeight = 0;
        buttonLayout.marginWidth = 0;
        buttonContainer.setLayout(buttonLayout);
    //Add filter button
        fAddFilterButton = createPushButton(buttonContainer, 
                DebugUIMessages.JavaStepFilterPreferencePage_Add__Filter_9, 
                DebugUIMessages.JavaStepFilterPreferencePage_Key_in_the_name_of_a_new_step_filter_10, null);
        fAddFilterButton.addListener(SWT.Selection, 
                new Listener() {
            public void handleEvent(Event e) {
                addFilter();
            }
        });
    //Add type button
        fAddTypeButton = createPushButton(buttonContainer, 
                DebugUIMessages.JavaStepFilterPreferencePage_Add__Type____11, 
                DebugUIMessages.JavaStepFilterPreferencePage_Choose_a_Java_type_and_add_it_to_step_filters_12, null);
        fAddTypeButton.addListener(SWT.Selection, 
                new Listener() {
            public void handleEvent(Event e) {
                addType();
            }
        });
    //Add package button
        fAddPackageButton = createPushButton(buttonContainer, 
                DebugUIMessages.JavaStepFilterPreferencePage_Add__Package____13, 
                DebugUIMessages.JavaStepFilterPreferencePage_Choose_a_package_and_add_it_to_step_filters_14, null);
        fAddPackageButton.addListener(SWT.Selection, 
                new Listener() {
            public void handleEvent(Event e) {
                addPackage();
            }
        });
    //Remove button
        fRemoveFilterButton = createPushButton(buttonContainer, 
                DebugUIMessages.JavaStepFilterPreferencePage__Remove_15, 
                DebugUIMessages.JavaStepFilterPreferencePage_Remove_all_selected_step_filters_16, 
                null);
        fRemoveFilterButton.addListener(SWT.Selection, 
                new Listener() {
            public void handleEvent(Event e) {
                removeFilters();
            }
        });
        fRemoveFilterButton.setEnabled(false);
        
        Label separator = new Label(buttonContainer, SWT.NONE);
        separator.setVisible(false);
        GridData gd = new GridData();
        gd.horizontalAlignment = GridData.FILL;
        gd.verticalAlignment = GridData.BEGINNING;
        gd.heightHint = 4;
        separator.setLayoutData(gd);
    //Select All button
        fSelectAllButton = createPushButton(buttonContainer, 
                DebugUIMessages.JavaStepFilterPreferencePage__Select_All_1, 
                DebugUIMessages.JavaStepFilterPreferencePage_Selects_all_step_filters_2, null);
        fSelectAllButton.addListener(SWT.Selection, 
                new Listener() {
            public void handleEvent(Event e) {
                fTableViewer.setAllChecked(true);
            }
        });
    //De-Select All button
        fDeselectAllButton = createPushButton(buttonContainer, 
                DebugUIMessages.JavaStepFilterPreferencePage_Deselect_All_3, 
                DebugUIMessages.JavaStepFilterPreferencePage_Deselects_all_step_filters_4, null);
        fDeselectAllButton.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event e) {
                fTableViewer.setAllChecked(false);
            }
        });
        
    }
        
    /**
     * Allows a new filter to be added to the listing
     */
    private void addFilter() {
        Filter newfilter = 
                showCreateFilterDialog(getShell(), 
                        getAllFiltersFromTable());
        if (newfilter != null) {
            fTableViewer.add(newfilter);
            fTableViewer.setChecked(newfilter, true);
            fTableViewer.refresh(newfilter);
        }
    }
    
    /**
     * add a new type to the listing of available filters 
     */
    private void addType() {
        try {
            SelectionDialog dialog = createTypeDialog(getShell(), 
                PlatformUI.getWorkbench().getProgressService(),
                SearchEngine.createWorkspaceScope(), 
                IJavaElementSearchConstants.CONSIDER_CLASSES, 
                false);
            dialog.setTitle(DebugUIMessages.JavaStepFilterPreferencePage_Add_type_to_step_filters_20); 
            dialog.setMessage(DebugUIMessages.JavaStepFilterPreferencePage_Select_a_type_to_filter_when_stepping_23); 
            if (dialog.open() == IDialogConstants.OK_ID) {
                Object[] types = dialog.getResult();
                if (types != null && types.length > 0) {
                    IType type = (IType) types[0];
                    addFilter(type.getFullyQualifiedName(), true);
                }
            }           
        } 
        catch (JavaModelException jme) { 
            ExceptionHandler.handle(jme, 
                    DebugUIMessages.JavaStepFilterPreferencePage_Add_type_to_step_filters_20, 
                    DebugUIMessages.JavaStepFilterPreferencePage_Could_not_open_type_selection_dialog_for_step_filters_21);
        }   
    }
    
    /**
     * add a new package to the list of all available package filters
     */
    private void addPackage() {
        try {
            ElementListSelectionDialog dialog = 
                    createAllPackagesDialog(getShell(), null, false);
            dialog.setTitle(DebugUIMessages.JavaStepFilterPreferencePage_Add_package_to_step_filters_24); 
            dialog.setMessage(DebugUIMessages.JavaStepFilterPreferencePage_Select_a_package_to_filter_when_stepping_27); 
            dialog.setMultipleSelection(true);
            if (dialog.open() == IDialogConstants.OK_ID) {
                Object[] packages = dialog.getResult();
                if (packages != null) {
                    IJavaElement pkg = null;
                    for (int i = 0; i < packages.length; i++) {
                        pkg = (IJavaElement) packages[i];
                        String filter = pkg.getElementName() + ".*";
                        addFilter(filter, true);
                    }
                }       
            }
            
        } 
        catch (JavaModelException jme) { 
            ExceptionHandler.handle(jme,
                    DebugUIMessages.JavaStepFilterPreferencePage_Add_package_to_step_filters_24,
                    DebugUIMessages.JavaStepFilterPreferencePage_Could_not_open_package_selection_dialog_for_step_filters_25);      
        }
    }
    
    /**
     * Removes the currently selected filters.
     */
    protected void removeFilters() {
        fTableViewer.remove(((IStructuredSelection)fTableViewer.getSelection()).toArray());
    }
    
    @Override
    public boolean performOk() {
        IPreferenceStore store = getPreferenceStore();
        store.setValue(USE_STEP_FILTERS, 
                fUseStepFiltersButton.getSelection());
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
        String pref = serializeList(active.toArray(new String[active.size()]));
        store.setValue(ACTIVE_FILTERS_LIST, pref);
        pref = serializeList(inactive.toArray(new String[inactive.size()]));
        store.setValue(INACTIVE_FILTERS_LIST, pref);
        
        store.setValue(FILTER_DEFAULT_ARGUMENTS_CODE, 
                fFilterDefaultArgumentMethodsButton.getSelection());
        store.setValue(DEBUG_AS_JAVACODE, 
                fDebugAsJavaCode.getSelection());
        return super.performOk();
    }
    
    @Override
    protected void performDefaults() {
        IPreferenceStore store = getPreferenceStore();
        boolean stepenabled = store.getBoolean(USE_STEP_FILTERS);
        fUseStepFiltersButton.setSelection(stepenabled);
        setPageEnablement(stepenabled);

        boolean filterDefaultArgumentMethods = store.getBoolean(FILTER_DEFAULT_ARGUMENTS_CODE);
        fFilterDefaultArgumentMethodsButton.setSelection(filterDefaultArgumentMethods);
        fTableViewer.getTable().removeAll();
        initTableState(true);               
        super.performDefaults();
    }
    
    /**
     * adds a single filter to the viewer
     * @param filter the new filter to add
     * @param checked the checked state of the new filter
     * @since 3.2
     */
    protected void addFilter(String filter, boolean checked) {
        if(filter != null) {
            Filter f = new Filter(filter, checked);
            fTableViewer.add(f);
            fTableViewer.setChecked(f, checked);
        }
    }
    
    /**
     * returns all of the filters from the table, this 
     * includes ones that have not yet been saved
     * @return a possibly empty lits of filters fron the table
     * @since 3.2
     */
    protected Filter[] getAllFiltersFromTable() {
        TableItem[] items = fTableViewer.getTable().getItems();
        Filter[] filters = new Filter[items.length];
        for(int i = 0; i < items.length; i++) {
            filters[i] = (Filter)items[i].getData();
            filters[i].setChecked(items[i].getChecked());
        }
        return filters;
    }
    
    /**
     * Returns all of the committed filters
     * @return an array of committed filters
     * @since 3.2
     */
    protected Filter[] getAllStoredFilters(boolean defaults) {
        Filter[] filters = null;
        String[] activefilters, inactivefilters;
        IPreferenceStore store = getPreferenceStore();
        
        String activeString;
        String inactiveString;
        if (defaults) {
            activeString = store.getDefaultString(ACTIVE_FILTERS_LIST);
            inactiveString = store.getDefaultString(INACTIVE_FILTERS_LIST); 
        }   
        else {
            activeString = store.getString(ACTIVE_FILTERS_LIST);
            inactiveString = store.getString(INACTIVE_FILTERS_LIST);
        }
        activefilters = parseList(activeString);
        inactivefilters = parseList(inactiveString);
        filters = new Filter[activefilters.length + inactivefilters.length];
        for(int i = 0; i < activefilters.length; i++) {
            filters[i] = new Filter(activefilters[i], true);
        }
        for(int i = 0; i < inactivefilters.length; i++) {
            filters[i+activefilters.length] = new Filter(inactivefilters[i], false);
        }
        return filters;
    }
}
