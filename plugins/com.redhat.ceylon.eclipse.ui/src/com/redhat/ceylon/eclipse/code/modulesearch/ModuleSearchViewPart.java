package com.redhat.ceylon.eclipse.code.modulesearch;

import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getProjectDeclaredSourceModules;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getProjects;
import static com.redhat.ceylon.eclipse.java2ceylon.Java2CeylonProxies.modelJ2C;
import static com.redhat.ceylon.eclipse.java2ceylon.Java2CeylonProxies.importsJ2C;
import static com.redhat.ceylon.eclipse.ui.CeylonResources.CEYLON_ADD;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.internal.ui.actions.CollapseAllAction;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.LocationAdapter;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.eclipse.ui.part.IShowInTarget;
import org.eclipse.ui.part.ShowInContext;
import org.eclipse.ui.part.ViewPart;

import com.github.rjeschke.txtmark.Configuration;
import com.github.rjeschke.txtmark.Processor;
import com.redhat.ceylon.common.config.Repositories;
import com.redhat.ceylon.common.config.Repositories.Repository;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.code.hover.CeylonBlockEmitter;
import com.redhat.ceylon.eclipse.code.hover.DocumentationHover;
import com.redhat.ceylon.eclipse.code.html.HTML;
import com.redhat.ceylon.eclipse.code.html.HTMLPrinter;
import com.redhat.ceylon.eclipse.code.navigator.SourceModuleNode;
import com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider;
import com.redhat.ceylon.eclipse.core.builder.CeylonNature;
import com.redhat.ceylon.eclipse.ui.CeylonPlugin;
import com.redhat.ceylon.eclipse.ui.CeylonResources;
import com.redhat.ceylon.eclipse.util.DocBrowser;
import com.redhat.ceylon.eclipse.util.EditorUtil;
import com.redhat.ceylon.eclipse.util.UnlinkedSpanEmitter;
import com.redhat.ceylon.ide.common.modulesearch.ModuleNode;
import com.redhat.ceylon.ide.common.modulesearch.ModuleVersionNode;
import com.redhat.ceylon.model.typechecker.model.Module;
import com.redhat.ceylon.model.typechecker.model.ModuleImport;

public class ModuleSearchViewPart extends ViewPart implements IShowInTarget {
    
    private class RemoveSelectedAction extends Action implements ISelectionChangedListener {

        public RemoveSelectedAction() {
            super("Remove Selected");
            setToolTipText("Remove Selected");
            setEnabled(false);
            
            ISharedImages workbenchImages = PlatformUI.getWorkbench().getSharedImages();
            setImageDescriptor(workbenchImages.getImageDescriptor(ISharedImages.IMG_ELCL_REMOVE));
            setHoverImageDescriptor(workbenchImages.getImageDescriptor(ISharedImages.IMG_ELCL_REMOVE));

            moduleTreeViewer.addSelectionChangedListener(this);
        }

        @Override
        public void run() {
            List<?> selectedElements = ((IStructuredSelection) moduleTreeViewer.getSelection()).toList();
            if (selectedElements != null) {
                int lastSelectedModuleIndex = -1;
                for (Object selectedElement : selectedElements) {
                    int selectedModuleIndex = moduleSearchManager.getModules().indexOf(selectedElement);
                    if (selectedModuleIndex != -1) {
                        lastSelectedModuleIndex = selectedModuleIndex;
                    }
                }

                if( lastSelectedModuleIndex != -1 ) {
                    moduleSearchManager.remove(selectedElements);

                    int nextSelectedModuleIndex = -1;
                    if (moduleSearchManager.getModules() != null) {
                        if (lastSelectedModuleIndex > moduleSearchManager.getModules().size() - 1) {
                            nextSelectedModuleIndex = moduleSearchManager.getModules().size() - 1;
                        } else {
                            nextSelectedModuleIndex = lastSelectedModuleIndex;
                        }
                    }

                    update(false);

                    if (nextSelectedModuleIndex != -1) {
                        moduleTreeViewer.setSelection(new StructuredSelection(moduleSearchManager.getModules().get(nextSelectedModuleIndex)));
                    }
                }
            }
        }

        @Override
        public void selectionChanged(SelectionChangedEvent e) {
            boolean isEnabled = false;
            
            List<?> selectedElements = ((IStructuredSelection) e.getSelection()).toList();
            if( selectedElements != null ) {
                for( Object selectedElement : selectedElements ) {
                    if( selectedElement instanceof ModuleNode ) {
                        isEnabled = true;
                        break;
                    }
                }
            }
            
            setEnabled(isEnabled);
        }

    }
    
