package com.redhat.ceylon.eclipse.code.editor;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.text.IRegion;
import org.eclipse.ui.texteditor.ITextEditor;

import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.eclipse.code.parse.CeylonParseController;
import com.redhat.ceylon.eclipse.util.Nodes;

abstract class TargetNavigationAction extends Action {
    protected CeylonEditor fEditor;
    //protected INavigationTargetFinder fNavTargetFinder;

    protected abstract Object getNavTarget(Object o, Object astRoot);

    protected TargetNavigationAction(String title, String actionDefID) {
        this(null, title, actionDefID);
    }

    public TargetNavigationAction(CeylonEditor editor, String title, String actionDefID) {
        setEditor(editor);
        setText(title);
        setActionDefinitionId(actionDefID);
    }

    public void setEditor(ITextEditor editor) {
        //fNavTargetFinder= null;
        if (editor instanceof CeylonEditor) {
            fEditor= (CeylonEditor) editor;
            //fNavTargetFinder= null; //TODO??
        } 
        else {
            fEditor= null;
        }
        //setEnabled(fNavTargetFinder!=null);
        setEnabled(false);
    }

    @Override
    public void run() {
        IRegion selection= fEditor.getSelection();
        CeylonParseController pc= fEditor.getParseController();
        Object curNode= Nodes.findNode(pc.getRootNode(), selection.getOffset(), 
                selection.getOffset() + selection.getLength() - 1);
        if (curNode == null || selection.getOffset() == 0) {
            curNode= pc.getRootNode();
        }
        Object prev= getNavTarget(curNode, pc.getRootNode());
        if (prev instanceof Node) {
            fEditor.selectAndReveal(Nodes.getStartOffset((Node)prev), 0);
        }
    }
}