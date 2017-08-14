package com.redhat.ceylon.eclipse.code.editor;

import java.util.Iterator;
import java.util.ResourceBundle;

import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.IVerticalRulerInfo;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.SelectMarkerRulerAction;

public class CeylonSelectAnnotationRulerAction extends SelectMarkerRulerAction {
    
    IVerticalRulerInfo ruler;
    CeylonEditor editor;
    
    public CeylonSelectAnnotationRulerAction(ResourceBundle bundle, String prefix,
            ITextEditor editor, IVerticalRulerInfo ruler) {
        super(bundle, prefix, editor, ruler);
        this.ruler = ruler;
        this.editor = (CeylonEditor) editor;
    }
    
    @Override
    public void update() {
        //don't let super.update() be called!
    }
    
    @Override
    public void run() {
        //super.run();
        int line = ruler.getLineOfLastMouseButtonActivity()+1;
        IAnnotationModel model= editor.getDocumentProvider()
                .getAnnotationModel(editor.getEditorInput());
        for (Iterator<Annotation> iter = 
                    model.getAnnotationIterator(); 
                iter.hasNext();) {
            Annotation ann = iter.next();
            if (ann instanceof RefinementAnnotation) {
                RefinementAnnotation ra = (RefinementAnnotation) ann;
                if (ra.getLine()==line) {
                    ra.gotoRefinedDeclaration(editor);
                }
            }
        }
    }

}
