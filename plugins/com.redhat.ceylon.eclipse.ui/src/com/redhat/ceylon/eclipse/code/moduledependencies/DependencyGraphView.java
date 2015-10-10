package com.redhat.ceylon.eclipse.code.moduledependencies;

import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getModule;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getModuleDependenciesForProject;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getProjectSourceModules;
import static com.redhat.ceylon.eclipse.ui.CeylonPlugin.PLUGIN_ID;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.draw2d.Connection;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.PolygonDecoration;
import org.eclipse.draw2d.Polyline;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.part.IShowInTarget;
import org.eclipse.ui.part.ShowInContext;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.zest.core.viewers.GraphViewer;
import org.eclipse.zest.core.viewers.IGraphContentProvider;
import org.eclipse.zest.core.viewers.ISelfStyleProvider;
import org.eclipse.zest.core.widgets.GraphConnection;
import org.eclipse.zest.core.widgets.GraphItem;
import org.eclipse.zest.core.widgets.GraphNode;
import org.eclipse.zest.core.widgets.ZestStyles;
import org.eclipse.zest.layouts.LayoutAlgorithm;
import org.eclipse.zest.layouts.LayoutStyles;
import org.eclipse.zest.layouts.algorithms.HorizontalTreeLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.RadialLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.SpringLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.TreeLayoutAlgorithm;

import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.code.navigator.SourceModuleNode;
import com.redhat.ceylon.eclipse.code.parse.CeylonParseController;
import com.redhat.ceylon.eclipse.core.builder.CeylonBuilder;
import com.redhat.ceylon.eclipse.core.builder.CeylonNature;
import com.redhat.ceylon.eclipse.core.model.ICeylonModelListener;
import com.redhat.ceylon.eclipse.core.model.JDTModule;
import com.redhat.ceylon.eclipse.ui.CeylonPlugin;
import com.redhat.ceylon.eclipse.ui.CeylonResources;
import com.redhat.ceylon.eclipse.util.EditorUtil;
import com.redhat.ceylon.ide.common.model.ModuleDependencies;
import com.redhat.ceylon.ide.common.model.ModuleDependencies.Dependency;
import com.redhat.ceylon.ide.common.model.ModuleDependencies.ModuleReference;
import com.redhat.ceylon.ide.common.model.ModuleDependencies.ModuleWeakReference;
import com.redhat.ceylon.model.typechecker.model.Module;
import com.redhat.ceylon.model.typechecker.model.ModuleImport;


public class DependencyGraphView extends ViewPart implements IShowInTarget, ICeylonModelListener {

    private enum Layout {
        spring("Spring"),
        radial("Radial"),
        horizonatlTree("Horizontal Tree"),
        verticalTree("Vertical Tree");
        
        String label;
        private Layout(String label) {
            this.label = label;
        }
        
        @Override
        public String toString() {
            return label;
        }
        
