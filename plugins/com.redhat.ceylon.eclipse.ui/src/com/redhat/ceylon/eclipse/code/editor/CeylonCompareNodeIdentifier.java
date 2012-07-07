package com.redhat.ceylon.eclipse.code.editor;

import org.eclipse.imp.editor.ModelTreeNode;
import org.eclipse.imp.services.ICompareNodeIdentifier;

import com.redhat.ceylon.compiler.typechecker.tree.Tree;

public class CeylonCompareNodeIdentifier implements ICompareNodeIdentifier {
    
    @Override
    public int getTypeCode(Object o) {
        if (o instanceof ModelTreeNode) {
            o = ((ModelTreeNode) o).getASTNode();
        }
        if (o instanceof Tree.Declaration) {
            return ((Tree.Declaration) o).getDeclarationModel()
                    .getDeclarationKind().ordinal();
        }
        else if (o instanceof Tree.CompilationUnit) {
            return 100;
        }
        else {
            return -1;
        }
    }
    
    @Override
    public String getID(Object o) {
        if (o instanceof ModelTreeNode) {
            o = ((ModelTreeNode) o).getASTNode();
        }
        if (o instanceof Tree.Declaration) {
            return ((Tree.Declaration) o).getDeclarationModel().getQualifiedNameString();
        }
        else if (o instanceof Tree.CompilationUnit) {
            return ((Tree.CompilationUnit) o).getUnit().getFilename();
        }
        else {
            return o.toString();
        }
    }
    
}
