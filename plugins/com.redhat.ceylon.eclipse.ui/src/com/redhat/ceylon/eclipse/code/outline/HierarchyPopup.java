package com.redhat.ceylon.eclipse.code.outline;

import static com.redhat.ceylon.eclipse.code.propose.CeylonContentProposer.getDescriptionFor;
import static com.redhat.ceylon.eclipse.ui.ICeylonResources.CEYLON_HIER;
import static com.redhat.ceylon.eclipse.ui.ICeylonResources.CEYLON_SUP;
import static org.eclipse.jface.viewers.AbstractTreeViewer.ALL_LEVELS;

import org.eclipse.jface.bindings.keys.KeyStroke;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;

import com.redhat.ceylon.compiler.typechecker.model.ClassOrInterface;
import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.ui.CeylonPlugin;

public class HierarchyPopup extends Popup {

    private CeylonHierarchyLabelProvider labelProvider;
	private CeylonHierarchyContentProvider contentProvider;
	private Label iconLabel;
	
    public HierarchyPopup(CeylonEditor editor, Shell parent, int shellStyle, 
    		int treeStyle, String commandId) {
        super(parent, shellStyle, treeStyle, commandId, editor);
    }
    
    @Override
    protected void adjustBounds() {
        Rectangle bounds = getShell().getBounds();
        int h = bounds.height;
        if (h>400) {
            bounds.height=400;
            bounds.y = bounds.y + (h-400)/3;
            getShell().setBounds(bounds);
        }
    }
    
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
 		return treeViewer;
	}

	@Override
	protected Text createFilterText(Composite parent) {
		Text result = super.createFilterText(parent);
		result.addKeyListener(new KeyListener() {
			@Override
			public void keyReleased(KeyEvent e) {}
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.character == 't' && (e.stateMask&SWT.MOD1)!=0) {
					contentProvider.reverse=!contentProvider.reverse;
					updateStatusFieldText();
					updateTitle();
					updateIcon();
					stringMatcherUpdated();
					e.doit=false;
				}
			}
		});
		return result;
	}
	
	@Override
	protected String getStatusFieldText() {
		String key = KeyStroke.getInstance(SWT.MOD1, 'T').format();
		if (contentProvider.reverse) {
			return key + " to show subtypes";
		}
		else {
			return key + " to show supertypes";
		}
	}
	
	private String getTitleText() {
		Declaration dec = contentProvider.declaration;
		String desc = getDescriptionFor(dec);
		if (contentProvider.isShowingRefinements()) {
			if (dec.isClassOrInterfaceMember()) {
				desc += " in " + ((ClassOrInterface) dec.getContainer()).getName();
			}
			return (contentProvider.reverse ?
					"All supertypes that generalize " : 
					"All subtypes that refine ") + desc;
		}
		else {
		    return (contentProvider.reverse ? 
		    		"All supertypes of " : 
		    		"Superclasses and all subtypes of ") + desc;
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
		boolean rev = contentProvider!=null && contentProvider.reverse;
		return CeylonPlugin.getInstance().getImageRegistry()
				.get(rev ? CEYLON_SUP : CEYLON_HIER);
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
	
}
