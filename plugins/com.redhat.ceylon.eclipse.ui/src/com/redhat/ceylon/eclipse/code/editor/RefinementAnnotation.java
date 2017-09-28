package org.eclipse.ceylon.ide.eclipse.code.editor;

import static org.eclipse.ceylon.ide.eclipse.code.editor.Navigation.gotoDeclaration;
import static org.eclipse.ceylon.ide.eclipse.ui.CeylonPlugin.PLUGIN_ID;

import org.eclipse.jface.text.source.Annotation;

import org.eclipse.ceylon.model.typechecker.model.Declaration;

public class RefinementAnnotation extends Annotation {

    private Declaration declaration;
    private int line;
    
    public RefinementAnnotation(String text, 
            Declaration dec, int line) {
        super(PLUGIN_ID + ".refinement", false, text);
        this.declaration = dec;
        this.line = line;
    }
    
    public Declaration getDeclaration() {
        return declaration;
    }
    
    public int getLine() {
        return line;
    }
    
    public void gotoRefinedDeclaration(CeylonEditor editor) {
        gotoDeclaration(declaration);
    }
    
}
