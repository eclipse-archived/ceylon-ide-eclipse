package com.redhat.ceylon.eclipse.code.outline;

import static com.redhat.ceylon.eclipse.code.editor.Navigation.gotoDeclaration;
import static com.redhat.ceylon.eclipse.code.outline.HierarchyMode.HIERARCHY;
import static com.redhat.ceylon.eclipse.code.outline.HierarchyMode.SUBTYPES;
import static com.redhat.ceylon.eclipse.code.outline.HierarchyMode.SUPERTYPES;
import static com.redhat.ceylon.eclipse.code.outline.HierarchyView.showHierarchyView;
import static com.redhat.ceylon.eclipse.ui.CeylonPlugin.PLUGIN_ID;
import static com.redhat.ceylon.eclipse.ui.CeylonPlugin.imageRegistry;
import static com.redhat.ceylon.eclipse.ui.CeylonResources.CEYLON_HIER;
import static com.redhat.ceylon.eclipse.ui.CeylonResources.CEYLON_SUB;
import static com.redhat.ceylon.eclipse.ui.CeylonResources.CEYLON_SUP;
import static com.redhat.ceylon.eclipse.ui.CeylonResources.imageRegistry;
import static com.redhat.ceylon.eclipse.util.EditorUtil.triggersBinding;
import static com.redhat.ceylon.eclipse.util.Nodes.getReferencedDeclaration;

import java.util.StringTokenizer;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.bindings.TriggerSequence;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.dialogs.PreferencesUtil;

import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.code.preferences.CeylonFiltersPreferencePage;
import com.redhat.ceylon.eclipse.code.preferences.CeylonOutlinesPreferencePage;
import com.redhat.ceylon.eclipse.code.search.FindContainerVisitor;
import com.redhat.ceylon.eclipse.ui.CeylonPlugin;
import com.redhat.ceylon.eclipse.ui.CeylonResources;
import com.redhat.ceylon.eclipse.util.EditorUtil;
import com.redhat.ceylon.eclipse.util.Highlights;
import com.redhat.ceylon.eclipse.util.ModelProxy;
import com.redhat.ceylon.model.typechecker.model.Declaration;
import com.redhat.ceylon.model.typechecker.model.Referenceable;

public class HierarchyPopup extends TreeViewPopup {
    
    private static final String EXCLUDE_ORACLE_JDK = "excludeOracleJDK";
    private static final String EXCLUDE_JDK = "excludeJDK";
    
    private static final Image SUB_IMAGE = imageRegistry().get(CEYLON_SUB);
    private static final Image SUP_IMAGE = imageRegistry().get(CEYLON_SUP);
    private static final Image HIER_IMAGE = imageRegistry().get(CEYLON_HIER);

