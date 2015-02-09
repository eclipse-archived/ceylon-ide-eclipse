package com.redhat.ceylon.eclipse.code.preferences;


import static com.redhat.ceylon.eclipse.core.debug.preferences.CreateFilterDialog.showCreateFilterDialog;
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
import org.eclipse.jface.dialogs.IDialogConstants;
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
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;

import com.redhat.ceylon.eclipse.code.open.DeclarationWithProject;
import com.redhat.ceylon.eclipse.code.open.OpenCeylonDeclarationDialog;
import com.redhat.ceylon.eclipse.util.EditorUtil;

public abstract class FiltersPreferencePage 
        extends PreferencePage 
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
    
    private CheckboxTableViewer fTableViewer;
//    private Button fUseStepFiltersButton;
    private Button fAddPackageButton;
    private Button fAddTypeButton;
    private Button fRemoveFilterButton;
    private Button fAddFilterButton;
    private Button fSelectAllButton;
    private Button fDeselectAllButton;
    
    public FiltersPreferencePage() {
        setPreferenceStore(EditorUtil.getPreferences());
    }
    
    @Override
    protected Control createContents(Composite parent) {
//        PlatformUI.getWorkbench().getHelpSystem().setHelp(getControl(), 
//            IJavaDebugHelpContextIds.JAVA_STEP_FILTER_PREFERENCE_PAGE);
        
    //The main composite
        Composite composite = SWTFactory.createComposite(parent, 
                parent.getFont(), 1, 1, GridData.FILL_BOTH, 0, 0);
        createStepFilterPreferences(composite);

//        Label sep = new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL);
//        GridData sgd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
//        sep.setLayoutData(sgd);

        return composite;   
    }
    
    public void init(IWorkbench workbench) {}
    
    private void handleFilterViewerKeyPress(KeyEvent event) {
        if (event.character == SWT.DEL && event.stateMask == 0) {
            removeFilters();
        }
    }
    
    private void createStepFilterPreferences(Composite parent) {
        Composite container = SWTFactory.createComposite(parent, 
                parent.getFont(), 2, 1, GridData.FILL_BOTH, 0, 0);
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
        SWTFactory.createLabel(container, "Defined filters:", 2);
        fTableViewer = CheckboxTableViewer.newCheckList(container, 
                SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER | SWT.MULTI | SWT.FULL_SELECTION);
        fTableViewer.getTable().setFont(container.getFont());
        fTableViewer.setLabelProvider(new FilterLabelProvider());
        fTableViewer.setComparator(new FilterViewerComparator());
        fTableViewer.setContentProvider(new FilterContentProvider());
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
        
        createStepFilterButtons(container);

//        setPageEnablement(fUseStepFiltersButton.getSelection());
    }
    
    private void initTableState(boolean defaults) {
        Filter[] filters = getAllStoredFilters(defaults);
        for(int i = 0; i < filters.length; i++) {
            fTableViewer.add(filters[i]);
            fTableViewer.setChecked(filters[i], filters[i].isChecked());
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
        fAddFilterButton = createPushButton(buttonContainer, 
                "Add &Filter...", 
                "Key in the Name of a New Filter", null);
        fAddFilterButton.addListener(SWT.Selection, 
                new Listener() {
            public void handleEvent(Event e) {
                addFilter();
            }
        });
    //Add type button
        fAddTypeButton = createPushButton(buttonContainer, 
                "Add &Declarations...", 
                "Choose Declaration(s) and Add to Filters", null);
        fAddTypeButton.addListener(SWT.Selection, 
                new Listener() {
            public void handleEvent(Event e) {
                addDeclaration();
            }
        });
    //Add package button
        fAddPackageButton = createPushButton(buttonContainer, 
                "Add &Packages...", 
                "Choose Package(s) to Add to Filters", null);
        fAddPackageButton.addListener(SWT.Selection, 
                new Listener() {
            public void handleEvent(Event e) {
                addPackage();
            }
        });
    //Remove button
        fRemoveFilterButton = createPushButton(buttonContainer, 
                "&Remove", 
                "Remove a Filter", 
                null);
        fRemoveFilterButton.addListener(SWT.Selection, 
                new Listener() {
            public void handleEvent(Event e) {
                removeFilters();
            }
        });
        fRemoveFilterButton.setEnabled(false);
        
        Label separator= new Label(buttonContainer, SWT.NONE);
        separator.setVisible(false);
        gd = new GridData();
        gd.horizontalAlignment= GridData.FILL;
        gd.verticalAlignment= GridData.BEGINNING;
        gd.heightHint= 4;
        separator.setLayoutData(gd);
    //Select All button
        fSelectAllButton = createPushButton(buttonContainer, 
                "&Select All", 
                "Selects all filters", null);
        fSelectAllButton.addListener(SWT.Selection, 
                new Listener() {
            public void handleEvent(Event e) {
                fTableViewer.setAllChecked(true);
            }
        });
    //De-Select All button
        fDeselectAllButton = createPushButton(buttonContainer, 
                "D&eselect All", 
                "Deselects all filters", null);
        fDeselectAllButton.addListener(SWT.Selection, 
                new Listener() {
            public void handleEvent(Event e) {
                fTableViewer.setAllChecked(false);
            }
        });
        
    }
    
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
    
    private void addDeclaration() {
        OpenCeylonDeclarationDialog dialog = 
                new OpenCeylonDeclarationDialog(true, getShell()) {
            @Override
            protected String getFilterListAsString() {
                return "";
            }
        };
        dialog.setTitle("Add Declaration to Filters");
        dialog.setMessage("&Select a type to exclude:");
        if (dialog.open() == IDialogConstants.OK_ID) {
            Object[] types = dialog.getResult();
            for (int i=0; i<types.length; i++) {
                DeclarationWithProject dwp = (DeclarationWithProject) types[i];
                addFilter(dwp.getDeclaration().getQualifiedNameString(), true);
            }
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
                (IStructuredSelection) fTableViewer.getSelection();
        fTableViewer.remove(structuredSelection.toArray());
    }
    
    @Override
    public boolean performOk() {
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
//        IPreferenceStore store = getPreferenceStore();
//        boolean stepenabled = store.getBoolean(StepFilterManager.PREF_USE_STEP_FILTERS);
//        fUseStepFiltersButton.setSelection(stepenabled);
//        setPageEnablement(true);
        fTableViewer.getTable().removeAll();
        initTableState(true);               
        super.performDefaults();
    }
    
    protected void addFilter(String filter, boolean checked) {
        if(filter != null) {
            Filter f = new Filter(filter, checked);
            fTableViewer.add(f);
            fTableViewer.setChecked(f, checked);
        }
    }
    
    protected Filter[] getAllFiltersFromTable() {
        TableItem[] items = fTableViewer.getTable().getItems();
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
