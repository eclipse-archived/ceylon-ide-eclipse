package com.redhat.ceylon.eclipse.util;

import static com.redhat.ceylon.compiler.typechecker.model.Util.isOverloadedVersion;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.Scope;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.Statement;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;

public class FindUtils {

    public static Tree.Declaration findDeclaration(Tree.CompilationUnit cu, Node node) {
        FindDeclarationVisitor fcv = new FindDeclarationVisitor(node);
        fcv.visit(cu);
        return fcv.getDeclarationNode();
    }

    public static Tree.NamedArgument findArgument(Tree.CompilationUnit cu, Node node) {
        FindArgumentVisitor fcv = new FindArgumentVisitor(node);
        fcv.visit(cu);
        return fcv.getArgumentNode();
    }

    public static Statement findStatement(Tree.CompilationUnit cu, Node node) {
        FindStatementVisitor visitor = new FindStatementVisitor(node, false);
        cu.visit(visitor);
        return visitor.getStatement();
    }

    public static Statement findToplevelStatement(Tree.CompilationUnit cu, Node node) {
        FindStatementVisitor visitor = new FindStatementVisitor(node, true);
        cu.visit(visitor);
        return visitor.getStatement();
    }

    public static Declaration getAbstraction(Declaration d) {
        if (isOverloadedVersion(d)) {
            return d.getContainer().getDirectMember(d.getName(), null, false);
        }
        else {
            return d;
        }
    }

    public static Tree.Declaration getContainer(Tree.CompilationUnit cu,
            final Declaration dec) {
        class FindContainer extends Visitor {
            final Scope container = dec.getContainer();
            Tree.Declaration result;
            @Override
            public void visit(Tree.Declaration that) {
                super.visit(that);
                if (that.getDeclarationModel().equals(container)) {
                    result = that;
                }
            }
        }
        FindContainer fc = new FindContainer();
        cu.visit(fc);
        if (fc.result instanceof Tree.Declaration) {
            return (Tree.Declaration) fc.result;
        }
        else {
            return null;
        }
    }

    public static Tree.ImportMemberOrType findImport(Tree.CompilationUnit cu, Node node) {
        
        if (node instanceof Tree.ImportMemberOrType) {
            return (Tree.ImportMemberOrType) node;
        }
        
        final Declaration declaration;
        if (node instanceof Tree.MemberOrTypeExpression) {
            declaration = ((Tree.MemberOrTypeExpression) node).getDeclaration();
        }
        else if (node instanceof Tree.SimpleType) {
            declaration = ((Tree.SimpleType) node).getDeclarationModel();
        }
        else if (node instanceof Tree.MemberLiteral) {
            declaration = ((Tree.MemberLiteral) node).getDeclaration();
        }
        else {
            return null;
        }
        
        class FindImportVisitor extends Visitor {
            Tree.ImportMemberOrType result;
            @Override
            public void visit(Tree.Declaration that) {}
            @Override
            public void visit(Tree.ImportMemberOrType that) {
                super.visit(that);
                if (that.getDeclarationModel()!=null &&
                        that.getDeclarationModel().equals(declaration)) {
                    result = that;
                }
            }
        }
        
        if (declaration!=null) {
            FindImportVisitor visitor = new FindImportVisitor();
            visitor.visit(cu);
            return visitor.result;
        }
        else {
            return null;
        }
    
    }

}
