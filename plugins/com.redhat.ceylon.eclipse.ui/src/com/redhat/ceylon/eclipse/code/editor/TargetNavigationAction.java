package com.redhat.ceylon.eclipse.code.editor;

import org.eclipse.imp.services.INavigationTargetFinder;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.text.IRegion;
import org.eclipse.ui.texteditor.ITextEditor;

import com.redhat.ceylon.eclipse.code.parse.CeylonParseController;
import com.redhat.ceylon.eclipse.code.parse.CeylonSourcePositionLocator;

abstract class TargetNavigationAction extends Action {
    protected CeylonEditor fEditor;
    protected INavigationTargetFinder fNavTargetFinder;

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
        fNavTargetFinder= null;
        if (editor instanceof CeylonEditor) {
            fEditor= (CeylonEditor) editor;
            fNavTargetFinder= null; //TODO??
        } 
        else {
            fEditor= null;
        }
        setEnabled(fNavTargetFinder != null);
    }

    @Override
    public void run() {
        IRegion selection= fEditor.getSelectedRegion();
        CeylonParseController pc= fEditor.getParseController();
        CeylonSourcePositionLocator locator= pc.getSourcePositionLocator();
        Object curNode= locator.findNode(pc.getCurrentAst(), selection.getOffset(), selection.getOffset() + selection.getLength() - 1);
        if (curNode == null || selection.getOffset() == 0) {
            curNode= pc.getCurrentAst();
        }
        Object prev= getNavTarget(curNode, pc.getCurrentAst());
    
        if (prev != null) {
            int prevOffset= locator.getStartOffset(prev);
    
            fEditor.selectAndReveal(prevOffset, 0);
        }
    }
}