    private class RemoveAllAction extends Action {

        public RemoveAllAction() {
            super("Remove All");
            setToolTipText("Remove All");
            
            ISharedImages workbenchImages = PlatformUI.getWorkbench().getSharedImages();
            setImageDescriptor(workbenchImages.getImageDescriptor(ISharedImages.IMG_ELCL_REMOVEALL));
            setHoverImageDescriptor(workbenchImages.getImageDescriptor(ISharedImages.IMG_ELCL_REMOVEALL));
        }

        @Override
        public void run() {
            moduleSearchManager.clear();
            update(true);
            searchCombo.setText("");
        }

    }
    
    private class ExpandAllAction extends Action {

        public ExpandAllAction() {
            super("Expand All");
            setToolTipText("Expand All");

            ImageDescriptor expandAllImage = 
                    CeylonPlugin.imageRegistry()
                        .getDescriptor(CeylonResources.EXPAND_ALL);
            setImageDescriptor(expandAllImage);
            setHoverImageDescriptor(expandAllImage);
        }

        @Override
        public void run() {
            moduleTreeViewer.expandAll();
        }

    }
    
    private class FetchNextAction extends Action {

        public FetchNextAction() {
            super("Fetch Next");
            setToolTipText("Fetch Next");
            setEnabled(false);

            ImageDescriptor fetchNextImage = 
                    CeylonPlugin.imageRegistry()
                        .getDescriptor(CeylonResources.PAGING);
            setHoverImageDescriptor(fetchNextImage);
            setImageDescriptor(fetchNextImage);
        }

        @Override
        public void run() {
            updateBeforeSearch(false);
            moduleSearchManager.fetchNextModules();
        }

    }
    
    private class AddModuleImportAction extends Action implements ISelectionChangedListener {

        public AddModuleImportAction() {
            super("Add Module Import...");
            setToolTipText("Add Module Import...");
            setEnabled(false);
            ImageDescriptor image = 
                    CeylonPlugin.imageRegistry()
                        .getDescriptor(CEYLON_ADD);
            setImageDescriptor(image);
            moduleTreeViewer.addSelectionChangedListener(this);
        }

        @Override
        public void run() {
            Object selectedElement = ((IStructuredSelection) moduleTreeViewer.getSelection()).getFirstElement();

            ModuleVersionNode versionNode = null;
            if (selectedElement instanceof ModuleNode) {
                versionNode = ((ModuleNode) selectedElement).getLastVersion();
            } else if (selectedElement instanceof ModuleVersionNode) {
                versionNode = (ModuleVersionNode) selectedElement;
            }

            if (versionNode != null) {
                addModuleImport(versionNode.getModule().getName(), versionNode.getVersion());
            }
        }
        
        private void addModuleImport(String moduleName, String moduleVersion) {
            Map<Module, IProject> moduleMap = new HashMap<Module, IProject>();
            for (IProject project : getProjects()) {
                for (Module module : getProjectDeclaredSourceModules(project)) {
                    moduleMap.put(module, project);
                }
            }
            
            List<Module> targets = new ArrayList<Module>();
            if (moduleMap.isEmpty()) {
                MessageDialog.openInformation(parent.getShell(), "Information", "Can not add module import, because there are no ceylon modules in workspace.");
            } else if (moduleMap.size() == 1) {
                targets.addAll(moduleMap.keySet());
            } else {
                ElementListSelectionDialog dlg = 
                        new ElementListSelectionDialog(parent.getShell(), 
                                new CeylonLabelProvider());
                dlg.setTitle("Select Module");
                dlg.setMessage("Select module, where to add module import.");
                dlg.setMultipleSelection(true);
                dlg.setHelpAvailable(false);
                dlg.setElements(moduleMap.keySet().toArray(new Module[] {}));
                if (dlg.open() == Dialog.OK) {
                    Object[] results = dlg.getResult();
                    for (Object result : results) {
                        targets.add((Module) result);
                    }
                }
            }

            if (!targets.isEmpty()) {
                for (Module target : targets) {
                    boolean containsImport = false;
                    
                    for (ModuleImport moduleImport : target.getImports()) {
                        if (moduleName.equals(moduleImport.getModule().getNameAsString())) {
                            containsImport = true;
                            break;
                        }
                    }
                    
                    if( containsImport ) {
                        MessageDialog.openInformation(parent.getShell(), "Information", "Can not add module import, because module '" + 
                                target.getNameAsString() + "' contains it already.");
                    } else {
                        importsJ2C().importUtil()
                            .addModuleImport(moduleMap.get(target), target, moduleName, moduleVersion);
                    }
                }
            }
        }

