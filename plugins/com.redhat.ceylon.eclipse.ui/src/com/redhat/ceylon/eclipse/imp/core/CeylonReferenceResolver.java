package com.redhat.ceylon.eclipse.imp.core;

import org.eclipse.imp.parser.IParseController;
import org.eclipse.imp.services.IReferenceResolver;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.ui.FindDeclarationVisitor;

public class CeylonReferenceResolver implements IReferenceResolver {

  public CeylonReferenceResolver() {
  }

  /**
   * Get the text associated with the given node for use in a link
   * from (or to) that node
   */
  public String getLinkText(Object node) {
    if (node instanceof Node)
    {
        if (node instanceof Tree.Primary) {
          return ((Tree.Primary) node).getDeclaration().getName();
        }
        else if (node instanceof Tree.SimpleType) {
          return ((Tree.SimpleType) node).getDeclarationModel().getName();
        }
        if (node instanceof Tree.Declaration) {
          return ((Tree.Declaration) node).getDeclarationModel().getName();
        }
      return ((Node) node).getText();
    }
    else
    {
      return null; //node.toString(); 
    }
  }

  /**
   * Get the target for the given source node in the AST produced by the
   * given Parse Controller.
   */
  public Object getLinkTarget(Object node, IParseController controller) {
    Declaration dec = null;
    if (node instanceof Tree.Primary) {
      dec = ((Tree.Primary) node).getDeclaration();
    }
    else if (node instanceof Tree.SimpleType) {
      dec = ((Tree.SimpleType) node).getDeclarationModel();
    }
    else if (node instanceof Tree.Declaration) {
      dec = ((Tree.Declaration) node).getDeclarationModel();
    }
    if (dec!=null && controller.getCurrentAst() != null) {
      Tree.CompilationUnit compilationUnit = (Tree.CompilationUnit) controller.getCurrentAst();
      FindDeclarationVisitor visitor = new FindDeclarationVisitor(dec);
      compilationUnit.visit(visitor);
      System.out.println("referenced node: " + visitor.getDeclarationNode());
      return visitor.getDeclarationNode();
    }
    else {
      return null;
    }
  }
  
}
