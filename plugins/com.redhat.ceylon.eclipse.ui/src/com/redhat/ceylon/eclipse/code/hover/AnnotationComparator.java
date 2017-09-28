package org.eclipse.ceylon.ide.eclipse.code.hover;

import java.util.Comparator;

import org.eclipse.jface.text.source.Annotation;

import org.eclipse.ceylon.ide.eclipse.code.editor.CeylonAnnotation;

public final class AnnotationComparator implements
        Comparator<Annotation> {
    @Override
    public int compare(Annotation x, Annotation y) {
        if (x instanceof CeylonAnnotation) {
            if (y instanceof CeylonAnnotation) {
                CeylonAnnotation cax = (CeylonAnnotation) x;
                CeylonAnnotation cay = (CeylonAnnotation) y;
                return -Integer.compare(cax.getSeverity(),
                                        cay.getSeverity());
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