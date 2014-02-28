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

import static com.redhat.ceylon.eclipse.ui.CeylonResources.CEYLON_OUTLINE;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.Widget;

import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.code.parse.CeylonParseController;
import com.redhat.ceylon.eclipse.code.parse.CeylonTokenColorer;
import com.redhat.ceylon.eclipse.ui.CeylonPlugin;

public class OutlinePopup extends TreeViewPopup {
    
    private CeylonOutlineContentProvider outlineContentProvider;
    private OutlineSorter outlineSorter;
    private ILabelProvider labelProvider;
    private LexicalSortingAction lexicalSortingAction;

    protected static final Object[] NO_CHILDREN= new Object[0];

    private class OutlineTreeViewer extends TreeViewer {
        private boolean fIsFiltering= false;

        private OutlineTreeViewer(Tree tree) {
            super(tree);
        }

        @Override
        protected Object[] getFilteredChildren(Object parent) {
            Object[] result= getRawChildren(parent);
            int unfilteredChildren= result.length;
            ViewerFilter[] filters= getFilters();
            if (filters != null) {
                for(int i= 0; i < filters.length; i++)
                    result= filters[i].filter(this, parent, result);
            }
            fIsFiltering= unfilteredChildren != result.length;
            return result;
        }

        @Override
        protected void internalExpandToLevel(Widget w, int level) {
            if (!fIsFiltering && w instanceof Item) {
                Item i= (Item) w;
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

        private static final int OTHER= 1;

        @Override
        public void sort(Viewer viewer, Object[] elements) {
            if (!lexicalSortingAction.isChecked())
                return;
            super.sort(viewer, elements);
        }

        @Override
        public int compare(Viewer viewer, Object e1, Object e2) {
            int cat1= category(e1);
            int cat2= category(e2);
            if (cat1 != cat2)
                return cat1 - cat2;
            String label1= labelProvider.getText(e1);
            String label2= labelProvider.getText(e2);

            return label1.compareTo(label2);
        }

        @Override
        public int category(Object element) {
            return OTHER;
        }
    }

    private class LexicalSortingAction extends Action {
        private static final String STORE_LEXICAL_SORTING_CHECKED= "LexicalSortingAction.isChecked";
        private TreeViewer fOutlineViewer;

        private LexicalSortingAction(TreeViewer outlineViewer) {
            super("Sort", IAction.AS_CHECK_BOX);
            setToolTipText("Sort by name");
            setDescription("Sort entries lexically by name");
//            CeylonPlugin.getInstance().image("alphab_sort_co.gif");
            fOutlineViewer= outlineViewer;
            setChecked(getDialogSettings().getBoolean(STORE_LEXICAL_SORTING_CHECKED));
        }

        @Override
        public void run() {
            valueChanged(isChecked(), true);
        }

        private void valueChanged(final boolean on, boolean store) {
            setChecked(on);
            BusyIndicator.showWhile(fOutlineViewer.getControl().getDisplay(), new Runnable() {
                @Override
                public void run() {
                    fOutlineViewer.refresh(false);
                }
            });
            if (store) {
                getDialogSettings().put(STORE_LEXICAL_SORTING_CHECKED, on);
            }
        }
    }

    public OutlinePopup(CeylonEditor editor, Shell parent, 
            int shellStyle, int treeStyle) {
        super(parent, shellStyle, treeStyle, editor,
                CeylonTokenColorer.getCurrentThemeColor("outline"));
        setTitleText("Outline of " + editor.getEditorInput().getName());
    }

    @Override
    protected TreeViewer createTreeViewer(Composite parent, int style) {
        Tree tree= new Tree(parent, SWT.SINGLE | (style & ~SWT.MULTI));
        GridData gd= new GridData(GridData.FILL_BOTH);
        gd.heightHint= tree.getItemHeight() * 12;
        tree.setLayoutData(gd);
        final TreeViewer treeViewer= new OutlineTreeViewer(tree);
        lexicalSortingAction= new LexicalSortingAction(treeViewer);
        outlineContentProvider= new CeylonOutlineContentProvider();
        labelProvider= new CeylonLabelProvider(true);
        treeViewer.setLabelProvider(labelProvider);
        treeViewer.addFilter(new OutlineNamePatternFilter(filterText));
        //    fSortByDefiningTypeAction= new SortByDefiningTypeAction(treeViewer);
        //    fShowOnlyMainTypeAction= new ShowOnlyMainTypeAction(treeViewer);
        treeViewer.setContentProvider(outlineContentProvider);
        outlineSorter= new OutlineSorter();
        treeViewer.setSorter(outlineSorter);
        treeViewer.setAutoExpandLevel(getDefaultLevel());
        //treeViewer.getTree().addKeyListener(getKeyAdapter());
        return treeViewer;
    }
    
    @Override
    protected String getId() {
        return "org.eclipse.jdt.internal.ui.text.QuickOutline";
    }

    @Override
    protected Control createTitleControl(Composite parent) {
        getPopupLayout().copy().numColumns(3).applyTo(parent);
        Label label = new Label(parent, SWT.NONE);
        label.setImage(CeylonPlugin.getInstance().getImageRegistry().get(CEYLON_OUTLINE));
        return super.createTitleControl(parent);
    }
    
    @Override
    public void setInput(Object information) {
        if (information == null || information instanceof String) {
            inputChanged(null, null);
        }
        else {
            inputChanged(information, information);
        }
    }

    @Override
    protected void fillViewMenu(IMenuManager viewMenu) {
        super.fillViewMenu(viewMenu);
        //    viewMenu.add(fShowOnlyMainTypeAction);
        viewMenu.add(new Separator("Sorters"));
        if (lexicalSortingAction != null)
            viewMenu.add(lexicalSortingAction);
        //    viewMenu.add(fSortByDefiningTypeAction);
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
