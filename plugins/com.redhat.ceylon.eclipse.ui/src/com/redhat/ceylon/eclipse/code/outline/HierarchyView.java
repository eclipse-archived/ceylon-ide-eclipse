package com.redhat.ceylon.eclipse.code.outline;

import static com.redhat.ceylon.eclipse.code.parse.CeylonSourcePositionLocator.findNode;
import static com.redhat.ceylon.eclipse.code.resolve.CeylonReferenceResolver.getReferencedDeclaration;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.part.ViewPart;

import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;

public class HierarchyView extends ViewPart {

	private CeylonHierarchyLabelProvider labelProvider;
	private CeylonHierarchyContentProvider contentProvider;
	
	private TreeViewer treeViewer;
	
	@Override
	public void createPartControl(Composite parent) {
        final Tree tree = new Tree(parent, SWT.SINGLE);
        GridData gd = new GridData(GridData.FILL_BOTH);
        gd.heightHint = tree.getItemHeight() * 12;
        tree.setLayoutData(gd);
        treeViewer = new TreeViewer(tree);
        contentProvider = new CeylonHierarchyContentProvider(getSite());
        labelProvider = new CeylonHierarchyLabelProvider(contentProvider);
        treeViewer.setContentProvider(contentProvider);
        treeViewer.setLabelProvider(labelProvider);
        treeViewer.setAutoExpandLevel(1);
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

	public void focusOnSelection(CeylonEditor editor) {
		Node node = findNode(editor.getParseController().getRootNode(), editor.getSelection().getOffset());
		treeViewer.setInput(new HierarchyInput(getReferencedDeclaration(node), 
				editor.getParseController().getTypeChecker()));
	}

}
