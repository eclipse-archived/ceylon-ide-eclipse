package com.redhat.ceylon.eclipse.imp.editor;

import org.eclipse.jface.text.source.Annotation;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.eclipse.imp.parser.CeylonParseController;

public class RefinementAnnotation extends Annotation {

    Declaration declaration;
    private int line;
    CeylonParseController parseController;
    
    public RefinementAnnotation(String type, String text, 
            Declaration dec, CeylonParseController parseController, int line) {
        super(type, false, text);
        this.declaration = dec;
        this.parseController = parseController;
        this.line = line;
    }
    
    public Declaration getDeclaration() {
        return declaration;
    }
    
    public CeylonParseController getParseController() {
        return parseController;
    }
    
    public int getLine() {
        return line;
    }
    
}
