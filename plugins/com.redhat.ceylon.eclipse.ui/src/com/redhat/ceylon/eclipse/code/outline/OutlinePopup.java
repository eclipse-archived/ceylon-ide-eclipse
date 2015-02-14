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

import static com.redhat.ceylon.eclipse.ui.CeylonPlugin.PLUGIN_ID;
import static com.redhat.ceylon.eclipse.ui.CeylonResources.CEYLON_OUTLINE;
import static com.redhat.ceylon.eclipse.ui.CeylonResources.HIDE_PRIVATE;
import static com.redhat.ceylon.eclipse.ui.CeylonResources.SORT_ALPHA;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.Widget;

import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.code.parse.CeylonParseController;
import com.redhat.ceylon.eclipse.ui.CeylonPlugin;

public class OutlinePopup extends TreeViewPopup {
    
    private static final ImageRegistry imageRegistry = 
            CeylonPlugin.getInstance().getImageRegistry();
    
    private static final Image OUTLINE = imageRegistry.get(CEYLON_OUTLINE);
    private static final Image SORT = imageRegistry.get(SORT_ALPHA);
    private static final Image PUBLIC = imageRegistry.get(HIDE_PRIVATE);
    
    private CeylonOutlineContentProvider outlineContentProvider;
    private OutlineSorter outlineSorter;
    private ILabelProvider labelProvider;
    private LexicalSortingAction lexicalSortingAction;
    private HideNonSharedAction hideNonSharedAction;

    protected static final Object[] NO_CHILDREN = new Object[0];

    private ToolItem sortButton;
    private ToolItem hideButton;

    private class OutlineTreeViewer extends TreeViewer {
        private boolean fIsFiltering= false;

        private OutlineTreeViewer(Tree tree) {
            super(tree);
        }

        @Override
        protected Object[] getFilteredChildren(Object parent) {
            Object[] result = getRawChildren(parent);
            int unfilteredChildren = result.length;
            ViewerFilter[] filters = getFilters();
            if (filters != null) {
                for(int i=0; i<filters.length; i++)
                    result = filters[i].filter(this, parent, result);
            }
            fIsFiltering = unfilteredChildren != result.length;
            return result;
        }

        @Override
        protected void internalExpandToLevel(Widget w, int level) {
            if (!fIsFiltering && w instanceof Item) {
                Item i = (Item) w;
                int cat = ((CeylonOutlineNode) i.getData()).getCategory();
                //TODO: leave unshared declarations collapsed?
                if (cat==CeylonOutlineNode.IMPORT_LIST_CATEGORY) {
                    setExpanded(i, false);
                    return;
                }
            }
            super.internalExpandToLevel(w, level);
        }

    }

    private class OutlineSorter extends ViewerSorter {

        private static final int OTHER = 1;

        @Override
        public void sort(Viewer viewer, Object[] elements) {
            if (lexicalSortingAction.isChecked()) {
                super.sort(viewer, elements);
            }
        }

        @Override
        public int compare(Viewer viewer, Object e1, Object e2) {
            CeylonOutlineNode n1 = (CeylonOutlineNode) e1;
            CeylonOutlineNode n2 = (CeylonOutlineNode) e2;
            int cat = n1.getCategory()-n2.getCategory();
            if (cat!=0) return cat;
            return n1.getName().compareTo(n2.getName());
        }

        @Override
        public int category(Object element) {
            return OTHER;
        }
    }

    private class LexicalSortingAction extends Action {
        private TreeViewer treeViewer;

        private LexicalSortingAction(TreeViewer viewer) {
            super("Sort by Name", IAction.AS_CHECK_BOX);
            setToolTipText("Sort by name");
            setDescription("Sort entries lexically by name");
            setImageDescriptor(imageRegistry.getDescriptor(SORT_ALPHA));
            treeViewer = viewer;
            setChecked(getDialogSettings().getBoolean("sort"));
        }

        @Override
        public void run() {
            boolean on = isChecked();
            setChecked(on);
            BusyIndicator.showWhile(treeViewer.getControl().getDisplay(), 
                    new Runnable() {
                @Override
                public void run() {
                    treeViewer.refresh(false);
                }
            });
            if (true) {
                getDialogSettings().put("sort", on);
            }
            sortButton.setSelection(on);
        }
    }

    private static final ViewerFilter filter = new ViewerFilter() {
        @Override
        public boolean select(Viewer viewer, Object parentElement, Object element) {
            return ((CeylonOutlineNode) element).isShared();
        }
    };
    
    private class HideNonSharedAction extends Action {
        private TreeViewer treeViewer;

