/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.code.editor;

import org.eclipse.jface.text.source.projection.ProjectionAnnotation;

final class CeylonProjectionAnnotation extends ProjectionAnnotation {
    private int tokenType;
    public CeylonProjectionAnnotation(int tokenType) {
        this.tokenType=tokenType;
    }
    public int getTokenType() {
        return tokenType;
    }
}