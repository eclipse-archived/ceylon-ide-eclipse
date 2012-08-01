package com.redhat.ceylon.eclipse.code.editor;

import static com.redhat.ceylon.eclipse.code.editor.EditorActionIds.SELECT_ENCLOSING;
import static com.redhat.ceylon.eclipse.code.parse.CeylonSourcePositionLocator.findNode;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.text.IRegion;
import org.eclipse.ui.texteditor.ITextEditor;

import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.CompilationUnit;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.Expression;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.StatementOrArgument;
import com.redhat.ceylon.eclipse.code.parse.CeylonParseController;

class SelectEnclosingAction extends Action {
    private CeylonEditor fEditor;

    public SelectEnclosingAction() {
        this(null);
    }

    public SelectEnclosingAction(CeylonEditor editor) {
        super("Select Enclosing");
        setActionDefinitionId(SELECT_ENCLOSING);
        setEditor(editor);
    }

    public void setEditor(ITextEditor editor) {
        if (editor instanceof CeylonEditor) {
            fEditor= (CeylonEditor) editor;
        } 
        else {
            fEditor= null;
        }
        setEnabled(fEditor!=null);
    }
    
    private static class EnclosingVisitor extends Visitor {
    	private Node node;
    	private Node current;
    	private Node result;
    	private int startOffset; 
    	private int endOffset;
    	private EnclosingVisitor(Node node, 
    			int startOffset, int endOffset) {
    		this.node = node;
    		this.startOffset = startOffset;
    		this.endOffset = endOffset;
    	}
		private boolean expandsSelection(Node that) {
			return that.getStartIndex()<startOffset ||
					that.getStopIndex()>endOffset;
		}
    	@Override
    	public void visitAny(Node that) {
    		if (that==node) {
    			result = current;
    		}
    		else {
    			super.visitAny(that);
    		}
    	}
    	@Override
    	public void visit(Expression that) {
    		Node oc = current;
    		if (expandsSelection(that)) current = that;
    		super.visit(that);
    		current = oc;
    	}
    	@Override
    	public void visit(StatementOrArgument that) {
    		Node oc = current;
    		if (expandsSelection(that)) current = that;
    		super.visit(that);
    		current = oc;
    	}
    }

    @Override
    public void run() {
        IRegion selection= fEditor.getSelection();
        CeylonParseController pc= fEditor.getParseController();
        int startOffset = selection.getOffset();
		int endOffset = startOffset + selection.getLength() - 1;
		CompilationUnit rootNode = pc.getRootNode();
		Node curNode= findNode(rootNode, startOffset, endOffset);
        if (curNode!=null) {
        	EnclosingVisitor ev = new EnclosingVisitor(curNode, 
        			startOffset, endOffset);
        	ev.visit(rootNode);
        	Node result = ev.result;
        	if (result!=null) {
        		fEditor.selectAndReveal(result.getStartIndex(), 
        				result.getStopIndex()-result.getStartIndex()+1);
        	}
        }
    }
}