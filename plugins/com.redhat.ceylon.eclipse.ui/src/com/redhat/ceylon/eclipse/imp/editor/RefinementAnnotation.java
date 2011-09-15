package com.redhat.ceylon.eclipse.imp.editor;

import org.eclipse.jface.text.source.Annotation;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;

public class RefinementAnnotation extends Annotation {

    Declaration declaration;
    private int line;
    //CeylonParseController parseController;
    
    public RefinementAnnotation(String text, Declaration dec, int line) {
        super(getType(dec), false, text);
        this.declaration = dec;
        //this.parseController = parseController;
        this.line = line;
    }

    private static String getType(Declaration dec) {
        return "com.redhat.ceylon.eclipse.ui.refinement." + 
                (dec.isFormal() ? "formal" : "default");
    }
    
    public Declaration getDeclaration() {
        return declaration;
    }
    
    /*public CeylonParseController getParseController() {
        return parseController;
    }*/
    
    public int getLine() {
        return line;
    }
    
}
