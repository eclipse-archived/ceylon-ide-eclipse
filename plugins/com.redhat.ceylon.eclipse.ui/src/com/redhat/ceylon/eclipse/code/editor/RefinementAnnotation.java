package com.redhat.ceylon.eclipse.code.editor;

import static com.redhat.ceylon.eclipse.code.editor.Navigation.gotoNode;
import static com.redhat.ceylon.eclipse.code.resolve.JavaHyperlinkDetector.gotoJavaNode;
import static com.redhat.ceylon.eclipse.ui.CeylonPlugin.PLUGIN_ID;

import org.eclipse.jface.text.source.Annotation;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.code.parse.CeylonParseController;
import com.redhat.ceylon.eclipse.util.Nodes;

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
        Declaration dec = getDeclaration();
        CeylonParseController cpc = editor.getParseController();
        Tree.CompilationUnit cu = Nodes.getCompilationUnit(dec, cpc);
        if (cu!=null) {
            gotoNode(Nodes.getReferencedNode(dec, cu), 
                    cpc.getProject(),
                    cpc.getTypeChecker());
        }
        else {
            gotoJavaNode(dec, cpc);
        }
    }
    
}
