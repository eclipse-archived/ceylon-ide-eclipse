package com.redhat.ceylon.eclipse.imp.editor;

import static com.redhat.ceylon.eclipse.imp.core.CeylonReferenceResolver.getCompilationUnit;
import static com.redhat.ceylon.eclipse.imp.core.CeylonReferenceResolver.getReferencedNode;

import java.util.Iterator;
import java.util.ResourceBundle;

import org.eclipse.imp.editor.UniversalEditor;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.IVerticalRulerInfo;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.SelectMarkerRulerAction;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.eclipse.imp.parser.CeylonParseController;

public class CeylonSelectAnnotationRulerAction extends SelectMarkerRulerAction {
    
    IVerticalRulerInfo ruler;
    UniversalEditor editor;
    
    public CeylonSelectAnnotationRulerAction(ResourceBundle bundle, String prefix,
            ITextEditor editor, IVerticalRulerInfo ruler) {
        super(bundle, prefix, editor, ruler);
        this.ruler = ruler;
        this.editor = (UniversalEditor) editor;
    }
    
    @Override
    public void update() {
        //don't let super.update() be called!
    }
    
    @Override
    public void run() {
        //super.run();
        int line = ruler.getLineOfLastMouseButtonActivity()+1;
        for (Iterator<Annotation> iter = getAnnotationModel().getAnnotationIterator(); 
                iter.hasNext();) {
            Annotation ann = iter.next();
            if (ann instanceof RefinementAnnotation) {
                RefinementAnnotation ra = (RefinementAnnotation) ann;
                if (ra.getLine()==line) {
                    Declaration dec = ra.getDeclaration();
                    //CeylonParseController cpc = ra.getParseController();
                    CeylonParseController cpc = getCurrentParseController();
                    cpc.getSourcePositionLocator().gotoNode(getReferencedNode(dec, 
                            getCompilationUnit(cpc, dec)));
                }
            }
        }
    }

    private CeylonParseController getCurrentParseController() {
        return (CeylonParseController) editor.getParseController();
    }

}
