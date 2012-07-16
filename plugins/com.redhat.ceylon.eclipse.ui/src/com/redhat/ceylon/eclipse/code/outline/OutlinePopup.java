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

import static org.eclipse.jface.viewers.AbstractTreeViewer.ALL_LEVELS;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.Widget;

import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.ImportList;
import com.redhat.ceylon.eclipse.ui.CeylonPlugin;

public class OutlinePopup extends Popup {
	
    private CeylonOutlineContentProvider fOutlineContentProvider;
    private Object fInput= null;
    private OutlineSorter fOutlineSorter;
    private CeylonOutlineLabelProvider fInnerLabelProvider;
    private LexicalSortingAction fLexicalSortingAction;
    private CeylonLabelProvider fLangLabelProvider;

    protected static final Object[] NO_CHILDREN= new Object[0];

    private class OutlineTreeViewer extends TreeViewer {
        private boolean fIsFiltering= false;

        private OutlineTreeViewer(Tree tree) {
            super(tree);
        }

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

        protected void internalExpandToLevel(Widget w, int level) {
            if (!fIsFiltering && w instanceof Item) {
                Item i= (Item) w;
                Node node = ((CeylonOutlineNode) i.getData()).getASTNode();
                /*if (node instanceof Declaration) {
                	boolean shared = ((Declaration) node).getDeclarationModel().isShared();
					setExpanded(i, shared);
                }
                else*/ 
                if (node instanceof ImportList) {
                	setExpanded(i, false);
                	return;
                }
            }
            super.internalExpandToLevel(w, level);
        }

    }

    private class OutlineSorter extends ViewerSorter {

        private static final int OTHER= 1;

        public void sort(Viewer viewer, Object[] elements) {
            if (!fLexicalSortingAction.isChecked())
                return;
            super.sort(viewer, elements);
        }

        public int compare(Viewer viewer, Object e1, Object e2) {
            int cat1= category(e1);
            int cat2= category(e2);
            if (cat1 != cat2)
                return cat1 - cat2;
            String label1= fLangLabelProvider.getText(e1);
            String label2= fLangLabelProvider.getText(e2);

            return label1.compareTo(label2);
        }

        public int category(Object element) {
            return OTHER;
        }
    }

    private class LexicalSortingAction extends Action {
        private static final String STORE_LEXICAL_SORTING_CHECKED= "LexicalSortingAction.isChecked"; //$NON-NLS-1$
        private TreeViewer fOutlineViewer;

        private LexicalSortingAction(TreeViewer outlineViewer) {
            super("Sort", IAction.AS_CHECK_BOX);
            setToolTipText("Sort by name");
            setDescription("Sort entries lexically by name");
            CeylonPlugin.getInstance().image("alphab_sort_co.gif"); //$NON-NLS-1$
            fOutlineViewer= outlineViewer;
            boolean checked= getDialogSettings().getBoolean(STORE_LEXICAL_SORTING_CHECKED);
            setChecked(checked);
        }

        public void run() {
            valueChanged(isChecked(), true);
        }

        private void valueChanged(final boolean on, boolean store) {
            setChecked(on);
            BusyIndicator.showWhile(fOutlineViewer.getControl().getDisplay(), new Runnable() {
                public void run() {
                    fOutlineViewer.refresh(false);
                }
            });
            if (store)
                getDialogSettings().put(STORE_LEXICAL_SORTING_CHECKED, on);
        }
    }

    public OutlinePopup(Shell parent, int shellStyle, int treeStyle, String commandId) {
        super(parent, shellStyle, treeStyle, commandId, true);
    }

    protected TreeViewer createTreeViewer(Composite parent, int style) {
        Tree tree= new Tree(parent, SWT.SINGLE | (style & ~SWT.MULTI));
        GridData gd= new GridData(GridData.FILL_BOTH);
        gd.heightHint= tree.getItemHeight() * 12;
        tree.setLayoutData(gd);
        final TreeViewer treeViewer= new OutlineTreeViewer(tree);
        fLexicalSortingAction= new LexicalSortingAction(treeViewer);
        fOutlineContentProvider= new CeylonOutlineContentProvider();
        fLangLabelProvider= new CeylonLabelProvider();
        fInnerLabelProvider= new CeylonOutlineLabelProvider(fLangLabelProvider);
        //fInnerLabelProvider.addLabelDecorator(new CeylonLabelDecorator());
        //	IDecoratorManager decoratorMgr= PlatformUI.getWorkbench().getDecoratorManager();
        //	if (decoratorMgr.getEnabled("org.eclipse.jdt.ui.override.decorator")) //$NON-NLS-1$
        //	    fInnerLabelProvider.addLabelDecorator(new OverrideIndicatorLabelDecorator(null));
        treeViewer.setLabelProvider(fInnerLabelProvider);
        treeViewer.addFilter(new NamePatternFilter());
        //	fSortByDefiningTypeAction= new SortByDefiningTypeAction(treeViewer);
        //	fShowOnlyMainTypeAction= new ShowOnlyMainTypeAction(treeViewer);
        treeViewer.setContentProvider(fOutlineContentProvider);
        fOutlineSorter= new OutlineSorter();
        treeViewer.setSorter(fOutlineSorter);
        treeViewer.setAutoExpandLevel(ALL_LEVELS);
        //treeViewer.getTree().addKeyListener(getKeyAdapter());
        return treeViewer;
    }
    
    protected String getId() {
        return "org.eclipse.jdt.internal.ui.text.QuickOutline"; //$NON-NLS-1$
    }

    public void setInput(Object information) {
        if (information == null || information instanceof String) {
            inputChanged(null, null);
            return;
        }
        fInput= information;
        inputChanged(fInput, information);
    }

    protected void fillViewMenu(IMenuManager viewMenu) {
        super.fillViewMenu(viewMenu);
        //	viewMenu.add(fShowOnlyMainTypeAction); //$NON-NLS-1$
        viewMenu.add(new Separator("Sorters")); //$NON-NLS-1$
        if (fLexicalSortingAction != null)
            viewMenu.add(fLexicalSortingAction);
        //	viewMenu.add(fSortByDefiningTypeAction);
    }

}
