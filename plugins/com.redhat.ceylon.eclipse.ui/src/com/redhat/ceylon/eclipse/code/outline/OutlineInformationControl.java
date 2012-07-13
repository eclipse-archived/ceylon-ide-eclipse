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

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.bindings.keys.KeySequence;
import org.eclipse.jface.bindings.keys.KeyStroke;
import org.eclipse.jface.bindings.keys.SWTKeySupport;
import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.Widget;

import com.redhat.ceylon.eclipse.code.outline.CeylonOutlineLabelProvider.IElementImageProvider;
import com.redhat.ceylon.eclipse.ui.CeylonPlugin;

public class OutlineInformationControl extends AbstractInformationControl {
    private KeyAdapter fKeyAdapter;
    private CeylonOutlineContentProvider fOutlineContentProvider;
    private Object fInput= null;
    private OutlineSorter fOutlineSorter;
    private CeylonOutlineLabelProvider fInnerLabelProvider;
    protected Color fForegroundColor;
    //    private boolean fShowOnlyMainType; // RMF what would this mean in general?
    private LexicalSortingAction fLexicalSortingAction;

    // All of the following should be provided by language-specific extensions.
    //    private ViewerFilter fElementFilter;
    private CeylonLabelProvider fLangLabelProvider;
    private IElementImageProvider fElemImageProvider;
    private final boolean fShowStorage= false;

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

        protected void internalExpandToLevel(Widget node, int level) {
            if (!fIsFiltering && node instanceof Item) {
                //		Item i= (Item) node;
                //		if (i.getData() instanceof IJavaElement) {
                //		    IJavaElement je= (IJavaElement) i.getData();
                //		    if (je.getElementType() == IJavaElement.IMPORT_CONTAINER || isInnerType(je)) {
                //			setExpanded(i, false);
                //			return;
                //		    }
                //		}
            }
            super.internalExpandToLevel(node, level);
        }

