package com.redhat.ceylon.eclipse.code.outline;

import static com.redhat.ceylon.eclipse.code.outline.HierarchyMode.HIERARCHY;
import static com.redhat.ceylon.eclipse.code.outline.HierarchyView.showHierarchyView;
import static com.redhat.ceylon.eclipse.ui.CeylonResources.CEYLON_HIER;
import static com.redhat.ceylon.eclipse.util.Highlights.getCurrentThemeColor;

import java.util.StringTokenizer;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.bindings.TriggerSequence;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.PartInitException;

import com.redhat.ceylon.eclipse.code.complete.CompletionUtil;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.code.editor.EditorUtil;
import com.redhat.ceylon.eclipse.code.parse.CeylonParseController;
import com.redhat.ceylon.eclipse.ui.CeylonPlugin;

public class HierarchyPopup extends TreeViewPopup {
    
    private final class ChangeViewListener implements KeyListener {
        @Override
        public void keyReleased(KeyEvent e) {}
        @Override
        public void keyPressed(KeyEvent e) {
            if (EditorUtil.triggersBinding(e, getCommandBinding())) {
                contentProvider.setMode(contentProvider.getMode().next());
                updateStatusFieldText();
                updateTitle();
                updateIcon();
                update();
                e.doit=false;
            }
            if (EditorUtil.triggersBinding(e, hierarchyBinding)) {
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
    
    public HierarchyPopup(CeylonEditor editor, Shell shell, int shellStyle, 
            int treeStyle) {
        super(shell, shellStyle, treeStyle, 
                "com.redhat.ceylon.eclipse.ui.editor.hierarchy",
                editor, getCurrentThemeColor("hierarchy"));
        hierarchyBinding = EditorUtil.getCommandBinding("com.redhat.ceylon.eclipse.ui.action.showInHierarchyView");
        setInfoText(getStatusFieldText());
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
    protected TreeViewer createTreeViewer(Composite parent, int style) {
        final Tree tree = new Tree(parent, SWT.SINGLE | (style & ~SWT.MULTI));
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
        CompletionUtil.styleProposal(result, tokens.nextToken());
        result.append("'");
        return result;
    }

    @Override
    protected Control createTitleControl(Composite parent) {
        getPopupLayout().copy().numColumns(3).spacing(6, 6).applyTo(parent);
        iconLabel = new Label(parent, SWT.NONE);
        //label.setImage(CeylonPlugin.getInstance().image("class_hi.gif").createImage());
        updateIcon();
        return super.createTitleControl(parent);
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
        return CeylonPlugin.getInstance().getImageRegistry().get(img);
    }
    
    @Override
    protected String getId() {
        return "com.redhat.ceylon.eclipse.ui.QuickHierarchy";
    }

    @Override
    public void setInput(Object information) {
        if (!(information instanceof HierarchyInput)) {
            inputChanged(null, null);
        }
        else {
            HierarchyInput rn = (HierarchyInput) information;
            if (rn.declaration==null) {
                inputChanged(null, null);
            }
            else {
                inputChanged(information, information);
                updateTitle();
            }
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
