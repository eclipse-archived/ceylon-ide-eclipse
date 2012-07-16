package com.redhat.ceylon.eclipse.code.editor;

import static com.redhat.ceylon.eclipse.code.editor.EditorActionIds.SELECT_ENCLOSING;

import org.eclipse.imp.services.INavigationTargetFinder;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.text.IRegion;
import org.eclipse.ui.texteditor.ITextEditor;

import com.redhat.ceylon.eclipse.code.parse.CeylonParseController;
import com.redhat.ceylon.eclipse.code.parse.CeylonSourcePositionLocator;

class SelectEnclosingAction extends Action {
    private CeylonEditor fEditor;
    private INavigationTargetFinder fNavTargetFinder;

    public SelectEnclosingAction() {
        this(null);
    }

    public SelectEnclosingAction(CeylonEditor editor) {
        super("Select Enclosing");
        setActionDefinitionId(SELECT_ENCLOSING);
        setEditor(editor);
    }

    public void setEditor(ITextEditor editor) {
        fNavTargetFinder= null;
        if (editor instanceof CeylonEditor) {
            fEditor= (CeylonEditor) editor;
            fNavTargetFinder= null; //TODO???
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
        Object enclosing= fNavTargetFinder.getEnclosingConstruct(curNode, pc.getCurrentAst());
    
        if (enclosing != null) {
            int enclOffset= locator.getStartOffset(enclosing);
            int enclEnd= locator.getEndOffset(enclosing);

            fEditor.selectAndReveal(enclOffset, enclEnd - enclOffset + 1);
        }
    }
}