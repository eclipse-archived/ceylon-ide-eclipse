package com.redhat.ceylon.eclipse.code.hover;

import static com.redhat.ceylon.eclipse.code.hover.CeylonDocumentationProvider.getRefinementDocumentation;
import static org.eclipse.imp.utils.AnnotationUtils.formatAnnotationList;
import static org.eclipse.imp.utils.AnnotationUtils.getAnnotationsForLine;

import java.util.List;

import org.eclipse.imp.services.IAnnotationHover;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.ISourceViewer;

import com.redhat.ceylon.eclipse.code.editor.RefinementAnnotation;


public class CeylonAnnotationHover implements IAnnotationHover {
    
    @Override
    public String getHoverInfo(ISourceViewer sourceViewer, int lineNumber) {
        List<Annotation> annotations = getAnnotationsForLine(sourceViewer, lineNumber);
        if (annotations.size()==1) {
            Annotation a = annotations.get(0);
            if (a instanceof RefinementAnnotation) {
                return getRefinementDocumentation(((RefinementAnnotation) a).getDeclaration());
            }
        }
        return formatAnnotationList(annotations);
    }

    
}