        @Override
        public void selectionChanged(SelectionChangedEvent e) {
            int selectionSize = ((IStructuredSelection) e.getSelection()).size();
            setEnabled(selectionSize == 1);
        }
        
    }
    
    private class ShowDocAction extends Action {
        
        private static final String IS_CHECKED = "ModuleSearch.ShowDocAction.isChecked";

        public ShowDocAction() {
            super("Show/Hide Documentation", AS_CHECK_BOX);
            setToolTipText("Show/Hide Documentation");

            ImageDescriptor showDocImage = 
                    CeylonPlugin.imageRegistry()
                        .getDescriptor(CeylonResources.SHOW_DOC);
            setImageDescriptor(showDocImage);
            setHoverImageDescriptor(showDocImage);
            
            setChecked(CeylonPlugin.getPreferences().getBoolean(IS_CHECKED));
            run();
        }

        @Override
        public void run() {
            if (isChecked()) {
                sashForm.setMaximizedControl(null);
            } else {
                sashForm.setMaximizedControl(moduleTreeViewer.getTree());
            }
            CeylonPlugin.getPreferences().setValue(IS_CHECKED, isChecked());
        }

    }
    
    private class ShowRepositoriesAction extends Action {
        
        public ShowRepositoriesAction() {
            super("Show Repositories");
            setToolTipText("Show Repositories");

            ImageDescriptor showRepositoriesImage = 
                    CeylonPlugin.imageRegistry()
                        .getDescriptor(CeylonResources.REPOSITORIES);
            setImageDescriptor(showRepositoriesImage);
            setHoverImageDescriptor(showRepositoriesImage);
        }

        @Override
        public void run() {
            ShowRepositoriesDialog showRepositoriesDialog = new ShowRepositoriesDialog();
            showRepositoriesDialog.open();
        }

    }
    
    private class ShowRepositoriesDialog extends TitleAreaDialog {

        public ShowRepositoriesDialog() {
            super(parent.getShell());
        }
        
        @Override
        public void create() {
            setHelpAvailable(false);
            super.create();
            setTitle("Ceylon repositories");
            setMessage("The following repositories will be searched.");
        }
        
        @Override
        protected Control createDialogArea(Composite parent) {
            Repositories repositories;
            IProject selectedProject = getSelectedProject();
            if( selectedProject != null ) {
                repositories = modelJ2C().ceylonModel().getProject(selectedProject).getConfiguration().getRepositories();
            } else {
                repositories = Repositories.get();
            }

            List<Repository> repositoryList = new ArrayList<Repository>();
            Collections.addAll(repositoryList, repositories.getLocalLookupRepositories());
            Collections.addAll(repositoryList, repositories.getGlobalLookupRepositories());
            Collections.addAll(repositoryList, repositories.getRemoteLookupRepositories());
            Collections.addAll(repositoryList, repositories.getOtherLookupRepositories());
            
            parent.setLayout(new GridLayout(1, false));

            TableViewer tableViewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);

            TableViewerColumn colName = new TableViewerColumn(tableViewer, SWT.NONE);
            colName.getColumn().setWidth(200);
            colName.getColumn().setText("Name");
            colName.setLabelProvider(new ColumnLabelProvider() {
                @Override
                public String getText(Object element) {
                    return ((Repository)element).getName();
                }
            });

            TableViewerColumn colUrl = new TableViewerColumn(tableViewer, SWT.NONE);
            colUrl.getColumn().setWidth(200);
            colUrl.getColumn().setText("URL");
            colUrl.setLabelProvider(new ColumnLabelProvider() {
                @Override
                public String getText(Object element) {
                    return ((Repository)element).getUrl();
                }
            });

            tableViewer.setContentProvider(ArrayContentProvider.getInstance());
            tableViewer.setInput(repositoryList);
            tableViewer.getTable().setHeaderVisible(true);
            tableViewer.getTable().setLinesVisible(true);
            tableViewer.getTable().setLayoutData(GridDataFactory.swtDefaults().align(SWT.FILL, SWT.FILL).grab(true, true).create());