        public LayoutAlgorithm getLayoutAlgorithm() {
            switch(this) {
                case spring : 
                    SpringLayoutAlgorithm sla = new SpringLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING);
                    return sla;
                case radial : 
                    RadialLayoutAlgorithm rla = new RadialLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING);
                    rla.setRangeToLayout(-1.41, 1.41);
                    return rla;
                case horizonatlTree : 
                    return new HorizontalTreeLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING);
                case verticalTree : 
                    return new TreeLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING);
            }
            return null;
        }
        
        public final static Layout[] values = Layout.values();
    }
    
    static final String ID = PLUGIN_ID + ".view.DependencyGraphView";

    private IProject project;
    private Map<String, IProject> projectMap = new ConcurrentHashMap<String, IProject>();
    
    private IResourceChangeListener updateProjectComboListener;
    
    private GraphViewer viewer;
    private Combo projectCombo;
    private Combo viewCombo;
    private Image overridesImage = null;

    
    public void setProject(IProject project) {
        if (project!=null) {
            this.project = project;
            int index = Arrays.asList(projectCombo.getItems())
                    .indexOf(project.getName());
            projectCombo.select(index);
            init();
        }
    }
    
    class GraphContentProvider implements IGraphContentProvider {
        @Override
        public void dispose() {}
        
        @Override
        public void inputChanged(Viewer viewer, 
                Object oldInput, Object newInput) {}
        
        @Override
        public Object getSource(Object rel) {
            assert (rel instanceof Dependency);
            return ((Dependency) rel).getSource();
        }
        
        @Override
        public Object getDestination(Object rel) {
            assert (rel instanceof Dependency);
            return ((Dependency) rel).getTarget();
        }
        
        @Override
        public Object[] getElements(Object input) {
            return ((ModuleDependencies) input).getAllDependencies().toArray();
        }
    }

    class GraphLabelProvider extends LabelProvider implements ISelfStyleProvider {
        
        @Override
        public String getText(Object element) {
            if (element instanceof ModuleWeakReference) {
                Module module = ((ModuleWeakReference) element).get();
                if (module != null) {
                    return module.getNameAsString() + "\n" + module.getVersion();
                }
            }
            if (element instanceof ModuleReference) {
                return ((ModuleReference) element).getIdentifier();
            }
            return "";
        }
        
        private String importPrefix(boolean optional, boolean export, boolean initialUpperCase) {
            if (optional) {
                return (initialUpperCase ? "O" : "o") + "ptional import";
            } else if (export) {
                return (initialUpperCase ? "E" : "e") + "xported import";
            } else {
                return (initialUpperCase ? "L" : "l") + "ocal import";
            }
        }
        
        @Override
        public void selfStyleConnection(Object element,
                GraphConnection connection) {
            connection.setLineWidth(1);
            
            String tooltipText = "";
            boolean overriding = false;
            if (element instanceof Dependency) {
                Dependency dep = (Dependency) element;
                if (dep.optional) {
                    connection.setConnectionStyle(ZestStyles.CONNECTIONS_DOT);
                }
                if (dep.exported) {
                    connection.setLineWidth(4);
                }
                tooltipText = importPrefix(dep.optional, dep.exported, true);
                Object source = dep.getSource();
                Object target = dep.getTarget();
                if (source instanceof ModuleWeakReference 
                        && target instanceof ModuleWeakReference) {
                    Module sourceMod = ((ModuleWeakReference) source).get();
                    Module targetMod = ((ModuleWeakReference) target).get();
                    if (sourceMod != null 
                            && targetMod != null) {
                        for (ModuleImport mi : sourceMod.getImports()) {
                            if (mi.getModule().equals(targetMod)) {
                                ModuleImport overridenImport = mi.getOverridenModuleImport();
                                if (overridenImport != null) {
                                    overriding = true;
                                    tooltipText += "\nOverrides the import of '" + 
                                    overridenImport.getModule().getNameAsString() + 
                                    "', version " + overridenImport.getModule().getVersion();
                                }
                                break;
                            }
                        }
                        List<ModuleImport> overridenModuleImports = sourceMod.getOverridenImports();
                        if (overridenModuleImports != null) {
                            boolean isChanged = true;
                            for (ModuleImport mi : overridenModuleImports) {
                                if (mi.getModule().equals(targetMod) &&
                                        mi.isExport() == dep.exported &&
                                        mi.isOptional() == dep.optional) {
                                    isChanged = false;
                                    break;
                                }
                            }
                            if (isChanged) {
                                overriding = true;
                                tooltipText += "\nModified by the dependency overrides of the importing module";
                            }
                        }
                    }
                }
            }
            if (overriding) {
                connection.setLineColor(connection.getDisplay().getSystemColor(SWT.COLOR_BLUE));
                connection.setHighlightColor(connection.getDisplay().getSystemColor(SWT.COLOR_DARK_BLUE));
                connection.setImage(overridesImage);
            } else {
                connection.setLineColor(connection.getDisplay().getSystemColor(SWT.COLOR_GRAY));
                connection.setHighlightColor(connection.getDisplay().getSystemColor(SWT.COLOR_DARK_GRAY));
            }
            Label tooltip = new Label(tooltipText);
            connection.setTooltip(tooltip);
            connection.setConnectionStyle(ZestStyles.CONNECTIONS_DIRECTED);
            Connection figure = connection.getConnectionFigure();
            if (figure instanceof Polyline) {
                ((Polyline) figure).setAntialias(SWT.ON);
            }
            PolygonDecoration decoration = new PolygonDecoration();
            decoration.setFill(true);
            decoration.setLineWidth(connection.getLineWidth());
            decoration.setLineJoin(SWT.JOIN_ROUND);
            decoration.setAntialias(SWT.ON);
            ((PolylineConnection) figure).setTargetDecoration(decoration);
        }
        
        @Override
        public Image getImage(Object element) {
            if (element instanceof ModuleReference) {
                return CeylonResources.MODULE;
            }
            else {
                return null;
            }
        }
        
        @Override
        public void selfStyleNode(Object element, GraphNode node) {
            if (element instanceof ModuleWeakReference) {
                Module module = ((ModuleWeakReference) element).get();
                if (module == null) {
                    node.setVisible(false);
                }
                else {
                    node.setForegroundColor(node.getDisplay()
                            .getSystemColor(SWT.COLOR_BLACK));
                    if (!module.isAvailable()) {
                        node.setBackgroundColor(node.getDisplay()
                                .getSystemColor(SWT.COLOR_GRAY));
                        node.setHighlightColor(node.getDisplay()
                                .getSystemColor(SWT.COLOR_DARK_GRAY));
                        node.setTooltip(new Label(
                                "Unresolved module\n(not visible from any project module)"));
                    }
                    else if (module.isJava()) {
                        node.setBackgroundColor(node.getDisplay()
                                .getSystemColor(SWT.COLOR_CYAN));
                        node.setHighlightColor(node.getDisplay()
                                .getSystemColor(SWT.COLOR_DARK_CYAN));
                        node.setTooltip(new Label(
                                "Java archive module"));
                    }
                    else if (module instanceof JDTModule) {
                        JDTModule jdtModule = (JDTModule) module;
                        if (jdtModule.isCeylonArchive()) {
                            node.setBackgroundColor(node.getDisplay()
                                    .getSystemColor(SWT.COLOR_GREEN));
                            node.setHighlightColor(node.getDisplay()
                                    .getSystemColor(SWT.COLOR_DARK_GREEN));
                            node.setTooltip(new Label(
                                    "Ceylon archive module"));
                        }
                        else if (jdtModule.isProjectModule()) {
                            node.setBackgroundColor(node.getDisplay()
                                    .getSystemColor(SWT.COLOR_YELLOW));
                            node.setHighlightColor(node.getDisplay()
                                    .getSystemColor(SWT.COLOR_DARK_YELLOW));
                            node.setTooltip(new Label(
                                    "Project source module"));
                        }
                    }
                }
            }
        }
    }

    
    @Override
    public void createPartControl(Composite parent) {
        setPartName("Ceylon Module Dependencies");
//        setContentDescription("Ceylon module dependencies");
        parent.setLayout(new GridLayout(4, false));
        initProjectCombo(parent);
        initViewCombo(parent);
        createViewer(parent);
        createMenu();
        setProject(projectMap.get(projectCombo.getText()));
        CeylonBuilder.addModelListener(this);
    }

    LayoutAlgorithm getCurrentLayoutAlgorithm() {
        int index = viewCombo.getSelectionIndex();
        if (index > -1) {
            return Layout.values[index].getLayoutAlgorithm();
        }
        return new HorizontalTreeLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING);
    }
    
    protected void createViewer(Composite parent) {
        Image iconImage = CeylonPlugin.imageRegistry.get(CeylonResources.CEYLON_DELETE_IMPORT);
        overridesImage = new Image(iconImage.getDevice(), iconImage.getImageData().scaledTo(20, 20));
        overridesImage.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_RED));
        viewer = new GraphViewer(parent, SWT.NONE);
        viewer.getGraphControl()
                .setNodeStyle(ZestStyles.NODES_NO_LAYOUT_RESIZE);
        viewer.setContentProvider(new GraphContentProvider());
        viewer.setLabelProvider(new GraphLabelProvider());
        viewer.setLayoutAlgorithm(getCurrentLayoutAlgorithm());
        viewer.getControl().setLayoutData(GridDataFactory.fillDefaults().span(4, 1).grab(true, true).create());
        viewer.addSelectionChangedListener(new ISelectionChangedListener() {
            
            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                ModuleDependencies deps = (ModuleDependencies) viewer.getInput();
                Object selection = event.getSelection();
                if (selection instanceof StructuredSelection) {
                    selection = ((StructuredSelection) selection).getFirstElement();
                }
                if (selection instanceof ModuleReference) {
                    for (Dependency dep : deps.getDirectDependencies((ModuleReference) selection)) {
                        GraphItem item = viewer.findGraphItem((ModuleReference) dep.getTarget());
                        if (item instanceof GraphNode) {
                            item.setVisible(true);
                            item.highlight();
                            item.unhighlight();
                        }
                    }
                    for (Dependency dep : deps.getDirectReverseDependencies((ModuleReference) selection)) {
                        GraphItem item = viewer.findGraphItem((ModuleReference) dep.getSource());
                        if (item instanceof GraphNode) {
                            item.setVisible(true);
                            item.highlight();
                            item.unhighlight();
                        }
                    }
                } else if (selection instanceof Dependency) {
                    Dependency dep = (Dependency) selection;
                    GraphItem item = viewer.findGraphItem((ModuleReference) dep.getSource());
                    if (item instanceof GraphNode) {
                        item.setVisible(true);
                        item.highlight();
                        item.unhighlight();
                    }
                    item = viewer.findGraphItem((ModuleReference) dep.getTarget());
                    if (item instanceof GraphNode) {
                        item.setVisible(true);
                        item.highlight();
                        item.unhighlight();
                    }
                }
            }
        });
    }
    
    private void initViewCombo(Composite parent) {
        org.eclipse.swt.widgets.Label graphLayoutLabel = 
                new org.eclipse.swt.widgets.Label(parent, SWT.RIGHT | SWT.WRAP);
        graphLayoutLabel.setText("Select the graph layout");
        GridData gd = GridDataFactory.swtDefaults().align(SWT.END, SWT.CENTER).create();
        graphLayoutLabel.setLayoutData(gd);
        
        viewCombo = new Combo(parent, SWT.SINGLE | SWT.BORDER | SWT.READ_ONLY);
        viewCombo.setLayoutData(GridDataFactory.swtDefaults().hint(120, SWT.DEFAULT).create());
        viewCombo.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                try {
                    viewer.setLayoutAlgorithm(getCurrentLayoutAlgorithm());
                    viewer.applyLayout();
                } catch(Exception ex){
                    e.doit = false;
                }
            }
        });
        
        List<String> items = new ArrayList<>(Layout.values.length);
        for (Layout layout : Layout.values) {
            items.add(layout.toString());
        }
        viewCombo.setItems(items.toArray(new String[0]));
        viewCombo.setText(viewCombo.getItem(0));
    }
    
    private void initProjectCombo(Composite parent) {
        org.eclipse.swt.widgets.Label projectLabel = 
                new org.eclipse.swt.widgets.Label(parent, SWT.RIGHT | SWT.WRAP);
        projectLabel.setText("Display modules for project");
        GridData gd = GridDataFactory.swtDefaults().align(SWT.END, SWT.CENTER).create();
        projectLabel.setLayoutData(gd);
        
        projectCombo = new Combo(parent, SWT.SINGLE | SWT.BORDER | SWT.READ_ONLY);
        projectCombo.setLayoutData(GridDataFactory.swtDefaults().hint(200, SWT.DEFAULT).create());
        projectCombo.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                setProject(projectMap.get(projectCombo.getText()));
            }
        });
        
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
//        projectNames.add("");
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
    
    protected void createMenu() {
        final IMenuManager partMenu = 
                getViewSite().getActionBars().getMenuManager();
        partMenu.add(new Action("Lay Out") {
            @Override
            public void run() {
                viewer.applyLayout();
            }
        });
        partMenu.add(new Action("Show All") {
            @Override
            public void run() {
                viewer.resetFilters();
                viewer.refresh();
                viewer.applyLayout();
            }
        });
        final MenuManager viewerMenu = new MenuManager();
        viewer.getControl().setMenu(viewerMenu.createContextMenu(viewer.getControl()));
        viewerMenu.setRemoveAllWhenShown(true);
        viewerMenu.addMenuListener(new IMenuListener() {
            @Override
            public void menuAboutToShow(IMenuManager manager) {
                manager.add(new Action("Show Dependencies Only") {
                    @Override
                    public boolean isEnabled() {
                        IStructuredSelection selection = 
                                (IStructuredSelection) 
                                    viewer.getSelection();
                        return selection.size() == 1
                                && selection.getFirstElement() != null;
                    }
                    @Override
                    public void run() {
                        IStructuredSelection selection = 
                                (IStructuredSelection) 
                                    viewer.getSelection();
                        Object first = selection.getFirstElement();
                        if (first instanceof ModuleReference) {
                            final ModuleReference selectedModuleRef = 
                                    (ModuleReference) first;
                            narrowToDependencies(selectedModuleRef);
                        }
                    }
                });
                manager.add(new Action("Show Referencing Modules Only") {
                    @Override
                    public boolean isEnabled() {
                        IStructuredSelection selection = 
                                (IStructuredSelection) viewer.getSelection();
                        return selection.size() == 1
                                && selection.getFirstElement() != null;
                    }
                    @Override
                    public void run() {
                        IStructuredSelection selection = 
                                (IStructuredSelection) viewer.getSelection();
                        Object first = selection.getFirstElement();
                        if (first instanceof ModuleReference) {
                            final ModuleReference selectedModuleRef = 
                                    (ModuleReference) first;
                            narrowToReferences(selectedModuleRef);
                        }
                    }
                });
                manager.add(new Action("Show All") {
                    @Override
                    public void run() {
                        viewer.resetFilters();
                        viewer.refresh();
                        viewer.applyLayout();
                    }
                });
            }
        });
    }
    
    protected void narrowToDependencies(
            final ModuleReference selectedModuleRef) {
        ModuleDependencies moduleDependencies = 
                (ModuleDependencies) viewer.getInput();
        final Iterable<Module> modulesToShow = 
                moduleDependencies.getTransitiveDependencies(selectedModuleRef);
        viewer.setFilters(new ViewerFilter[] { 
                new ViewerFilter() {
                    @Override
                    public boolean select(Viewer viewer, 
                            Object parentElement, Object element) {
                        if (element.equals(selectedModuleRef) || 
                                element instanceof Dependency) {
                            return true;
                        }
                        if (element instanceof ModuleWeakReference) {
                            Module moduleToSort = 
                                    ((ModuleWeakReference) element).get();
                            for (Module moduleToShow : modulesToShow) {
                                if (moduleToShow.equals(moduleToSort)) {
                                    return true;
                                }
                            }
                        }
                        return false;
                    }
                } 
        });
        viewer.refresh();
        viewer.applyLayout();
    }
    
    protected void narrowToReferences(
            final ModuleReference selectedModuleRef) {
        ModuleDependencies moduleDependencies = 
                (ModuleDependencies) viewer.getInput();
        final Iterable<Module> modulesToShow = 
                moduleDependencies.getReferencingModules(selectedModuleRef);
        viewer.setFilters(new ViewerFilter[] { 
                new ViewerFilter() {
                    @Override
                    public boolean select(Viewer viewer, 
                            Object parentElement, Object element) {
                        if (element.equals(selectedModuleRef) || 
                                element instanceof Dependency) {
                            return true;
                        }
                        if (element instanceof ModuleWeakReference) {
                            Module moduleToSort = 
                                    ((ModuleWeakReference) element).get();
                            for (Module moduleToShow : modulesToShow) {
                                if (moduleToShow.equals(moduleToSort)) {
                                    return true;
                                }
                            }
                        }
                        return false;
                    }
                } 
        });
        viewer.refresh();
        viewer.applyLayout();
    }
    
    private void init() {
        if (project==null) {
            return;
        }
        ModuleDependencies dependencies = 
                getModuleDependenciesForProject(project);
        if (dependencies == null) {
            return;
        }
        viewer.setInput(dependencies);
    }
    
    
    
    @Override
    public void dispose() {
        ResourcesPlugin.getWorkspace().removeResourceChangeListener(updateProjectComboListener);
        CeylonBuilder.removeModelListener(this);
    }
    
    @Override
    public void setFocus() {}

    @Override
    public boolean show(ShowInContext context) {
        ISelection selection = context.getSelection();
        if (selection instanceof IStructuredSelection) {
            IStructuredSelection ss = 
                    (IStructuredSelection) selection;
            Object first = ss.getFirstElement();
            if (first instanceof SourceModuleNode) {
                SourceModuleNode module = (SourceModuleNode) first;
                setProject(module.getProject());
                viewer.setSelection(new StructuredSelection(ModuleDependencies.reference(module.getModule())), true);
                return true;
            }
            else if (first instanceof IProject) {
                setProject((IProject) first);
                Collection<Module> modules = getProjectSourceModules(project);
                if (!modules.isEmpty()) {
                    List<ModuleReference> list = new ArrayList<ModuleReference>();
                    for (Module module: modules) {
                        list.add(ModuleDependencies.reference(module));
                    }
                    viewer.setSelection(new StructuredSelection(list), true);
                }
                return true;
            }
            else if (first instanceof IJavaElement) {
                setProject(((IJavaElement) first).getJavaProject().getProject());
                if (first instanceof IPackageFragment) {
                    Module module = getModule((IPackageFragment) first);
                    if (module!=null) {
                        viewer.setSelection(new StructuredSelection(ModuleDependencies.reference(module)), true);
                    }
                }
                return true;
            }
            else if (first instanceof IResource) {
                setProject(((IResource) first).getProject());
                if (first instanceof IFile) {
                    Module module = getModule((IFile) first);
                    if (module!=null) {
                        viewer.setSelection(new StructuredSelection(ModuleDependencies.reference(module)), true);
                    }
                }
                else if (first instanceof IFolder) {
                    Module module = getModule((IFolder) first);
                    if (module!=null) {
                        viewer.setSelection(new StructuredSelection(ModuleDependencies.reference(module)), true);
                    }
                }
                return true;
            }
        }
        else {
            IEditorPart editor = EditorUtil.getCurrentEditor();
            if (editor instanceof CeylonEditor) {
                CeylonParseController controller = 
                        ((CeylonEditor) editor).getParseController();
                setProject(controller.getProject());
                Module module = controller.getLastPhasedUnit().getPackage().getModule();
                viewer.setSelection(new StructuredSelection(ModuleDependencies.reference(module)), true);
                return true;
            }
        }
        return false;
    }

    @Override
    public void modelParsed(final IProject project) {
        Display.getDefault().asyncExec(new Runnable() {
            @Override
            public void run() {
                if (!projectCombo.isDisposed()) {
                    if (project.getName().equals(projectCombo.getText())) {
                        setProject(project);
                    }
                }
            }
        });
    }
    
}