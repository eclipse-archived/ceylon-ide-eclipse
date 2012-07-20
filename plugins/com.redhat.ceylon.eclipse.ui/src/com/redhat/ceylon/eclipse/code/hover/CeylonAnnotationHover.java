package com.redhat.ceylon.eclipse.code.hover;

import static com.redhat.ceylon.eclipse.util.AnnotationUtils.formatAnnotationList;
import static com.redhat.ceylon.eclipse.util.AnnotationUtils.getAnnotationsForLine;

import org.eclipse.jface.text.source.IAnnotationHover;
import org.eclipse.jface.text.source.ISourceViewer;


public class CeylonAnnotationHover implements IAnnotationHover {
    
    @Override
    public String getHoverInfo(ISourceViewer sourceViewer, int lineNumber) {
        return formatAnnotationList(getAnnotationsForLine(sourceViewer, lineNumber));
    }

}
