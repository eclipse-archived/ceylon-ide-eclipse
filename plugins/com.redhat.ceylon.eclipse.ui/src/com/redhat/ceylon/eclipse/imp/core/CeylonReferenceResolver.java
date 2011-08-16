package com.redhat.ceylon.eclipse.imp.core;

import lpg.runtime.IAst;

import org.eclipse.imp.parser.IParseController;
import org.eclipse.imp.parser.SimpleLPGParseController;
import org.eclipse.imp.parser.SymbolTable;
import org.eclipse.imp.services.IReferenceResolver;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.CompilationUnit;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.Primary;
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
      return ((Node) node).getText();
    }
    else
    {
      return node.toString(); 
    }
  }

  /**
   * Get the target for the given source node in the AST produced by the
   * given Parse Controller.
   */
  public Object getLinkTarget(Object node, IParseController controller) {
    if (node instanceof Primary && controller.getCurrentAst() != null) {
      Primary primaryNode = (Primary) node;
      Declaration declarationModel = primaryNode.getDeclaration();
      CompilationUnit compilationUnit = (CompilationUnit) controller.getCurrentAst();
      FindDeclarationVisitor visitor = new FindDeclarationVisitor(declarationModel);
      compilationUnit.visit(visitor);
      return visitor.getDeclarationNode();
    }
    return null;
  }
}
