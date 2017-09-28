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