package com.redhat.ceylon.eclipse.code.hover;

import static com.redhat.ceylon.eclipse.util.AnnotationUtils.formatAnnotationList;
import static com.redhat.ceylon.eclipse.util.AnnotationUtils.getAnnotationsForLine;

import java.util.List;
import java.util.ListIterator;

import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.IAnnotationHover;
import org.eclipse.jface.text.source.ISourceViewer;

import com.redhat.ceylon.eclipse.code.editor.CeylonInitializerAnnotation;

public class CeylonAnnotationHover implements IAnnotationHover {

    @Override
    public String getHoverInfo(ISourceViewer sourceViewer, int lineNumber) {
        List<Annotation> annotations = getAnnotationsForLine(sourceViewer, lineNumber);

        CeylonInitializerAnnotation deepestInitializerAnnotation = null;
        ListIterator<Annotation> annotationIterator = annotations.listIterator();
        while (annotationIterator.hasNext()) {
            Annotation annotation = annotationIterator.next();
            if (annotation instanceof CeylonInitializerAnnotation) {
                annotationIterator.remove();
                CeylonInitializerAnnotation initializerAnnotation = (CeylonInitializerAnnotation) annotation;
                if (deepestInitializerAnnotation == null || deepestInitializerAnnotation.getDepth() < initializerAnnotation.getDepth()) {
                    deepestInitializerAnnotation = initializerAnnotation;
                }
            }
        }
        if (annotations.isEmpty()) {
            annotations.add(deepestInitializerAnnotation);
        }

        return formatAnnotationList(annotations);
    }

}