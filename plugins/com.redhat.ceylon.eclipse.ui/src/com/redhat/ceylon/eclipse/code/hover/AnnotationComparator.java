package com.redhat.ceylon.eclipse.code.hover;

import java.util.Comparator;

import org.eclipse.jface.text.source.Annotation;

import com.redhat.ceylon.eclipse.code.editor.CeylonAnnotation;

public final class AnnotationComparator implements
        Comparator<Annotation> {
    @Override
    public int compare(Annotation x, Annotation y) {
        if (x instanceof CeylonAnnotation) {
            if (y instanceof CeylonAnnotation) {
                return -Integer.compare(((CeylonAnnotation) x).getSeverity(),
                        ((CeylonAnnotation) y).getSeverity());
            }
            else {
                return -1;
            }
        }
        else {
            if (y instanceof CeylonAnnotation) {
                return 1;
            }
            else {
                return x.getType().compareTo(y.getType());
            }
        }
    }
}