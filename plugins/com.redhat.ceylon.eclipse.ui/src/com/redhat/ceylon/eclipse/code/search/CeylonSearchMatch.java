package com.redhat.ceylon.eclipse.code.search;

import static com.redhat.ceylon.eclipse.util.Nodes.getIdentifyingNode;

import org.eclipse.search.ui.text.Match;

import com.redhat.ceylon.compiler.typechecker.io.VirtualFile;
import com.redhat.ceylon.model.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.ide.common.search.FindContainerVisitor;

public class CeylonSearchMatch extends Match {
    
    private boolean inImport;
    
    public static CeylonSearchMatch create(
            Node match, 
            //the containing declaration or named arg
            Tree.CompilationUnit rootNode,
            //the file in which the match occurs
            VirtualFile file) {
        FindContainerVisitor fcv = 
                new FindContainerVisitor(match) {
            @Override
            public boolean accept(
                    Tree.StatementOrArgument node) {
                if (node instanceof Tree.Declaration) {
                    Tree.Declaration dec = 
                            (Tree.Declaration) node;
                    Declaration d = 
                            dec.getDeclarationModel();
                    return d.isToplevel() || 
                           d.isClassOrInterfaceMember();
                }
                else {
                    return true;
                }
            }
        };
        rootNode.visit(fcv);
        Tree.StatementOrArgument result = 
                fcv.getStatementOrArgument();
        Node node = result==null ? rootNode : result;
        return new CeylonSearchMatch(match, node, file);
    }
    
    private CeylonSearchMatch(Node match, 
            //the containing declaration or named arg
            Node node,
            //the file in which the match occurs
            VirtualFile file) {
        super(new CeylonElement(node, file, 
                match.getToken().getLine()),
                //the exact location of the match:
                getIdentifyingNode(match).getStartIndex(), 
                getIdentifyingNode(match).getDistance());
        inImport = 
                node instanceof Tree.Import || 
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
