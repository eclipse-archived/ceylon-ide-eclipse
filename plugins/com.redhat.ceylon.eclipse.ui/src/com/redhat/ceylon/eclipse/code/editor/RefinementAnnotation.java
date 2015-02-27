package com.redhat.ceylon.eclipse.code.editor;

import static com.redhat.ceylon.eclipse.code.editor.Navigation.gotoDeclaration;
import static com.redhat.ceylon.eclipse.ui.CeylonPlugin.PLUGIN_ID;

import org.eclipse.jface.text.source.Annotation;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;

public class RefinementAnnotation extends Annotation {

    private Declaration declaration;
    private int line;
    
    public RefinementAnnotation(String text, Declaration dec, int line) {
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
//        Declaration declaration = getDeclaration();
//        Tree.CompilationUnit rootNode = 
//		        getCompilationUnit(declaration, 
//		                editor.getParseController());
//        if (rootNode!=null) {
//            gotoNode(getReferencedNodeInUnit(declaration, rootNode));
//        }
//        else {
//            gotoJavaNode(declaration);
//        }
    }
    
}
