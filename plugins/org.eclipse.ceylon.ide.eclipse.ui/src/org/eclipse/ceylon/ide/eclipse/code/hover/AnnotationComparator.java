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