    private final class ChangeViewListener implements KeyListener {
        @Override
        public void keyReleased(KeyEvent e) {}
        @Override
        public void keyPressed(KeyEvent e) {
            if (triggersBinding(e, getCommandBinding())) {
                HierarchyMode nextMode = 
                        contentProvider.getMode().next();
                contentProvider.setMode(nextMode);
                switchMode();
                e.doit=false;
            }
            if (triggersBinding(e, hierarchyBinding)) {
                try {
                    showHierarchyView()
                        .focusOn(contentProvider.getDeclaration());
                    close();
                }
                catch (PartInitException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }
    
    private CeylonHierarchyLabelProvider labelProvider;
    private CeylonHierarchyContentProvider contentProvider;
    private Label iconLabel;
    private TriggerSequence hierarchyBinding;
    private ToolItem button1;
    private ToolItem button2;
    private ToolItem button3;
    
    public HierarchyPopup(CeylonEditor editor, Shell shell, int shellStyle) {
        super(shell, shellStyle, PLUGIN_ID + ".editor.hierarchy", editor);
        hierarchyBinding = 
                EditorUtil.getCommandBinding(PLUGIN_ID + 
                        ".action.showInHierarchyView");
        setInfoText(getStatusFieldText());
    }
    
    private void switchMode() {
        updateStatusFieldText();
        updateTitle();
        updateIcon();
        updateButtonSelection();
        update();
    }
    /*@Override
    protected void adjustBounds() {
        Rectangle bounds = getShell().getBounds();
        int h = bounds.height;
        if (h>400) {
            bounds.height=400;
            bounds.y = bounds.y + (h-400)/3;
            getShell().setBounds(bounds);
        }
    }*/
    
    @Override
    protected TreeViewer createTreeViewer(Composite parent) {
        final Tree tree = new Tree(parent, SWT.SINGLE);
        GridData gd = new GridData(GridData.FILL_BOTH);
        gd.heightHint = tree.getItemHeight() * 12;
        tree.setLayoutData(gd);
        final TreeViewer treeViewer = new TreeViewer(tree);
        IDialogSettings dialogSettings = getDialogSettings();
        contentProvider = 
                new CeylonHierarchyContentProvider(getEditor().getSite(),
                        "Quick Hierarchy",
                        dialogSettings.getBoolean(EXCLUDE_JDK),
                        dialogSettings.get(EXCLUDE_ORACLE_JDK)==null || 
                        dialogSettings.getBoolean(EXCLUDE_ORACLE_JDK));
        labelProvider = new CeylonHierarchyLabelProvider() {
            @Override
            Font getFont() {
                return treeViewer.getControl().getFont();
            }
            @Override
            String getPrefix() {
                return getFilterText().getText();
            }
            @Override
            String getViewInterfacesShortcut() {
                TriggerSequence binding = getCommandBinding();
                return binding==null ? "" : 
                    " (" + binding.format() + " to view)";
            }
            @Override
            boolean isShowingRefinements() {
                return contentProvider.isShowingRefinements();
            }
        };
        treeViewer.setContentProvider(contentProvider);
        treeViewer.setLabelProvider(labelProvider);
        treeViewer.addFilter(new HierarchyNamePatternFilter(getFilterText()));
        treeViewer.setAutoExpandLevel(getDefaultLevel());
        tree.addKeyListener(new ChangeViewListener());
        tree.setFont(CeylonPlugin.getOutlineFont());
//        treeViewer.setUseHashlookup(false);
        return treeViewer;
    }

    @Override
    protected Text createFilterText(Composite parent) {
        Text result = super.createFilterText(parent);
        result.addKeyListener(new ChangeViewListener());
        return result;
    }
    
    @Override
    protected String getStatusFieldText() {
        TriggerSequence binding = getCommandBinding();
        if (binding==null) return "";
        String viewHint = 
                hierarchyBinding==null ? "" :
                    hierarchyBinding.format() + 
                    " to show in hierarchy view \u00b7 ";
        switch (contentProvider.getMode()) {
        case SUBTYPES:
            return viewHint + binding.format() + 
                    " to show hierarchy";
        case SUPERTYPES:
            return viewHint + binding.format() + 
                    " to show subtypes";
        case HIERARCHY:
            return viewHint + binding.format() + 
                    " to show supertypes";
        default:
            throw new RuntimeException();
        }
    }
    
    private String getTitleText() {
        return contentProvider.getDescription();
    }

    @Override
    protected StyledString styleTitle(final StyledText title) {
        StyledString result = new StyledString();
        StringTokenizer tokens = 
                new StringTokenizer(title.getText(), "\u2014", false);
        styleDescription(title, result, tokens.nextToken());
        result.append("\u2014");
        String rest = tokens.nextToken();
        int loc = rest.indexOf(" of ") + 4;
        result.append(rest.substring(0, loc));
        Highlights.styleFragment(result, 
                rest.substring(loc), false, null, 
                CeylonPlugin.getOutlineFont());
        return result;
    }

    @Override
    protected Control createTitleControl(Composite parent) {
        getPopupLayout().copy()
            .numColumns(4)
            .spacing(6, 6)
            .applyTo(parent);
        iconLabel = new Label(parent, SWT.NONE);
        super.createTitleControl(parent);
        updateIcon();
        createModeButtons(parent);
        return null;
    }

    private void createModeButtons(Composite parent) {
        ToolBar toolBar = new ToolBar(parent, SWT.FLAT);
        button1 = new ToolItem(toolBar, SWT.CHECK);
        button1.setImage(HIER_IMAGE);
        button1.setToolTipText("Show Hierarchy");
        button2 = new ToolItem(toolBar, SWT.CHECK);
        button2.setImage(SUP_IMAGE);
        button2.setToolTipText("Show Supertypes/Generalizations");
        button3 = new ToolItem(toolBar, SWT.CHECK);
        button3.setImage(SUB_IMAGE);
        button3.setToolTipText("Show Subtypes/Refinements");
        updateButtonSelection();
        button1.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (button1.getSelection()) {
                    contentProvider.setMode(HIERARCHY);
                    switchMode();
                }
                else {
                    button1.setSelection(true);
                }
            }
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {}
        });
        button2.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (button2.getSelection()) {
                    contentProvider.setMode(SUPERTYPES);
                    switchMode();
                }
                else {
                    button2.setSelection(true);
                }
            }
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {}
        });
        button3.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (button3.getSelection()) {
                    contentProvider.setMode(SUBTYPES);
                    switchMode();
                }
                else {
                    button3.setSelection(true);
                }
            }
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {}
        });
    }

    private void updateButtonSelection() {
        button1.setSelection(contentProvider==null || contentProvider.getMode()==HIERARCHY);
        button2.setSelection(contentProvider!=null && contentProvider.getMode()==SUPERTYPES);
        button3.setSelection(contentProvider!=null && contentProvider.getMode()==SUBTYPES);
    }

    public void updateTitle() {
        setTitleText(getTitleText());
    }
    public void updateIcon() {
        iconLabel.setImage(getIcon());
    }
    
    private Image getIcon() {
        String img = contentProvider==null ? 
                CEYLON_HIER : contentProvider.getMode().image();
        return imageRegistry.get(img);
    }
    
    @Override
    protected String getId() {
        return CeylonPlugin.PLUGIN_ID + ".QuickHierarchy";
    }

    @Override
    public void setInput(Object information) {
        Declaration dec = getSelectedDeclaration();
        ModelProxy input = 
                dec == null ? null : 
                    new ModelProxy(dec);
        inputChanged(input, input);
        updateTitle();
    }
    
    private Declaration getSelectedDeclaration() {
        Node selectedNode = getEditor().getSelectedNode();
        Referenceable declaration = 
                getReferencedDeclaration(selectedNode);
        if (declaration==null) {
            FindContainerVisitor fcv = 
                    new FindContainerVisitor(selectedNode);
            fcv.visit(getEditor().getParseController().getLastCompilationUnit());
            Node node = fcv.getStatementOrArgument();
            if (node instanceof com.redhat.ceylon.compiler.typechecker.tree.Tree.Declaration) {
                declaration = ((com.redhat.ceylon.compiler.typechecker.tree.Tree.Declaration) node).getDeclarationModel();
            }
        }
        if (declaration instanceof Declaration) {
            return (Declaration) declaration;
        }
        else {
            return null;
        }
    }
    
    @Override
    protected void gotoSelectedElement() {
        Object selectedElement = getSelectedElement();
        if (selectedElement instanceof CeylonHierarchyNode) {
            dispose();
            CeylonHierarchyNode node = 
                    (CeylonHierarchyNode) 
                        selectedElement;
            gotoDeclaration(node.getDeclaration());
        }
    }

    @Override
    protected void reveal() {
        if (contentProvider.isEmpty()) return;
        int depth;
        if (contentProvider.getMode()==HIERARCHY) {
            depth = contentProvider.getDepthInHierarchy();
        }
        else {
            depth = 1;
        }
        if (contentProvider.isVeryAbstractType()) {
            depth+=1;
        }
        else {
            depth+=3;
        }
        getTreeViewer().expandToLevel(depth);
    }
    
    @Override
    protected int getDefaultLevel() {
        return 1;
    }
    
    @Override
    protected void fillViewMenu(IMenuManager viewMenu) {
        super.fillViewMenu(viewMenu);
        viewMenu.add(new Separator());
        final Action javaSDKAction = 
                new Action("Exclude Java SDK", 
                        IAction.AS_CHECK_BOX) {
            @Override
            public void run() {
                boolean checked = isChecked();
                contentProvider.setExcludeJDK(checked);
                getDialogSettings()
                    .put(EXCLUDE_JDK, checked);
                update();
            }
        };
        javaSDKAction.setChecked(contentProvider.isExcludeJDK());
        viewMenu.add(javaSDKAction);
        final Action oracleSDKAction = 
                new Action("Exclude Java SDK Internals", 
                        IAction.AS_CHECK_BOX) {
            @Override
            public void run() {
                boolean checked = isChecked();
                contentProvider.setExcludeOracleJDK(checked);
                getDialogSettings()
                    .put(EXCLUDE_ORACLE_JDK, checked);
                update();
            }
        };
        oracleSDKAction.setChecked(contentProvider.isExcludeOracleJDK());
        viewMenu.add(oracleSDKAction);
        
        viewMenu.add(new Separator());
        Action configureAction = 
                new Action("Configure Labels...", 
                        CeylonPlugin.imageRegistry()
                            .getDescriptor(CeylonResources.CONFIG_LABELS)) {
            @Override
            public void run() {
                PreferencesUtil.createPreferenceDialogOn(
                        getShell(), 
                        CeylonOutlinesPreferencePage.ID,
                        new String[] {
                                CeylonOutlinesPreferencePage.ID,
                                CeylonPlugin.COLORS_AND_FONTS_PAGE_ID,
                                CeylonFiltersPreferencePage.ID
                        }, 
                        null).open();
                getTreeViewer().getTree()
                    .setFont(CeylonPlugin.getOutlineFont());
                getTreeViewer().refresh();
            }
        };
        viewMenu.add(configureAction);
    }

}
