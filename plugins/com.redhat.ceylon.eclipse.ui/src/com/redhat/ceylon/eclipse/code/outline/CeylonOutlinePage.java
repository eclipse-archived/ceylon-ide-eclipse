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

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.imp.parser.IModelListener;
import org.eclipse.imp.parser.IParseController;
import org.eclipse.imp.parser.ISourcePositionLocator;
import org.eclipse.imp.runtime.RuntimePlugin;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ILabelProvider;
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

import com.redhat.ceylon.eclipse.code.editor.Util;
import com.redhat.ceylon.eclipse.code.parse.CeylonParseController;

public class CeylonOutlinePage extends ContentOutlinePage implements IModelListener {
	
    private final ITreeContentProvider fContentProvider;
    private final CeylonTreeModelBuilder fModelBuilder;
    private final ILabelProvider fLabelProvider;
    private final CeylonParseController fParseController;

    public CeylonOutlinePage(CeylonParseController parseController,
            CeylonTreeModelBuilder modelBuilder,
            ILabelProvider labelProvider) {
    	
        fParseController= parseController;
        fModelBuilder= modelBuilder;
        fLabelProvider= labelProvider;

        fContentProvider= new ITreeContentProvider() {
            public Object[] getChildren(Object element) {
            	return ((CeylonOutlineNode) element).getChildren();
            }
            public Object getParent(Object element) {
            	return ((CeylonOutlineNode) element).getParent();
            }
        	public boolean hasChildren(Object element) {
        	    Object[] children= getChildren(element);
        	    return (children != null) && children.length > 0;
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

    public AnalysisRequired getAnalysisRequired() {
        return IModelListener.AnalysisRequired.SYNTACTIC_ANALYSIS;
    }

    public void update(final IParseController parseController, IProgressMonitor monitor) {
        if (getTreeViewer() != null && !getTreeViewer().getTree().isDisposed()) {
            getTreeViewer().getTree().getDisplay().asyncExec(new Runnable() {
                public void run() {
                	if (getTreeViewer() != null && !getTreeViewer().getTree().isDisposed())
                		getTreeViewer().setInput(fModelBuilder.buildTree(fParseController.getRootNode()));
                }
            });
        }
    }

    @Override
    public void selectionChanged(SelectionChangedEvent event) {
        super.selectionChanged(event);
        
        ITreeSelection sel= (ITreeSelection) event.getSelection();
        if (!sel.isEmpty()) {
        	ISourcePositionLocator locator= fParseController.getSourcePositionLocator();
        	Object node = ((CeylonOutlineNode) sel.getFirstElement()).getASTNode();
        	int startOffset= locator.getStartOffset(node);
        	int endOffset= locator.getEndOffset(node);
        	int length= endOffset - startOffset + 1;
            
        	ITextEditor textEditor= (ITextEditor) Util.getCurrentEditor();
        	textEditor.selectAndReveal(startOffset, length);
        }
    }

    public void createControl(Composite parent) {
        super.createControl(parent);
        TreeViewer viewer= getTreeViewer();
        viewer.setContentProvider(fContentProvider);
        if (fLabelProvider != null) {
            viewer.setLabelProvider(fLabelProvider);
        }
        viewer.addSelectionChangedListener(this);
        CeylonOutlineNode rootNode= fModelBuilder.buildTree(fParseController.getRootNode());
        viewer.setInput(rootNode);
        viewer.setAutoExpandLevel(2);

        IPageSite site= getSite();
        IActionBars actionBars= site.getActionBars();

        IToolBarManager toolBarManager= actionBars.getToolBarManager();
		toolBarManager.add(new LexicalSortingAction());
		
		MenuManager mm = new MenuManager();
		mm.add(new GroupMarker("find"));
		mm.add(new Separator());
		mm.add(new GroupMarker("refactor"));
		mm.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
		
		getSite().registerContextMenu("com.redhat.ceylon.eclipse.ui.outline.popupMenu", 
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
                    return fLabelProvider.getText(t1).compareTo(fLabelProvider.getText(t2));
                }
                return cat1 - cat2;
            }
        };
        private ISourcePositionLocator fLocator= fParseController.getSourcePositionLocator();

        private ViewerComparator fPositionComparator= new ViewerComparator() {
            @Override
            public int compare(Viewer viewer, Object e1, Object e2) {
                int pos1= fLocator.getStartOffset(e1);
                int pos2= fLocator.getStartOffset(e2);

                return pos1 - pos2;
            }
        };

        public LexicalSortingAction() {
            super();
            setText("Sort");
            setToolTipText("Sort by name");
            setDescription("Sort entries lexically by name");

            ImageDescriptor desc= RuntimePlugin.getImageDescriptor("icons/alphab_sort_co.gif"); //$NON-NLS-1$
            this.setHoverImageDescriptor(desc);
            this.setImageDescriptor(desc); 

            boolean checked= RuntimePlugin.getInstance().getPreferenceStore()
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
                RuntimePlugin.getInstance().getPreferenceStore()
                        .setValue("LexicalSortingAction.isChecked", on); //$NON-NLS-1$
            }
        }
    }
    
}

