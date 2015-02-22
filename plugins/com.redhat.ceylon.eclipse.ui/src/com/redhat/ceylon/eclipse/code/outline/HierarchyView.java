package com.redhat.ceylon.eclipse.code.outline;

import static com.redhat.ceylon.compiler.typechecker.model.Util.isAbstraction;
import static com.redhat.ceylon.eclipse.code.complete.CodeCompletions.getLabelDescriptionFor;
import static com.redhat.ceylon.eclipse.code.complete.CodeCompletions.getQualifiedDescriptionFor;
import static com.redhat.ceylon.eclipse.code.complete.CodeCompletions.getStyledDescriptionFor;
import static com.redhat.ceylon.eclipse.code.complete.CompletionUtil.overloads;
import static com.redhat.ceylon.eclipse.code.editor.Navigation.gotoDeclaration;
import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.getImageForDeclaration;
import static com.redhat.ceylon.eclipse.code.outline.HierarchyMode.HIERARCHY;
import static com.redhat.ceylon.eclipse.code.outline.HierarchyMode.SUBTYPES;
import static com.redhat.ceylon.eclipse.code.outline.HierarchyMode.SUPERTYPES;
import static com.redhat.ceylon.eclipse.code.resolve.JavaHyperlinkDetector.gotoJavaNode;
import static com.redhat.ceylon.eclipse.ui.CeylonPlugin.PLUGIN_ID;
import static com.redhat.ceylon.eclipse.ui.CeylonResources.CEYLON_HIER;
import static com.redhat.ceylon.eclipse.ui.CeylonResources.CEYLON_INHERITED;
import static com.redhat.ceylon.eclipse.ui.CeylonResources.CEYLON_SUB;
import static com.redhat.ceylon.eclipse.ui.CeylonResources.CEYLON_SUP;
import static com.redhat.ceylon.eclipse.ui.CeylonResources.EXPAND_ALL;
import static com.redhat.ceylon.eclipse.ui.CeylonResources.GOTO;
import static com.redhat.ceylon.eclipse.ui.CeylonResources.TYPE_MODE;
import static com.redhat.ceylon.eclipse.util.Nodes.findNode;
import static com.redhat.ceylon.eclipse.util.Nodes.getReferencedDeclaration;
import static org.eclipse.ui.PlatformUI.getWorkbench;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.internal.ui.JavaPluginImages;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ViewForm;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.eclipse.ui.part.ViewPart;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.DeclarationWithProximity;
import com.redhat.ceylon.compiler.typechecker.model.Referenceable;
import com.redhat.ceylon.compiler.typechecker.model.Scope;
import com.redhat.ceylon.compiler.typechecker.model.TypeDeclaration;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.code.parse.CeylonParseController;
import com.redhat.ceylon.eclipse.code.preferences.CeylonPreferencePage;
import com.redhat.ceylon.eclipse.core.model.JavaClassFile;
import com.redhat.ceylon.eclipse.ui.CeylonPlugin;
import com.redhat.ceylon.eclipse.ui.CeylonResources;
import com.redhat.ceylon.eclipse.util.EditorUtil;

public class HierarchyView extends ViewPart {

    private static final ImageRegistry imageRegistry = 
            CeylonPlugin.getInstance().getImageRegistry();
    private static final Image GOTO_IMAGE = 
            imageRegistry.get(GOTO);
    static final Image INHERITED_IMAGE = 
            imageRegistry.get(CEYLON_INHERITED);
    private static final Image SORT_IMAGE = 
            imageRegistry.get(TYPE_MODE);
    
    private CeylonHierarchyLabelProvider labelProvider;
    private CeylonHierarchyContentProvider contentProvider;
    private MembersLabelProvider membersLabelProvider;
    private MembersContentProvider membersContentProvider;
    
    private static class History {
        private IProject project;
        private CeylonHierarchyNode proxy;
        private History(HierarchyInput input) {
            this.project = input.project;
            this.proxy = new CeylonHierarchyNode(input.declaration);
        }
        private Declaration declaration() {
            return proxy.getDeclaration(project);
        }
    }
    
