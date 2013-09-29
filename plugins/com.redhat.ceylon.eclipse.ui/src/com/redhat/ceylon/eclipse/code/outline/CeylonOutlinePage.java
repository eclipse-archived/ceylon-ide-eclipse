/*******************************************************************************
* Copyright (c) 2007 IBM Corporation.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*    Robert Fuhrer (rfuhrer@watson.ibm.com) - initial API and implementation
*******************************************************************************/

package com.redhat.ceylon.eclipse.code.outline;

import static com.redhat.ceylon.eclipse.code.editor.Util.getCurrentEditor;
import static com.redhat.ceylon.eclipse.code.outline.CeylonOutlineNode.IMPORT_LIST_CATEGORY;
import static com.redhat.ceylon.eclipse.code.parse.CeylonSourcePositionLocator.getEndOffset;
import static com.redhat.ceylon.eclipse.code.parse.CeylonSourcePositionLocator.getStartOffset;
import static com.redhat.ceylon.eclipse.ui.CeylonPlugin.PLUGIN_ID;
import static com.redhat.ceylon.eclipse.ui.CeylonResources.EXPAND_ALL;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.actions.CollapseAllAction;
import org.eclipse.jdt.internal.ui.packageview.DefaultElementComparer;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.DecoratingStyledCellLabelProvider;
import org.eclipse.jface.viewers.DecorationContext;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.custom.CaretEvent;
import org.eclipse.swt.custom.CaretListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.IPageSite;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.CompilationUnit;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.SyntheticVariable;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;
import com.redhat.ceylon.eclipse.code.editor.CeylonSourceViewer;
import com.redhat.ceylon.eclipse.code.parse.CeylonParseController;
import com.redhat.ceylon.eclipse.code.parse.TreeLifecycleListener;
import com.redhat.ceylon.eclipse.ui.CeylonPlugin;

