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
import static com.redhat.ceylon.eclipse.code.parse.CeylonSourcePositionLocator.getEndOffset;
import static com.redhat.ceylon.eclipse.code.parse.CeylonSourcePositionLocator.getStartOffset;
import static com.redhat.ceylon.eclipse.code.parse.TreeLifecycleListener.Stage.TYPE_ANALYSIS;
import static com.redhat.ceylon.eclipse.ui.CeylonPlugin.PLUGIN_ID;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.internal.ui.actions.CollapseAllAction;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.part.IPageSite;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.code.parse.CeylonParseController;
import com.redhat.ceylon.eclipse.code.parse.TreeLifecycleListener;
import com.redhat.ceylon.eclipse.ui.CeylonPlugin;

public class CeylonOutlinePage extends ContentOutlinePage 
        implements TreeLifecycleListener {
	
    private static final String OUTLINE_POPUP_MENU_ID = PLUGIN_ID + 
    		".outline.popupMenu";
    
	private final ITreeContentProvider contentProvider;
    private final CeylonOutlineBuilder modelBuilder;
    private final CeylonLabelProvider labelProvider;
    private final CeylonParseController parseController;

    public CeylonOutlinePage(CeylonParseController parseController,
            CeylonOutlineBuilder modelBuilder) {
    	
        this.parseController= parseController;
        this.modelBuilder= modelBuilder;
        labelProvider= new CeylonLabelProvider();

        contentProvider= new ITreeContentProvider() {
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
        };
    }

    public Stage getStage() {
        return TYPE_ANALYSIS;
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
                		treeViewer.setInput(modelBuilder.buildTree(parseController.getRootNode()));
                	}
                }
            });
        }
    }

    @Override
    public void selectionChanged(SelectionChangedEvent event) {
        super.selectionChanged(event);
        ITreeSelection sel= (ITreeSelection) event.getSelection();
        if (!sel.isEmpty()) {
        	Node node = ((CeylonOutlineNode) sel.getFirstElement()).getASTNode();
        	int startOffset= getStartOffset(node);
        	int endOffset= getEndOffset(node);
        	int length= endOffset - startOffset + 1;
        	((ITextEditor) getCurrentEditor()).selectAndReveal(startOffset, length);
        }
    }

    public void createControl(Composite parent) {
        super.createControl(parent);
        TreeViewer viewer= getTreeViewer();
        viewer.setContentProvider(contentProvider);
        if (labelProvider != null) {
            viewer.setLabelProvider(labelProvider);
        }
        viewer.addSelectionChangedListener(this);
        CeylonOutlineNode rootNode= modelBuilder.buildTree(parseController.getRootNode());
        viewer.setAutoExpandLevel(4);
        viewer.setInput(rootNode);

        IPageSite site= getSite();
        IActionBars actionBars= site.getActionBars();

        IToolBarManager toolBarManager= actionBars.getToolBarManager();
        toolBarManager.add(new ExpandAllAction());
        toolBarManager.add(new CollapseAllAction(viewer));
		toolBarManager.add(new LexicalSortingAction());
		toolBarManager.add(new HideNonSharedAction());
		
		MenuManager mm = new MenuManager();
		mm.add(new GroupMarker("find"));
		mm.add(new Separator());
		mm.add(new GroupMarker("refactor"));
		mm.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
		
		getSite().registerContextMenu(OUTLINE_POPUP_MENU_ID, 
				mm, getSite().getSelectionProvider());

		viewer.getControl().setMenu(mm.createContextMenu(viewer.getControl()));
     }
    
    private class ExpandAllAction extends Action {

        public ExpandAllAction() {
            super("Expand All");
            setToolTipText("Expand All");
            
            ImageDescriptor desc= CeylonPlugin.getInstance().image("expandall.gif"); //$NON-NLS-1$
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

    class LexicalSortingAction extends Action {

        private ViewerComparator fElementComparator= new ViewerComparator() {
            @Override
            public int compare(Viewer viewer, Object e1, Object e2) {
            	CeylonOutlineNode t1= (CeylonOutlineNode) e1;
            	CeylonOutlineNode t2= (CeylonOutlineNode) e2;
                int cat1= t1.getCategory();
                int cat2= t2.getCategory();
                if (cat1 == cat2) {
                    return labelProvider.getText(t1).compareTo(labelProvider.getText(t2));
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

            ImageDescriptor desc= CeylonPlugin.getInstance().image("alphab_sort_co.gif"); //$NON-NLS-1$
            this.setHoverImageDescriptor(desc);
            this.setImageDescriptor(desc); 

            boolean checked= CeylonPlugin.getInstance().getPreferenceStore()
                    .getBoolean("LexicalSortingAction.isChecked"); //$NON-NLS-1$
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
                        .setValue("LexicalSortingAction.isChecked", on); //$NON-NLS-1$
            }
        }
    }
    
    private class HideNonSharedAction extends Action {
        
        private ViewerFilter filter = new ViewerFilter() {
            @Override
            public boolean select(Viewer viewer, Object parentElement, Object element) {
                Node node = ((CeylonOutlineNode) element).getASTNode();
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
            super();
            setText("Hide non-shared");
            setToolTipText("Hide non-shared members");
            setDescription("Hide non-shared members");

            ImageDescriptor desc= CeylonPlugin.getInstance().image("public_co.gif"); //$NON-NLS-1$
            setHoverImageDescriptor(desc);
            setImageDescriptor(desc); 

            boolean checked= CeylonPlugin.getInstance().getPreferenceStore().getBoolean("HideNonSharedAction.isChecked"); //$NON-NLS-1$
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
                CeylonPlugin.getInstance().getPreferenceStore().setValue("HideNonSharedAction.isChecked", on); //$NON-NLS-1$
            }
        }
        
    }
    
}

