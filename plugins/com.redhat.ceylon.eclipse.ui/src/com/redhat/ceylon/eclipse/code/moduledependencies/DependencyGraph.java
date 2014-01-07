package com.redhat.ceylon.eclipse.code.moduledependencies;

import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.draw2d.Connection;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.PolygonDecoration;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.gef4.geometry.planar.Dimension;
import org.eclipse.gef4.zest.core.viewers.GraphViewer;
import org.eclipse.gef4.zest.core.viewers.IGraphContentProvider;
import org.eclipse.gef4.zest.core.viewers.ISelfStyleProvider;
import org.eclipse.gef4.zest.core.widgets.GraphConnection;
import org.eclipse.gef4.zest.core.widgets.GraphNode;
import org.eclipse.gef4.zest.core.widgets.ZestStyles;
import org.eclipse.gef4.layout.algorithms.TreeLayoutAlgorithm;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;

import com.redhat.ceylon.compiler.typechecker.model.Module;
import com.redhat.ceylon.eclipse.core.builder.CeylonBuilder;
import com.redhat.ceylon.eclipse.core.model.ModuleDependencies;
import com.redhat.ceylon.eclipse.core.model.ModuleDependencies.Dependency;
import com.redhat.ceylon.eclipse.core.model.ModuleDependencies.ModuleReference;
import com.redhat.ceylon.eclipse.core.model.ModuleDependencies.ModuleWeakReference;

public class DependencyGraph implements IWorkbenchWindowActionDelegate {

    private IProject fProject;

    public void dispose() {
    }

    public void init(IWorkbenchWindow window) {
    }

    @SuppressWarnings("unchecked")
    public void run(IAction action) {
        if (fProject == null) {
            return;
        }
        final ModuleDependencies dependencies = CeylonBuilder
                .getModuleDependenciesForProject(fProject);
        if (dependencies == null) {
            return;
        }
        if (dependencies.getAllDependencies().isEmpty()) {
            MessageBox messageBox = new MessageBox(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
            messageBox.setText("Ceylon Module Dependencies");
            messageBox.setMessage("There is no declared module dependency within the project : '"  + fProject.getName() + "'.");
            messageBox.open();
            return;
        }

        final Shell shell = new Shell();

        class GraphContentProvider implements IGraphContentProvider {
            @Override
            public void dispose() {
            }
            @Override
            public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
            }
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
                connection.setDirected(true);
                Connection figure = connection.getConnectionFigure();
                PolygonDecoration decoration = new PolygonDecoration();
                decoration.setFill(true);
                decoration.setLineWidth(connection.getLineWidth());
                ((PolylineConnection) figure).setTargetDecoration(decoration);
            }
            @Override
            public void selfStyleNode(Object element, GraphNode node) {
                if (element instanceof ModuleWeakReference) {
                    Module module = ((ModuleWeakReference) element).get();
                    if (module == null) {
                        node.setVisible(false);
                    }
                    if (module != null && !module.isAvailable()) {
                        node.setBackgroundColor(node.getDisplay()
                                .getSystemColor(SWT.COLOR_RED));
                        node.setTooltip(new Label(
                                "The module is not available due to some error."));
                    }
                }
            }
        }

        shell.setText("Ceylon module dependencies for project '" + fProject.getName() + "'");
        shell.setLayout(new FillLayout());
        shell.setSize(1200, 900);

        final GraphViewer viewer = new GraphViewer(shell, ZestStyles.NONE);
        viewer.getGraphControl()
                .setNodeStyle(ZestStyles.NODES_NO_LAYOUT_RESIZE);
        viewer.setContentProvider(new GraphContentProvider());
        viewer.setLabelProvider(new GraphLabelProvider());
        viewer.setInput(dependencies);

        TreeLayoutAlgorithm tla = new TreeLayoutAlgorithm();
        tla.setDirection(TreeLayoutAlgorithm.LEFT_RIGHT);

        Dimension maxDimension = new Dimension();
        for (GraphNode node : (List<GraphNode>) viewer.getGraphControl()
                .getNodes()) {
            Dimension nodeDim = new Dimension(node.getSize().preciseWidth(), node.getSize().preciseWidth());
            maxDimension = maxDimension.getUnioned(nodeDim);
        }
        tla.setNodeSpace(maxDimension.expand(5, 15));
        viewer.setLayoutAlgorithm(tla);
        viewer.applyLayout();

        final MenuManager mgr = new MenuManager();
        viewer.getControl().setMenu(mgr.createContextMenu(viewer.getControl()));
        mgr.setRemoveAllWhenShown(true);
        mgr.addMenuListener(new IMenuListener() {
            @Override
            public void menuAboutToShow(IMenuManager manager) {
                IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();
                if (selection.isEmpty()) {
                    manager.add(new Action() {
                        @Override
                        public String getText() {
                            return "Reset filters";
                        }
                        @Override
                        public void run() {
                            viewer.resetFilters();
                            viewer.refresh();
                            viewer.applyLayout();
                        }
                    });
                }
                if (selection.size() == 1
                        && selection.getFirstElement() != null) {
                    Object first = selection.getFirstElement();
                    if (first instanceof ModuleReference) {
                        final ModuleReference selectedModuleRef = (ModuleReference) first;
                        manager.add(new Action() {
                            @Override
                            public String getText() {
                                return "Show dependencies only";
                            }
                            @Override
                            public void run() {
                                final Iterable<Module> modulesToShow = dependencies.getTransitiveDependencies(selectedModuleRef);
                                viewer.setFilters(new ViewerFilter[] { new ViewerFilter() {
                                    @Override
                                    public boolean select(Viewer viewer, Object parentElement, Object element) {
                                        if (element.equals(selectedModuleRef) || element instanceof Dependency) {
                                            return true;
                                        }
                                        if (element instanceof ModuleWeakReference) {
                                            Module moduleToSort = ((ModuleWeakReference) element).get();
                                            for (Module moduleToShow : modulesToShow) {
                                                if (moduleToShow.equals(moduleToSort)) {
                                                    return true;
                                                }
                                            }
                                        }
                                        return false;
                                    }
                                } });
                                viewer.refresh();
                                viewer.applyLayout();
                            }
                        });
                        manager.add(new Action() {
                            @Override
                            public String getText() {
                                return "Show referencing modules only";
                            }
                            @Override
                            public void run() {
                                final Iterable<Module> modulesToShow = dependencies.getReferencingModules(selectedModuleRef);
                                viewer.setFilters(new ViewerFilter[] { new ViewerFilter() {
                                    @Override
                                    public boolean select(Viewer viewer, Object parentElement, Object element) {
                                        if (element.equals(selectedModuleRef) || element instanceof Dependency) {
                                            return true;
                                        }
                                        if (element instanceof ModuleWeakReference) {
                                            Module moduleToSort = ((ModuleWeakReference) element).get();
                                            for (Module moduleToShow : modulesToShow) {
                                                if (moduleToShow.equals(moduleToSort)) {
                                                    return true;
                                                }
                                            }
                                        }
                                        return false;
                                    }
                                } });
                                viewer.refresh();
                                viewer.applyLayout();
                            }
                        });
                    }
                }
            }
        });
        shell.open();
    }

    public void selectionChanged(IAction action, ISelection selection) {
        if (selection instanceof IStructuredSelection) {
            IStructuredSelection ss = (IStructuredSelection) selection;
            Object first = ss.getFirstElement();

            if (first instanceof IProject) {
                fProject = (IProject) first;
            } else if (first instanceof IJavaProject) {
                fProject = ((IJavaProject) first).getProject();
            }
        }
    }
}
