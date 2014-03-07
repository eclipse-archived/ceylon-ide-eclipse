package com.redhat.ceylon.eclipse.code.search;

import org.eclipse.search.ui.text.Match;

import com.redhat.ceylon.compiler.typechecker.io.VirtualFile;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.util.Nodes;

public class CeylonSearchMatch extends Match {
    
    public CeylonSearchMatch(Tree.StatementOrArgument container, 
            VirtualFile file, Node node) {
        super(new CeylonElement(container, file, node.getToken().getLine()), 
                Nodes.getStartOffset(node), 
                Nodes.getLength(node));
    }
    
    @Override
    public CeylonElement getElement() {
        return (CeylonElement) super.getElement();
    }
    
}
