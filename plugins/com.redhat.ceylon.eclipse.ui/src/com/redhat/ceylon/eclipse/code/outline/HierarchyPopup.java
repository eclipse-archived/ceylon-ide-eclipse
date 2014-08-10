package com.redhat.ceylon.eclipse.code.outline;

import static com.redhat.ceylon.eclipse.code.outline.HierarchyMode.HIERARCHY;
import static com.redhat.ceylon.eclipse.code.outline.HierarchyMode.SUBTYPES;
import static com.redhat.ceylon.eclipse.code.outline.HierarchyMode.SUPERTYPES;
import static com.redhat.ceylon.eclipse.code.outline.HierarchyView.showHierarchyView;
import static com.redhat.ceylon.eclipse.ui.CeylonPlugin.PLUGIN_ID;
import static com.redhat.ceylon.eclipse.ui.CeylonResources.CEYLON_HIER;
import static com.redhat.ceylon.eclipse.ui.CeylonResources.CEYLON_SUB;
import static com.redhat.ceylon.eclipse.ui.CeylonResources.CEYLON_SUP;
import static com.redhat.ceylon.eclipse.util.EditorUtil.triggersBinding;
import static com.redhat.ceylon.eclipse.util.Nodes.getReferencedDeclaration;

import java.util.StringTokenizer;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.bindings.TriggerSequence;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
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

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.Referenceable;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.code.parse.CeylonParseController;
import com.redhat.ceylon.eclipse.code.search.FindContainerVisitor;
import com.redhat.ceylon.eclipse.ui.CeylonPlugin;
import com.redhat.ceylon.eclipse.util.EditorUtil;
import com.redhat.ceylon.eclipse.util.Highlights;

public class HierarchyPopup extends TreeViewPopup {
    
    private static final ImageRegistry imageRegistry = 
            CeylonPlugin.getInstance().getImageRegistry();
    private static final Image SUB_IMAGE = imageRegistry.get(CEYLON_SUB);
    private static final Image SUP_IMAGE = imageRegistry.get(CEYLON_SUP);
    private static final Image HIER_IMAGE = imageRegistry.get(CEYLON_HIER);

    private final class ChangeViewListener implements KeyListener {
        @Override
        public void keyReleased(KeyEvent e) {}
        @Override
        public void keyPressed(KeyEvent e) {
            if (triggersBinding(e, getCommandBinding())) {
                contentProvider.setMode(contentProvider.getMode().next());
                switchMode();
                e.doit=false;
            }
            if (triggersBinding(e, hierarchyBinding)) {
                IProject project = editor.getParseController().getProject();
                try {
                    showHierarchyView().focusOn(project, 
                            contentProvider.getDeclaration(project));
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
        hierarchyBinding = EditorUtil.getCommandBinding(PLUGIN_ID + 
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
        GridData gd= new GridData(GridData.FILL_BOTH);
        gd.heightHint= tree.getItemHeight() * 12;
        tree.setLayoutData(gd);
        final TreeViewer treeViewer = new TreeViewer(tree);
        contentProvider = new CeylonHierarchyContentProvider(editor.getSite());
        labelProvider = new CeylonHierarchyLabelProvider() {
            @Override
            String getViewInterfacesShortcut() {
                TriggerSequence binding = getCommandBinding();
                return binding==null ? "" : " (" + binding.format() + " to view)";
            }
            @Override
            IProject getProject() {
                return editor.getParseController().getProject();
            }
            @Override
            boolean isShowingRefinements() {
                return contentProvider.isShowingRefinements();
            }
        };
        treeViewer.setContentProvider(contentProvider);
        treeViewer.setLabelProvider(labelProvider);
        treeViewer.addFilter(new HierarchyNamePatternFilter(filterText));
        treeViewer.setAutoExpandLevel(getDefaultLevel());
        tree.addKeyListener(new ChangeViewListener());
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
        String viewHint = hierarchyBinding==null ? "" :
                hierarchyBinding.format() + " to show in hierarchy view - ";
        switch (contentProvider.getMode()) {
        case SUBTYPES:
            return viewHint + binding.format() + " to show hierarchy";
        case SUPERTYPES:
            return viewHint + binding.format() + " to show subtypes";
        case HIERARCHY:
            return viewHint + binding.format() + " to show supertypes";
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
                new StringTokenizer(title.getText(), "-'", false);
        styleDescription(title, result, tokens.nextToken());
        result.append("-");
        result.append(tokens.nextToken());
        result.append("'");
        Highlights.styleProposal(result, tokens.nextToken(), false);
        result.append("'");
        return result;
    }

    @Override
    protected Control createTitleControl(Composite parent) {
        getPopupLayout().copy().numColumns(4).spacing(6, 6).applyTo(parent);
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
        return "com.redhat.ceylon.eclipse.ui.QuickHierarchy";
    }

    @Override
    public void setInput(Object information) {
        HierarchyInput info = getInformation();
        inputChanged(info, info);
        updateTitle();
    }
    
    private HierarchyInput getInformation() {
        Node selectedNode = editor.getSelectedNode();
        Referenceable declaration = 
                getReferencedDeclaration(selectedNode);
        if (declaration==null) {
            FindContainerVisitor fcv = 
                    new FindContainerVisitor(selectedNode);
            fcv.visit(editor.getParseController().getRootNode());
            Node node = fcv.getStatementOrArgument();
            if (node instanceof com.redhat.ceylon.compiler.typechecker.tree.Tree.Declaration) {
                declaration = ((com.redhat.ceylon.compiler.typechecker.tree.Tree.Declaration) node).getDeclarationModel();
            }
        }
        if (declaration instanceof Declaration) {
            return new HierarchyInput((Declaration) declaration, 
                    editor.getParseController().getProject());
        }
        else {
            return null;
        }
    }
    
    @Override
    protected void gotoSelectedElement() {
        CeylonParseController cpc = editor.getParseController();
        if (cpc!=null) {
            Object object = getSelectedElement();
            if (object instanceof CeylonHierarchyNode) {
                dispose();
                CeylonHierarchyNode hn = (CeylonHierarchyNode) object;
                hn.gotoHierarchyDeclaration(cpc.getProject(), cpc);
            }
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

}
