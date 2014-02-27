package com.redhat.ceylon.eclipse.code.outline;

import static com.redhat.ceylon.eclipse.code.outline.HierarchyMode.HIERARCHY;
import static com.redhat.ceylon.eclipse.ui.CeylonResources.CEYLON_HIER;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.bindings.keys.KeyStroke;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
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

import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.code.parse.CeylonParseController;
import com.redhat.ceylon.eclipse.code.parse.CeylonTokenColorer;
import com.redhat.ceylon.eclipse.ui.CeylonPlugin;

public class HierarchyPopup extends TreeViewPopup {
    
    private static final String KEY = KeyStroke.getInstance(SWT.MOD1, 'T').format();
    private static final String VIEW_INTERFACES = " (" + KEY + " to view)";
    
    private final class ChangeViewListener implements KeyListener {
        @Override
        public void keyReleased(KeyEvent e) {}
        @Override
        public void keyPressed(KeyEvent e) {
            if (e.character == 't' && (e.stateMask&SWT.MOD1)!=0) {
                contentProvider.setMode(contentProvider.getMode().next());
                updateStatusFieldText();
                updateTitle();
                updateIcon();
                update();
                e.doit=false;
            }
        }
    }
    
    private CeylonHierarchyLabelProvider labelProvider;
    private CeylonHierarchyContentProvider contentProvider;
    private Label iconLabel;
    
    public HierarchyPopup(CeylonEditor editor, Shell parent, int shellStyle, 
            int treeStyle) {
        super(parent, shellStyle, treeStyle, editor,
                CeylonTokenColorer.getCurrentThemeColor("hierarchy"));
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
                return VIEW_INTERFACES;
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
        switch (contentProvider.getMode()) {
        case SUBTYPES:
            return KEY + " to show hierarchy";
        case SUPERTYPES:
            return KEY + " to show subtypes";
        case HIERARCHY:
            return KEY + " to show supertypes";
        default:
            throw new RuntimeException();
        }
    }
    
    private String getTitleText() {
        return contentProvider.getDescription();
    }

    
    @Override
    protected Control createTitleControl(Composite parent) {
        getPopupLayout().copy().numColumns(3).applyTo(parent);
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
        return "org.eclipse.jdt.internal.ui.typehierarchy.QuickHierarchy";
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
