package com.redhat.ceylon.eclipse.code.hover;

import java.util.Iterator;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.ui.texteditor.MarkerAnnotation;

import com.redhat.ceylon.eclipse.code.editor.CeylonAnnotation;
import com.redhat.ceylon.eclipse.code.editor.CeylonInitializerAnnotation;
import com.redhat.ceylon.eclipse.code.editor.RefinementAnnotation;
import com.redhat.ceylon.eclipse.core.builder.CeylonBuilder;


/**
 * Filters problems based on their types.
 */
public class AnnotationIterator implements Iterator<Annotation> {

    private Iterator<Annotation> iterator;
    private Annotation nextAnnotation;
    private boolean includeRefinementAnnotations;
    
    /**
     * Returns a new JavaAnnotationIterator.
     * @param parent the parent iterator to iterate over annotations
     * @param returnAllAnnotations whether to return all annotations or just problem annotations
     */
    public AnnotationIterator(Iterator<Annotation> parent,
            boolean includeRefinementAnnotations) {
        this.iterator = parent;
        this.includeRefinementAnnotations = includeRefinementAnnotations;
        skip();
    }

    private void skip() {
        while (iterator.hasNext()) {
            Annotation next = (Annotation) iterator.next();
            if (!next.isMarkedDeleted()) {
                //TODO: rethink this condition!
                if (next instanceof CeylonAnnotation || 
                    includeRefinementAnnotations &&
                        (next instanceof RefinementAnnotation || next instanceof CeylonInitializerAnnotation) ||
                    isProblemMarkerAnnotation(next)) {
                    nextAnnotation = next;
                    return;
                }
            }
        }
        nextAnnotation = null;
    }

    private static boolean isProblemMarkerAnnotation(Annotation annotation) {
        if (!(annotation instanceof MarkerAnnotation))
            return false;
        try {
            MarkerAnnotation ma = (MarkerAnnotation) annotation;
            return ma.getMarker().isSubtypeOf(IMarker.PROBLEM) &&
                    !ma.getMarker().getType().equals(CeylonBuilder.PROBLEM_MARKER_ID);
        } 
        catch (CoreException e) {
            return false;
        }
    }
    
    public boolean hasNext() {
        return nextAnnotation != null;
    }
    
    public Annotation next() {
        try {
            return nextAnnotation;
        }
        finally {
            skip();
        }
    }
    
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
