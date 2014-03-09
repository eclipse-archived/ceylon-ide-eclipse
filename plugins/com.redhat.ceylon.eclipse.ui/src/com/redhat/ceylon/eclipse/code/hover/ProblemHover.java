package com.redhat.ceylon.eclipse.code.hover;

import java.util.Map;

import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;

import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;

public class ProblemHover extends AbstractAnnotationHover {
    
    private final CeylonEditor editor;
    
    public ProblemHover(CeylonEditor editor) {
        super(false);
        this.editor = editor;
    }
    
    @Override
    protected AnnotationInfo createAnnotationInfo(ITextViewer textViewer, 
            Map<Annotation,Position> annotationPositions) {
        return new ProblemInfo(editor, annotationPositions, textViewer);
    }
}
