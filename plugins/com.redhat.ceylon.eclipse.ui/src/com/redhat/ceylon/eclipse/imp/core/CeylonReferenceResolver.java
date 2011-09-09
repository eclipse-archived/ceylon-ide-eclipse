package com.redhat.ceylon.eclipse.imp.core;

import org.eclipse.imp.parser.IParseController;
import org.eclipse.imp.services.IReferenceResolver;

import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.Unit;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.imp.parser.CeylonParseController;
import com.redhat.ceylon.eclipse.util.FindDeclarationVisitor;

public class CeylonReferenceResolver implements IReferenceResolver {

    public CeylonReferenceResolver() {
    }

    /**
     * Get the text associated with the given node for use in a link from (or
     * to) that node
     */
    public String getLinkText(Object node) {
        if (node instanceof Node) {
            if (node instanceof Tree.Primary) {
                return ((Tree.Primary) node).getDeclaration().getName();
            } else if (node instanceof Tree.SimpleType) {
                return ((Tree.SimpleType) node).getDeclarationModel().getName();
            } else if (node instanceof Tree.ImportMemberOrType) {
                return ((Tree.ImportMemberOrType) node).getDeclarationModel()
                        .getName();
            }
            if (node instanceof Tree.Declaration) {
                return ((Tree.Declaration) node).getDeclarationModel()
                        .getName();
            } else if (node instanceof Tree.NamedArgument) {
                return ((Tree.NamedArgument) node).getParameter().getName();
            }
            return ((Node) node).getText();
        } else {
            return null; // node.toString();
        }
    }

    /**
     * Get the target for the given source node in the AST produced by the given
     * Parse Controller.
     */
    public Tree.Declaration getLinkTarget(Object node,
            IParseController controller) {
        return getDeclarationNode((CeylonParseController) controller,
                getReferencedDeclaration(node));
    }

    public static Declaration getReferencedDeclaration(Object node) {
        if (node instanceof Tree.Primary) {
            return ((Tree.Primary) node).getDeclaration();
        } else if (node instanceof Tree.SimpleType) {
            return ((Tree.SimpleType) node).getDeclarationModel();
        } else if (node instanceof Tree.ImportMemberOrType) {
            return ((Tree.ImportMemberOrType) node).getDeclarationModel();
        } else if (node instanceof Tree.Declaration) {
            return ((Tree.Declaration) node).getDeclarationModel();
        } else if (node instanceof Tree.NamedArgument) {
            return ((Tree.NamedArgument) node).getParameter();
        } else {
            return null;
        }
    }

    public static Tree.Declaration getDeclarationNode(CeylonParseController cpc, 
            Declaration dec) {
        if (dec==null) {
            return null;
        }
        else {
            Tree.CompilationUnit cu = getCompilationUnit(cpc, dec);
            return cu==null ? null : 
                getDocumentationString(dec, cu);
        }
    }

    public static Tree.Declaration getDocumentationString(Declaration dec,
            Tree.CompilationUnit compilationUnit) {
        FindDeclarationVisitor visitor = new FindDeclarationVisitor(dec);
        compilationUnit.visit(visitor);
        System.out.println("referenced node: " + visitor.getDeclarationNode());
        return visitor.getDeclarationNode();
    }

    public static Tree.CompilationUnit getCompilationUnit(CeylonParseController cpc,
            Declaration dec) {
        Tree.CompilationUnit root = cpc.getRootNode();
        if (root==null) {
            return null;
        }
        else if (root.getUnit().equals(dec.getUnit())) {
            return root;
        }
        else {
            Unit targetUnit = dec.getUnit();
            String relativePath = targetUnit.getPackage()
                    .getQualifiedNameString().replace('.', '/')
                    + "/" + targetUnit.getFilename();
            PhasedUnit targetPhasedUnit = cpc.getPhasedUnits()
                    .getPhasedUnitFromRelativePath(relativePath);
            if (targetPhasedUnit != null) {
                return targetPhasedUnit.getCompilationUnit();
            }
        }
        return null;
    }

}