        //	private boolean isInnerType(IJavaElement element) {
        //	    if (element != null && element.getElementType() == IJavaElement.TYPE) {
        //		IType type= (IType) element;
        //		return type.isMember();
        //	    }
        //	    return false;
        //	}
    }

    private class OutlineSorter extends ViewerSorter {
        // TODO Should be extensible by lang-specific implementation.
        // Maybe an interface with a pair of methods: categoryOf(e) and compare(e1, e2)?
        private static final int OTHER= 1;

        //	private static final int TYPE= 2;
        //	private static final int ANONYM= 3;
        //	private JavaElementSorter fJavaElementSorter= new JavaElementSorter();

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
            //	    if (cat1 == OTHER) { // method or field
            //		if (fSortByDefiningTypeAction.isChecked()) {
            //		    IType def1= (e1 instanceof IMethod) ? getDefiningType((IMethod) e1) : null;
            //		    IType def2= (e2 instanceof IMethod) ? getDefiningType((IMethod) e2) : null;
            //		    if (def1 != null) {
            //			if (def2 != null) {
            //			    if (!def2.equals(def1)) {
            //				return compareInHierarchy(getSuperTypeHierarchy(def1), def1, def2);
            //			    }
            //			} else {
            //			    return -1;
            //			}
            //		    } else {
            //			if (def2 != null) {
            //			    return 1;
            //			}
            //		    }
            //		}
            //	    } else if (cat1 == ANONYM) {
            //		return 0;
            //	    }
            String label1= fLangLabelProvider.getText(e1);
            String label2= fLangLabelProvider.getText(e2);

            return label1.compareTo(label2);
        }

        /*
         * @see org.eclipse.jface.viewers.ViewerSorter#category(java.lang.Object)
         */
        public int category(Object element) {
            //	    if (element instanceof IType) {
            //		IType type= (IType) element;
            //		if (type.getElementName().length() == 0) {
            //		    return ANONYM;
            //		}
            //		return TYPE;
            //	    }
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
            //	    PlatformUI.getWorkbench().getHelpSystem().setHelp(this, IJavaHelpContextIds.LEXICAL_SORTING_BROWSING_ACTION);
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

    public OutlineInformationControl(Shell parent, int shellStyle, int treeStyle, String commandId) {
        super(parent, shellStyle, treeStyle, commandId, true);
    }

    protected Text createFilterText(Composite parent) {
        Text text= super.createFilterText(parent);
        text.addKeyListener(getKeyAdapter());
        return text;
    }

    protected TreeViewer createTreeViewer(Composite parent, int style) {
        Tree tree= new Tree(parent, SWT.SINGLE | (style & ~SWT.MULTI));
        GridData gd= new GridData(GridData.FILL_BOTH);
        gd.heightHint= tree.getItemHeight() * 12;
        tree.setLayoutData(gd);
        final TreeViewer treeViewer= new OutlineTreeViewer(tree);
        // Hard-coded filters
        fLexicalSortingAction= new LexicalSortingAction(treeViewer);
        // JJV: this code is broken:
        //	if (fElementFilter != null)
        //	    treeViewer.addFilter(fElementFilter);
        fForegroundColor= parent.getDisplay().getSystemColor(SWT.COLOR_DARK_GRAY);

        // RMF 7/7/2006 - oops, fLanguage is still null at this point, b/c createTreeViewer() gets called from super ctor and field inits haven't happened yet...
        fOutlineContentProvider= new CeylonOutlineContentProvider(this);

        fOutlineContentProvider.setInfoControl(this);
        fLangLabelProvider= new CeylonLabelProvider();
        fElemImageProvider= null; //ServiceFactory.getInstance().getElementImageProvider(fLanguage);
        // JJV; commented out because untested
        //	fElementFilter= (ViewerFilter) ExtensionPointFactory.createExtensionPoint(fLanguage, ILanguageService.VIEWER_FILTER_SERVICE);

        fInnerLabelProvider= new CeylonOutlineLabelProvider(fLangLabelProvider, 
        		fElemImageProvider, fOutlineContentProvider.fShowInheritedMembers, 
        		fShowStorage, fForegroundColor);
        fInnerLabelProvider.addLabelDecorator(new CeylonLabelDecorator());
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
        treeViewer.setAutoExpandLevel(AbstractTreeViewer.ALL_LEVELS);
        treeViewer.getTree().addKeyListener(getKeyAdapter());
        return treeViewer;
    }

    @Override
    public TreeViewer getTreeViewer() { // make visible to OutlineContentProviderBase
        return super.getTreeViewer();
    }

    @Override
    public Object getSelectedElement() { // make visible to OutlineContentProviderBase
        return super.getSelectedElement();
    }

    protected String getStatusFieldText() {
        return "";
    }

    /*
     * @see org.eclipse.jdt.internal.ui.text.AbstractInformationControl#getId()
     * @since 3.0
     */
    protected String getId() {
        return "org.eclipse.imp.QuickOutline"; //$NON-NLS-1$
    }

    /**
     * {@inheritDoc}
     */
    public void setInput(Object information) {
        if (information == null || information instanceof String) {
            inputChanged(null, null);
            return;
        }
        //	IJavaElement je= (IJavaElement) information;
        //	ICompilationUnit cu= (ICompilationUnit) je.getAncestor(IJavaElement.COMPILATION_UNIT);
        //	if (cu != null)
        //	    fInput= cu;
        //	else
        //	    fInput= je.getAncestor(IJavaElement.CLASS_FILE);
        fInput= information;
        inputChanged(fInput, information);
    }

    private KeyAdapter getKeyAdapter() {
        if (fKeyAdapter == null) {
            fKeyAdapter= new KeyAdapter() {
                public void keyPressed(KeyEvent e) {
                    int accelerator= SWTKeySupport.convertEventToUnmodifiedAccelerator(e);
                    KeySequence keySequence= KeySequence.getInstance(SWTKeySupport.convertAcceleratorToKeyStroke(accelerator));
                    KeyStroke[] strokes= keySequence.getKeyStrokes();
                    for(int i= 0; i < strokes.length; i++) {
                        // HACK Hard-wired code for detecting Ctrl-O...
                        if ((strokes[i].getModifierKeys() & SWT.CTRL) != 0 && strokes[i].getNaturalKey() == 'O') {
                            e.doit= false;
                            toggleShowInheritedMembers();
                            return;
                        }
                    }
                    //		    KeySequence[] sequences= new KeySequence[0]; // getInvokingCommandKeySequences(); // RMF 6/1/2006 - disabled since impl used deprecated API
                    //		    if (sequences == null)
                    //			return;
                    //		    for(int i= 0; i < sequences.length; i++) {
                    //			if (sequences[i].equals(keySequence)) {
                    //			    e.doit= false;
                    //			    toggleShowInheritedMembers();
                    //			    return;
                    //			}
                    //		    }
                }
            };
        }
        return fKeyAdapter;
    }

    protected void handleStatusFieldClicked() {
        toggleShowInheritedMembers();
    }

    protected void toggleShowInheritedMembers() {
        //	long flags= AppearanceAwareLabelProvider.DEFAULT_TEXTFLAGS | JavaElementLabels.F_APP_TYPE_SIGNATURE;
        //	if (!fOutlineContentProvider.isShowingInheritedMembers())
        //	    flags|= JavaElementLabels.ALL_POST_QUALIFIED;
        //	fInnerLabelProvider.setTextFlags(flags);
        fOutlineContentProvider.toggleShowInheritedMembers();
        updateStatusFieldText();
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
