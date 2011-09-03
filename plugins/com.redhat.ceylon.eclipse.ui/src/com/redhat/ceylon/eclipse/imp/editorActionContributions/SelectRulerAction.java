package com.redhat.ceylon.eclipse.imp.editorActionContributions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.text.source.IVerticalRulerInfo;
import org.eclipse.ui.texteditor.AbstractRulerActionDelegate;
import org.eclipse.ui.texteditor.ITextEditor;

//TODO: Max look here
public class SelectRulerAction extends AbstractRulerActionDelegate {
    @Override
    protected IAction createAction(ITextEditor editor, IVerticalRulerInfo rulerInfo) {
        System.out.println("why doesn't anymore call me?");
        return new CeylonSelectAnnotationRulerAction(EditorActionMessages.ResBundle, 
                "CeylonSelectAnnotationRulerAction.", editor, rulerInfo);
    }
}
