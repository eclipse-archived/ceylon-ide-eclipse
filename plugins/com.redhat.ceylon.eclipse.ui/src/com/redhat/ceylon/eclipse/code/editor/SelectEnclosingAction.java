package com.redhat.ceylon.eclipse.code.editor;

import static com.redhat.ceylon.eclipse.code.editor.EditorActionIds.SELECT_ENCLOSING;
import static com.redhat.ceylon.eclipse.code.parse.CeylonSourcePositionLocator.findNode;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.text.IRegion;
import org.eclipse.ui.texteditor.ITextEditor;

import com.redhat.ceylon.eclipse.code.parse.CeylonParseController;

class SelectEnclosingAction extends Action {
    private CeylonEditor fEditor;
    //private INavigationTargetFinder fNavTargetFinder;

    public SelectEnclosingAction() {
        this(null);
    }

    public SelectEnclosingAction(CeylonEditor editor) {
        super("Select Enclosing");
        setActionDefinitionId(SELECT_ENCLOSING);
        setEditor(editor);
    }

    public void setEditor(ITextEditor editor) {
        //fNavTargetFinder= null;
        if (editor instanceof CeylonEditor) {
            fEditor= (CeylonEditor) editor;
            //fNavTargetFinder= null; //TODO???
        } 
        else {
            fEditor= null;
        }
        //setEnabled(fNavTargetFinder != null);
        setEnabled(false);
    }

    @Override
    public void run() {
        IRegion selection= fEditor.getSelectedRegion();
        CeylonParseController pc= fEditor.getParseController();
        Object curNode= findNode(pc.getRootNode(), selection.getOffset(), 
        		selection.getOffset() + selection.getLength() - 1);
        if (curNode == null || selection.getOffset() == 0) {
            curNode= pc.getRootNode();
        }
        /*Object enclosing= fNavTargetFinder.getEnclosingConstruct(curNode, pc.getRootNode());
        if (enclosing != null) {
            int enclOffset= getStartOffset(enclosing);
            int enclEnd= getEndOffset(enclosing);
            fEditor.selectAndReveal(enclOffset, enclEnd - enclOffset + 1);
        }*/
    }
}