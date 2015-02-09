package com.redhat.ceylon.eclipse.code.moduledependencies;

import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getModuleDependenciesForProject;
import static com.redhat.ceylon.eclipse.ui.CeylonPlugin.PLUGIN_ID;

import org.eclipse.core.resources.IProject;
import org.eclipse.draw2d.Connection;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.PolygonDecoration;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.IShowInTarget;
import org.eclipse.ui.part.ShowInContext;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.zest.core.viewers.GraphViewer;
import org.eclipse.zest.core.viewers.IGraphContentProvider;
import org.eclipse.zest.core.viewers.ISelfStyleProvider;
import org.eclipse.zest.core.widgets.GraphConnection;
import org.eclipse.zest.core.widgets.GraphNode;
import org.eclipse.zest.core.widgets.ZestStyles;
import org.eclipse.zest.layouts.LayoutStyles;
import org.eclipse.zest.layouts.algorithms.HorizontalTreeLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.TreeLayoutAlgorithm;

import com.redhat.ceylon.compiler.typechecker.model.Module;
import com.redhat.ceylon.eclipse.core.model.JDTModule;
import com.redhat.ceylon.eclipse.core.model.ModuleDependencies;
import com.redhat.ceylon.eclipse.core.model.ModuleDependencies.Dependency;
import com.redhat.ceylon.eclipse.core.model.ModuleDependencies.ModuleReference;
import com.redhat.ceylon.eclipse.core.model.ModuleDependencies.ModuleWeakReference;
import com.redhat.ceylon.eclipse.ui.CeylonResources;

public class DependencyGraphView extends ViewPart implements IShowInTarget {

    private IProject project;
    
    static final String ID = PLUGIN_ID + ".view.DependencyGraphView";

    private GraphViewer viewer;

    public void setProject(IProject project) {
        this.project = project;
        init();
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
        
        @Override
        public void selfStyleConnection(Object element,
                GraphConnection connection) {
            connection.setLineWidth(1);
            if (element instanceof Dependency) {
                if (((Dependency) element).optional) {
                    connection
                    .setConnectionStyle(ZestStyles.CONNECTIONS_DOT);
                    connection.setTooltip(new Label("Optional"));
                }
                if (((Dependency) element).exported) {
                    connection.setLineWidth(4);
                    connection.setTooltip(new Label("Exported"));
                }
            }
            connection.setConnectionStyle(ZestStyles.CONNECTIONS_DIRECTED);
            Connection figure = connection.getConnectionFigure();
            PolygonDecoration decoration = new PolygonDecoration();
            decoration.setFill(true);
            decoration.setLineWidth(connection.getLineWidth());
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
                                "Unavailable module\n(not visible from any project module)"));
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
        setContentDescription("Ceylon module dependencies");
        createViewer(parent);
        createMenu();    
    }

    protected void createViewer(Composite parent) {
        viewer = new GraphViewer(parent, SWT.NONE);
        viewer.getGraphControl()
                .setNodeStyle(ZestStyles.NODES_NO_LAYOUT_RESIZE);
        viewer.setContentProvider(new GraphContentProvider());
        viewer.setLabelProvider(new GraphLabelProvider());
        TreeLayoutAlgorithm tla = 
                new HorizontalTreeLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING);
        viewer.setLayoutAlgorithm(tla);
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
    
    private void init() {
        if (project==null) {
            return;
        }
        ModuleDependencies dependencies = 
                getModuleDependenciesForProject(project);
        if (dependencies == null) {
            return;
        }
        /*if (dependencies.getAllDependencies().isEmpty()) {
            IWorkbenchWindow activeWorkbenchWindow = 
                    getWorkbench().getActiveWorkbenchWindow();
            MessageBox messageBox = new MessageBox(activeWorkbenchWindow.getShell());
            messageBox.setText("Ceylon Module Dependencies");
            messageBox.setMessage("There is no declared module dependency within the project : '"  + 
                    fProject.getName() + "'.");
            messageBox.open();
            return;
        }*/
        setContentDescription("Ceylon module dependencies for project '" + 
                project.getName() + "'");
        viewer.setInput(dependencies);
        viewer.refresh();
        viewer.applyLayout();
    }
    
    @Override
    public void setFocus() {}

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
            else if (first instanceof IJavaProject) {
                setProject(((IJavaProject) first).getProject());
                return true;
            }
        }        
        return false;
    }
    
}