public class CeylonOutlinePage extends ContentOutlinePage 
        implements TreeLifecycleListener, CaretListener {
    
    private static final String OUTLINE_POPUP_MENU_ID = PLUGIN_ID + 
            ".outline.popupMenu";
    
    private static final ImageDescriptor PUBLIC = CeylonPlugin.getInstance().image("public_co.gif");
    private static final ImageDescriptor ALPHA = CeylonPlugin.getInstance().image("alphab_sort_co.gif");
    
    private ITreeContentProvider contentProvider;
    private StyledCellLabelProvider labelProvider;
    private CeylonParseController parseController;
    private CeylonSourceViewer sourceViewer;
    
    public CeylonOutlinePage(CeylonParseController parseController,
            CeylonSourceViewer sourceViewer) {
        this.parseController = parseController;
        this.sourceViewer = sourceViewer;
        this.contentProvider = new OutlineContentProvider();
        this.labelProvider = new DecoratingStyledCellLabelProvider(new CeylonLabelProvider(true), 
                PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator(), 
                DecorationContext.DEFAULT_CONTEXT);
    }

    public Stage getStage() {
        return Stage.DONE;
    }
    
    @Override
    public void update(CeylonParseController parseController, 
            IProgressMonitor monitor) {
        update(parseController);
    }

    public void update(final CeylonParseController parseController) {
        TreeViewer treeViewer = getTreeViewer();
        if (treeViewer!=null && 
                !treeViewer.getTree().isDisposed()) {
            treeViewer.getTree().getDisplay()
                   .asyncExec(new Runnable() {
                public void run() {
                    TreeViewer treeViewer = getTreeViewer();
                    if (treeViewer!=null && 
                            !treeViewer.getTree().isDisposed()) {
                        Object[] expanded = treeViewer.getExpandedElements();
                        CeylonOutlineNode rootNode = new CeylonOutlineBuilder()
                                .buildTree(parseController);
                        treeViewer.setInput(rootNode);
                        for (Object obj: expanded) {
                            treeViewer.expandToLevel(obj, 1);
                        }
                        expandCaretedNode(sourceViewer.getSelectedRange().x);
                    }
                }
            });
        }
    }
    
    private volatile boolean suspend = false;
    
    public void createControl(Composite parent) {
        super.createControl(parent);
        TreeViewer viewer = getTreeViewer();
        viewer.setContentProvider(contentProvider);
        viewer.setLabelProvider(labelProvider);
        viewer.addSelectionChangedListener(this);
        CeylonOutlineNode rootNode = new CeylonOutlineBuilder()
                .buildTree(parseController);
        viewer.setInput(rootNode);
        viewer.setComparer(new DefaultElementComparer());

        IPageSite site = getSite();
        
        MenuManager menuManager = new MenuManager();
        JavaPlugin.createStandardGroups(menuManager);
        /*mm.add(new GroupMarker("find"));
        mm.add(new Separator());
        mm.add(new GroupMarker("refactor"));
        mm.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));*/
        
        site.registerContextMenu(OUTLINE_POPUP_MENU_ID, menuManager, getTreeViewer());
        site.setSelectionProvider(getTreeViewer());

        viewer.getControl().setMenu(menuManager.createContextMenu(viewer.getControl()));
        
        expand(viewer, rootNode);
    }
    
    @Override
    public void makeContributions(IMenuManager menuManager,
            IToolBarManager toolBarManager, IStatusLineManager statusLineManager) {
        super.makeContributions(menuManager, toolBarManager, statusLineManager);
        toolBarManager.add(new ExpandAllAction());
        toolBarManager.add(new CollapseAllAction(getTreeViewer()));
        toolBarManager.add(new LexicalSortingAction());
        toolBarManager.add(new HideNonSharedAction());
    }
    
    @Override
    public void dispose() {
        getTreeViewer().removeSelectionChangedListener(this);
        getTreeViewer().setSelection(null);
        getSite().getSelectionProvider().setSelection(null);
        super.dispose();
        if (labelProvider!=null) {
            labelProvider.dispose();
            labelProvider = null;
        }
        if (contentProvider!=null) {
            contentProvider.dispose();
            contentProvider = null;
        }
        IPageSite site = getSite();
        //TODO: how the hell do we clean up the actions?
        //      they hang around, preventing this object
        //      from being garbage collected!
//        site.getActionBars().getToolBarManager().removeAll();
        site.setSelectionProvider(null);
        sourceViewer = null;
        parseController = null;
    }

     void expand(TreeViewer viewer, CeylonOutlineNode rootNode) {
        for (CeylonOutlineNode con: rootNode.getChildren()) {
            if (con.getCategory()==IMPORT_LIST_CATEGORY) {
                viewer.collapseToLevel(con, TreeViewer.ALL_LEVELS);
            }
            else {
                viewer.expandToLevel(con, TreeViewer.ALL_LEVELS);
            }
        }
    }
    
    private static final class OutlineContentProvider 
            implements ITreeContentProvider {
        public Object[] getChildren(Object element) {
            return ((CeylonOutlineNode) element).getChildren().toArray();
        }
        public Object getParent(Object element) {
            return ((CeylonOutlineNode) element).getParent();
        }
        public boolean hasChildren(Object element) {
            Object[] children= getChildren(element);
            return children!=null && children.length > 0;
        }
        public Object[] getElements(Object inputElement) {
            return getChildren(inputElement);
        }
        @Override
        public void dispose() {}
        @Override
        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}
    }

    private class ExpandAllAction extends Action {

        public ExpandAllAction() {
            super("Expand All");
            setToolTipText("Expand All");
            
            ImageDescriptor desc = CeylonPlugin.getInstance().getImageRegistry()
                    .getDescriptor(EXPAND_ALL);
            setHoverImageDescriptor(desc);
            setImageDescriptor(desc);
        }

        public void run() {
            TreeViewer outlineViewer = getTreeViewer();
            if (outlineViewer != null) {
                outlineViewer.expandAll();
            }
        }
        
    }

    private class LexicalSortingAction extends Action {

        private ViewerComparator fElementComparator= new ViewerComparator() {
            @Override
            public int compare(Viewer viewer, Object e1, Object e2) {
                CeylonOutlineNode t1= (CeylonOutlineNode) e1;
                CeylonOutlineNode t2= (CeylonOutlineNode) e2;
                int cat1= t1.getCategory();
                int cat2= t2.getCategory();
                if (cat1 == cat2) {
                    return t1.getIdentifier().compareTo(t2.getIdentifier());
                }
                return cat1 - cat2;
            }
        };
        
        private ViewerComparator fPositionComparator= new ViewerComparator() {
            @Override
            public int compare(Viewer viewer, Object e1, Object e2) {
                return getStartOffset(e1) - getStartOffset(e2);
            }
        };

        public LexicalSortingAction() {
            super();
            setText("Sort");
            setToolTipText("Sort by name");
            setDescription("Sort entries lexically by name");

            ImageDescriptor desc= ALPHA;
            this.setHoverImageDescriptor(desc);
            this.setImageDescriptor(desc); 

            boolean checked= CeylonPlugin.getInstance().getPreferenceStore()
                    .getBoolean("LexicalSortingAction.isChecked");
            valueChanged(checked, false);
        }

        public void run() {
            valueChanged(isChecked(), true);
        }

        private void valueChanged(final boolean on, boolean store) {
            final TreeViewer outlineViewer= getTreeViewer();
            setChecked(on);
            BusyIndicator.showWhile(outlineViewer.getControl().getDisplay(), new Runnable() {
                public void run() {
                    if (on)
                        outlineViewer.setComparator(fElementComparator);
                    else
                        outlineViewer.setComparator(fPositionComparator);
                }
            });

            if (store) {
                CeylonPlugin.getInstance().getPreferenceStore()
                        .setValue("LexicalSortingAction.isChecked", on);
            }
        }
    }
    
    private class HideNonSharedAction extends Action {
        
        private ViewerFilter filter = new ViewerFilter() {
            @Override
            public boolean select(Viewer viewer, Object parentElement, Object element) {
                Node node = ((CeylonOutlineNode) element).getTreeNode();
                if (node instanceof Tree.Declaration) {
                    Declaration declaration = ((Tree.Declaration) node).getDeclarationModel();
                    if (declaration != null) {
                        return declaration.isShared();
                    }
                }
                return true;
            }
        };

        public HideNonSharedAction() {
            setText("Hide non-shared");
            setToolTipText("Hide non-shared members");
            setDescription("Hide non-shared members");

            ImageDescriptor desc= PUBLIC;
            setHoverImageDescriptor(desc);
            setImageDescriptor(desc); 

            boolean checked= CeylonPlugin.getInstance().getPreferenceStore()
                    .getBoolean("HideNonSharedAction.isChecked");
            valueChanged(checked, false);
        }

        public void run() {
            valueChanged(isChecked(), true);
        }

        private void valueChanged(final boolean on, boolean store) {
            final TreeViewer outlineViewer= getTreeViewer();
            setChecked(on);
            BusyIndicator.showWhile(outlineViewer.getControl().getDisplay(), new Runnable() {
                public void run() {
                    if (on)
                        outlineViewer.addFilter(filter);
                    else
                        outlineViewer.removeFilter(filter);
                }
            });

            if (store) {
                CeylonPlugin.getInstance().getPreferenceStore()
                        .setValue("HideNonSharedAction.isChecked", on);
            }
        }
        
    }
    
    @Override
    public void selectionChanged(SelectionChangedEvent event) {
        super.selectionChanged(event);
        if (!suspend) {
            ITreeSelection sel= (ITreeSelection) event.getSelection();
            if (!sel.isEmpty()) {
                Node node = ((CeylonOutlineNode) sel.getFirstElement()).getTreeNode();
                if (node instanceof PackageNode || 
                    node instanceof Tree.ImportList ||
                    node instanceof Tree.CompilationUnit) {
                    return;
                }
                suspend = true;
                try {
                    int startOffset = getStartOffset(node);
                    int endOffset = getEndOffset(node);
                    ((ITextEditor) getCurrentEditor())
                            .selectAndReveal(startOffset, endOffset-startOffset);
                }
                finally {
                    suspend = false;
                }
            }
        }
    }

    @Override
    public void caretMoved(CaretEvent event) {
        int offset = sourceViewer.widgetOffset2ModelOffset(event.caretOffset);
        expandCaretedNode(offset);
    }

    private void expandCaretedNode(int offset) {
        if (suspend) return;
        if (offset==0) return; //right at the start of file, don't expand the import list
        CompilationUnit rootNode = parseController.getRootNode();
        if (rootNode==null) return;
        suspend = true;
        try {
            OutlineNodeVisitor visitor = new OutlineNodeVisitor(offset);
            rootNode.visit(visitor);
            if (!visitor.result.isEmpty()) {
                TreePath treePath = new TreePath(visitor.result.toArray());
                if (!visitor.result.get(visitor.result.size()-1)
                        .getChildren().isEmpty()) {
                    getTreeViewer().expandToLevel(treePath, 1);
                }
                setSelection(new TreeSelection(treePath));
            }
        }
        finally {
            suspend = false;
        }
    }
    
    static class OutlineNodeVisitor extends Visitor {
        private final int offset;
        private final boolean hideNonshared;
        OutlineNodeVisitor(int offset) {
            this.offset = offset;
            hideNonshared = CeylonPlugin.getInstance().getPreferenceStore()
                    .getBoolean("HideNonSharedAction.isChecked");
        }
        List<CeylonOutlineNode> result = new ArrayList<CeylonOutlineNode>();
        @Override
        public void visit(Tree.Declaration that) {
            if (!(that instanceof Tree.TypeParameterDeclaration) &&
                !(that instanceof Tree.TypeConstraint) &&
                !(that instanceof Tree.Variable && 
                            ((Tree.Variable) that).getType() instanceof SyntheticVariable)) {
                if (inBounds(that)) {
                    Declaration dm = that.getDeclarationModel();
                    if (!hideNonshared||dm!=null&&dm.isShared()) {
                        result.add(new CeylonOutlineNode(that));
                        super.visit(that);
                    }
                }
                else {
                    super.visit(that);
                }
            }
            else {
                super.visit(that);
            }
        }
        @Override
        public void visit(Tree.Parameter that) {}
        @Override
        public void visit(Tree.ImportList that) {
            if (inBounds(that)) {
                result.add(new CeylonOutlineNode(that, IMPORT_LIST_CATEGORY));
            }
            super.visit(that);
        }
        @Override
        public void visit(Tree.PackageDescriptor that) {
            if (inBounds(that)) {
                result.add(new CeylonOutlineNode(that));
            }
            super.visit(that);
        }
        @Override
        public void visit(Tree.ModuleDescriptor that) {
            if (inBounds(that)) {
                result.add(new CeylonOutlineNode(that));
            }
            super.visit(that);
        }
        @Override
        public void visit(Tree.Import that) {
            if (inBounds(that)) {
                result.add(new CeylonOutlineNode(that));
            }
            super.visit(that);
        }
        @Override
        public void visit(Tree.ImportModule that) {
            if (inBounds(that)) {
                result.add(new CeylonOutlineNode(that));
            }
            super.visit(that);
        }
        private boolean inBounds(Node that) {
            Integer tokenStartIndex = that.getStartIndex();
            Integer tokenStopIndex = that.getStopIndex();
            return tokenStartIndex!=null && tokenStopIndex!=null &&
                    tokenStartIndex<=offset && tokenStopIndex+1>=offset;
        }
    }
    
}

