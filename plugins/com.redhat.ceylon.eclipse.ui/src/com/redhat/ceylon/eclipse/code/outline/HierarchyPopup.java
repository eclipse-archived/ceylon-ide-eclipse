package com.redhat.ceylon.eclipse.code.outline;

import static com.redhat.ceylon.eclipse.code.parse.CeylonSourcePositionLocator.findNode;
import static com.redhat.ceylon.eclipse.code.propose.CeylonContentProposer.getDescriptionFor;
import static com.redhat.ceylon.eclipse.code.resolve.CeylonReferenceResolver.getReferencedDeclaration;
import static org.eclipse.jface.viewers.AbstractTreeViewer.ALL_LEVELS;

import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.TypeDeclaration;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.code.outline.Popup.NamePatternFilter;
import com.redhat.ceylon.eclipse.code.parse.CeylonParseController;
import com.redhat.ceylon.eclipse.code.search.FindContainerVisitor;

public class HierarchyPopup extends Popup {

    private Object fInput= null;
    private CeylonEditor editor;
    private CeylonHierarchyLabelProvider labelProvider;
	private CeylonHierarchyContentProvider contentProvider;
    
    public HierarchyPopup(Shell parent, int shellStyle, int treeStyle, String commandId) {
        super(parent, shellStyle, treeStyle, commandId, true);
    }
    
    //TODO: this is a copy/paste from AbstractFindAction
    private static Node getSelectedNode(CeylonEditor editor) {
        CeylonParseController cpc = editor.getParseController();
        return cpc.getRootNode()==null ? null : 
            findNode(cpc.getRootNode(), 
                (ITextSelection) editor.getSelectionProvider().getSelection());
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
        final Object root = new Object();
        labelProvider = new CeylonHierarchyLabelProvider();
        contentProvider = new CeylonHierarchyContentProvider();
        treeViewer.setContentProvider(contentProvider);
        treeViewer.setLabelProvider(labelProvider);
        treeViewer.addFilter(new NamePatternFilter());
        treeViewer.setInput(root);
        treeViewer.setAutoExpandLevel(ALL_LEVELS);
 		return treeViewer;
	}

	@Override
	protected String getId() {
		return "org.eclipse.jdt.internal.ui.typehierarchy.QuickHierarchy";
	}

	@Override
	public void setInput(Object information) {
        if (information == null || information instanceof String) {
            inputChanged(null, null);
            return;
        }
        if (information instanceof CeylonEditor) {
        	this.editor = (CeylonEditor) information;
        	Node selectedNode = getSelectedNode(editor);
			Declaration declaration = getReferencedDeclaration(selectedNode);
        	if (declaration==null) {
        		FindContainerVisitor fcv = new FindContainerVisitor(selectedNode);
        		fcv.visit(editor.getParseController().getRootNode());
        		com.redhat.ceylon.compiler.typechecker.tree.Tree.StatementOrArgument node = fcv.getDeclaration();
        		if (node instanceof com.redhat.ceylon.compiler.typechecker.tree.Tree.Declaration) {
        			declaration = ((com.redhat.ceylon.compiler.typechecker.tree.Tree.Declaration) node).getDeclarationModel();
        		}
        	}
        	if (declaration!=null) {
        		setTitleText("Hierarchy of '" + getDescriptionFor(declaration) + "'");
        		fInput = contentProvider.init(declaration, editor);
        		labelProvider.isMember = !(declaration instanceof TypeDeclaration);
        		setInput(fInput);
        	}
            inputChanged(fInput, information);
        }
	}
    
}
