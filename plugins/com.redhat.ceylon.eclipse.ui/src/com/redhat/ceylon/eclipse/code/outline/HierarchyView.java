package com.redhat.ceylon.eclipse.code.outline;

import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.getImageForDeclaration;
import static com.redhat.ceylon.eclipse.code.outline.HierarchyMode.HIERARCHY;
import static com.redhat.ceylon.eclipse.code.outline.HierarchyMode.SUBTYPES;
import static com.redhat.ceylon.eclipse.code.outline.HierarchyMode.SUPERTYPES;
import static com.redhat.ceylon.eclipse.code.parse.CeylonSourcePositionLocator.findNode;
import static com.redhat.ceylon.eclipse.code.propose.CeylonContentProposer.getDescriptionFor;
import static com.redhat.ceylon.eclipse.code.propose.CeylonContentProposer.getStyledDescriptionFor;
import static com.redhat.ceylon.eclipse.code.resolve.CeylonReferenceResolver.getReferencedDeclaration;
import static com.redhat.ceylon.eclipse.ui.CeylonResources.CEYLON_HIER;
import static com.redhat.ceylon.eclipse.ui.CeylonResources.CEYLON_SUB;
import static com.redhat.ceylon.eclipse.ui.CeylonResources.CEYLON_SUP;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.part.ViewPart;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.TypeDeclaration;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.ui.CeylonPlugin;

public class HierarchyView extends ViewPart {

	private CeylonHierarchyLabelProvider labelProvider;
	private CeylonHierarchyContentProvider contentProvider;
	private MembersLabelProvider membersLabelProvider;
	private MembersContentProvider membersContentProvider;
	
	private TreeViewer treeViewer;
	private TableViewer tableViewer;
	
	private ModeAction hierarchyAction;
	private ModeAction supertypesAction;
	private ModeAction subtypesAction;

	private final class MembersContentProvider implements IStructuredContentProvider {
		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}

		@Override
		public void dispose() {}

		@Override
		public Object[] getElements(Object inputElement) {
			if (inputElement instanceof TypeDeclaration) {
				return ((TypeDeclaration) inputElement).getMembers().toArray();
			}
			else {
				return null;
			}
		}
	}

	class MembersLabelProvider extends StyledCellLabelProvider 
	        implements DelegatingStyledCellLabelProvider.IStyledLabelProvider, ILabelProvider {

		@Override
		public void addListener(ILabelProviderListener listener) {}

		@Override
		public void dispose() {}

		@Override
		public boolean isLabelProperty(Object element, String property) {
			return false;
		}

		@Override
		public void removeListener(ILabelProviderListener listener) {}

		@Override
		public Image getImage(Object element) {
			return getImageForDeclaration((Declaration) element);
		}

		@Override
		public String getText(Object element) {
			return getDescriptionFor((Declaration) element);
		}

		@Override
		public StyledString getStyledText(Object element) {
			return getStyledDescriptionFor((Declaration) element);
		}

		@Override
		public void update(ViewerCell cell) {
			Object element = cell.getElement();
			if (element!=null) {
				StyledString styledText = getStyledText(element);
				cell.setText(styledText.toString());
				cell.setStyleRanges(styledText.getStyleRanges());
				cell.setImage(getImage(element));
				super.update(cell);
			}
		}

	}
	
	@Override
	public void createPartControl(Composite parent) {
		SashForm sash = new SashForm(parent, SWT.VERTICAL);
        final Tree tree = new Tree(sash, SWT.SINGLE);
        GridData gd = new GridData(GridData.FILL_BOTH);
        gd.heightHint = tree.getItemHeight() * 12;
        tree.setLayoutData(gd);
        treeViewer = new TreeViewer(tree);
        contentProvider = new CeylonHierarchyContentProvider(getSite());
        labelProvider = new CeylonHierarchyLabelProvider(contentProvider);
        treeViewer.setContentProvider(contentProvider);
        treeViewer.setLabelProvider(labelProvider);
        treeViewer.setAutoExpandLevel(getDefaultLevel());
        IToolBarManager tbm = getViewSite().getActionBars().getToolBarManager();
        tbm.add(hierarchyAction=new ModeAction("Hierarchy", 
        		"Switch to hierarchy mode", 
        				CEYLON_HIER, HIERARCHY));
        tbm.add(supertypesAction=new ModeAction("Supertypes", 
        		"Switch to supertypes mode", 
        				CEYLON_SUP, SUPERTYPES));
        tbm.add(subtypesAction=new ModeAction("Subtypes", 
        		"Switch to subtypes mode", 
        				CEYLON_SUB, SUBTYPES));
        updateActions(HIERARCHY);
//        ViewForm viewForm = new ViewForm(sash, SWT.FLAT);
//        GridLayout layout = new GridLayout(1, false);
//        layout.horizontalSpacing = 0;
//        layout.verticalSpacing = 0;
//        viewForm.setLayout(layout);
		GridData vfgd = new GridData(GridData.FILL_BOTH);
//		viewForm.setLayoutData(vfgd);
        tableViewer = new TableViewer(sash);
        tableViewer.getTable().setLayoutData(vfgd);
        membersLabelProvider=new MembersLabelProvider();
        membersContentProvider=new MembersContentProvider();
        tableViewer.setLabelProvider(membersLabelProvider);
        tableViewer.setContentProvider(membersContentProvider);
	}

	private int getDefaultLevel() {
		return 4;
	}
	
	private void updateActions(HierarchyMode mode) {
		hierarchyAction.setChecked(mode==HIERARCHY);
		supertypesAction.setChecked(mode==SUPERTYPES);
		subtypesAction.setChecked(mode==SUBTYPES);
	}

    protected void update() {
        treeViewer.getControl().setRedraw(false);
        // refresh viewer to re-filter
        treeViewer.refresh();
        reveal();
        //fTreeViewer.expandAll();
//        selectFirstMatch(); //TODO select the main declaration instead!
        treeViewer.getControl().setRedraw(true);
    }
    
	private void reveal() {
        treeViewer.expandToLevel(getDefaultLevel());
    }

	@Override
	public void setFocus() {}

	public void focusOnSelection(CeylonEditor editor) {
		Node node = findNode(editor.getParseController().getRootNode(), editor.getSelection().getOffset());
		Declaration dec = getReferencedDeclaration(node);
		if (dec!=null) {
			tableViewer.setInput(dec);
			treeViewer.setInput(new HierarchyInput(dec, 
					editor.getParseController().getTypeChecker()));
		}
	}

	private class ModeAction extends Action {
		HierarchyMode mode;

		ModeAction(String label, String tooltip, 
				String imageKey, HierarchyMode mode) {
			super(label);
			setToolTipText(tooltip);
			setImageDescriptor(CeylonPlugin.getInstance()
					.getImageRegistry()
					.getDescriptor(imageKey));
			this.mode = mode;
		}

		@Override
		public void run() {
			contentProvider.setMode(mode);
			update();
			updateActions(mode);
		}

	}

}