    private List<History> history = 
            new ArrayList<History>();
    
    private void addToHistory(HierarchyInput input) {
        history.add(0, new History(input));
        if (history.size()>10) {
            history.remove(10);
        }
    }
    
    private TreeViewer treeViewer;
    private TableViewer tableViewer;
    
    private ModeAction hierarchyAction =
            new ModeAction("Hierarchy", 
                    "Switch to hierarchy mode", 
                    CEYLON_HIER, HIERARCHY);
    private ModeAction supertypesAction = 
            new ModeAction("Supertypes", 
                    "Switch to supertypes mode", 
                    CEYLON_SUP, SUPERTYPES);
    private ModeAction subtypesAction =
            new ModeAction("Subtypes", 
                    "Switch to subtypes mode", 
                    CEYLON_SUB, SUBTYPES);
    
    private IProject project;
    
    private CLabel title;
    
    private boolean showInherited;
    private ViewForm viewForm;
    
    private IPropertyChangeListener propertyChangeListener;
    
    @Override
    public void init(IViewSite site) throws PartInitException {
        super.init(site);
        propertyChangeListener = new IPropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent event) {
                treeViewer.refresh();
                tableViewer.refresh();
            }
        };
        EditorUtil.getPreferences().addPropertyChangeListener(propertyChangeListener);
    }
    
    @Override
    public void dispose() {
        super.dispose();
        if (propertyChangeListener!=null) {
            EditorUtil.getPreferences().removePropertyChangeListener(propertyChangeListener);
            propertyChangeListener = null;
        }
    }
    
    void toggle() {
        showInherited=!showInherited;
    }
    
    final class HistoryAction extends Action implements IMenuCreator {
        private Menu menu;
        
        HistoryAction() {
            super(null, AS_DROP_DOWN_MENU);
            setMenuCreator(this);
            setToolTipText("Previous Type Hierarchies");
            JavaPluginImages.setLocalImageDescriptors(this, "history_list.gif");
        }
        
        @Override
        public void runWithEvent(Event event) {
            if (history.size()>1) {
                History h = history.remove(1);
                Declaration declaration = h.declaration();
                IProject project = h.project;
                HierarchyView.this.project = project;
                title.setImage(getImageForDeclaration(declaration));
                title.setText(getName(declaration));
                tableViewer.setInput(declaration);
                treeViewer.setInput(new HierarchyInput(declaration, project));
                HierarchyView.this.setDescription(declaration);
                history.add(0, h);
            }
        }

        private String getName(Declaration declaration) {
            String name = declaration.getName();
            return declaration.isClassOrInterfaceMember() ?
                    ((Declaration) declaration.getContainer()).getName() + '.' + name
                    : name;
        }
        
        @Override
        public Menu getMenu(Menu parent) {
            return null;
        }
        
        @Override
        public Menu getMenu(Control parent) {
            if (menu!=null) menu.dispose();
            menu = new Menu(parent);
            for (final History h: history) {
                final MenuItem item = new MenuItem(menu, SWT.PUSH);
                final Declaration declaration = h.declaration();
                if (declaration!=null) {
                    final Image image =
                            getImageForDeclaration(declaration);
                    final IProject project = h.project;
                    item.setText(getName(declaration));
                    item.setImage(image);
                    item.addSelectionListener(new SelectionAdapter() {
                        @Override
                        public void widgetSelected(SelectionEvent e) {
                            HierarchyView.this.project = project;
                            title.setImage(item.getImage());
                            title.setText(item.getText());
                            tableViewer.setInput(declaration);
                            treeViewer.setInput(new HierarchyInput(declaration, project));
                            HierarchyView.this.setDescription(declaration);
                            history.remove(h);
                            history.add(0, h);
                        }
                    });
                }
            }
            new MenuItem(menu, SWT.SEPARATOR);
            MenuItem clear = new MenuItem(menu, SWT.PUSH);
            clear.setText("Clear History");
            clear.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    history.clear();
                }
            });
            return menu;
        }
        
        @Override
        public void dispose() {
            if (menu!=null) menu.dispose();
        }
        
    }

    private final class MemberSorter extends ViewerSorter {
        private boolean sortByType;
        @Override
        public int compare(Viewer viewer, Object x, Object y) {
            if (sortByType) {
                int result = super.compare(viewer, 
                        ((Declaration) x).getContainer(), 
                        ((Declaration) y).getContainer());
                if (result!=0) return result;
            }
            return super.compare(viewer, x, y);
        }
        public void toggle() {
            sortByType = !sortByType;
        }
    }

    private final class MembersContentProvider 
            implements IStructuredContentProvider {
        
        @Override
        public void inputChanged(Viewer viewer, 
                Object oldInput, Object newInput) {}

        @Override
        public void dispose() {}

        @Override
        public Object[] getElements(Object inputElement) {
            if (inputElement instanceof TypeDeclaration) {
                TypeDeclaration declaration = 
                        (TypeDeclaration) inputElement;
                ArrayList<Declaration> list = 
                        new ArrayList<Declaration>();
                if (showInherited) {
                    Collection<DeclarationWithProximity> children = 
                            declaration.getMatchingMemberDeclarations(
                                    declaration.getUnit(), //TODO: is this correct??
                                    declaration, "", 0)
                                    .values();
                    for (DeclarationWithProximity dwp: children) {
                        for (Declaration dec: 
                            overloads(dwp.getDeclaration())) {
                            list.add(dec);
                        }
                    }
                }
                else {
                    for (Declaration d: declaration.getMembers()) {
                        if (!isAbstraction(d)) {
                            list.add(d);
                        }
                    }
                }
                return list.toArray();
            }
            else {
                return new Object[0];
            }
        }

    }

    class MembersLabelProvider extends StyledCellLabelProvider 
            implements DelegatingStyledCellLabelProvider.IStyledLabelProvider, 
                       ILabelProvider {

        @Override
        public void addListener(ILabelProviderListener listener) {}

        @Override
        public void dispose() {}

        @Override
        public boolean isLabelProperty(Object element, String property) {
            return false;
        }

        @Override
        public void removeListener(ILabelProviderListener listener) {}

        @Override
        public Image getImage(Object element) {
            return getImageForDeclaration((Declaration) element);
        }

        @Override
        public String getText(Object element) {
            Declaration dec = (Declaration) element;
            String desc = getLabelDescriptionFor(dec);
            Scope container = dec.getContainer();
            if (showInherited && 
                    container instanceof Declaration) {
                desc += " - " + ((Declaration) container).getName();
            }
            return desc;
        }

        @Override
        public StyledString getStyledText(Object element) {
            Declaration dec = (Declaration) element;
            return showInherited ? 
                    getQualifiedDescriptionFor(dec) :
                    getStyledDescriptionFor(dec);
            /*StyledString desc = 
                    getStyledDescriptionFor(dec);
            Scope container = dec.getContainer();
            if (showInherited && 
                    container instanceof Declaration) {
                desc.append(" - ", Highlights.PACKAGE_STYLER)
                    .append(((Declaration) container).getName(), 
                            Highlights.TYPE_STYLER);
            }
            return desc;*/
        }

        @Override
        public void update(ViewerCell cell) {
            Object element = cell.getElement();
            if (element!=null) {
                StyledString styledText = getStyledText(element);
                cell.setText(styledText.toString());
                cell.setStyleRanges(styledText.getStyleRanges());
                cell.setImage(getImage(element));
                super.update(cell);
            }
        }

    }
    
    private void gotoCeylonOrJavaDeclaration(Declaration dec) {
        if (dec.getUnit() instanceof JavaClassFile) { //TODO: is this right?!
            gotoJavaNode(dec);
        }
        else {
            gotoDeclaration(dec, project);
        }
    }
    
    @Override
    public void createPartControl(Composite parent) {
        setContentDescription("");
        final SashForm sash = new SashForm(parent, 
                SWT.HORIZONTAL | SWT.SMOOTH);
        sash.addControlListener(new ControlListener() {
            boolean reentrant;
            @Override
            public void controlResized(ControlEvent e) {
                if (reentrant) return;
                reentrant = true;
                try {
                    Rectangle bounds = sash.getBounds();
                    IActionBars actionBars = getViewSite()
                        .getActionBars();
                    IToolBarManager toolBarManager = 
                            actionBars.getToolBarManager();
                    if (bounds.height>bounds.width) {
                        if (sash.getOrientation()!=SWT.VERTICAL) {
                            sash.setOrientation(SWT.VERTICAL);
                            createMainToolBar(toolBarManager);
                            toolBarManager.update(false);
                            viewForm.setTopLeft(null);
                        }
                    }
                    else {
                        if (sash.getOrientation()!=SWT.HORIZONTAL) {
                            sash.setOrientation(SWT.HORIZONTAL);
                            toolBarManager.removeAll();
                            toolBarManager.update(false);
                            ToolBarManager tbm = 
                                    new ToolBarManager(SWT.NONE);
                            createMainToolBar(tbm);
                            tbm.createControl(viewForm);
                            viewForm.setTopLeft(tbm.getControl());
                        }
                    }
                    actionBars.updateActionBars();
                }
                finally {
                    reentrant = false;
                }
            }
            @Override
            public void controlMoved(ControlEvent e) {}
        });
        
        createTreeMenu(createTree(sash));
        createTableMenu(createTable(sash));
        
        IActionBars actionBars = getViewSite().getActionBars();
        IMenuManager menuManager = actionBars.getMenuManager();
        menuManager.add(new ExpandAllAction());
        Action configureAction =
        new Action("Configure Labels...", 
                CeylonPlugin.getInstance().getImageRegistry()
                .getDescriptor(CeylonResources.CONFIG_LABELS)) {
            @Override
            public void run() {
                PreferencesUtil.createPreferenceDialogOn(
                        getSite().getShell(), 
                        CeylonPreferencePage.ID, 
                        new String[] {CeylonPreferencePage.ID}, 
                        null).open();
            }
        };
        menuManager.add(new Separator());
        menuManager.add(configureAction);
    }

    private class ExpandAllAction extends Action {

        private ExpandAllAction() {
            super("Expand All");
            setToolTipText("Expand All");
            
            ImageDescriptor desc = imageRegistry
                    .getDescriptor(EXPAND_ALL);
            setHoverImageDescriptor(desc);
            setImageDescriptor(desc);
        }

        @Override
        public void run() {
            if (treeViewer != null) {
                treeViewer.expandAll();
            }
        }
        
    }

    private Tree createTree(SashForm sash) {
        viewForm = new ViewForm(sash, SWT.FLAT);
        final Tree tree = new Tree(viewForm, SWT.SINGLE);
        GridData gd = new GridData(GridData.FILL_BOTH);
        gd.heightHint = tree.getItemHeight() * 12;
        tree.setLayoutData(gd);
        viewForm.setContent(tree);
        treeViewer = new TreeViewer(tree);
        contentProvider = 
                new CeylonHierarchyContentProvider(getSite(), 
                        "Hierarchy");
        labelProvider = 
                new CeylonHierarchyLabelProvider() {
            @Override
            IProject getProject() {
                return project;
            }
            @Override
            boolean isShowingRefinements() {
                return contentProvider.isShowingRefinements();
            }
        };
        treeViewer.setContentProvider(contentProvider);
        treeViewer.setLabelProvider(labelProvider);
        treeViewer.setAutoExpandLevel(getDefaultLevel());
        treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                TreeSelection selection = 
                        (TreeSelection) event.getSelection();
                CeylonHierarchyNode firstElement = 
                        (CeylonHierarchyNode) selection.getFirstElement();
                if (firstElement!=null) {
                    Declaration dec = firstElement.getDeclaration(project);
                    if (dec!=null) {
                        title.setImage(getImageForDeclaration(dec));
                        title.setText(dec.getName());
                        tableViewer.setInput(dec);
                    }
                }
            }
        });
        treeViewer.addDoubleClickListener(new IDoubleClickListener() {
            @Override
            public void doubleClick(DoubleClickEvent event) {
                TreeSelection selection = 
                        (TreeSelection) event.getSelection();
                CeylonHierarchyNode firstElement = 
                        (CeylonHierarchyNode) selection.getFirstElement();
                firstElement.gotoHierarchyDeclaration(project, null);
            }
        });
        return tree;
    }

    private Table createTable(SashForm sash) {
        ViewForm viewForm = new ViewForm(sash, SWT.FLAT);
        tableViewer = new TableViewer(viewForm, 
                SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL);
        GridData gd = new GridData(GridData.FILL_BOTH);
//        gd.heightHint = tableViewer.getTable().getItemHeight() * 12;
        tableViewer.getTable().setLayoutData(gd);
        viewForm.setContent(tableViewer.getTable());
        title = new CLabel(viewForm, SWT.NONE);
        ToolBar toolBar = new ToolBar(viewForm, SWT.NONE);
        ToolItem toolItem = new ToolItem(toolBar, SWT.CHECK);
        toolItem.setImage(INHERITED_IMAGE);
        toolItem.setToolTipText("Show Inherited Members");
        toolItem.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                toggle();
                tableViewer.refresh();
            }
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {}
        });
        toolItem = new ToolItem(toolBar, SWT.CHECK);
        toolItem.setImage(SORT_IMAGE);
        toolItem.setToolTipText("Sort Members by Declaring Type");
        final MemberSorter sorter = new MemberSorter();
        tableViewer.setSorter(sorter);
        toolItem.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                sorter.toggle();
                tableViewer.refresh();
            }
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {}
        });
        viewForm.setTopRight(toolBar);
        viewForm.setTopLeft(title);
        viewForm.setTopCenter(title);
        membersLabelProvider=new MembersLabelProvider();
        membersContentProvider=new MembersContentProvider();
        tableViewer.setLabelProvider(membersLabelProvider);
        tableViewer.setContentProvider(membersContentProvider);
        tableViewer.addDoubleClickListener(new IDoubleClickListener() {
            @Override
            public void doubleClick(DoubleClickEvent event) {
                StructuredSelection selection = 
                        (StructuredSelection) event.getSelection();
                Declaration firstElement = 
                        (Declaration) selection.getFirstElement();
                gotoCeylonOrJavaDeclaration(firstElement);
            }
        });
        return tableViewer.getTable();
    }

    private void createMainToolBar(IToolBarManager toolBarManager) {
        toolBarManager.add(hierarchyAction);
        toolBarManager.add(supertypesAction);
        toolBarManager.add(subtypesAction);
        updateActions(contentProvider.getMode());
        toolBarManager.add(new ExpandAllAction());
        toolBarManager.add(new HistoryAction());
    }

    private void createTreeMenu(final Tree tree) {
        Menu menu = new Menu(tree);
        MenuItem item = new MenuItem(menu, SWT.PUSH);
        item.setText("Focus on Selection");
        item.setImage(getTitleImage());
        tree.setMenu(menu);
        item.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                TreeSelection selection = 
                        (TreeSelection) treeViewer.getSelection();
                Object firstElement = selection.getFirstElement();
                if (firstElement instanceof CeylonHierarchyNode) {
                    CeylonHierarchyNode node = 
                            (CeylonHierarchyNode) firstElement;
                    Declaration declaration = node.getDeclaration(project);
                    HierarchyInput input = new HierarchyInput(declaration, project);
                    addToHistory(input);
                    treeViewer.setInput(input);
                    setDescription(declaration);
                }
            }
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {}
        });
        item = new MenuItem(menu, SWT.PUSH);
        item.setText("Go to Selection");
        item.setImage(GOTO_IMAGE);
        tree.setMenu(menu);
        item.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                TreeSelection selection = 
                        (TreeSelection) treeViewer.getSelection();
                Object firstElement = selection.getFirstElement();
                if (firstElement instanceof CeylonHierarchyNode) {
                    CeylonHierarchyNode node = 
                            (CeylonHierarchyNode) firstElement;
                    Declaration declaration = node.getDeclaration(project);
                    gotoCeylonOrJavaDeclaration(declaration);
                }
            }
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {}
        });
    }

    private void createTableMenu(final Table table) {
        Menu menu = new Menu(table);
        MenuItem item = new MenuItem(menu, SWT.PUSH);
        item.setText("Focus on Selection");
        item.setImage(getTitleImage());
        table.setMenu(menu);
        item.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                StructuredSelection selection = 
                        (StructuredSelection) tableViewer.getSelection();
                Object firstElement = selection.getFirstElement();
                if (firstElement instanceof Declaration) {
                    Declaration declaration = (Declaration) firstElement;
                    HierarchyInput input = new HierarchyInput(declaration, project);
                    addToHistory(input);
                    treeViewer.setInput(input);
                    setDescription(declaration);
                }
            }
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {}
        });
        item = new MenuItem(menu, SWT.PUSH);
        item.setText("Go to Selection");
        item.setImage(GOTO_IMAGE);
        table.setMenu(menu);
        item.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                StructuredSelection selection = 
                        (StructuredSelection) tableViewer.getSelection();
                Object firstElement = selection.getFirstElement();
                if (firstElement instanceof Declaration) {
                    gotoCeylonOrJavaDeclaration((Declaration) firstElement);
                }
            }
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {}
        });
    }

    private int getDefaultLevel() {
        return 4;
    }
    
    private void updateActions(HierarchyMode mode) {
        hierarchyAction.setChecked(mode==HIERARCHY);
        supertypesAction.setChecked(mode==SUPERTYPES);
        subtypesAction.setChecked(mode==SUBTYPES);
    }

    private void update() {
        setDescription((Declaration) tableViewer.getInput());
        treeViewer.getControl().setRedraw(false);
        // refresh viewer to re-filter
        treeViewer.refresh();
        reveal();
        //fTreeViewer.expandAll();
//        selectFirstMatch(); //TODO select the main declaration instead!
        treeViewer.getControl().setRedraw(true);
    }
    
    private void reveal() {
        treeViewer.expandToLevel(getDefaultLevel());
    }

    @Override
    public void setFocus() {}

    public void focusOnSelection(CeylonEditor editor) {
        CeylonParseController cpc = editor.getParseController();
        Node node = findNode(cpc.getRootNode(), 
                editor.getSelection().getOffset());
        Referenceable dec = getReferencedDeclaration(node);
        if (dec instanceof Declaration) {
            focusOn(cpc.getProject(), (Declaration) dec);
        }
    }

    public void focusOn(IProject project, Declaration dec) {
        this.project = project;
        if (dec!=null) {
            title.setImage(getImageForDeclaration(dec));
            title.setText(dec.getName());
            tableViewer.setInput(dec);
            HierarchyInput input = new HierarchyInput(dec, project);
            addToHistory(input);
            treeViewer.setInput(input);
            setDescription(dec);
        }
    }

    private void setDescription(Declaration dec) {
//        setContentDescription("Displaying " +
//                contentProvider.getMode().name().toLowerCase() + 
//                " of '" + dec.getName() + "'");
        setContentDescription(contentProvider.getDescription());
    }

    public static HierarchyView showHierarchyView() 
            throws PartInitException {
        IWorkbenchPage page = getWorkbench()
                .getActiveWorkbenchWindow()
                .getActivePage();
        return (HierarchyView) page.showView(PLUGIN_ID + 
                ".view.HierarchyView");
    }
    
    /*private class MembersAction extends Action {
        MembersAction() {
            super("Show Inherited Members");
            setToolTipText("Show inherited members");
            setImageDescriptor(CeylonPlugin.getInstance()
                    .getImageRegistry()
                    .getDescriptor(CEYLON_INHERITED));
        }
        @Override
        public void run() {
            membersContentProvider.toggle();
            update();
            setChecked(!isChecked());
        }
    }*/

    private class ModeAction extends Action {
        HierarchyMode mode;

        ModeAction(String label, String tooltip, 
                String imageKey, HierarchyMode mode) {
            super(label);
            setToolTipText(tooltip);
            setImageDescriptor(CeylonPlugin.getInstance()
                    .getImageRegistry()
                    .getDescriptor(imageKey));
            this.mode = mode;
        }

        @Override
        public void run() {
            contentProvider.setMode(mode);
            update();
            updateActions(mode);
        }

    }

}
