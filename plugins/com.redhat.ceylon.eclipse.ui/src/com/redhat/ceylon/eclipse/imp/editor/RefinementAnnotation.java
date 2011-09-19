package com.redhat.ceylon.eclipse.imp.editor;

import org.eclipse.jface.text.source.Annotation;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;

public class RefinementAnnotation extends Annotation {

    private Declaration declaration;
    private int line;
    
    public RefinementAnnotation(String text, Declaration dec, int line) {
        super("com.redhat.ceylon.eclipse.ui.refinement", false, text);
        this.declaration = dec;
        this.line = line;
    }

    public Declaration getDeclaration() {
        return declaration;
    }
    
    public int getLine() {
        return line;
    }
    
}
