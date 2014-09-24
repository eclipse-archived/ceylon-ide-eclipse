package com.redhat.ceylon.eclipse.code.search;

import org.eclipse.search.ui.text.Match;

import com.redhat.ceylon.compiler.typechecker.io.VirtualFile;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.util.Nodes;

public class CeylonSearchMatch extends Match {
    
    public CeylonSearchMatch(Node match, 
            //the containing declaration or named arg
            Tree.StatementOrArgument container,
            //the file in which the match occurs
            VirtualFile file) {
        super(new CeylonElement(container, file, match.getToken().getLine()),
                //the exact location of the match:
                Nodes.getStartOffset(match), 
                Nodes.getLength(match));
    }
    
    @Override
    public CeylonElement getElement() {
        return (CeylonElement) super.getElement();
    }
    
}
