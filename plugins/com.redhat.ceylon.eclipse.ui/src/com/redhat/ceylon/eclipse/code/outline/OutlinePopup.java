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

import static com.redhat.ceylon.eclipse.code.complete.CodeCompletions.getQualifiedDescriptionFor;
import static com.redhat.ceylon.eclipse.code.complete.CompletionUtil.overloads;
import static com.redhat.ceylon.eclipse.code.editor.Navigation.gotoDeclaration;
import static com.redhat.ceylon.eclipse.code.preferences.CeylonPreferenceInitializer.PARAMS_IN_OUTLINES;
import static com.redhat.ceylon.eclipse.code.preferences.CeylonPreferenceInitializer.PARAM_TYPES_IN_OUTLINES;
import static com.redhat.ceylon.eclipse.code.preferences.CeylonPreferenceInitializer.RETURN_TYPES_IN_OUTLINES;
import static com.redhat.ceylon.eclipse.code.preferences.CeylonPreferenceInitializer.TYPE_PARAMS_IN_OUTLINES;
import static com.redhat.ceylon.eclipse.ui.CeylonPlugin.PLUGIN_ID;
import static com.redhat.ceylon.eclipse.ui.CeylonResources.CEYLON_OUTLINE;
import static com.redhat.ceylon.eclipse.ui.CeylonResources.CONFIG_LABELS;
import static com.redhat.ceylon.eclipse.ui.CeylonResources.HIDE_PRIVATE;
import static com.redhat.ceylon.eclipse.ui.CeylonResources.SORT_ALPHA;
import static com.redhat.ceylon.eclipse.util.EditorUtil.getPreferences;
import static com.redhat.ceylon.eclipse.util.EditorUtil.triggersBinding;
import static org.eclipse.ui.dialogs.PreferencesUtil.createPreferenceDialogOn;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.bindings.TriggerSequence;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.Widget;