        private HideNonSharedAction(TreeViewer viewer) {
            treeViewer = viewer;
            setText("Hide Unshared");
            setToolTipText("Hide Unhared Declarations");
            setDescription("Hide unshared declarations");
            
            setImageDescriptor(imageRegistry.getDescriptor(HIDE_PRIVATE)); 
            
            boolean checked = getDialogSettings().getBoolean("hideNonShared");
            valueChanged(checked, false);
        }

        @Override
        public void run() {
            boolean on = isChecked();
            valueChanged(on, true);
            hideButton.setSelection(on);
        }

        private void valueChanged(final boolean on, boolean store) {
            setChecked(on);
            BusyIndicator.showWhile(treeViewer.getControl().getDisplay(), 
                    new Runnable() {
                @Override
                public void run() {
                    if (on) {
                        treeViewer.addFilter(filter);
                    }
                    else {
                        treeViewer.removeFilter(filter);
                    }
                }
            });
            
            if (store) {
                getDialogSettings().put("hideNonShared", on);
            }
        }
    }
    
    public OutlinePopup(CeylonEditor editor, Shell shell, int shellStyle) {
        super(shell, shellStyle, PLUGIN_ID + ".editor.showOutline", editor);
        setTitleText("Quick Outline - " + editor.getEditorInput().getName());
    }

    @Override
    protected TreeViewer createTreeViewer(Composite parent) {
        Tree tree= new Tree(parent, SWT.SINGLE);
        GridData gd = new GridData(GridData.FILL_BOTH);
        gd.heightHint = tree.getItemHeight() * 12;
        tree.setLayoutData(gd);
        final TreeViewer treeViewer = new OutlineTreeViewer(tree);
        lexicalSortingAction = new LexicalSortingAction(treeViewer);
        hideNonSharedAction = new HideNonSharedAction(treeViewer);
        outlineContentProvider = new CeylonOutlineContentProvider();
        labelProvider = new CeylonLabelProvider();
        treeViewer.setLabelProvider(labelProvider);
        treeViewer.addFilter(new OutlineNamePatternFilter(filterText));
        //    fSortByDefiningTypeAction= new SortByDefiningTypeAction(treeViewer);
        //    fShowOnlyMainTypeAction= new ShowOnlyMainTypeAction(treeViewer);
        treeViewer.setContentProvider(outlineContentProvider);
        outlineSorter = new OutlineSorter();
        treeViewer.setSorter(outlineSorter);
        treeViewer.setAutoExpandLevel(getDefaultLevel());
        //treeViewer.getTree().addKeyListener(getKeyAdapter());
        return treeViewer;
    }
    
    @Override
    protected String getId() {
        return CeylonPlugin.PLUGIN_ID + ".QuickOutline";
    }

    @Override
    protected Control createTitleControl(Composite parent) {
        getPopupLayout().copy()
            .numColumns(4)
            .spacing(6, 6)
            .applyTo(parent);
        Label label = new Label(parent, SWT.NONE);
        label.setImage(OUTLINE);
        super.createTitleControl(parent);
        ToolBar bar = new ToolBar(parent, SWT.FLAT);
        sortButton = new ToolItem(bar, SWT.CHECK);
        sortButton.setImage(SORT);
        sortButton.setToolTipText("Sort by Name");
        sortButton.setSelection(getDialogSettings().getBoolean("sort"));
        sortButton.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                lexicalSortingAction.setChecked(sortButton.getSelection());
                lexicalSortingAction.run();
            }
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {}
        });
        hideButton = new ToolItem(bar, SWT.CHECK);
        hideButton.setImage(PUBLIC);
        hideButton.setToolTipText("Hide Unshared Declarations");
        hideButton.setSelection(getDialogSettings().getBoolean("hideNonShared"));
        hideButton.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                hideNonSharedAction.setChecked(hideButton.getSelection());
                hideNonSharedAction.run();
            }
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {}
        });
        return null;
    }
    
    @Override
    public void setInput(Object information) {
        CeylonOutlineNode info = 
                new CeylonOutlineBuilder().buildTree(editor.getParseController());
        inputChanged(info, info);
    }

    @Override
    protected void fillViewMenu(IMenuManager viewMenu) {
        super.fillViewMenu(viewMenu);
        viewMenu.add(new Separator("Sorters"));
        if (lexicalSortingAction != null) {
            viewMenu.add(lexicalSortingAction);
        }
        if (hideNonSharedAction != null) {
            viewMenu.add(hideNonSharedAction);
        }
    }

    @Override
    protected void gotoSelectedElement() {
        CeylonParseController cpc = editor.getParseController();
        if (cpc!=null) {
            Object object = getSelectedElement();
            if (object instanceof CeylonOutlineNode) {
                dispose();
                CeylonOutlineNode on = (CeylonOutlineNode) object;
                editor.selectAndReveal(on.getStartOffset(), 
                        on.getEndOffset()-on.getStartOffset());
            }
        }
    }

}
