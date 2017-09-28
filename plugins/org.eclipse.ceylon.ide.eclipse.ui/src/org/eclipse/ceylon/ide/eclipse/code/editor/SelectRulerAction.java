package org.eclipse.ceylon.ide.eclipse.code.editor;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.text.source.IVerticalRulerInfo;
import org.eclipse.ui.texteditor.AbstractRulerActionDelegate;
import org.eclipse.ui.texteditor.ITextEditor;

public class SelectRulerAction extends AbstractRulerActionDelegate {
    @Override
    protected IAction createAction(ITextEditor editor, IVerticalRulerInfo rulerInfo) {
        return new CeylonSelectAnnotationRulerAction(
                EditorActionMessages.ResBundle, "SelectRulerAction.", editor, rulerInfo);
    }
}
