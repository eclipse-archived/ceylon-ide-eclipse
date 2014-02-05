package com.redhat.ceylon.eclipse.code.correct;

import java.util.List;

import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;

class FindImportNodeVisitor extends Visitor {
    
    private final String[] packageNameComponents;
    private Tree.Import result;
    
    public FindImportNodeVisitor(String packageName) {
        super();
        this.packageNameComponents = packageName.split("\\.");
    }
    
    public Tree.Import getResult() {
        return result;
    }

    public void visit(Tree.Import that) {
        if (result != null) {
            return;
        }

        List<Tree.Identifier> identifiers = that.getImportPath().getIdentifiers();
        if (identifiersEqual(identifiers, packageNameComponents)) {
            result = that;
        }
    }

    private static boolean identifiersEqual(List<Tree.Identifier> identifiers,
            String[] components) {
        if (identifiers.size() != components.length) {
            return false;
        }
        
        for (int i = 0; i < components.length; i++) {
            if (!identifiers.get(i).getText().equals(components[i])) {
                return false;
            }
        }
        
        return true;
    }
}