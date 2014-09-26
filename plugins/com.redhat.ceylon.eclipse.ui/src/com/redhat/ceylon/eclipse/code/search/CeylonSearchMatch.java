package com.redhat.ceylon.eclipse.code.search;

import org.eclipse.search.ui.text.Match;

import com.redhat.ceylon.compiler.typechecker.io.VirtualFile;
import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.util.Nodes;

public class CeylonSearchMatch extends Match {
    
    private boolean inImport;
    
    public static CeylonSearchMatch create(Node match, 
            //the containing declaration or named arg
            Tree.CompilationUnit rootNode,
            //the file in which the match occurs
            VirtualFile file) {
        FindContainerVisitor fcv = new FindContainerVisitor(match) {
            @Override
            protected boolean accept(Tree.StatementOrArgument node) {
                if (node instanceof Tree.Declaration) {
                    Declaration d = ((Tree.Declaration) node).getDeclarationModel();
                    return d.isToplevel() || d.isClassOrInterfaceMember();
                }
                else {
                    return true;
                }
            }
        };
        rootNode.visit(fcv);
        Tree.StatementOrArgument result = fcv.getStatementOrArgument();
        return new CeylonSearchMatch(match, 
                result==null ? rootNode : result, file);
    }
    
    private CeylonSearchMatch(Node match, 
            //the containing declaration or named arg
            Node node,
            //the file in which the match occurs
            VirtualFile file) {
        super(new CeylonElement(node, file, match.getToken().getLine()),
                //the exact location of the match:
                Nodes.getStartOffset(match), 
                Nodes.getLength(match));
        inImport = node instanceof Tree.Import || 
                node instanceof Tree.ImportModule;
    }
    
    @Override
    public CeylonElement getElement() {
        return (CeylonElement) super.getElement();
    }
    
    public boolean isInImport() {
        return inImport;
    }
    
}