import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.ClassOrInterface;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.CompilationUnit;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.ObjectDefinition;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.code.parse.CeylonParseController;
import com.redhat.ceylon.eclipse.code.preferences.CeylonPreferencePage;
import com.redhat.ceylon.eclipse.ui.CeylonPlugin;
import com.redhat.ceylon.eclipse.util.Nodes;
import com.redhat.ceylon.model.typechecker.model.Declaration;
import com.redhat.ceylon.model.typechecker.model.DeclarationWithProximity;
import com.redhat.ceylon.model.typechecker.model.Referenceable;
import com.redhat.ceylon.model.typechecker.model.TypeDeclaration;
import com.redhat.ceylon.model.typechecker.model.TypeParameter;
import com.redhat.ceylon.model.typechecker.model.Unit;

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
    
    private boolean mode;
    
    private final class ContentProvider 
            extends CeylonOutlineContentProvider {
        @Override
        public Object getParent(Object element) {
            if (element instanceof CeylonOutlineNode) {
                return super.getParent(element);
            }
            else {
                return null; //TODO!!!
            }
        }

        @Override
        public Object[] getChildren(Object element) {
            if (element instanceof CeylonOutlineNode) {
                if (mode) {
                    boolean includeParameters =
                            !getPreferences()
                                .getBoolean(PARAMS_IN_OUTLINES);
                    CeylonOutlineNode node = 
                            (CeylonOutlineNode) element;
                    CompilationUnit rootNode = 
                            getEditor().getParseController()
                                .getRootNode();
                    Node treeNode = 
                            Nodes.findNode(rootNode, 
                                    node.getStartOffset());
                    TypeDeclaration td;
                    if (treeNode instanceof ClassOrInterface) {
                        ClassOrInterface ci = 
                                (ClassOrInterface) treeNode;
                        td = ci.getDeclarationModel();
                    }
                    else if (treeNode instanceof ObjectDefinition) {
                        ObjectDefinition od = 
                                (ObjectDefinition) treeNode;
                        td = od.getDeclarationModel()
                                .getTypeDeclaration();
                    }
                    else {
                        return super.getChildren(element);
                    }
                    List<Declaration> list = 
                            new ArrayList<Declaration>();
                    String filter = getFilterText().getText();
                    for (int i=0; i<filter.length(); i++) {
                        char ch = filter.charAt(i);
                        if (ch=='*' ||
                                i>0 && Character.isUpperCase(ch)) {
                            filter = filter.substring(0, i);
                            break;
                        }
                    }
                    Collection<DeclarationWithProximity> members = 
                            td.getMatchingMemberDeclarations(
                                    rootNode.getUnit(), 
                                    td, filter, 0).values();
                    for (DeclarationWithProximity dwp: members) {
                        for (Declaration dec: 
                                overloads(dwp.getDeclaration())) {
                            if (!(dec instanceof TypeParameter)) {
                                if (includeParameters || !dec.isParameter()) {
                                    list.add(dec);
                                }
                            }
                        }
                    }
                    if (!lexicalSortingAction.isChecked()) {
                        Collections.sort(list, 
                                new Comparator<Declaration>() {
                            public int compare(Declaration x, Declaration y) {
                                String xn = x.getContainer()
                                        .getQualifiedNameString();
                                String yn = y.getContainer()
                                        .getQualifiedNameString();
                                return xn.compareTo(yn);
                            }
                        });
                    }
                    return list.toArray();
                }
                else {
                    return super.getChildren(element);
                }
            }
            else {
                return null;
            }
        }
    }

    private final class LabelProvider extends CeylonLabelProvider {
        //TODO: refactor to not inherit CeylonLabelProvider
        
        private Font getFont() {
            return getTreeViewer().getControl().getFont();
        }

        private String getPrefix() {
            return getFilterText().getText();
        }

        @Override
        public StyledString getStyledText(Object element) {
            if (element instanceof CeylonOutlineNode) {
                CeylonOutlineNode node = 
                        (CeylonOutlineNode) element;
                return node.getLabel(getPrefix(), getFont());
            }
            else if (element instanceof Declaration) {
                IPreferenceStore prefs = getPreferences();
                return getQualifiedDescriptionFor((Declaration) element,
                        prefs.getBoolean(TYPE_PARAMS_IN_OUTLINES),
                        prefs.getBoolean(PARAMS_IN_OUTLINES),
                        prefs.getBoolean(PARAM_TYPES_IN_OUTLINES),
                        prefs.getBoolean(RETURN_TYPES_IN_OUTLINES),
                        getPrefix(), getFont());
            }
            else {
                return new StyledString();
            }
        }

        @Override
        public Image getImage(Object element) {
            if (element instanceof CeylonOutlineNode) {
                return super.getImage(element);
            }
            else if (element instanceof Declaration) {
                return getImageForDeclaration((Declaration) element);
            }
            else {
                return null;
            }
        }
        
    }

    private final class ChangeViewListener implements KeyListener {
        @Override
        public void keyReleased(KeyEvent e) {}
        @Override
        public void keyPressed(KeyEvent e) {
            if (triggersBinding(e, getCommandBinding())) {
                mode = !mode;
                modeButton.setSelection(mode);
                updateStatusFieldText();
                getTreeViewer().refresh();
                getTreeViewer().expandToLevel(getDefaultLevel());
                e.doit=false;
            }
        }
    }
    
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
                if (i.getData() instanceof CeylonOutlineNode) {
                    CeylonOutlineNode node = 
                            (CeylonOutlineNode) i.getData();
                    int cat = node.getCategory();
                    //TODO: leave unshared declarations collapsed?
                    if (cat==CeylonOutlineNode.IMPORT_LIST_CATEGORY) {
                        setExpanded(i, false);
                        return;
                    }
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
            if (e1 instanceof CeylonOutlineNode && 
                e2 instanceof CeylonOutlineNode) {
                CeylonOutlineNode n1 = (CeylonOutlineNode) e1;
                CeylonOutlineNode n2 = (CeylonOutlineNode) e2;
                int cat = n1.getCategory()-n2.getCategory();
                if (cat!=0) return cat;
                String n1n = n1.getName();
                String n2n = n2.getName();
                if (n1n==n2n) return 0;
                if (n1n==null) return -1;
                if (n2n==null) return 1;
                return n1n.compareTo(n2n);
            }
            else if (e1 instanceof Declaration && 
                     e2 instanceof Declaration) {
                Unit unit = 
                        getEditor().getParseController()
                            .getRootNode().getUnit();
                String e1n = ((Declaration) e1).getName(unit);
                String e2n = ((Declaration) e2).getName(unit);
                if (e1n==e2n) return 0;
                if (e1n==null) return -1;
                if (e2n==null) return 1;
                return e1n.compareTo(e2n);
            }
            else {
                return 0;
            }
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
            boolean checked = 
                    getDialogSettings().getBoolean("sort");
            setChecked(checked);
        }

        @Override
        public void run() {
            boolean on = isChecked();
            setChecked(on);
            Display display = 
                    treeViewer.getControl().getDisplay();
            BusyIndicator.showWhile(display, 
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
        public boolean select(Viewer viewer, 
                Object parentElement, Object element) {
            if (element instanceof CeylonOutlineNode) {
                CeylonOutlineNode node = (CeylonOutlineNode) element;
                return node.isShared();
            }
            else if (element instanceof Declaration) {
                return ((Declaration) element).isShared();
            }
            else {
                return true;
            }
        }
    };

    private ToolItem modeButton;

    private class HideNonSharedAction extends Action {
        private TreeViewer treeViewer;

        private HideNonSharedAction(TreeViewer viewer) {
            treeViewer = viewer;
            setText("Hide Unshared");
            setToolTipText("Hide Unhared Declarations");
            setDescription("Hide unshared declarations");
            
            setImageDescriptor(imageRegistry.getDescriptor(HIDE_PRIVATE)); 
            
            boolean checked = 
                    getDialogSettings().getBoolean("hideNonShared");
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
            Display display = 
                    treeViewer.getControl().getDisplay();
            BusyIndicator.showWhile(display, 
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
    
    public OutlinePopup(CeylonEditor editor, Shell shell, 
            int shellStyle) {
        super(shell, shellStyle, 
                PLUGIN_ID + ".editor.showOutline", editor);
        setTitleText("Quick Outline - " + 
                editor.getEditorInput().getName());
    }

    @Override
    protected TreeViewer createTreeViewer(Composite parent) {
        
        Tree tree = new Tree(parent, SWT.SINGLE);
        GridData gd = new GridData(GridData.FILL_BOTH);
        gd.heightHint = tree.getItemHeight() * 12;
        tree.setLayoutData(gd);
        
        final TreeViewer treeViewer = new OutlineTreeViewer(tree);
        lexicalSortingAction = new LexicalSortingAction(treeViewer);
        hideNonSharedAction = new HideNonSharedAction(treeViewer);
        
        outlineContentProvider = new ContentProvider();
        labelProvider = new LabelProvider();
        
        treeViewer.setLabelProvider(labelProvider);
        treeViewer.addFilter(new OutlineNamePatternFilter(getFilterText()));
        //    fSortByDefiningTypeAction= new SortByDefiningTypeAction(treeViewer);
        //    fShowOnlyMainTypeAction= new ShowOnlyMainTypeAction(treeViewer);
        treeViewer.setContentProvider(outlineContentProvider);
        outlineSorter = new OutlineSorter();
        treeViewer.setSorter(outlineSorter);
        treeViewer.setAutoExpandLevel(getDefaultLevel());
        tree.addKeyListener(new ChangeViewListener());
        return treeViewer;
    }
    
    @Override
    protected String getStatusFieldText() {
        TriggerSequence binding = getCommandBinding();
        if (binding==null) return "";
        String action = mode ? " to hide " : " to show ";
        return binding.format() + action + 
                "inherited members of classes and interfaces";
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
        createToolBar(parent);
        return null;
    }

    private void createToolBar(Composite parent) {
        ToolBar toolBar = new ToolBar(parent, SWT.FLAT);
        modeButton = new ToolItem(toolBar, SWT.CHECK);
        modeButton.setImage(HierarchyView.INHERITED_IMAGE);
        modeButton.setToolTipText("Show Inherited Members");
        modeButton.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                mode = !mode;
                getTreeViewer().refresh();
                getTreeViewer().expandToLevel(getDefaultLevel());
            }
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {}
        });
        sortButton = new ToolItem(toolBar, SWT.CHECK);
        sortButton.setImage(SORT);
        sortButton.setToolTipText("Sort by Name");
        boolean sortChecker = 
                getDialogSettings().getBoolean("sort");
        sortButton.setSelection(sortChecker);
        sortButton.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                lexicalSortingAction.setChecked(sortButton.getSelection());
                lexicalSortingAction.run();
            }
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {}
        });
        hideButton = new ToolItem(toolBar, SWT.CHECK);
        hideButton.setImage(PUBLIC);
        hideButton.setToolTipText("Hide Unshared Declarations");
        boolean hideChecked = 
                getDialogSettings().getBoolean("hideNonShared");
        hideButton.setSelection(hideChecked);
        hideButton.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                hideNonSharedAction.setChecked(hideButton.getSelection());
                hideNonSharedAction.run();
            }
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {}
        });
    }
    
    @Override
    public void setInput(Object information) {
        CeylonParseController controller = 
                getEditor().getParseController();
        CeylonOutlineNode info = 
                new CeylonOutlineBuilder()
                    .buildTree(controller);
        inputChanged(info, info);
    }

    @Override
    protected Text createFilterText(Composite parent) {
        Text result = super.createFilterText(parent);
        result.addKeyListener(new ChangeViewListener());
        return result;
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
        viewMenu.add(new Separator());
        Action configureAction = 
                new Action("Configure Labels...", 
                        CeylonPlugin.getInstance()
                            .getImageRegistry()
                            .getDescriptor(CONFIG_LABELS)) {
            @Override
            public void run() {
                createPreferenceDialogOn(getShell(), 
                        CeylonPreferencePage.ID, 
                        new String[] { CeylonPreferencePage.ID }, 
                        null).open();
                getTreeViewer().refresh();
            }
        };
        viewMenu.add(configureAction);
    }

    @Override
    protected void gotoSelectedElement() {
        Object object = getSelectedElement();
        if (object instanceof CeylonOutlineNode) {
            dispose();
            CeylonOutlineNode on = 
                    (CeylonOutlineNode) object;
            int startOffset = on.getStartOffset();
            int endOffset = on.getEndOffset();
            getEditor().selectAndReveal(startOffset, 
                    endOffset-startOffset);
        }
        else if (object instanceof Referenceable) {
            dispose();
            gotoDeclaration((Referenceable) object);
        }
    }

}