            return parent;
        }

        @Override
        protected void createButtonsForButtonBar(Composite parent) {
            createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
        }

    }

    private ModuleSearchManager moduleSearchManager = new ModuleSearchManager(this);
    
    private RemoveSelectedAction removeSelectedAction;
    private RemoveAllAction removeAllAction;
    private ExpandAllAction expandAllAction;
    private CollapseAllAction collapseAllAction;
    private FetchNextAction fetchNextAction;
    private AddModuleImportAction addModuleImportAction;
    private ShowDocAction showDocAction;
    private ShowRepositoriesAction showRepositoriesAction;
    
    private Composite parent;
    private Combo searchCombo;
    private Button searchButton;
    private Combo projectCombo;
    private Link searchInfo;
    private SashForm sashForm;
    private TreeViewer moduleTreeViewer;
    private DocBrowser docBrowser;
    private List<String> queryHistory = new ArrayList<String>();
    private Map<String, IProject> projectMap = new ConcurrentHashMap<String, IProject>();
    private IResourceChangeListener updateProjectComboListener;
    
    @Override
    public void setFocus() {
        searchCombo.setFocus();
    }

    @Override
    public void createPartControl(Composite parent) {
        this.parent = parent;
        this.parent.setLayout(new GridLayout(2, false));
        
        initSearchCombo();       
        initSearchButton();
        initProjectCombo();
        initSearchInfo();
        initSashForm();
        initModuleTreeViewer();
        initDocBrowser();
        initActions();
    }

    private void initSearchCombo() {
        searchCombo = new Combo(parent, SWT.SINGLE | SWT.BORDER);
        searchCombo.setVisibleItemCount(10);
        GridData gd = GridDataFactory.swtDefaults().span(1, 1).hint(250, SWT.DEFAULT).create();
        searchCombo.setLayoutData(gd);
        searchCombo.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.keyCode == SWT.CR || e.keyCode == SWT.KEYPAD_CR) {
                    updateBeforeSearch(true);
                    moduleSearchManager.searchModules(searchCombo.getText());
                }
            }
        });
    }

    private void initSearchButton() {
        searchButton = new Button(parent, SWT.PUSH);
        searchButton.setText("&Search");
        searchButton.setLayoutData(GridDataFactory.swtDefaults().hint(120, SWT.DEFAULT).create());
        searchButton.setAlignment(SWT.CENTER);
        searchButton.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                updateBeforeSearch(true);
                moduleSearchManager.searchModules(searchCombo.getText());
            }
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {}
        });
    }

    private void initProjectCombo() {
        Label projectLabel = new Label(parent, SWT.RIGHT | SWT.WRAP);
        projectLabel.setText("Search in repositories of project");
        GridData gd = GridDataFactory.swtDefaults().align(SWT.END, SWT.CENTER).create();
        projectLabel.setLayoutData(gd);
        
        projectCombo = new Combo(parent, SWT.SINGLE | SWT.BORDER | SWT.READ_ONLY);
        projectCombo.setLayoutData(GridDataFactory.swtDefaults().hint(120, SWT.DEFAULT).create());
        
        updateProjectCombo();
        
        updateProjectComboListener = new IResourceChangeListener() {
            @Override
            public void resourceChanged(IResourceChangeEvent event) {
                if (event.getResource() == null || event.getResource() instanceof IProject) {
                    updateProjectComboAsync();
                }
            }
        };
        ResourcesPlugin.getWorkspace().addResourceChangeListener(updateProjectComboListener);
    }
    
    private void initSearchInfo() {
        searchInfo = new Link(parent, 0);
        searchInfo.setLayoutData(GridDataFactory.swtDefaults().span(3, 1).create());
        searchInfo.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event e) {
                updateBeforeSearch(false);
                moduleSearchManager.fetchNextModules();
            }
        });
        updateInfoLabel();
    }

    private void initSashForm() {
        sashForm = new SashForm(parent, SWT.HORIZONTAL | SWT.SMOOTH);
        sashForm.setLayoutData(GridDataFactory.swtDefaults().align(SWT.FILL, SWT.FILL).grab(true, true).span(4, 1).create());
        sashForm.addControlListener(new ControlListener() {
            boolean reentrant;
            @Override
            public void controlResized(ControlEvent e) {
                if (reentrant) return;
                reentrant = true;
                try {
                    Rectangle bounds = sashForm.getBounds();
//                    IToolBarManager toolBarManager = 
//                            getViewSite().getActionBars().getToolBarManager();
                    if (bounds.height>bounds.width) {
                        if (sashForm.getOrientation()!=SWT.VERTICAL) {
                            sashForm.setOrientation(SWT.VERTICAL);
//                            createMainToolBar(toolBarManager);
//                            toolBarManager.update(false);
//                            viewForm.setTopLeft(null);
                        }
                    }
                    else {
                        if (sashForm.getOrientation()!=SWT.HORIZONTAL) {
                            sashForm.setOrientation(SWT.HORIZONTAL);
//                            toolBarManager.removeAll();
//                            toolBarManager.update(false);
//                            ToolBarManager tbm = new ToolBarManager(SWT.NONE);
//                            createMainToolBar(tbm);
//                            tbm.createControl(viewForm);
//                            viewForm.setTopLeft(tbm.getControl());
                        }
                    }
                    getViewSite().getActionBars().updateActionBars();
                }
                finally {
                    reentrant = false;
                }
            }
            @Override
            public void controlMoved(ControlEvent e) {}
        });
    }

    private void initModuleTreeViewer() {
        moduleTreeViewer = new TreeViewer(sashForm, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
        moduleTreeViewer.setContentProvider(new ModuleSearchViewContentProvider());
        moduleTreeViewer.setLabelProvider(new ModuleSearchViewLabelProvider());
        moduleTreeViewer.addDoubleClickListener(new IDoubleClickListener() {
            @Override
            public void doubleClick(DoubleClickEvent e) {
                if( e.getSelection() instanceof IStructuredSelection ) {
                    Object selectedElement = ((IStructuredSelection)e.getSelection()).getFirstElement();
                    if( selectedElement != null ) {
                        boolean isExpanded = moduleTreeViewer.getExpandedState(selectedElement);
                        moduleTreeViewer.setExpandedState(selectedElement, !isExpanded);
                    }
                }
            }
        });
    }

    private void initDocBrowser() {
        docBrowser = new DocBrowser(sashForm, SWT.NONE);
        docBrowser.setMenu(new Menu(parent.getShell(), SWT.NONE));
        
        docBrowser.addLocationListener(new LocationAdapter() {
            @Override
            public void changing(LocationEvent e) {
                if (e.location.startsWith("module:")) {
                    e.doit = false;
                    
                    String[] split = e.location.split(":");
                    String moduleName = split[1];
                    String moduleVersion = split[2];
                    
                    moduleSearchManager.fetchDocumentation(moduleName, moduleVersion);
                }
            }
        });
        
        moduleTreeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
            @Override
            public void selectionChanged(SelectionChangedEvent e) {
                updateDoc();                
            }
        });
        
        updateDoc();
    }

    private void initActions() {
        removeSelectedAction = new RemoveSelectedAction();
        removeAllAction = new RemoveAllAction();
        expandAllAction = new ExpandAllAction();
        collapseAllAction = new CollapseAllAction(moduleTreeViewer);
        fetchNextAction = new FetchNextAction();
        addModuleImportAction = new AddModuleImportAction();
        showDocAction = new ShowDocAction();
        showRepositoriesAction = new ShowRepositoriesAction();

        IToolBarManager toolBarManager = getViewSite().getActionBars().getToolBarManager();
        toolBarManager.add(fetchNextAction);
        toolBarManager.add(new Separator());
        toolBarManager.add(removeSelectedAction);
        toolBarManager.add(removeAllAction);
        toolBarManager.add(new Separator());
        toolBarManager.add(expandAllAction);
        toolBarManager.add(collapseAllAction);
        toolBarManager.add(new Separator());
        toolBarManager.add(showDocAction);
        toolBarManager.add(showRepositoriesAction);

        MenuManager menuManager = new MenuManager();
        menuManager.add(addModuleImportAction);
        menuManager.add(new Separator());
        menuManager.add(removeSelectedAction);
        menuManager.add(removeAllAction);
        menuManager.add(new Separator());
        menuManager.add(expandAllAction);
        menuManager.add(collapseAllAction);

        Menu menu = menuManager.createContextMenu(moduleTreeViewer.getTree());
        moduleTreeViewer.getTree().setMenu(menu);
    }

    public void update(boolean newModel) {
        if (newModel) {
            moduleTreeViewer.setInput(moduleSearchManager.getModules());
        } else {
            moduleTreeViewer.refresh();
        }
        
        updateInfoLabel();
        updateEnabledState();
        updateFocusAndSelection(newModel);
        updateSearchComboHistory(newModel);
        parent.setCursor(null);
    }

    private void updateInfoLabel() {
        StringBuilder info = new StringBuilder();
        if( moduleSearchManager.getModules() != null ) {
            if (moduleSearchManager.getModules().isEmpty()) {
                info.append("No module found"); 
            } else {
                info.append("Found ");
                if (moduleSearchManager.canFetchNext()) {
                    info.append("first ");
                }
                info.append(moduleSearchManager.getModules().size());
                if( moduleSearchManager.getModules().size() == 1 ) {
                    info.append(" module");
                } else {
                    info.append(" modules");
                }
            }
    
            /*if( !moduleSearchManager.getLastQuery().isEmpty() ) {
                info.append(" for query '");
                info.append(moduleSearchManager.getLastQuery());
                info.append("'");
            }*/
    
            if (moduleSearchManager.canFetchNext()) {
                info.append(", click here to <a>see more</a> results");
            }
        } else {
            info.append("Click 'Search' to find modules by module name");
        }
        if (info.length() != 0 && info.charAt(info.length() - 1) != '.') {
            info.append(".");
        }
        searchInfo.setText(info.toString());
        searchInfo.pack();
    }

    private void updateEnabledState() {
        searchCombo.setEnabled(true);
        searchButton.setEnabled(true);
        if (moduleSearchManager.canFetchNext()) {
            fetchNextAction.setEnabled(true);
        } else {
            fetchNextAction.setEnabled(false);
        }
    }

    private void updateFocusAndSelection(boolean newModel) {
        if (moduleSearchManager.getModules() == null || moduleSearchManager.getModules().isEmpty()) {
            searchCombo.setFocus();
        } else {
            moduleTreeViewer.setSelection(new StructuredSelection(moduleSearchManager.getModules().get(0)));
            moduleTreeViewer.getTree().setFocus();
        }
    }

    private void updateSearchComboHistory(boolean newModel) {
        String lastQuery = moduleSearchManager.getLastQuery();
        if (newModel && lastQuery != null && !lastQuery.isEmpty() ) {
            if (queryHistory.contains(lastQuery)) {
                queryHistory.remove(lastQuery);
            }
            queryHistory.add(0, lastQuery);
            searchCombo.setItems(queryHistory.toArray(new String[queryHistory.size()]));
            searchCombo.setText(lastQuery);
        }
    }
    
    private void updateBeforeSearch(boolean newModel) {
        searchInfo.setText("Searching module repositories...");
        searchInfo.pack();
        searchButton.setEnabled(false);
        if( newModel ) {
            moduleTreeViewer.setInput(null);
        }
        parent.setCursor(Display.getDefault().getSystemCursor(SWT.CURSOR_WAIT));
    }

    public void updateDoc() {
        ModuleVersionNode versionNode = null;
        
        Object selectedElement = ((IStructuredSelection) moduleTreeViewer.getSelection()).getFirstElement();
        if (selectedElement instanceof ModuleNode) {
            versionNode = ((ModuleNode) selectedElement).getLastVersion();
        } else if (selectedElement instanceof ModuleVersionNode) {
            versionNode = (ModuleVersionNode) selectedElement;
        }
        
        docBrowser.setText(getModuleDoc(versionNode));
    }

    public static String getModuleDoc(ModuleVersionNode versionNode) {
        StringBuilder docBuilder = new StringBuilder();
        HTMLPrinter.insertPageProlog(docBuilder, 0, HTML.getStyleSheet());

        if (versionNode != null) {
            HTML.addImageAndLabel(docBuilder, null, 
                    HTML.fileUrl("jar_l_obj.gif").toExternalForm(), 
                    16, 16, 
                    "<tt><span style='font-size:"+ DocumentationHover.largerSize + "%'>" + 
                    HTML.highlightLine(description(versionNode)) +
                    "</span></tt>", 
                    20, 4);
//            docBuilder.append("<br/>");
            
            if (versionNode.isFilled()) {
                
                if( versionNode.getDoc() == null || versionNode.getDoc().isEmpty() ) {
                    docBuilder.append("<p>");
                    docBuilder.append("<i>Module does not have documentation.</i>");
                    docBuilder.append("</p>");
                } else {
                    docBuilder.append(markdown(versionNode.getDoc()));
                }
                
                if (versionNode.getAuthors() != null && !versionNode.getAuthors().isEmpty()) {
                    docBuilder.append("<p><i>By: ");
                    docBuilder.append(versionNode.getAuthorsCommaSeparated());
                    docBuilder.append("</i></p>");
                }
                
                if( versionNode.getLicense() != null && !versionNode.getLicense().isEmpty() ) {
                    docBuilder.append("<p><i>License: ");
                    docBuilder.append(versionNode.getLicense());
                    docBuilder.append("</i></p>");
                }
                
            } else {
                docBuilder.append("<p><i>Click here to <a href='module:");
                docBuilder.append(versionNode.getModule().getName() + ":" + versionNode.getVersion());
                docBuilder.append("'>fetch documentation</a></i> for this module version.");
                docBuilder.append("</p>");
            }
        }
        
        HTMLPrinter.addPageEpilog(docBuilder);
        return docBuilder.toString();
    }

    public static String description(ModuleVersionNode versionNode) {
        return "module " + versionNode.getModule().getName() + 
                " \"" + versionNode.getVersion() + "\"";
    }
    
    private void updateProjectComboAsync() {
        Display.getDefault().asyncExec(new Runnable() {
            @Override
            public void run() {
                if (!projectCombo.isDisposed()) {
                    updateProjectCombo();
                }
            }
        });
    }

    private void updateProjectCombo() {
        projectMap.clear();
        IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
        if (projects != null) {
            for (IProject project : projects) {
                if (project.isOpen() && CeylonNature.isEnabled(project)) {
                    projectMap.put(project.getName(), project);
                }
            }
        }
        
        List<String> projectNames = new ArrayList<String>(projectMap.keySet());
        projectNames.add("");
        Collections.sort(projectNames);
        
        int selectedIndex = projectCombo.getSelectionIndex();
        String selectedProjectName = selectedIndex != -1 ? projectCombo.getItem(selectedIndex) : "";
        
        projectCombo.setItems(projectNames.toArray(new String[]{}));
        
        if (projectNames.contains(selectedProjectName)) {
            projectCombo.select(projectNames.indexOf(selectedProjectName));
        } else {
            projectCombo.select(0);
        }
    }
    
    public IProject getSelectedProject() {
        IProject selectedProject = null;

        int selectionIndex = projectCombo.getSelectionIndex();
        if (selectionIndex != -1) {
            String selectedProjectName = projectCombo.getItem(selectionIndex);
            selectedProject = projectMap.get(selectedProjectName);
        }

        return selectedProject;
    }

    private static String markdown(String text) {
        if( text == null || text.length() == 0 ) {
            return text;
        }
        
        Configuration config = Configuration.builder().
                forceExtentedProfile().
                setCodeBlockEmitter(new CeylonBlockEmitter()).
                setSpecialLinkEmitter(new UnlinkedSpanEmitter()).
                build();
        
        return Processor.process(text, config);
    }

    @Override
    public void dispose() {
        ResourcesPlugin.getWorkspace().removeResourceChangeListener(updateProjectComboListener);
    }
    
    public void setProject(IProject project) {
        if (project!=null) {
            int index = Arrays.asList(projectCombo.getItems())
                    .indexOf(project.getName());
            projectCombo.select(index);
            updateBeforeSearch(true);
            moduleSearchManager.searchModules(searchCombo.getText());
        }
    }

    @Override
    public boolean show(ShowInContext context) {
        ISelection selection = context.getSelection();
        if (selection instanceof IStructuredSelection) {
            IStructuredSelection ss = (IStructuredSelection) selection;
            Object first = ss.getFirstElement();
            if (first instanceof IProject) {
                setProject((IProject) first);
                return true;
            }
            else if (first instanceof IJavaElement) {
                setProject(((IJavaElement) first).getJavaProject().getProject());
                return true;
            }
            else if (first instanceof IResource) {
                setProject(((IResource) first).getProject());
                return true;
            }
            else if (first instanceof SourceModuleNode) {
                SourceModuleNode mod = (SourceModuleNode) first;
                setProject(mod.getProject());
                return true;
            }
        }
        else {
            IEditorPart editor = EditorUtil.getCurrentEditor();
            if (editor instanceof CeylonEditor) {
                setProject(((CeylonEditor) editor).getParseController().getProject());
                return true;
            }
        }
        return false;
    }
    
}