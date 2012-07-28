package com.redhat.ceylon.eclipse.code.outline;

import static com.redhat.ceylon.eclipse.code.parse.CeylonSourcePositionLocator.gotoNode;
import static com.redhat.ceylon.eclipse.code.propose.CeylonContentProposer.getDescriptionFor;
import static com.redhat.ceylon.eclipse.code.resolve.CeylonReferenceResolver.getCompilationUnit;
import static com.redhat.ceylon.eclipse.code.resolve.CeylonReferenceResolver.getReferencedNode;
import static com.redhat.ceylon.eclipse.ui.ICeylonResources.CEYLON_HIER;
import static org.eclipse.jface.viewers.AbstractTreeViewer.ALL_LEVELS;

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

import com.redhat.ceylon.compiler.typechecker.model.ClassOrInterface;
import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.code.parse.CeylonParseController;
import com.redhat.ceylon.eclipse.code.parse.CeylonTokenColorer;
import com.redhat.ceylon.eclipse.ui.CeylonPlugin;

public class HierarchyPopup extends TreeViewPopup {
	
	private final class ChangeViewListener implements KeyListener {
		@Override
		public void keyReleased(KeyEvent e) {}

		@Override
		public void keyPressed(KeyEvent e) {
			if (e.character == 't' && (e.stateMask&SWT.MOD1)!=0) {
				contentProvider.mode=contentProvider.mode.next();
				updateStatusFieldText();
				updateTitle();
				updateIcon();
				stringMatcherUpdated();
				e.doit=false;
			}
		}
	}

	static final String KEY = KeyStroke.getInstance(SWT.MOD1, 'T').format();
	
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
        contentProvider = new CeylonHierarchyContentProvider(editor);
        labelProvider = new CeylonHierarchyLabelProvider(contentProvider);
        treeViewer.setContentProvider(contentProvider);
        treeViewer.setLabelProvider(labelProvider);
        treeViewer.addFilter(new NamePatternFilter());
        treeViewer.setAutoExpandLevel(ALL_LEVELS);
        tree.addKeyListener(new ChangeViewListener());
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
		switch (contentProvider.mode) {
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
		Declaration dec = contentProvider.declaration;
		String desc = getDescriptionFor(dec);
		if (contentProvider.isShowingRefinements()) {
			if (dec.isClassOrInterfaceMember()) {
				desc += " in " + ((ClassOrInterface) dec.getContainer()).getName();
			}
			switch (contentProvider.mode) {
			case HIERARCHY:
				return "Refinement hierarchy of " + desc;
			case SUPERTYPES:
				return "Supertypes generalizing " + desc;
			case SUBTYPES:
				return "Subtypes refining " + desc;
			default:
				throw new RuntimeException();
			}
		}
		else {
			switch (contentProvider.mode) {
			case HIERARCHY:
				return "Type hierarchy of " + desc;
			case SUPERTYPES:
				return "Supertypes of " + desc;
			case SUBTYPES:
				return "Subtypes of " + desc;
			default:
				throw new RuntimeException();
			}
		}
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
				CEYLON_HIER : contentProvider.mode.image();
		return CeylonPlugin.getInstance().getImageRegistry().get(img);
	}
	
	@Override
	protected String getId() {
		return "org.eclipse.jdt.internal.ui.typehierarchy.QuickHierarchy";
	}

	@Override
	public void setInput(Object information) {
        if (information == null || information instanceof String) {
            inputChanged(null, null);
        }
        else {
        	inputChanged(information, information);
        	updateTitle();
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
	        	Declaration dec = hn.getDeclaration();
	        	if (dec!=null) {
	        		//TODO: this is broken for Java declarations
	        		Node refNode = getReferencedNode(dec, getCompilationUnit(cpc, dec));
	        		if (refNode!=null) {
	        			gotoNode(refNode, cpc.getProject(), cpc.getTypeChecker());
	        		}
	        	}
	        }
    	}
    }

}
