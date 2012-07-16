package com.redhat.ceylon.eclipse.code.editor;

import static com.redhat.ceylon.eclipse.code.resolve.CeylonReferenceResolver.getReferencedDeclaration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.code.parse.CeylonParseController;
import com.redhat.ceylon.eclipse.util.FindDeclarationVisitor;
import com.redhat.ceylon.eclipse.util.FindReferenceVisitor;

public class CeylonOccurrenceMarker {
    
    private List<Object> occurrences = Collections.emptyList();
    
    public String getKindName() {
        return "Ceylon Occurence Marker";
    }
    
    public List<Object> getOccurrencesOf(CeylonParseController parseController, Object node) {
        
        if (node instanceof Node) {
        	
            // Check whether we even have an AST in which to find occurrences
            Tree.CompilationUnit root = parseController.getRootNode();
            if (root == null) {
                return Collections.emptyList();
            }
            
            occurrences = new ArrayList<Object>();
            Declaration declaration = getReferencedDeclaration((Node) node);
            if (declaration != null) {
                FindReferenceVisitor frv = new FindReferenceVisitor(declaration);
                root.visit(frv);
                occurrences.addAll(frv.getNodes());
                FindDeclarationVisitor fdv = new FindDeclarationVisitor(frv.getDeclaration());
                root.visit(fdv);
                Tree.Declaration decNode = fdv.getDeclarationNode();
                if (decNode!=null) {
                    occurrences.add(decNode);
                }
            }
            return occurrences;
            
        }
        else {
            return Collections.emptyList();
        }
        
    }
    
}
