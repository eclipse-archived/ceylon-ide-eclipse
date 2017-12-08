/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 1.0 which is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.code.hover;

import static org.eclipse.ceylon.ide.eclipse.core.builder.CeylonBuilder.PROBLEM_MARKER_ID;

import java.util.Iterator;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.ui.texteditor.MarkerAnnotation;

import org.eclipse.ceylon.ide.eclipse.code.editor.CeylonAnnotation;
import org.eclipse.ceylon.ide.eclipse.code.editor.CeylonInitializerAnnotation;
import org.eclipse.ceylon.ide.eclipse.code.editor.RefinementAnnotation;


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
        this.includeRefinementAnnotations = 
                includeRefinementAnnotations;
        skip();
    }

    private void skip() {
        while (iterator.hasNext()) {
            Annotation next = (Annotation) iterator.next();
            if (!next.isMarkedDeleted()) {
                //TODO: rethink this condition!
                if (next instanceof CeylonAnnotation || 
                    includeRefinementAnnotations &&
                        (next instanceof RefinementAnnotation || 
                         next instanceof CeylonInitializerAnnotation) ||
                         isProblemMarkerAnnotation(next)) {
                    nextAnnotation = next;
                    return;
                }
            }
        }
        nextAnnotation = null;
    }

    private static boolean isProblemMarkerAnnotation(
            Annotation annotation) {
        if (annotation instanceof MarkerAnnotation) {
            try {
                MarkerAnnotation ma = 
                        (MarkerAnnotation) annotation;
                return ma.getMarker()
                            .isSubtypeOf(IMarker.PROBLEM) &&
                        !ma.getMarker().getType()
                            .equals(PROBLEM_MARKER_ID);
            } 
            catch (CoreException e) {
                return false;
            }
        }
        else {
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
