package com.redhat.ceylon.eclipse.imp.core;

import org.eclipse.core.resources.IProject;
import org.eclipse.imp.parser.IParseController;
import org.eclipse.imp.services.IReferenceResolver;

import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.Parameter;
import com.redhat.ceylon.compiler.typechecker.model.Setter;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.imp.builder.CeylonBuilder;
import com.redhat.ceylon.eclipse.imp.parser.CeylonParseController;
import com.redhat.ceylon.eclipse.util.FindDeclarationVisitor;

public class CeylonReferenceResolver implements IReferenceResolver {

    /**
     * Get the text associated with the given node for use in a link from (or
     * to) that node
     */
    public String getLinkText(Object node) {
        if (node instanceof Node) {
            return getNodeDeclarationName((Node) node);
        } 
        else {
            return null;
        }
    }

    /**
     * Get the target for the given source node in the AST produced by the given
     * Parse Controller.
     */
    public Tree.Declaration getLinkTarget(Object node,
            IParseController controller) {
        if (node instanceof Node) {
            return getReferencedNode(node, controller);
        }
        else {
            return null;
        }
    }

    public static Tree.Declaration getReferencedNode(Object node, IParseController controller) {
        Declaration dec = getReferencedDeclaration((Node) node);
        if (dec instanceof Parameter) {
            Declaration pd = ((Parameter) dec).getDeclaration();
            if (pd instanceof Setter) {
                dec = pd;
            }
            else {
                Declaration att = pd.getMemberOrParameter(dec.getUnit(), dec.getName(), null);
                if (att!=null) dec = att;
            }
        }
        return getReferencedNode(dec, 
            getCompilationUnit((CeylonParseController) controller, dec));
    }

    private String getNodeDeclarationName(Node node) {
        if (node instanceof Tree.MemberOrTypeExpression) {
            return ((Tree.MemberOrTypeExpression) node).getDeclaration().getName();
        } 
        else if (node instanceof Tree.SimpleType) {
            return ((Tree.SimpleType) node).getDeclarationModel().getName();
        } 
        else if (node instanceof Tree.ImportMemberOrType) {
            return ((Tree.ImportMemberOrType) node).getDeclarationModel()
                    .getName();
        }
        if (node instanceof Tree.Declaration) {
            return ((Tree.Declaration) node).getDeclarationModel()
                    .getName();
        } 
        else if (node instanceof Tree.NamedArgument) {
            return ((Tree.NamedArgument) node).getParameter().getName();
        }
        else {
            return null;
        }
    }

    public static Node getIdentifyingNode(Node node) {
        if (node instanceof Tree.Declaration) {
            return ((Tree.Declaration) node).getIdentifier();
        }
        else if (node instanceof Tree.NamedArgument) {
            return ((Tree.NamedArgument) node).getIdentifier();
        }
        else if (node instanceof Tree.StaticMemberOrTypeExpression) {
            return ((Tree.StaticMemberOrTypeExpression) node).getIdentifier();
        }
        else if (node instanceof Tree.ExtendedTypeExpression) {
            //TODO: whoah! this is really ugly!
            return ((Tree.SimpleType) ((Tree.ExtendedTypeExpression) node).getChildren().get(0))
                    .getIdentifier();
        }
        else if (node instanceof Tree.SimpleType) {
            return ((Tree.SimpleType) node).getIdentifier();
        }
        else if (node instanceof Tree.ImportMemberOrType) {
            return ((Tree.ImportMemberOrType) node).getIdentifier();
        }
        else {    
            return node;
        }
    }

    public static Declaration getReferencedDeclaration(Node node) {
        //NOTE: this must accept a null node, returning null!
        if (node instanceof Tree.MemberOrTypeExpression) {
            return ((Tree.MemberOrTypeExpression) node).getDeclaration();
        } 
        else if (node instanceof Tree.SimpleType) {
            return ((Tree.SimpleType) node).getDeclarationModel();
        } 
        else if (node instanceof Tree.ImportMemberOrType) {
            return ((Tree.ImportMemberOrType) node).getDeclarationModel();
        } 
        else if (node instanceof Tree.Declaration) {
            return ((Tree.Declaration) node).getDeclarationModel();
        } 
        else if (node instanceof Tree.NamedArgument) {
            return ((Tree.NamedArgument) node).getParameter();
        } 
        else {
            return null;
        }
    }

    public static Tree.Declaration getReferencedNode(Declaration dec,
            Tree.CompilationUnit compilationUnit) {
        if (compilationUnit==null || dec==null) {
            return null;
        }
        else {
            FindDeclarationVisitor visitor = new FindDeclarationVisitor(dec);
            compilationUnit.visit(visitor);
            //System.out.println("referenced node: " + visitor.getDeclarationNode());
            return visitor.getDeclarationNode();
        }
    }
    
    public static Tree.CompilationUnit getCompilationUnit(IProject project, 
            Declaration dec) {
        PhasedUnit phasedUnit = getPhasedUnit(project, dec);
        return phasedUnit==null ? null : phasedUnit.getCompilationUnit();
    }

    public static PhasedUnit getPhasedUnit(IProject project, 
            Declaration dec) {
        return CeylonBuilder.getProjectTypeChecker(project)
                        .getPhasedUnitFromRelativePath(getRelativePath(dec));
    }

    public static Tree.CompilationUnit getCompilationUnit(CeylonParseController cpc,
            Declaration dec) {
        if (cpc==null || dec==null) {
            return null;
        }
        else {
            Tree.CompilationUnit root = cpc.getRootNode();
            if (root!=null && root.getUnit() != null && 
                    root.getUnit().equals(dec.getUnit())) {
                return root;
            }
            else {
                PhasedUnit pu = cpc.getTypeChecker()
                        .getPhasedUnitFromRelativePath(getRelativePath(dec));
                return pu==null ? null : pu.getCompilationUnit();
            }
        }
    }

    private static String getRelativePath(Declaration dec) {
        return dec.getUnit().getPackage()
                .getQualifiedNameString().replace('.', '/')
                + "/" + dec.getUnit().getFilename();
    }

}
