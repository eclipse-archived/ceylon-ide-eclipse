package com.redhat.ceylon.eclipse.util;

import org.antlr.runtime.CommonToken;

import com.redhat.ceylon.model.typechecker.model.Class;
import com.redhat.ceylon.model.typechecker.model.Declaration;
import com.redhat.ceylon.model.typechecker.model.ProducedType;
import com.redhat.ceylon.model.typechecker.model.TypeDeclaration;
import com.redhat.ceylon.model.typechecker.model.TypedDeclaration;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;

public class Types {

    public static ProducedType getResultType(Declaration d) {
        if (d instanceof TypeDeclaration) {
            if (d instanceof Class) {
                if (!((Class) d).isAbstract()) {
                    return ((TypeDeclaration) d).getType();
                }
            }
            return null;
        }
        else if (d instanceof TypedDeclaration) {
            return ((TypedDeclaration) d).getType();
        }
        else {
            return null;//impossible
        }
    }

    public static ProducedType getRequiredType(Tree.CompilationUnit rootNode,
            Node node, CommonToken token) {
        RequiredTypeVisitor rtv = new RequiredTypeVisitor(node, token);
        rtv.visit(rootNode);
        return rtv.getType();
    }

}
