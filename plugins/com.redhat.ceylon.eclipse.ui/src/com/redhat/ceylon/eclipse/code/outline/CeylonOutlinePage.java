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
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.part.IPageSite;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;

import com.redhat.ceylon.compiler.typechecker.tree.Node;
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
		toolBarManager.add(new LexicalSortingAction());
		//TODO: add a control for filtering out non-shared members!!
		
		MenuManager mm = new MenuManager();
		mm.add(new GroupMarker("find"));
		mm.add(new Separator());
		mm.add(new GroupMarker("refactor"));
		mm.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
		
		getSite().registerContextMenu(OUTLINE_POPUP_MENU_ID, 
				mm, getSite().getSelectionProvider());

		viewer.getControl().setMenu(mm.createContextMenu(viewer.getControl()));
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
                int pos1= getStartOffset(e1);
                int pos2= getStartOffset(e2);

                return pos1 - pos2;
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
    
}

